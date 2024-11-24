/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.utils;

import com.deliveryexpress.de.orders.Order;
import com.deliveryexpress.de.orders.OrderStatus;

/**
 *
 * @author DeliveryExpress
 */
public class OrdersUtils {
    
    /**
     * 
     * @param o
     * @return la representacion de un estado de la orden en emojis string
     */
    
       public static String getCurrentStatusSymbol(Order o){
            
            String status="";
            
            switch (o.getStatus()) {

                case OrderStatus.PREPARACION:

                    status = "⏰🍔";
                    if (o.getDeliveryMan()!=null) {
                        status += "🛵";
                    }
                    break;

                case OrderStatus.LISTO:
                    status = "✅🍔";

                    if (o.getDeliveryMan()!=null) {
                        status += "🛵";
                    }
                    break;

                case OrderStatus.EN_CAMINO:
                    status = "🛵➡🏠️";
                    break;
                case OrderStatus.EN_DOMICILIO:

                    status = "🛵⏰🏠️";
                    break;

                case OrderStatus.ENTREGADO:
                    status = "🏠✅️";
                    break;
                case OrderStatus.CANCELADO:

                    status = "🚫️";
                    break;

            }

            return "";

        }

    
}
