/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.deliveryexpress.de;

import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.objects.users.DBot;
import com.deliveryexpress.telegram.UsersController;
import com.deliveryexpress.utils.Settings;
import com.monge.tbotboot.messenger.Bot;
import com.monge.tbotboot.messenger.BotsHandler;
import com.monge.xsqlite.xsqlite.BaseDao;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 *
 * @author DeliveryExpress
 */
public class Main {

    public static void main(String[] args) {

        Settings.cargar();
        /*Cargamos variables*/
        Global.load();
        /*Iniciamos la base de datos*/
        DataBase.init();
        /*Cargamos los bots e iniciamos el mensajero executor*/
       
        BotsHandler.init(new UsersController(), DBot.readAll(DBot.class)
                .stream()
                .map(DBot::getBot)
                .toArray(Bot[]::new));
        
        OrdersControl.init();
        
     

    }
}
