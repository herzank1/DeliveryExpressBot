/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

/**
 *
 * @author DeliveryExpress
 */
public class ServerHttps implements Runnable {

    int port;

    public ServerHttps(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        // Crear el servidor HTTP
        HttpsServer server;
        try {
            // Configurar el contexto SSL
            SSLContext sslContext = createSSLContext();

            server = HttpsServer.create(new InetSocketAddress(port), 0);
            server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                @Override
                public void configure(HttpsParameters params) {
                    try {
                        // Configurar los parámetros HTTPS
                        SSLContext context = getSSLContext();
                        SSLEngine engine = context.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        // Configurar el contexto SSL predeterminado
                        params.setSSLParameters(context.getDefaultSSLParameters());
                    } catch (Exception ex) {
                        Logger.getLogger(ServerHttps.class.getName()).log(Level.SEVERE, "Failed to create SSL parameters", ex);
                        throw new RuntimeException(ex);
                    }
                }
            });

            server.createContext("/", new StaticFilesHandler());
            server.createContext("/bussines", new BussinesMethodsHandler());
            server.createContext("/validate", new ValidateHandler());
            server.setExecutor(null); // Utiliza el ejecutor por defecto
            server.start();

            System.out.println("Servidor HTTPs iniciado en el puerto " + port);
        } catch (Exception ex) {
            Logger.getLogger(ServerHttps.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private SSLContext createSSLContext() throws Exception {
        // Cargar el keystore con la clave privada y el certificado
        char[] password = "dvvm1516".toCharArray(); // Reemplazar con la contraseña real
      
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream("keystore.jks")) { // Ruta al archivo JKS
            keyStore.load(fis, password);
        }
       

        // Inicializar el KeyManagerFactory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, password);

        // Inicializar el TrustManagerFactory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        // Configurar el contexto SSL
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return sslContext;
    }

}
