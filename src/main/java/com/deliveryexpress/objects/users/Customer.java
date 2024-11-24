/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects.users;

import com.deliveryexpress.objects.Position;
import com.j256.ormlite.field.DatabaseField;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */

@Data
 public  class Customer {

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
