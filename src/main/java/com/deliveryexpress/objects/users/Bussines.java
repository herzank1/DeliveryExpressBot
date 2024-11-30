/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects.users;

import com.deliveryexpress.objects.GroupArea;
import com.j256.ormlite.field.DatabaseField;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.objects.Position;
import com.monge.xsqlite.xsqlite.BaseDao;
import static com.monge.xsqlite.xsqlite.BaseDao.read;
import java.util.UUID;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class Bussines extends BaseDao {
    
    String telegramId;

    @DatabaseField(id = true)
    String accountId;
    @DatabaseField
    String name;
    @DatabaseField
    String phone;
    @DatabaseField
    String address;
    @DatabaseField
    String location;
    @DatabaseField
    String balanceAccountNumber;
    @DatabaseField
    String accountStatus;
    @DatabaseField
    String areaId;

    String serviceType;

    float serviceCost;
    float kmBaseCost;
    float kmExtraCost;
    int kmBase; //Banderazo

    String ownerName;
    String schedule;
    String tags;

    public Bussines() {
        this.accountId = UUID.randomUUID().toString();
    }

    public Bussines(String name, String phone, String address, String areaId) {
        this.accountId = UUID.randomUUID().toString();
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.areaId = areaId;
    }

    public Position getPosition() {
        return new Position(this.location);
    }

    public MessageMenu getDeliveryTable() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public GroupArea getGrouArea() {
        GroupArea read = GroupArea.read(this.getClass(), this.areaId);
        return read;

    }

}
