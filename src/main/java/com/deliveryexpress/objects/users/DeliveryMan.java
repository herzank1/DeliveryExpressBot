/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects.users;

import com.deliveryexpress.objects.Position;
import com.deliveryexpress.utils.Utils;
import com.j256.ormlite.field.DatabaseField;
import java.util.UUID;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class DeliveryMan {

    private static final long LAST_UPDATE_TOLERANCE = 60;
    
    String telegramId;

    @DatabaseField(id = true)
    String accountId;
     @DatabaseField
    String name;
      @DatabaseField
    String phone;
       @DatabaseField
    String deliveryManLevel;
        @DatabaseField
    String balanceAccountNumber;
         @DatabaseField
    String accountStatus;
          @DatabaseField
    String tags;

    /*non storables values*/
    boolean connected;
    String location;
    long lastLocationUpdate;

    public DeliveryMan() {
        this.accountId = UUID.randomUUID().toString();
        this.deliveryManLevel = DeliveryManLevel.TUERCA;
    }

    public DeliveryMan(String name, String phone) {
        this.accountId = UUID.randomUUID().toString();
        this.name = name;
        this.phone = phone;
        this.deliveryManLevel = DeliveryManLevel.TUERCA;
    }
    
    

    public boolean isSharingLocation() {

        long unixTimeStamp = Utils.DateUtils.getUnixTimeStamp();

        long diff = unixTimeStamp - this.getLastLocationUpdate();

        try {
            return this.getLocation() != null && (diff < LAST_UPDATE_TOLERANCE);

        } catch (java.lang.NullPointerException e) {
            return false;
        }

    }
    
    public Position getPosition() {
        return new Position(this.location);
    }

}
