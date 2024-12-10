/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui.wallets;

import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.objects.GroupArea;
import com.deliveryexpress.objects.users.AccountType;
import com.deliveryexpress.objects.users.Tuser;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class Wallet {

    String ownerName;
    Tuser user;
    BalanceAccount balance;

    public Wallet(Tuser user) {

        this.user = user;

        switch (user.getAccountType()) {

            case AccountType.BUSSINES:
                this.ownerName = user.getBussines().getName();
                this.balance = user.getBussines().getBalanceAccount();
                break;

            case AccountType.MODERATOR:
                this.ownerName = user.getModerator().getName();
                this.balance = user.getModerator().getBalanceAccount();
                break;

            case AccountType.DELIVERYMAN:

                this.ownerName = user.getDeliveryMan().getName();
                this.balance = user.getDeliveryMan().getBalanceAccount();

                break;

            case AccountType.NOT_REGISTRED:
            case AccountType.IS_GROUP:
                this.ownerName = "Null";
                this.balance = null;
                break;

        }

    }

    public String toStringForTelegram() {
        return "👤 Name: " + ownerName + "\n"
                + "🆔 Numero de cuenta: " + balance.getAccountNumber() + "\n"
                + "💰 Balance : " + balance.getBalance() + "\n";

    }

}
