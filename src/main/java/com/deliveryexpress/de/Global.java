/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de;

import com.deliveryexpress.utils.Settings;
import com.deliveryexpress.utils.Utils;
import com.google.gson.Gson;

/**
 *
 * @author DeliveryExpress
 */
public class Global {
    
    private static Global global;
    
    public  static String city = "Mexicali, B.C.";
    public static float execed_delivery_distance = 10f;
    public static int  delivery_max_order_count = 2;
    public static int max_users_transacctions_view = 20;
    public static boolean manual_order_only = false;
    public static boolean request_delivery_location = false;
    public static String mainBalanceAccount = "0x000000";
    public static float dinamic_rate = 1.0f; //tarifa dinamica, el total del envio se multipica por el factor
    //minimo 1.0 - maximo 1.5
    
    
    public static void load() {

      city = Settings.getString("city", "Mexicali, B.C.");
      execed_delivery_distance = Settings.getFloat("execed_delivery_distance", 10f);
      city = Settings.getString("city", "Mexicali, B.C.");
      city = Settings.getString("city", "Mexicali, B.C.");
      

    }
    
    public static Global Global() {
        return global;

    }
    
    public static String setValue(String variableName, String value) {

        try {

            switch (variableName) {

                case "city":
                    city = String.valueOf(value);
                    break;
                    
               case "execed_delivery_distance":
                    execed_delivery_distance = Float.parseFloat(value) ;

                    break; 
                    
                case "max_users_transacctions_view":
                    max_users_transacctions_view = Integer.parseInt(value) ;

                    break;
                    
                case "manual_order_only":
                    manual_order_only =  Boolean.parseBoolean(value);

                    break;
                    
               case "request_delivery_location":
                   request_delivery_location =  Boolean.parseBoolean(value);

                    break; 
                    
                case "delivery_max_order_count":
                    delivery_max_order_count =  Integer.parseInt(value);

                    break;      

            }
        } catch (Exception e) {
            return e.getMessage();
        }

        
        return "UPDATE SUCCESS";
    }


    
}
