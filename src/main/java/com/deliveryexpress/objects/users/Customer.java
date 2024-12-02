/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects.users;

import com.j256.ormlite.field.DatabaseField;
import com.monge.tbotboot.objects.Position;
import com.monge.xsqlite.xsqlite.BaseDao;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Customer extends BaseDao {

    @DatabaseField(id = true)
    String phone;
    @DatabaseField
    String name;
    @DatabaseField
    String lastAddress;
    @DatabaseField
    String lastLoction;
    @DatabaseField
    String lastNote;

    public Customer() {
    }

    /***
     * 
     * @param phone
     * @param name
     * @param lastAddress
     * @param lastNote 
     */
    public Customer(String phone, String name, String lastAddress, String lastNote) {
        this.phone = phone;
        this.name = name;
        this.lastAddress = lastAddress;
        this.lastNote = lastNote;
    }

    public Position getPosition() {
        return new Position(this.lastLoction);
    }

}
