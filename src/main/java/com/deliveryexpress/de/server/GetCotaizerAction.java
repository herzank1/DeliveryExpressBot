/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;

import com.deliveryexpress.de.Global;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.Tuser;
import com.deliveryexpress.utils.Cotization;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author DeliveryExpress
 */
public class GetCotaizerAction implements GetBussinesAction {

    @Override
    public void handle(HttpExchange exchange, QueryParams params) throws IOException {
        
        
        Tuser tuser = Tuser.read(Tuser.class, params.getParam("userid"));
        Bussines bussines = tuser.getBussines();
       
        String address = params.getParam("address");
        if (address != null) {
            address = URLDecoder.decode(address, StandardCharsets.UTF_8);
        }
        System.out.println("Address después de decodificar: " + address);

        Cotization cot = new Cotization(Global.getInstance().getOrdersConfig().getCity(),
                bussines.getAddress(), address);
        float deliveryCost = cot.getDeliveryCost(bussines.getContract());

        // Crear el objeto JSON
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("city", cot.getCityContext());
        jsonResponse.addProperty("from", cot.getFrom());
        jsonResponse.addProperty("to", cot.getTo());
        jsonResponse.addProperty("distance", cot.getDistance());
        jsonResponse.addProperty("duration", cot.getDuration());
        jsonResponse.addProperty("deliveryCost", deliveryCost);;

        String jsonResponseString = new Gson().toJson(jsonResponse);

        ResponseHelper.sendJsonResponse(exchange, jsonResponseString);

    }

}
