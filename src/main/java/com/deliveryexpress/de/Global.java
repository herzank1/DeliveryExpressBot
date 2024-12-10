/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de;

import com.deliveryexpress.utils.JsonStorable;
import com.deliveryexpress.utils.JsonStorable.path;
import com.google.gson.GsonBuilder;
import com.monge.tbotboot.messenger.SystemSecurity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author DeliveryExpress Configuracion Global del sistema
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
        this.ordersConfig = new OrdersConfig();
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

    public liveServerData getLiveData() {
        return new liveServerData();
    }

    public String toStringForTelegram() {
        return "🌎 *Global Configurations*\n"
                + // "📂 File Name: " + _fileName + "\n" +
                ordersConfig.toStringForTelegram();
        //  enterprise.toStringForTelegram() + "\n" +
        // systemUsers.toStringForTelegram();
    }

    @Data
    public class OrdersConfig {

        private String city;
        private String serverIp;
        private int serverPort;
        private boolean checkDeliveryPositionOnChangeStatus;
        private float execed_delivery_distance;
        private int delivery_max_order_count;
        private boolean manual_order_only;
        private boolean request_delivery_location;
        private float dinamic_rate; //tarifa dinamica, el total del envio se multipica por el factor
        private  boolean allowNewOrderTroughApp;
        
        //minimo 1.0 - maximo 1.5

        public OrdersConfig() {
            this.allowNewOrderTroughApp = true;
            this.serverIp = "https://34.71.89.3";
            this.serverPort = 8080;
            this.city = "Mexicali B,C.";
            this.checkDeliveryPositionOnChangeStatus = false;
            this.execed_delivery_distance = 10;
            this.delivery_max_order_count = 2;
            this.manual_order_only = false;
            this.request_delivery_location = false;
            this.dinamic_rate = 1.0f;
        }

        public String toStringForTelegram() {
            return "🚛 *Orders Configurations*\n"
                    + "🏙️ City: " + city + "\n"
                    + "📍 Check Delivery Position: " + checkDeliveryPositionOnChangeStatus + "\n"
                    + "📏 Exceeded Delivery Distance: " + execed_delivery_distance + " km\n"
                    + "📦 Max Order Count: " + delivery_max_order_count + "\n"
                    + "🛠️ Manual Order Only: " + manual_order_only + "\n"
                    + "🗺️ Request Delivery Location: " + request_delivery_location + "\n"
                    + "📈 Dynamic Rate: " + dinamic_rate;
        }

    }

    @Data
    public class Enterprise {

        private boolean startServer;
        private boolean https;
        private String system_name;
        private String bussines_name;
        private String email;
        private String phone;

        public Enterprise() {
            this.startServer = true;
            this.https = false;
            this.system_name = "Delivery Express System";
            this.bussines_name = "DeliveryExpress";
            this.email = "diego.villarreal.monge@gmail.com";
            this.phone = "6863095448";
        }

        public String toStringForTelegram() {
            return "🏢 *Enterprise Configurations*\n"
                    + "🔧 System Name: " + system_name + "\n"
                    + "📛 Business Name: " + bussines_name + "\n"
                    + "✉️ Email: " + email + "\n"
                    + "📞 Phone: " + phone;
        }

    }

    @Data
    public class SystemUsers {

        private String bussines_name;
        private int max_users_transacctions_view;
        private String phone;
        private int MAX_REQUEST_COUNTER;

        public SystemUsers() {
            this.bussines_name = "Delivery Express Bot";
            this.max_users_transacctions_view = 10;
            this.phone = "6863095448";
            this.MAX_REQUEST_COUNTER = 10;

            SystemSecurity.setMAX_REQUEST_COUNTER(MAX_REQUEST_COUNTER);

        }

        public String toStringForTelegram() {
            return "👥 *System Users Configurations*\n"
                    + "📛 Business Name: " + bussines_name + "\n"
                    + "📊 Max Transactions View: " + max_users_transacctions_view + "\n"
                    + "📞 Phone: " + phone + "\n"
                    + "🔒 Max Request Counter: " + MAX_REQUEST_COUNTER;
        }

    }

    @Data
    public class liveServerData {

        private int connectedDeliveries;
        private int currentOrders;

        public liveServerData() {
            this.connectedDeliveries = OrdersControl.getConnectedDeliveries().size();
            this.currentOrders = OrdersControl.currentOrders.size();
        }


        public String toStringForTelegram() {
            return "👥 *Live Server Data*\n"
                    + "🚚 Conected Deliveries: " + connectedDeliveries + "\n"
                    + "#⃣  Current Orders: " + currentOrders + "\n";

        }

    }

}
