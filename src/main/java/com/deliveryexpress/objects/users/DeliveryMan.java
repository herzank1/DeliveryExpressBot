/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects.users;

import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.utils.DateUtils;
import com.deliveryexpress.utils.Utils;
import com.j256.ormlite.field.DatabaseField;
import com.monge.tbotboot.objects.Position;
import com.monge.tbotboot.objects.Receptor;
import com.monge.tbotboot.objects.TelegramUser;
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
public class DeliveryMan extends BaseDao {

    private static final long LAST_UPDATE_TOLERANCE = 30;

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
    String conecctionStaus;
    String location;
    long lastLocationUpdate;

    public DeliveryMan() {
        this.accountId = UUID.randomUUID().toString();
        this.deliveryManLevel = DeliveryManLevel.TUERCA;
        this.conecctionStaus = ConnectionStatus.DISCONECTED;
    }

    public DeliveryMan(String name, String phone) {
        this.accountId = UUID.randomUUID().toString();
        this.name = name;
        this.phone = phone;
        this.deliveryManLevel = DeliveryManLevel.TUERCA;
        this.conecctionStaus = ConnectionStatus.DISCONECTED;
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

    public boolean isSharingLocation() {

        long unixTimeStamp = DateUtils.getUnixTimeStamp();

        long diff = unixTimeStamp - this.getLastLocationUpdate();

        try {
            return this.getLocation() != null && (diff < LAST_UPDATE_TOLERANCE);

        } catch (java.lang.NullPointerException e) {
            return false;
        }

    }

    public boolean isConnected() {
        return this.conecctionStaus.equals(ConnectionStatus.CONECTED)
                && this.isSharingLocation();
    }

    public void switchConnection() {
        if (this.conecctionStaus.equals(ConnectionStatus.DISCONECTED)) {
            this.conecctionStaus = ConnectionStatus.CONECTED;
        } else {
            this.conecctionStaus = ConnectionStatus.DISCONECTED;
        }

    }

    public Receptor getReceptor() {
        Tuser tuser = BaseDao.read(Tuser.class, this.telegramId);
        return tuser.getReceptor();
    }

    public Position getPosition() {
        return new Position(this.location);
    }

    public String toStringForTelegram() {
        return "🆔 Telegram ID: " + telegramId + "\n"
                + "💳 Account ID: " + accountId + "\n"
                + "👤 Name: " + name + "\n"
                + "📞 Phone: " + phone + "\n"
                + "⭐ Delivery Man Level: " + deliveryManLevel + "\n"
                + "🏦 Balance Account Number: " + balanceAccountNumber + "\n"
                + "🔓 Account Status: " + accountStatus + "\n"
                + "🏷️ Tags: " + tags;
    }

    interface ConnectionStatus {

        String CONECTED = "CONECTED";
        String DISCONECTED = "DISCONECTED";
    }

}
