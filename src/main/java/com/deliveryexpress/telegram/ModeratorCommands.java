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
import com.deliveryexpress.objects.users.Moderator;
import com.deliveryexpress.objects.users.Tuser;
import com.deliveryexpress.utils.PageListViewer;
import com.monge.tbotboot.commands.Command;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;
import java.util.ArrayList;

/**
 *
 * @author DeliveryExpress
 */
public class ModeratorCommands {

    public static void execute(Xupdate xupdate) {

        Tuser user = Tuser.read(Tuser.class, xupdate.getSenderId());
        Moderator moderator = user.getModerator();

        if (moderator.getAccountStatus().equals(AccountStatus.PAUSED)) {
            Response.sendMessage(xupdate.getTelegramUser(), "Tu cuenta esta en proceso de aprovacion."
                    + "\n contacte a al Bot para mas informacion.", null);
            return;

        }

        if (moderator.getAccountStatus().equals(AccountStatus.INACTIVE)) {
            Response.sendMessage(xupdate.getTelegramUser(), "Tu cuenta esta Inactiva!", null);
            return;

        }

        Command command = xupdate.getCommand();
        switch (command.command()) {

            case "/menu":
            case "/start":
                BalanceAccount read = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(moderator.getBalanceAccountNumber());
                Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(),
                        "Moderator " + moderator.getName()
                        + "\nID:" + user.getId()
                        + "\nCartera:" + read.getBalance() + "$", getMenu(moderator));

                break;

            case "/myorders":
              
                    ArrayList<Order> orders = OrdersControl.getOrdersOf(moderator, false);

                    PageListViewer book = new PageListViewer(orders, 8);
                    
                    int page = book.getValueOf(command.getParam(1));
                    MessageMenu menu = book.getNavMenu(page);

                    /*agregamos las ordenes a visualizar*/
                    menu.merge(getOrderListAsMenu(book.getPage(page)));

                    Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(), orders.size()+" orden(es) - Pag. "+page,
                            menu);
             

                break;

            case "/mytodayhistory":
                orders = OrdersControl.getOrdersOf(moderator, true);
                     book = new PageListViewer(orders, 8);
                    
                     page = book.getValueOf(command.getParam(1));
                     menu = book.getNavMenu(page);

                    /*agregamos las ordenes a visualizar*/
                    menu.merge(getOrderListAsMenu(book.getPage(page)));

                    Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(), orders.size()+" orden(es) finalizadas - Pag. "+page,
                            menu);
             
                break;

            case "/vieworder":
                String orderId = command.getParam(1);
                Order o = OrdersControl.getOrder(orderId);
                if (o == null) {
                    Response.sendMessage(xupdate.getTelegramUser(), "Esta orden ya no esta disponible."
                            + " id: " + orderId, null);
                }

                try {
                    Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(), o.toTelegramStringForMod(),
                            getCurrentOrdersModMenu(o));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }

    }

    private static MessageMenu getCurrentOrdersModMenu(Order o) {

        MessageMenu menu = new MessageMenu();

        switch (o.getStatus()) {

            case OrderStatus.PREPARACION:
            case OrderStatus.LISTO:

                break;

            case OrderStatus.EN_CAMINO:

                break;
            case OrderStatus.EN_DOMICILIO:

                break;

            case OrderStatus.ENTREGADO:

                break;

        }

        if (o.getDeliveryMan() != null) {
            menu.addUrlButton("🚚 Ubicacion", o.getDeliveryMan().getPosition().getUrlGoogleMapsMark());
        }

        menu.addButton("🚚 Asignar", "/asigndelivery&" + o.getId());

        menu.newLine();
        menu.addBackButton("/myorders");

        return menu;

    }

    private static MessageMenu getOrderListAsMenu(ArrayList<Order> orders) {
        MessageMenu menu = new MessageMenu();
        for (Order o : orders) {

            menu.addButton("📦 " + o.getCustomer().getName(), "/vieworder&" + o.getId(), true);

        }
        menu.addBackButton("/menu");

        return menu;

    }

    private static MessageMenu getMenu(Moderator moderator) {

        MessageMenu menu = new MessageMenu();

        menu.addButton("🗒 Ordenes", "/myorders", true);
        menu.addButton("🕰 Historial (Hoy)", "/mytodayhistory", true);
        // menu.addButton("💳 Mi cartera", "/mywallet", true);
        menu.addButton("♻ Actualizar", "/menu", true);

        return menu;

    }

}
