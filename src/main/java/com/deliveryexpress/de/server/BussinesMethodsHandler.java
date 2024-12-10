/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author DeliveryExpress
 */
public class BussinesMethodsHandler implements HttpHandler {

    private static final Logger logger = Logger.getLogger(BussinesMethodsHandler.class.getName());

    private final Map<String, GetBussinesAction> getActionMap;
    private final Map<String, PostBussinesAction> postActionMap;

    public BussinesMethodsHandler() {

        // Crear el mapa de acciones
        getActionMap = new HashMap<>();
        getActionMap.put("fetchAddreses", new FetchAddresesAction());
        getActionMap.put("getCustomer", new GetCustomerAction());
        getActionMap.put("cotizer", new GetCotaizerAction());
        getActionMap.put("getBussinesData", new GetBussinesDataAction());

        postActionMap = new HashMap<>();
        postActionMap.put("newOrder", new PostNewOrderAction());

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {// Agregar cabeceras CORS
            ResponseHelper.setCORSHeaders(exchange);

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // Responder a preflight
                return;
            }

            String requestPath = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            System.out.println("method: " + method + " requestPath: " + requestPath + " RequestURI: " + exchange.getRequestURI());

            if ("/bussines".equals(requestPath)) {
                if ("GET".equalsIgnoreCase(method)) {
                    handleGetRequest(exchange);
                } else if ("POST".equalsIgnoreCase(method)) {
                    handlePostRequest(exchange);
                } else {
                    exchange.sendResponseHeaders(405, -1); // Método no permitido
                }
            } else {
                exchange.sendResponseHeaders(404, -1); // Ruta no encontrada
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {

        QueryParams params = new QueryParams(exchange.getRequestURI().getQuery());

        if (getActionMap.containsKey(params.getAction())) {
            getActionMap.get(params.getAction()).handle(exchange, params);
        } else {
            handleInvalidAction(exchange);
        }

    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {

        // Leer todo el cuerpo de la solicitud
        InputStream requestBody = exchange.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        JsonObject obj = new Gson().fromJson(body, JsonObject.class);

        System.out.println("handlePostRequest-> "+obj);

        String action = obj.get("action").getAsString();
        if (postActionMap.containsKey(action)) {
            postActionMap.get(action).handle(exchange, obj);
        } else {
            handleInvalidAction(exchange);
        }

    }

    private void handleInvalidAction(HttpExchange exchange) throws IOException {
        ResponseHelper.sendErrorResponse(exchange, 400, "Acción no válida");
        logger.warning("Acción no válida: " + exchange.getRequestURI().getQuery());
    }

}
