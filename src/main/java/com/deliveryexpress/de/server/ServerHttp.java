/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DeliveryExpress
 */
public class ServerHttp implements Runnable {

    int port;

    public ServerHttp(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        // Crear el servidor HTTP
        HttpServer server;
        try {
       
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new StaticFilesHandler());
            server.createContext("/bussines", new BussinesMethodsHandler());
            server.createContext("/validate", new ValidateHandler());
            server.setExecutor(null); // Utiliza el ejecutor por defecto
            server.start();

            System.out.println("Servidor HTTP iniciado en el puerto " + port);
        } catch (Exception ex) {
            Logger.getLogger(ServerHttp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


}
