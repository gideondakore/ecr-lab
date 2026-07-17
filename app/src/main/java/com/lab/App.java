package com.lab;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Minimal dependency-free HTTP server. Serves the static lab UI on "/"
 * and a health endpoint on "/health" for ALB target group checks.
 */
public final class App {

    private static final String FULL_NAME = "Gideon Dakore";
    private static final String LAB_NAME = "ECS CI/CD Lab";
    private static final String VERSION =
            System.getenv().getOrDefault("APP_VERSION", "1.0.0");

    private static final String PAGE = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="utf-8">
              <title>%s</title>
              <style>
                body { font-family: sans-serif; background: #0f172a; color: #e2e8f0;
                       display: flex; align-items: center; justify-content: center;
                       height: 100vh; margin: 0; }
                .card { background: #1e293b; padding: 2.5rem 3rem; border-radius: 12px;
                        text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,.4); }
                h1 { margin-top: 0; color: #38bdf8; }
                .muted { color: #94a3b8; font-size: .9rem; }
              </style>
            </head>
            <body>
              <div class="card">
                <h1>%s</h1>
                <p>%s</p>
                <p class="muted">version %s</p>
              </div>
            </body>
            </html>
            """.formatted(LAB_NAME, FULL_NAME, LAB_NAME, VERSION);

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/health", exchange ->
                respond(exchange, 200, "{\"status\":\"ok\"}", "application/json"));
        server.createContext("/", exchange ->
                respond(exchange, 200, PAGE, "text/html; charset=utf-8"));

        server.start();
        System.out.printf("ecs-lab-app v%s listening on port is life%d%n", VERSION, port);
    }

    private static void respond(com.sun.net.httpserver.HttpExchange exchange,
                                int status, String body, String contentType)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private App() {
    }
}
