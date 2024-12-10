/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.OrdersControl;
import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.de.orders.Order;
import com.deliveryexpress.de.orders.OrderStatus;
import com.deliveryexpress.objects.users.AccountStatus;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.objects.users.Tuser;
import com.deliveryexpress.utils.DateUtils;
import com.monge.tbotboot.commands.Command;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.objects.Position;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 *
 * @author DeliveryExpress
 */
public class DeliveryManCommands {

    static void execute(Xupdate xupdate) {

     
        Tuser user = Tuser.read(Tuser.class, xupdate.getSenderId());
        DeliveryMan deliveryMan = user.getDeliveryMan();

        if (deliveryMan.getAccountStatus().equals(AccountStatus.PAUSED)) {
            Response.sendMessage(xupdate.getTelegramUser(), "Tu cuenta esta en proceso de aprovacion."
                    + "\n contacte a un moderador para mas informacion.", null);
            return;

        }

        if (deliveryMan.getAccountStatus().equals(AccountStatus.INACTIVE)) {
            Response.sendMessage(xupdate.getTelegramUser(), "Tu cuenta esta Inactiva!", null);
            return;

        }

        if (xupdate.hasLocation()) {
            DeliveryMan get = OrdersControl.getDeliveries().get(deliveryMan.getAccountId());
            get.setLocation(xupdate.getLocation().toString());

            get.setLastLocationUpdate(DateUtils.getUnixTimeStamp());
            return;
        }

        Command command = xupdate.getCommand();
        switch (command.command()) {

            case "/menu":
            case "/start":
                BalanceAccount read = deliveryMan.getBalanceAccount();
                Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(),
                        "Repartidor " + deliveryMan.getName()
                        + "\nID:" + user.getId()
                        + "\nCartera:" + read.getBalance() + "$", getMenu(deliveryMan));

                break;

            case "/switchconnection":

                if (!deliveryMan.isConnected() && !deliveryMan.isSharingLocation()) {
                    Response.sendMessage(xupdate.getTelegramUser(), "Comparte tu ubicacion en vivo"
                            + " para conectarse!", MessageMenu.okAndDeleteMessage());

                } else {
                    deliveryMan.switchConnection();
                    read = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(deliveryMan.getBalanceAccountNumber());
                    Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(),
                            "Repartidor " + deliveryMan.getName()
                            + "\nID:" + user.getId()
                            + "\nCartera:" + read.getBalance() + "$", getMenu(deliveryMan));
                }

                break;

