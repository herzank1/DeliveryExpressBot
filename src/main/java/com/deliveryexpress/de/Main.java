/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.deliveryexpress.de;


import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.utils.Settings;
import com.monge.tbotboot.messenger.BotsHandler;


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
        BotsHandler.init();
       

    }
}
