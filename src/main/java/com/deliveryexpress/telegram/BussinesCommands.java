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
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.Tuser;
import com.deliveryexpress.quizes.QuizNewOrderAtm;
import com.deliveryexpress.quizes.QuizNewOrderManual;
import com.deliveryexpress.quizes.SubQuizCotization;
import com.monge.tbotboot.commands.Command;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.QuizesControl;
import java.util.ArrayList;

/**
 *
 * @author DeliveryExpress
 */
public class BussinesCommands {

    static void execute(Xupdate xupdate) {

        Tuser user = Tuser.read(Tuser.class, xupdate.getSenderId());
        Bussines bussines = user.getBussines();

        if (bussines.getAccountStatus().equals(AccountStatus.PAUSED)) {
            Response.sendMessage(xupdate.getTelegramUser(), "Tu cuenta esta en proceso de aprovacion."
                    + "\n contacte a un moderador para mas informacion.", null);
            return;

        }

        if (bussines.getAccountStatus().equals(AccountStatus.INACTIVE)) {
            Response.sendMessage(xupdate.getTelegramUser(), "Tu cuenta esta Inactiva!", null);
            return;

        }

        Command command = xupdate.getCommand();
        switch (command.command()) {

            case "/menu":
            case "/start":
                BalanceAccount read = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(bussines.getBalanceAccountNumber());
                Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(),
                        bussines.getName()
                        + "\nID:" + user.getId()
                        + "\nCartera:" + read.getBalance() + "$", getMenu());

                break;

            case "/new_order_atm":
                QuizesControl.add(new QuizNewOrderAtm(xupdate.getSenderId(), bussines, false));
                QuizesControl.execute(xupdate);

                break;

            case "/new_order_manual":
                QuizesControl.add(new QuizNewOrderManual(xupdate.getSenderId(), bussines, false));
                QuizesControl.execute(xupdate);
                break;
                
              case "/cotaizer":
                QuizesControl.add(new QuizNewOrderAtm(xupdate.getSenderId(),bussines,true));
                QuizesControl.execute(xupdate);
                break;    

            case "/myorders":
                ArrayList<Order> orders = OrdersControl.getOrdersOf(bussines, false);
                Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(), "Mis ordenes",
                        getOrderListAsMenu(orders));
                break;
            case "/mytodayhistory":
                orders = OrdersControl.getOrdersOf(bussines, true);
                Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(), "Hoy",
                        getOrderListAsMenu(orders));
                break;

            case "/changestatus":
                String statusCode = command.getParam(1);
                String orderId = command.getParam(2);

                boolean changeOrderStatus = OrdersControl.changeOrderStatus(bussines, statusCode, orderId);
                try {
                    if (changeOrderStatus) {
                        Order o = OrdersControl.getOrder(orderId);
                        Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(), o.toTelegramString(),
                                getCurrentOrdersBussinesMenu(o));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Response.sendMessage(bussines.getReceptor(), e.getLocalizedMessage(), MessageMenu.okAndDeleteMessage());

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
                            getCurrentOrdersBussinesMenu(o));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case "/my_orders":
                break;
            case "/delete_msg":
                Response.deleteMessage(xupdate);

                break;
        }

    }

    private static MessageMenu getMenu() {
        MessageMenu menu = new MessageMenu();

        menu.addButton("✳ Nueva Orden", "/new_order_atm", true);
        //menu.addButton("✳ Nueva Orden(ATM)", "/new_order_atm", true);
        //menu.addButton("✴️ Nueva Orden(Manual)", "/new_order_manual&2", true);
        menu.addButton("🛵 Cotizar envio(ATM)", "/cotaizer", true);
        //menu.addButton("🛵 Tabla KM", "/deliverytable", true);
        menu.addButton("📑 Mis ordenes", "/myorders", true);
        menu.addButton("🕰 Historial (Hoy)", "/mytodayhistory", true);
        menu.addButton("💳 Mi cartera", "/mywallet", true);
        //menu.addButton("📢 Canal official", "/bussineschannel",true);
        menu.addButton("❓ Soporte", "/support");
        menu.addButton("♻ Actualizar", "/menu&refresh", true);

        return menu;

    }
    
       public static MessageMenu getCurrentOrdersBussinesMenu(Order o) {

        MessageMenu menu = new MessageMenu();

        switch (o.getStatus()) {

            case OrderStatus.PREPARACION:
                    menu.addButton("Listo ✅", "/changestatus&"+BussinesOrderChangeStatusCode.SET_READY+ "&" + o.getId());
                break;

        }

        menu.newLine();
        menu.addButton("♻ Actualizar", "/vieworder&"+ o.getId());
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
    
        public static interface BussinesOrderChangeStatusCode {

        String SET_READY = "b01";
        String CANCEL = "b02";


    }

}
