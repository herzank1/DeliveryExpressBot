/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

/**
 *
 * @author DeliveryExpress
 */
public interface GetBussinesAction {

        void handle(HttpExchange exchange, QueryParams params) throws IOException;
    }