/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;

import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author DeliveryExpress
 */
public class ResponseHelper {

    public static void setCORSHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    public static void sendJsonResponse(HttpExchange exchange, String responseBody) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseBody.getBytes().length);

        OutputStream os = exchange.getResponseBody();
        os.write(responseBody.getBytes());
        os.close();
    }

    public static void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.sendResponseHeaders(statusCode, message.length());
        OutputStream os = exchange.getResponseBody();
        os.write(message.getBytes());
        os.close();
    }

    public static void sendFile(HttpExchange exchange, InputStream fileStream, String mimeType) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", mimeType);
        exchange.sendResponseHeaders(200, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } finally {
            fileStream.close();
        }
    }

}
