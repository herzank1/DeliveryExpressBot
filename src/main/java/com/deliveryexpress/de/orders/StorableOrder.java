/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.orders;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.monge.xsqlite.xsqlite.BaseDao;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StorableOrder extends BaseDao {

    @DatabaseField(id = true)
    String id;
    @DatabaseField
    String status;
    @DatabaseField
    String creationDate;
    @DatabaseField
    String bussines;
    @DatabaseField
    String customer;
    @DatabaseField
    String delivery;
    @DatabaseField
    float ordersCost;
    @DatabaseField
    float deliveryCost;
    @DatabaseField
    String logs;

    public StorableOrder() {
    }

    StorableOrder(Order order) {
        this.id = order.getId();
        this.status = order.getStatus();
        this.creationDate = order.getCreationDate();
        this.bussines = order.getBusssines().getAccountId();
        this.customer = order.getCustomer().getPhone();
        if (order.getDeliveryMan() != null) {
            this.delivery = order.getDeliveryMan().getAccountId();
        }
        this.ordersCost = order.getOrderCost();
        this.deliveryCost = order.getDeliveryCost();
        this.logs = new Gson().toJson(order.getLogs());

    }

}
