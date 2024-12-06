/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.orders;

import com.deliveryexpress.de.Global;
import com.deliveryexpress.de.OrdersControl;
import com.deliveryexpress.objects.GroupArea;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.Customer;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.utils.DateUtils;
import com.deliveryexpress.utils.Utils;
import com.monge.tbotboot.messenger.builders.HtmlStringBuilder;
import java.util.ArrayList;
import java.util.UUID;
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

    /*variables para asiginacion automatica*/
    boolean waitingDeliveryConfirmation;
    boolean confirmTake;
    ArrayList<String> rejectedList = new ArrayList<>();

    boolean sentToExternal;

    String bussinesUserId;

    ArrayList<String> logs = new ArrayList<>();

    public Order(Bussines bussines, boolean b) {
        this.id = UUID.randomUUID().toString();
        this.busssines = bussines;
        this.creationDate = DateUtils.getNowDate();
        this.status = OrderStatus.PREPARACION;

    }

    public Float getTotal() {
        return this.getOrderCost() + this.getDeliveryCost();
    }

    public String getShortId() {
        return this.id.substring(this.getId().length() - 5, this.getId().length());

    }

    public GroupArea getArea() {
        return this.getBusssines().getGrouArea();
    }

    public StorableOrder getStorableOrder() {
        return new StorableOrder(this);
    }

    public boolean deliveryIsNearFromBussines() {

        if (!Global.getInstance().getOrdersConfig()  .isCheckDeliveryPositionOnChangeStatus()) {
            return true;
        }

        try {
            int calcDistanceInKmTo = this.deliveryMan.getPosition().calcDistanceInKmTo(this.busssines.getPosition());
            return calcDistanceInKmTo < 1;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }

    public boolean deliveryIsNearFromCustomer() {
        if (!Global.getInstance().getOrdersConfig() .isCheckDeliveryPositionOnChangeStatus()) {
            return true;
        }

        try {
            int calcDistanceInKmTo = this.deliveryMan.getPosition().calcDistanceInKmTo(this.customer.getPosition());
            return calcDistanceInKmTo < 1;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }

    public String toTelegramString() {
        StringBuilder sb = new StringBuilder();
        sb.append("🆔 Id: " + this.getShortId()).append("\n");
        sb.append("📅 Fecha: " + this.creationDate).append("\n");
        sb.append("📌 Estado: " + this.status).append("\n");
        sb.append("⏱️ Preparación en: " + this.preparationTimeMinutes + " min.").append("\n")
                .append("\n");

        sb.append("🏢 Negocio: " + this.busssines.getName()).append("\n");
        sb.append("📞 Teléfono: " + this.busssines.getPhone()).append("\n");
        sb.append("📍 Dirección: " + this.busssines.getAddress()).append("\n")
                .append("\n");

        sb.append("👤 Cliente: " + this.customer.getName()).append("\n");
        sb.append("📞 Teléfono: " + this.customer.getPhone()).append("\n");
        sb.append("📍 Dirección: " + this.customer.getLastAddress()).append("\n");
        sb.append("📝 Nota: " + this.customer.getLastNote()).append("\n")
                .append("\n");

        if (this.deliveryMan != null) {
            sb.append("🚚 Repartidor: " + this.deliveryMan.getName()).append("\n");
            sb.append("📞 Teléfono: " + this.deliveryMan.getPhone()).append("\n")
                    .append("\n");
        }

        sb.append("💵 Costo de orden: " + this.orderCost).append("\n");
        sb.append("🚚 Costo de envío: " + this.deliveryCost).append("\n");
        sb.append("💰 Total a cobrar: " + this.getTotal()).append("\n");

        return sb.toString();
    }

    public String toTelegramStringForMod() {

        StringBuilder sb = new StringBuilder();
        sb.append("🆔 Id: " + this.getId()).append("\n");
        sb.append("📅 Fecha: " + this.creationDate).append("\n");
        sb.append("📌 Estado: " + this.status).append("\n");
        sb.append("⏱️ Preparación en: " + this.preparationTimeMinutes + " min.").append("\n")
                .append("\n");

        sb.append("🏢 Negocio: " + this.busssines.getName()).append("\n");
        sb.append("📞 Teléfono: " + this.busssines.getPhone()).append("\n");
        sb.append("📍 Dirección: " + this.busssines.getAddress()).append("\n")
                .append("\n");

        sb.append("👤 Cliente: " + this.customer.getName()).append("\n");
        sb.append("📞 Teléfono: " + this.customer.getPhone()).append("\n");
        sb.append("📍 Dirección: " + this.customer.getLastAddress()).append("\n");
        sb.append("📝 Nota: " + this.customer.getLastNote()).append("\n")
                .append("\n");

        if (this.deliveryMan != null) {
            sb.append("🚚 Repartidor: " + this.deliveryMan.getName()).append("\n");
            sb.append("📞 Teléfono: " + this.deliveryMan.getPhone()).append("\n")
                    .append("\n");

        }

        sb.append("💵 Costo de orden: " + this.orderCost).append("\n");
        sb.append("🚚 Costo de envío: " + this.deliveryCost).append("\n");
        sb.append("💰 Total a cobrar: " + this.getTotal()).append("\n")
                .append("\n");

        sb.append("Log\n" + String.join("\n", this.logs));

        return sb.toString();

    }

    public String toTelegramStringAfterTaken() {

        StringBuilder sb = new StringBuilder();
        sb.append("🆔 " + this.getShortId()).append("\n");
        sb.append("📅 " + this.creationDate).append("\n");
        sb.append("📌 " + this.status).append("\n")
                .append("\n");

        sb.append("🏢 " + this.busssines.getName()).append("\n")
                .append("\n");

        if (this.deliveryMan != null) {
            sb.append("🚚 " + this.deliveryMan.getName()).append("\n")
                    .append("\n");
        }

        sb.append("💵: " + this.orderCost).append("\n");
        sb.append("🚚: " + this.deliveryCost).append("\n");

        return sb.toString();

    }

    public String toTelegramStringBeforeTake() {
    
                StringBuilder sb = new StringBuilder();
        sb.append("🆔 Id: " + this.getShortId()).append("\n");
        sb.append("📅 Fecha: " + this.creationDate).append("\n");
        sb.append("📌 Estado: " + this.status).append("\n");
        sb.append("⏱️ Preparación en: " + this.preparationTimeMinutes + " min.").append("\n")
                .append("\n");

        sb.append("🏢 Negocio: " + this.busssines.getName()).append("\n");
        sb.append("📍 Dirección: " + this.busssines.getAddress()).append("\n")
                .append("\n");

        sb.append("👤 Cliente: " + this.customer.getName()).append("\n");
        sb.append("📍 Dirección: " + this.customer.getLastAddress()).append("\n")
                .append("\n");

        if (this.deliveryMan != null) {
            sb.append("🚚 Repartidor: " + this.deliveryMan.getName()).append("\n")
                    .append("\n");
        }

        sb.append("💵 Costo de orden: " + this.orderCost).append("\n");
        sb.append("🚚 Costo de envío: " + this.deliveryCost).append("\n");
        sb.append("💰 Total a cobrar: " + this.getTotal()).append("\n");

        return sb.toString();
    
    }
    
        public String toTelegramStringForModHtml() {

        HtmlStringBuilder sb = new HtmlStringBuilder();
        sb.appendBold("🆔 Id: " + this.getId()).appendNewline();
        sb.append("📅 Fecha: " + this.creationDate).appendNewline();
        sb.append("📌 Estado: " + this.status).appendNewline();
        sb.append("⏱️ Preparación en: " + this.preparationTimeMinutes + " min.").appendNewline()
                .append("\n");

        sb.appendBold("🏢 Negocio: ").appendUserMention(this.busssines.getName(), this.busssines.getTelegramId()).appendNewline();
        sb.append("📞 Teléfono: " + this.busssines.getPhone()).appendNewline();
        sb.append("📍 Dirección: " + this.busssines.getAddress()).appendNewline()
                .appendNewline();

        sb.appendBold("👤 Cliente: " + this.customer.getName()).appendNewline();
        sb.append("📞 Teléfono: " + this.customer.getPhone()).appendNewline();
        sb.append("📍 Dirección: " + this.customer.getLastAddress()).appendNewline();
        sb.appendUnderline("📝 Nota: " + this.customer.getLastNote()).appendNewline()
                .append("\n");

        if (this.deliveryMan != null) {
            sb.appendBold("🚚 Repartidor: ").appendUserMention( this.deliveryMan.getName(),this.deliveryMan.getTelegramId()).appendNewline();
            sb.append("📞 Teléfono: " + this.deliveryMan.getPhone()).appendNewline()
                    .appendNewline();

        }

        sb.append("💵 Costo de orden: " + this.orderCost).appendNewline();
        sb.append("🚚 Costo de envío: " + this.deliveryCost).appendNewline();
        sb.appendBold("💰 Total a cobrar: " + this.getTotal()).appendNewline()
               .appendNewline();

        sb.appendNewline().append("Log").appendNewline().append(String.join("\n", this.logs));

        return sb.build();

    }

}
