/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.objects.users.AccountStatus;
import com.deliveryexpress.objects.users.Moderator;
import com.deliveryexpress.objects.users.Tuser;
import com.monge.tbotboot.commands.Command;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;

/**
 *
 * @author DeliveryExpress
 */
class GroupModeratorCommands {

    static void execute(Xupdate xupdate) {
        try {
            Tuser user = DataBase.Accounts.getTelegramUser(xupdate);
            Moderator moderator = user.getModerator();
            Tuser group = DataBase.Accounts.getTelegramGroup(xupdate);

            if (moderator.getAccountStatus().equals(AccountStatus.PAUSED)) {
                Response.sendMessage(group.getReceptor(), "Tu cuenta esta en proceso de aprovacion."
                        + "\n contacte a al Bot para mas informacion.", null);
                return;

            }

            if (moderator.getAccountStatus().equals(AccountStatus.INACTIVE)) {
                Response.sendMessage(group.getReceptor(), "Tu cuenta esta Inactiva!", null);
                return;

            }

            Command command = xupdate.getCommand();
            switch (command.command()) {

                case "/info":
                    Response.sendMessage(group.getReceptor(), group.toStringForTelegram(), MessageMenu.okAndDeleteMessage());

                    break;

                    
                case "/delete_msg":
                    Response.deleteGroupMessage(xupdate);

                    break;    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
