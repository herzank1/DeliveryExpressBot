/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;

/**
 *
 * @author DeliveryExpress
 */
public class OrderRequest {

    private String userid;
    private String name;
    private String phone;
    private String address;
    private String note;
    private float orderCost;
    private float deliveryCost;

    // Constructor vacío
    public OrderRequest() {
    }

    // Constructor para inicializar todos los campos
    public OrderRequest(String userid, String name, String phone, String address, String note, float orderCost, float deliveryCost) {
        this.userid = userid;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.note = note;
        this.orderCost = orderCost;
        this.deliveryCost = deliveryCost;
    }

    // Getters
    public String getBussinesid() {
        return userid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getNote() {
        return note;
    }

    public float getOrderCost() {
        return orderCost;
    }

    public float getDeliveryCost() {
        return deliveryCost;
    }

    // Setters
    public void setBussinesid(String bussinesid) {
        this.userid = bussinesid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setOrderCost(float orderCost) {
        this.orderCost = orderCost;
    }

    public void setDeliveryCost(float deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "bussinesid='" + userid + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", note='" + note + '\'' +
                ", orderCost=" + orderCost +
                ", deliveryCost=" + deliveryCost +
                '}';
    }
}

