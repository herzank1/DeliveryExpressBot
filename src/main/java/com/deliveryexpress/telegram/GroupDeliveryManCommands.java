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
import com.deliveryexpress.utils.DateUtils;
import com.monge.tbotboot.commands.Command;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;

/**
 *
 * @author DeliveryExpress
 */
class GroupDeliveryManCommands {

    static void execute(Xupdate xupdate) {

        try {
            Tuser user = DataBase.Accounts.getTelegramUser(xupdate);
            DeliveryMan delivery = user.getDeliveryMan();
            Tuser group = DataBase.Accounts.getTelegramGroup(xupdate);

            if (delivery.getAccountStatus().equals(AccountStatus.PAUSED)) {
                Response.sendMessage(group.getReceptor(), "Tu cuenta esta en proceso de aprovacion."
                        + "\n contacte a al Bot para mas informacion.", null);
                return;

            }

            if (delivery.getAccountStatus().equals(AccountStatus.INACTIVE)) {
                Response.sendMessage(group.getReceptor(), "Tu cuenta esta Inactiva!", null);
                return;

            }

            if (xupdate.hasLocation()) {
                DeliveryMan get = OrdersControl.getDeliveries().get(delivery.getAccountId());
                get.setLocation(xupdate.getLocation().toString());
                get.setLastLocationUpdate(DateUtils.getUnixTimeStamp());
                return;
            }

            Command command = xupdate.getCommand();
            switch (command.command()) {

                case "/take":

                    String orderId = command.getParam(1);
                    Order o = OrdersControl.getOrder(orderId);
                    boolean success = OrdersControl.deliveryManTakeOrder(o, delivery);
                    if (success) {

                        Response.editMessage(group.getReceptor(), xupdate.getMessageId(), o.toTelegramStringAfterTaken(),
                                null);

                        Response.sendMessage(xupdate.getTelegramUser(), o.toTelegramString(),
                                DeliveryManCommands.getCurrentOrdersDeliveryMenu(o));
                    } else {

                        Response.sendMessage(group.getReceptor(), "Imposible tomar esta orden."
                                + " id: " + orderId, null);

                    }

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
