/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.deliveryexpress.de;

import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.telegram.BotsHandler;
import com.deliveryexpress.utils.Settings;


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
        /*activamos el sistema de seguridad contra ataques de fuerza bruta*/
        SystemSecurity.init();

    }
}
