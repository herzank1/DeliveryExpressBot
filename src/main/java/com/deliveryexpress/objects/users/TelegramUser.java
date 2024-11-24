/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects.users;

import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.de.database.DbBalancer;
import com.deliveryexpress.telegram.Xupdate;
import com.j256.ormlite.field.DatabaseField;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class TelegramUser {

    @DatabaseField(id = true)
    String id;
    @DatabaseField
    String lastNodeBot;
    @DatabaseField
    boolean blackList;
    @DatabaseField
    String accountType;
    @DatabaseField
    String accountId;

    public TelegramUser() {
    }

    /**
     * *
     *
     * @param id
     * @param lastNodeBot
     * @param blackList
     * @param accountType
     * @param accountId
     */
    public TelegramUser(String id, String lastNodeBot, boolean blackList, String accountType, String accountId) {
        this.id = id;
        this.lastNodeBot = lastNodeBot;
        this.blackList = blackList;
        this.accountType = accountType;
        this.accountId = accountId;
    }

    public TelegramUser(Xupdate aThis) {
        if (aThis.isGroupMessage()) {
            this.id = aThis.getFromId();
            this.accountType = AccountType.IS_GROUP;
        } else {
            this.id = aThis.getSenderId();
            this.accountType = AccountType.NOT_REGISTRED;
        }
        this.lastNodeBot = aThis.getBotUserName();
        this.blackList = false;
        this.accountId = null;
    }

    public DeliveryMan getDeliveryMan() {
        return DataBase.Accounts.Deliveries.Deliveries().read(accountId);
    }

    public Bussines getBussines() {
        return DataBase.Accounts.Bussiness.Bussiness().read(accountId);
    }

    public Moderator getModerator() {
        return DataBase.Accounts.Moderators.Moderators().read(accountId);
    }

}