            case "/myorders":
                ArrayList<Order> orders = OrdersControl.getOrdersOf(deliveryMan, false);
                Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(), "Mis ordenes",
                        getOrderListAsMenu(orders));
                break;
            case "/mytodayhistory":
                orders = OrdersControl.getOrdersOf(deliveryMan, true);
                Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(), "Hoy",
                        getOrderListAsMenu(orders));
                break;

            case "/changestatus":
                String statusCode = command.getParam(1);
                String orderId = command.getParam(2);

                boolean changeOrderStatus = OrdersControl.changeOrderStatus(deliveryMan, statusCode, orderId);
                try {
                    if (changeOrderStatus) {
                        Order o = OrdersControl.getOrder(orderId);
                        Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(), o.toTelegramString(),
                                getCurrentOrdersDeliveryMenu(o));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Response.sendMessage(deliveryMan.getReceptor(), e.getLocalizedMessage(), MessageMenu.okAndDeleteMessage());

                }

                break;

            case "/vieworder":
                orderId = command.getParam(1);
                Order o = OrdersControl.getOrder(orderId);
                if (o == null) {
                    Response.sendMessage(xupdate.getTelegramUser(), "Esta orden ya no esta disponible."
                            + " id: " + orderId, null);
                }

                try {
                    Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(), o.toTelegramString(),
                            getCurrentOrdersDeliveryMenu(o));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case "/take":
                orderId = command.getParam(1);
                o = OrdersControl.getOrder(orderId);
                boolean success = OrdersControl.deliveryManTakeOrder(o, deliveryMan);
                if (success) {
                    Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(), o.toTelegramString(),
                            getCurrentOrdersDeliveryMenu(o));
                } else {

                    Response.sendMessage(xupdate.getTelegramUser(), "Imposible tomar esta orden."
                            + " id: " + orderId, null);

                }

                break;

            case "/reject":
                orderId = command.getParam(1);
                o = OrdersControl.getOrder(orderId);
                success = OrdersControl.deliveryManRejectOrder(o, deliveryMan);
                if (success) {
                    Response.deleteMessage(xupdate);
                } else {

                    Response.sendMessage(xupdate.getTelegramUser(), "Error desconocido!"
                            + " id: " + orderId, null);

                }

                break;

            case "/delete_msg":
                Response.deleteMessage(xupdate);

                break;

        }

    }

    public static MessageMenu getCurrentOrdersDeliveryMenu(Order o) {

        MessageMenu menu = new MessageMenu();

        switch (o.getStatus()) {

            case OrderStatus.PREPARACION:
            case OrderStatus.LISTO:

                if (!o.isDeliveryManArrivedToBussines()) {
                    menu.addUrlButton("Navegar 🍔", new Position(o.getBusssines()
                            .getLocation())
                            .getUrlNavigateTo());
                    menu.addButton("Llegue ✅", "/changestatus&"
                            + DeliveryOrderChangeStatusCode.ARRIVED_TO_BUSSINES + "&" + o.getId());
                } else {

                    menu.addButton("Recolectar ✅", "/changestatus&"
                            + DeliveryOrderChangeStatusCode.PICK_UP + "&" + o.getId());

                }

                break;

            case OrderStatus.EN_CAMINO:

                if (o.getCustomer().getPosition() != null) {
                    menu.addUrlButton("Navegar 👤", o.getCustomer()
                            .getPosition()
                            .getUrlNavigateTo());
                } else {
                    menu.addUrlButton("Navegar 👤", Position.navigateTo(o.getCustomer().getLastAddress()));

                }

                menu.addButton("Llegue ✅", "/changestatus&"
                        + DeliveryOrderChangeStatusCode.ARRIVED_TO_CUSTOMER + "&" + o.getId());

                break;
            case OrderStatus.EN_DOMICILIO:
                menu.addButton("Entregar ✅", "/changestatus&"
                        + DeliveryOrderChangeStatusCode.DELIVERED + "&" + o.getId());
                break;

        }

        menu.newLine();
        menu.addBackButton("/myorders");

        return menu;

    }

    public static interface DeliveryOrderChangeStatusCode {

        String ARRIVED_TO_BUSSINES = "d01";
        String PICK_UP = "d02";
        String ARRIVED_TO_CUSTOMER = "d03";
        String DELIVERED = "d04";

    }

    private static MessageMenu getOrderListAsMenu(ArrayList<Order> orders) {
        MessageMenu menu = new MessageMenu();
        for (Order o : orders) {

            menu.addButton("📦 " + o.getCustomer().getName(), "/vieworder&" + o.getId(), true);

        }
        menu.addBackButton("/menu");

        return menu;

    }

    private static MessageMenu getMenu(DeliveryMan delivery) {

        MessageMenu menu = new MessageMenu();
        if (delivery.isConnected()) {
            menu.addButton("🟢 Conectado", "/switchconnection", true);
        } else {
            menu.addButton("⚪ Desconectado", "/switchconnection", true);
        }

        menu.addButton("🗒 Mis ordenes", "/myorders", true);
        menu.addButton("🕰 Historial (Hoy)", "/mytodayhistory", true);
        // menu.addButton("💳 Mi cartera", "/mywallet", true);
        menu.addButton("❓ Soporte", "/support", true);
        menu.addButton("♻ Actualizar", "/menu", true);

        return menu;

    }

}
