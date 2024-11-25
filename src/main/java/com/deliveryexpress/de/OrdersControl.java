/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de;

import com.deliveryexpress.de.orders.Order;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DeliveryExpress
 */
public class OrdersControl {
    
    static List<Order> currentOrders = new ArrayList<>();
    
    public static void addNewOrder(Order order) {
        currentOrders.add(order);
    }
    
    public static void onNewOrderReceivedEvent(Order order) {
        
    }
    
}
