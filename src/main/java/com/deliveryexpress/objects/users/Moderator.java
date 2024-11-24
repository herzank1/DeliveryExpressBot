/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects.users;

import com.j256.ormlite.field.DatabaseField;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */

@Data
public class Moderator {

    @DatabaseField(id = true)
    String accountId;
     @DatabaseField
    String name;
      @DatabaseField
    String phone;
       @DatabaseField
    String balanceAccountNumber;
        @DatabaseField
    String accountStatus;
         @DatabaseField
    String area;
          @DatabaseField
    String tags;

    public Moderator() {
    }

    public Moderator(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
    
    

}
