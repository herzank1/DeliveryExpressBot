/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de;

import com.deliveryexpress.utils.JsonStorable;
import com.deliveryexpress.utils.JsonStorable.path;
import com.deliveryexpress.utils.Settings;
import com.deliveryexpress.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author DeliveryExpress
 */
@path(path = "config")
public class Global implements JsonStorable {

    private static Global instance;
    public static String fileName = "settings";
    @fileName
    String _fileName = fileName;

    @Getter
    @Setter
    OrdersConfig ordersConfig;
    @Getter
    @Setter
    Enterprise enterprise;
    @Getter
    @Setter
    SystemUsers systemUsers;

    public Global() {
        this.ordersConfig = new OrdersConfig() ;
        this.enterprise = new Enterprise();
        this.systemUsers = new SystemUsers();
    }

   

    public static Global getInstance() {
        if (instance == null) {

            try {
                instance = JsonStorable.load(Global.class, Global.fileName);
                System.out.println(new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(instance));
                instance.save();
            } catch (Exception e) {
                instance = new Global();
                instance.save();
            }
        }

        return instance;

    }

    @Data
    public class OrdersConfig {

        private String city;
        private boolean checkDeliveryPositionOnChangeStatus;
        private float execed_delivery_distance;
        private int delivery_max_order_count;
        private boolean manual_order_only;
        private boolean request_delivery_location;
        private float dinamic_rate; //tarifa dinamica, el total del envio se multipica por el factor
        //minimo 1.0 - maximo 1.5

        public OrdersConfig() {
            this.city = "Mexicali B,C.";
            this.checkDeliveryPositionOnChangeStatus = false;
            this.execed_delivery_distance = 10;
            this.delivery_max_order_count = 2;
            this.manual_order_only = false;
            this.request_delivery_location = false;
            this.dinamic_rate = 1.0f;
        }

    }

    @Data
    public class Enterprise {

        private String system_name;
        private String bussines_name;
        private String email;
        private String phone;

        public Enterprise() {
            this.system_name = "Delivery Express System";
            this.bussines_name = "DeliveryExpress";
            this.email = "diego.villarreal.monge@gmail.com";
            this.phone = "6863095448";
        }

    }

    @Data
    public class SystemUsers {

        private String bussines_name;
        private int max_users_transacctions_view;
        private String phone;

        public SystemUsers() {
            this.bussines_name = "Delivery Express Bot";
            this.max_users_transacctions_view = 10;
            this.phone = "6863095448";
        }

    }

}
