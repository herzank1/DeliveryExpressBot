/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.deliveryexpress.de;

import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.de.server.ServerHttp;
import com.deliveryexpress.de.server.ServerHttps;
import com.deliveryexpress.objects.users.DBot;
import com.deliveryexpress.telegram.UsersController;
import com.monge.tbotboot.messenger.Bot;
import com.monge.tbotboot.messenger.BotsHandler;

/**
 *
 * @author DeliveryExpress
 */
public class Main {

    public static void main(String[] args) {
        try {

            Global.getInstance();
            /*Iniciamos la base de datos*/
            DataBase.init();
            /*Cargamos los bots e iniciamos el mensajero executor*/

            BotsHandler.init(new UsersController(), DBot.readAll(DBot.class)
                    .stream()
                    .map(DBot::getBot)
                    .toArray(Bot[]::new));

            OrdersControl.init();

            if (Global.getInstance().getEnterprise().isStartServer()) {
                int serverPort = Global.getInstance().getOrdersConfig().getServerPort();
                boolean https = Global.getInstance().enterprise.isHttps();
                if (https) {
                    Thread serverThread = new Thread(new ServerHttps(serverPort));
                    serverThread.start();
                } else {
                    Thread serverThread = new Thread(new ServerHttp(serverPort));
                    serverThread.start();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
