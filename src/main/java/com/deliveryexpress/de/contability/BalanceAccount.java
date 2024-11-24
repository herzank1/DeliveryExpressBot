/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.contability;

import com.j256.ormlite.field.DatabaseField;
import java.util.UUID;
import lombok.Data;

@Data
public class BalanceAccount {

     @DatabaseField(id = true)
    String accountNumber;
      @DatabaseField
    float balance;

    public BalanceAccount() {
        this.accountNumber=UUID.randomUUID().toString();
        this.balance=0f;
    }

    public BalanceAccount(String accountNumber, float balance) {
        this.accountNumber = UUID.randomUUID().toString();
        this.balance = balance;
    }
    
    

}
   
