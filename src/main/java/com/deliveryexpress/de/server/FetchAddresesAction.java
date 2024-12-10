/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;

import com.deliveryexpress.utils.GoogleMapsUtils;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author DeliveryExpress
 */
  public class FetchAddresesAction implements GetBussinesAction {

        @Override
        public void handle(HttpExchange exchange, QueryParams params) throws IOException {
            String queryParam = params.getParam("query");
            if (queryParam == null || queryParam.isEmpty()) {
                ResponseHelper.sendErrorResponse(exchange, 400, "Parámetro query vacío o inválido");
                return;
            }
            List<String> fetchGeocodingResults = GoogleMapsUtils.fetchGeocodingResults(queryParam);
            String jsonResponse = new Gson().toJson(fetchGeocodingResults);
            ResponseHelper.sendJsonResponse(exchange, jsonResponse);
        }
    }