/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects.users;

import com.monge.tbotboot.messenger.Xupdate;
import com.j256.ormlite.field.DatabaseField;
import com.monge.tbotboot.objects.Receptor;
import com.monge.xsqlite.xsqlite.BaseDao;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Tuser extends BaseDao {

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

    public Tuser() {
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
    public Tuser(String id, String lastNodeBot, boolean blackList, String accountType, String accountId) {
        this.id = id;
        this.lastNodeBot = lastNodeBot;
        this.blackList = blackList;
        this.accountType = accountType;
        this.accountId = accountId;
    }

    public Tuser(Xupdate aThis) {
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
        DeliveryMan read = DeliveryMan.read(DeliveryMan.class, accountId);
        read.setTelegramId(id);
        return read;
    }

    public Bussines getBussines() {
        Bussines read = Bussines.read(Bussines.class, this.accountId);
        read.setTelegramId(id);
        return read;
    }

    public Moderator getModerator() {
        Moderator read = Moderator.read(Moderator.class, this.accountId);
        read.setTelegramId(id);
        return read;
    }

    public Receptor getReceptor() {
        return new Receptor(this.id, this.lastNodeBot);
    }

    public String toStringForTelegram() {
        return "🆔 ID: " + id + "\n"
                + "🤖 Last Node Bot: " + lastNodeBot + "\n"
                + "⛔ Blacklist: " + (blackList ? "Yes" : "No") + "\n"
                + "📂 Account Type: " + accountType + "\n"
                + "💳 Account ID: " + accountId;
    }

}
