/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects.users;

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
    String ownerName;
    @DatabaseField
    String schedule;
    @DatabaseField
    String tags;

    public BussinesContract() {
        this.id = UUID.randomUUID().toString();
        this.serviceType = ServiceType.POR_ORDEN;
        this.serviceCost = 22;
        this.kmBaseCost = 45;
        this.kmExtraCost = 9;
        this.kmBase = 5;
        this.payCuota = true;
        this.ownerName = null;
        this.schedule = null;
        this.tags = null;
    }

    interface ServiceType {

        String POR_ORDEN = "POR_ORDEN";

    }

}
