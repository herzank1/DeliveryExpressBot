/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DeliveryExpress
 */
public class StaticFilesHandler implements HttpHandler {

    private static final Logger logger = Logger.getLogger(StaticFilesHandler.class.getName());

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Agregar cabeceras CORS
        ResponseHelper.setCORSHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // Responder a preflight
            return;
        }

           try {
            String requestedFile = exchange.getRequestURI().getPath();
            if (requestedFile.equals("/")) {
                requestedFile = "/index.html"; // Redirigir la raíz a index.html
            }
            
            logger.info("Intentando cargar recurso: " + requestedFile);
             InputStream fileStream = getClass().getResourceAsStream("/templates" + requestedFile);


            if (fileStream != null) {
                String mimeType = determineMimeType(requestedFile);
                logger.info("Recurso encontrado: " + requestedFile + ", MIME Type: " + mimeType);
                ResponseHelper.sendFile(exchange, fileStream, mimeType);
            } else {
                logger.warning("No se encontró el recurso: " + requestedFile);
                exchange.sendResponseHeaders(404, -1);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al manejar la solicitud", e);
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private String determineMimeType(String filename) {
        if (filename.endsWith(".js")) {
            return "application/javascript";
        }
        if (filename.endsWith(".css")) {
            return "text/css";
        }
        if (filename.endsWith(".html")) {
            return "text/html";
        }
        if (filename.endsWith(".png")) {
            return "image/png";
        }
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (filename.endsWith(".ico")) {
            return "image/x-icon";
        }
        return "application/octet-stream";
    }

}
