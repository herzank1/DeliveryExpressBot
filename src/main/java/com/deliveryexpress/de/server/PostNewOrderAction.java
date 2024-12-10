/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;

import com.deliveryexpress.de.OrdersControl;
import com.deliveryexpress.de.orders.Order;
import com.deliveryexpress.objects.users.AccountStatus;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.Customer;
import com.deliveryexpress.objects.users.Tuser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author DeliveryExpress
 */
public class PostNewOrderAction implements PostBussinesAction {

    @Override
    public void handle(HttpExchange exchange, JsonObject obj) throws IOException {

        try {
            
            System.out.println(" public void handle(HttpExchange exchange, JsonObject obj) throws IOException");
          
            OrderRequest or = new Gson().fromJson(obj, OrderRequest.class);
            String userid = or.getBussinesid();
            Tuser tuser = Tuser.read(Tuser.class, userid);
            Bussines bussines = tuser.getBussines();
            
            boolean equals = bussines.getAccountStatus().equals(AccountStatus.INACTIVE);

            Customer customer = new Customer();
            customer.setName(or.getName());
            customer.setPhone(or.getPhone());
            customer.setLastAddress(or.getAddress());
            customer.setLastNote(or.getNote());

            Order order = new Order(bussines, false);

            order.setCustomer(customer);
            order.setDeliveryCost(or.getDeliveryCost());
            order.setOrderCost(or.getOrderCost());

            OrdersControl.addNewOrder(order);
            // Crear la respuesta
            String response = "Formulario recibido correctamente";
            exchange.sendResponseHeaders(200, response.getBytes().length);

            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(response.getBytes());
            responseBody.close();
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }

    }

}
