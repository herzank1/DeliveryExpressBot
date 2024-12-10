/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author DeliveryExpress
 */
public class ValidateHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            byte[] requestBody = exchange.getRequestBody().readAllBytes();
            String requestBodyString = new String(requestBody, StandardCharsets.UTF_8);

            // Parsear datos enviados (ej. initData)
            Map<String, String> payload = parseFormData(requestBodyString);
            String initData = payload.get("initData");

            // Validar datos
            boolean isValid = validateTelegramData("BotToken",initData);

            // Responder al cliente
            String response = isValid
                    ? "{\"status\":\"valid\",\"message\":\"Datos válidos\"}"
                    : "{\"status\":\"invalid\",\"message\":\"Datos no válidos\"}";

            
            ResponseHelper.sendJsonResponse(exchange, response);

        } else {
            exchange.sendResponseHeaders(405, -1); // Método no permitido
        }

    }

    private boolean validateTelegramData(String BOT_TOKEN, String initData) {
        try {
            Map<String, String> data = parseInitData(initData);
            String receivedHash = data.remove("hash");

            // Crear data_check_string
            String dataCheckString = data.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse("");

            // Crear llave secreta
            byte[] secretKey = hmacSha256(BOT_TOKEN.getBytes(StandardCharsets.UTF_8), "WebAppData".getBytes(StandardCharsets.UTF_8));

            // Calcular el hash
            byte[] calculatedHash = hmacSha256(dataCheckString.getBytes(StandardCharsets.UTF_8), secretKey);
            String calculatedHashHex = bytesToHex(calculatedHash);

            return calculatedHashHex.equals(receivedHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Map<String, String> parseInitData(String initData) {
        Map<String, String> data = new HashMap<>();
        String[] pairs = initData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                data.put(keyValue[0], keyValue[1]);
            }
        }
        return data;
    }

    private Map<String, String> parseFormData(String formData) {
        Map<String, String> data = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                data.put(keyValue[0], keyValue[1]);
            }
        }
        return data;
    }

    private byte[] hmacSha256(byte[] data, byte[] key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(data);
    }
    
        private String bytesToHex(byte[] bytes) {
            StringBuilder hexString = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
    

}
