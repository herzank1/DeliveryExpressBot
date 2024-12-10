/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;

import com.deliveryexpress.objects.users.Customer;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;


    public class GetCustomerAction implements GetBussinesAction {

        @Override
        public void handle(HttpExchange exchange, QueryParams params) throws IOException {

            Customer c = Customer.read(Customer.class, params.getParam("phone"));
            String jsonResponse = new Gson().toJson(c);

            ResponseHelper.sendJsonResponse(exchange, jsonResponse);
        }
    }