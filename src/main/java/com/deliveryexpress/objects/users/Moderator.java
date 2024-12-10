/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects.users;

import com.deliveryexpress.de.contability.BalanceAccount;
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
public class Moderator extends BaseDao {

    String telegramId;

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
        this.accountId = UUID.randomUUID().toString();
        this.name = name;
        this.phone = phone;
    }
    
      public BalanceAccount getBalanceAccount() {
        BalanceAccount read = BalanceAccount.read(BalanceAccount.class, this.balanceAccountNumber);
        if (read == null) {
            read = new BalanceAccount();
            read.create();
            this.setBalanceAccountNumber(read.getAccountNumber());
            this.update();
        }

        return read;
    }


    public String toStringForTelegram() {
        return "🆔 Telegram ID: " + telegramId + "\n"
                + "💳 Account ID: " + accountId + "\n"
                + "👤 Name: " + name + "\n"
                + "📞 Phone: " + phone + "\n"
                + "🏦 Balance Account Number: " + balanceAccountNumber + "\n"
                + "🔓 Account Status: " + accountStatus + "\n"
                + "🌍 Area: " + area + "\n"
                + "🏷️ Tags: " + tags;
    }

}
