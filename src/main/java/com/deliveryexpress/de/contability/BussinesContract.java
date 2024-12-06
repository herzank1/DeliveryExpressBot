/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.contability;

import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.de.contability.ContabilityControl;
import com.deliveryexpress.de.contability.Transacction;
import com.deliveryexpress.de.orders.Order;
import com.j256.ormlite.field.DatabaseField;
import com.monge.xsqlite.xsqlite.BaseDao;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BussinesContract extends BaseDao {

    @DatabaseField(id = true)
    String id;
    @DatabaseField
    String serviceType;
    @DatabaseField
    float serviceCost;
    @DatabaseField
    float kmBaseCost;
    @DatabaseField
    float kmExtraCost;
    @DatabaseField
    int kmBase; //Banderazo
    @DatabaseField
    boolean payCuota;
    @DatabaseField
    float payCuotaMount;
    @DatabaseField
    String ownerName;
    @DatabaseField
    String schedule;
    @DatabaseField
    String balanceAccountId;
    @DatabaseField
    float maxDebLimit;
    @DatabaseField
    String tags;

    /***
     * Default constructor
     */
    public BussinesContract() {
        
        this.id = UUID.randomUUID().toString();
        this.serviceType = ServiceType.POR_ORDEN;
        this.serviceCost = 22;
        this.kmBaseCost = 45;
        this.kmExtraCost = 9;
        this.kmBase = 5;
      
        this.payCuota = false;
        this.payCuotaMount = 250;
        this.ownerName = "";
        this.schedule = "00:00 AM";
        this.balanceAccountId = null;
        this.maxDebLimit = 250;
        this.tags = "";
    }
    
 
    interface ServiceType {

        String POR_ORDEN = "POR_ORDEN";
        String MENSUAL = "MENSUAL";

    }

    /**
     * *
     *
     * @return null if success, else return a message
     */
    public boolean validate() {

        BalanceAccount balanceAccount = getBalanceAccount();

        float limit = -maxDebLimit;

        if (balanceAccount.getBalance() < limit) {
            return false;
        }

        return true;
    }

    /**
     * *
     * Executar cuando se entrege o se realize un servicio de entrega
     *
     * @param order
     */
    public void executeOnOrderDelivered(Order order) {

        switch (this.serviceType) {

            case ServiceType.POR_ORDEN:

                /*registramos trasnaccion*/
                Transacction t = new Transacction();
                t.setFrom(this.getBalanceAccountId());
                t.setTo(ContabilityControl.MAIN_BALANCE_ACCOUNT);
                t.setMount(serviceCost);
                t.setConcept("Entrega");
                t.setReference("Contract: " + this.id);
                t.create();
                /*actualizamos balance*/
                BalanceAccount balanceAccount = getBalanceAccount();
                balanceAccount.setBalance(balanceAccount.getBalance() - serviceCost);
                balanceAccount.update();

                break;

            case ServiceType.MENSUAL:

                /*registramos trasnaccion*/
                t = new Transacction();
                t.setFrom(this.getBalanceAccountId());
                t.setTo(ContabilityControl.MAIN_BALANCE_ACCOUNT);
                t.setMount(0);
                t.setConcept("Entrega");
                t.setReference("Contract: " + this.id);
                t.create();

                break;

        }

    }

    /**
     * *
     * Correr cada mes para cargar el servicio
     */
    public void chargeMothly() {
        if (!this.serviceType.equals(ServiceType.MENSUAL)) {
            return;
        }

        /*registramos trasnaccion*/
        Transacction t = new Transacction();
        t.setFrom(this.getBalanceAccountId());
        t.setTo(ContabilityControl.MAIN_BALANCE_ACCOUNT);
        t.setMount(this.serviceCost);
        t.setConcept("Cargo mensual de servicio");
        t.setReference("Contract: " + this.id);
        t.create();
        /*actualizamos balance*/
        BalanceAccount balanceAccount = getBalanceAccount();
        balanceAccount.setBalance(balanceAccount.getBalance() - this.serviceCost);
        balanceAccount.update();

    }

    /*Executar metodo cuando se corra el cobro de cuota*/
    public void chargeCuota() {

        if (!this.payCuota) {
            return;
        }
        /*registramos trasnaccion*/
        Transacction t = new Transacction();
        t.setFrom(this.getBalanceAccountId());
        t.setTo(ContabilityControl.MAIN_BALANCE_ACCOUNT);
        t.setMount(payCuotaMount);
        t.setConcept("Cuota de servicio");
        t.setReference("Contract: " + this.id);
        t.create();
        /*actualizamos balance*/
        BalanceAccount balanceAccount = getBalanceAccount();
        balanceAccount.setBalance(balanceAccount.getBalance() - payCuotaMount);
        balanceAccount.update();

    }

    public BalanceAccount getBalanceAccount() {
        return BalanceAccount.read(BalanceAccount.class, this.getBalanceAccountId());
    }
    
    public String toStringForTelegram() {
    return "🆔 ID del Servicio: " + id + "\n"
            + "🛠️ Tipo de Servicio:*" + serviceType + "\n"
            + "💰 Costo del Servicio: " + String.format("%.2f", serviceCost) + " MXN\n"
            + "📏 Costo Base por KM: " + String.format("%.2f", kmBaseCost) + " MXN\n"
            + "➕ Costo Extra por KM: " + String.format("%.2f", kmExtraCost) + " MXN\n"
            + "🚦 Kilómetros Base (Banderazo): " + kmBase + " km\n"
            + "💳 Paga Cuota: " + (payCuota ? "Sí" : "No") + "\n"
            + "💸 Monto de Cuota: " + String.format("%.2f", payCuotaMount) + " MXN\n"
            + "👤 Propietario: " + ownerName + "\n"
            + "📆 Horario: " + schedule + "\n"
            + "🏦 ID de Cuenta de Balance: " + balanceAccountId + "\n"
            + "📉 Límite Máximo de Deuda: " + String.format("%.2f", maxDebLimit) + " MXN\n"
            + "🏷️ Etiquetas: " + tags;
}
    

}
