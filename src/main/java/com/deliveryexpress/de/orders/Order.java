/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.orders;

import com.deliveryexpress.objects.GroupArea;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.Customer;
import com.deliveryexpress.objects.users.DeliveryMan;
import java.util.ArrayList;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class Order {

    String id;
    String status;
    int preparationTimeMinutes;
    String creationDate;

    Bussines busssines;
    Customer customer;

    float orderCost;
    float deliveryCost;

    DeliveryMan deliveryMan;

    boolean deliveryManArrivedToBussines;
    boolean sentToExternal;

    String bussinesUserId;

    ArrayList<String> logs = new ArrayList<>();

    public Float getTotal() {
        return this.getOrderCost() + this.getDeliveryCost();
    }

    public String getShortId() {
        return this.id.substring(this.getId().length() - 5, this.getId().length());

    }

    public GroupArea getArea() {
        return this.getBusssines().getGrouArea();
    }

}
