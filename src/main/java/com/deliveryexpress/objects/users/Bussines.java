/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects.users;

import com.deliveryexpress.objects.GroupArea;
import com.j256.ormlite.field.DatabaseField;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.objects.Position;
import com.monge.tbotboot.objects.Receptor;
import com.monge.xsqlite.xsqlite.BaseDao;
import static com.monge.xsqlite.xsqlite.BaseDao.read;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 */
@Data
@EqualsAndHashCode(callSuper = false)
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
    @DatabaseField
    String contractId;

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

        MessageMenu menu = new MessageMenu();

        menu.addButton("0 - 5 km = 45", String.valueOf(45), true);
        menu.addButton("6 km = " + String.valueOf(45 + 9), String.valueOf(45 + 9), true);
        menu.addButton("7 km = " + String.valueOf(45 + 9 + 9), String.valueOf(45 + 9 + 9), true);
        menu.addButton("8 km = " + String.valueOf(45 + 9 + 9 + 9), String.valueOf(45 + 9 + 9 + 9), true);

        return menu;

    }

    public GroupArea getGrouArea() {
        GroupArea read = GroupArea.read(this.getClass(), this.areaId);
        return read;

    }

    public Receptor getReceptor() {
        Tuser tuser = BaseDao.read(Tuser.class, this.telegramId);
        return tuser.getReceptor();
    }

    /*return safe Contract*/
    public BussinesContract getContract() {
        BussinesContract read = BussinesContract.read(BussinesContract.class, this.contractId);
        if (read == null) {
            read = new BussinesContract();
            this.setContractId(read.getId());
            this.update();
            read.create();
        }

        return read;

    }

}
