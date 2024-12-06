/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.OrdersControl;
import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.de.orders.Order;
import com.deliveryexpress.objects.users.AccountStatus;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.objects.users.Moderator;
import com.deliveryexpress.objects.users.Tuser;
import com.google.gson.GsonBuilder;
import com.monge.tbotboot.commands.Command;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;
import java.util.ArrayList;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

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

            if (xupdate.hasReply()) {
                ModGroupReplyActions.execute(xupdate);
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

                case "/take":

                    String orderId = command.getParam(1);
                    Order o = OrdersControl.getOrder(orderId);
                    if (o != null) {
                        Response.editMessage(xupdate.getTelegramGroup(), xupdate.getMessageId(), o.toTelegramStringAfterTaken(),
                                null);
                    } else {
                        Response.sendMessage(xupdate.getTelegramUser(), "la orden " + orderId + " ya no esta disponible.",
                                null);
                    }

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static class ModGroupReplyActions {

        private static void execute(Xupdate xupdate) {

            /*comando y parametros*/
            String repliedText = xupdate.getRepliedMessage().getText();
            ArrayList<User> replyMencionedUsers = xupdate.getReplyMencionedUsers(xupdate.getMessage());

            Command command = new Command(xupdate.getText(), " ");
            System.out.println("Command:" + command.command());

            switch (command.command()) {

                case "/set":
                case "/assign":
                    try {
                        Order order = OrdersControl.extractAndGetOrderFromText(repliedText);
                        System.out.println("extractAndGetOrderFromText: " + order.getId());
                        String id = String.valueOf(replyMencionedUsers.get(0).getId());
                        System.out.println("getReplyMencionedUserId: " + id);
                        Tuser read = Tuser.read(Tuser.class, id);
                        DeliveryMan deliveryMan = read.getDeliveryMan();

                        if (order != null && deliveryMan != null) {
                            order.setDeliveryMan(deliveryMan);
                            OrdersControl.onNewOrderAsignedEvent(order);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

            }

        }

    }

}
