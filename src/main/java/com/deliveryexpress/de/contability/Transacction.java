/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.contability;

import com.deliveryexpress.utils.Utils;
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
public class Transacction extends BaseDao {
    
    @DatabaseField(id = true)
    String id;
    @DatabaseField
    String from, to;
    @DatabaseField
    float mount;
    @DatabaseField
    String concept,reference;
    @DatabaseField
    String date;

    public Transacction() {
        this.date = Utils.DateUtils.getNowDate();
    }

    public Transacction(String from, String to, float mount, String concept, String reference) {
        this.from = from;
        this.to = to;
        this.mount = mount;
        this.concept = concept;
        this.reference = reference;
        this.date = Utils.DateUtils.getNowDate();
    }

 

 
}
