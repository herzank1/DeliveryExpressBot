/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.contability;

import com.j256.ormlite.field.DatabaseField;
import com.monge.xsqlite.xsqlite.BaseDao;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BalanceAccount extends BaseDao {

    @DatabaseField(id = true)
    String accountNumber;
    @DatabaseField
    float balance;

    public BalanceAccount() {
        this.accountNumber = UUID.randomUUID().toString();
        this.balance = 0f;
    }

    public BalanceAccount(String accountNumber, float balance) {
        this.accountNumber = UUID.randomUUID().toString();
        this.balance = balance;
    }

    public String toTelegramString() {
        StringBuilder sb = new StringBuilder();
        sb.append("🆔 " + this.accountNumber).append("\n");
        sb.append("💵 Saldo: " + this.balance).append("\n")
                .append("\n");

        return sb.toString();
    }

    public String getReference() {
    return this.accountNumber.toUpperCase().replace("-", "").substring(0,7);
    }

}
