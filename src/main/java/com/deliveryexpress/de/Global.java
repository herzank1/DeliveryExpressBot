/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de;

import com.deliveryexpress.objects.TelegramUser.BalanceAccount;
import com.deliveryexpress.utils.Utils;
import com.google.gson.Gson;

/**
 *
 * @author DeliveryExpress
 */
public class Global {
    
    private static Global global;
    
    public  String city = "Mexicali, B.C.";
    public  float execed_delivery_distance = 10f;
    public int  delivery_max_order_count = 2;
    public  int max_users_transacctions_view = 20;
    public  boolean manual_order_only = false;
    public  boolean request_delivery_location = false;
    public String mainBalanceAccount = "0x000000";
    public  float dinamic_rate = 1.0f; //tarifa dinamica, el total del envio se multipica por el factor
    //minimo 1.0 - maximo 1.5
    
    
    public static void load() {

        Utils.FilesUtils.checkFile("Global.json", Utils.toJsonString(new Global()));

        String readJsonFile = Utils.FilesUtils.readJsonFile("Global.json");
        Gson gson = new Gson();
        global = gson.fromJson(readJsonFile, Global.class);
        System.out.println("Global variables loade!\n"+Utils.toJsonString(global));

    }
    
    public static Global Global() {
        return global;

    }
    
    public String setValue(String variableName, String value) {

        try {

            switch (variableName) {

                case "city":
                    this.city = String.valueOf(value);
                    break;
                    
               case "execed_delivery_distance":
                    this.execed_delivery_distance = Float.parseFloat(value) ;

                    break; 
                    
                case "max_users_transacctions_view":
                    this.max_users_transacctions_view = Integer.parseInt(value) ;

                    break;
                    
                case "manual_order_only":
                    this.manual_order_only =  Boolean.parseBoolean(value);

                    break;
                    
               case "request_delivery_location":
                    this.request_delivery_location =  Boolean.parseBoolean(value);

                    break; 
                    
                case "delivery_max_order_count":
                    this.delivery_max_order_count =  Integer.parseInt(value);

                    break;      

            }
        } catch (Exception e) {
            return e.getMessage();
        }

        
        return "UPDATE SUCCESS";
    }

    @Override
    public String toString() {
        return Utils.toJsonString(this);
    }
    
    public BalanceAccount getMainBalanceAccount() {

        return DataBase.BalancesAccounts.getBalanceAccount(this.mainBalanceAccount);
    }
    
}
