/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de;

import com.deliveryexpress.de.OrdersControl.Order;
import com.deliveryexpress.objects.OperationAreaMagnament;
import com.deliveryexpress.objects.TelegramUser;
import com.deliveryexpress.objects.TelegramUser.BalanceAccount;
import com.deliveryexpress.objects.TelegramUser.DeliveryMan;
import com.deliveryexpress.objects.TelegramUser.Group;
import com.deliveryexpress.objects.Transacction;
import com.deliveryexpress.quizes.QuizModDirectTransfer;
import com.deliveryexpress.quizes.QuizNewBussines;
import com.deliveryexpress.quizes.QuizNewDeliveryMan;
import com.deliveryexpress.quizes.QuizNewModerator;
import com.deliveryexpress.quizes.QuizNewOrderAtm;
import com.deliveryexpress.quizes.QuizNewOrderManual;
import com.deliveryexpress.quizes.QuizTransfer;
import com.deliveryexpress.quizes.QuizesControl;
import com.deliveryexpress.telegram.Messenger;
import com.deliveryexpress.telegram.Messenger.MessageMenu;
import com.deliveryexpress.telegram.Messenger.MessageMenu.Button;
import com.deliveryexpress.telegram.Messenger.TelegramAction;
import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;
import com.deliveryexpress.utils.Utils.PageListViewer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 *
 * @author DeliveryExpress
 */
public class UserInteracionHandlers {

    public static void execute(Xupdate xupdate) {

        try {
            //System.out.println("execute--> group message:"+xupdate.isGroupMessage()+" -> " + xupdate.getCommand().command());


            /*revisa si el xupdate es de un area autorizada OAM*/
            //xupdate.getTelegramUser().getGroup();
            if (xupdate.isGroupMessage()) {

                Group group = xupdate.getTelegramUser().getGroup();

                TelegramUser callerTelegramUser = xupdate.getCallerTelegramUser();
                
                
                 if(xupdate.hasNewChatMember()){
                     
                    // registreNewChatMembersAsDeliveries(xupdate.getNewChatMembers());
                
                }

                /*
                System.out.println("[Group]\n" + group.toString());
                System.out.println("[OAM]\n" + group.getOam().toString());
                System.out.println("[CALLER\n" + callerTelegramUser.toString());
                */
                if (callerTelegramUser != null) {
                    switch (callerTelegramUser.getAccountType()) {

                        case TelegramUser.AccountType.DELIVERYMAN:

                            /*si tiene locacion*/
                            if (xupdate.hasLocation()) {
                                DeliveriesControl.updateDeliveryManLocation(xupdate);
                                break;
                            }

                            groupChatDeliveriesCommands.execute(xupdate);

                            break;

                        case TelegramUser.AccountType.MODERATOR:

                            groupChatModeratorsCommands.execute(xupdate);

                            break;

                    }
                } 
                
               

                

                return;

            } else {

                if (xupdate.hasQuiz()) {
                    QuizesControl.execute(xupdate);
                } else {

                    TelegramUser telegramUser = xupdate.getTelegramUser();

                    switch (telegramUser.getAccountType()) {

                        case TelegramUser.AccountType.NOT_REGISTRED:

                            privateChatNotRegistredCommands.execute(xupdate);

                            break;

                        case TelegramUser.AccountType.BUSSINES:

                            privateChatBussinesCommands.execute(xupdate);

                            break;

                        case TelegramUser.AccountType.DELIVERYMAN:

                            privateChatDeliveriesCommands.execute(xupdate);

                            break;

                        case TelegramUser.AccountType.MODERATOR:

                            privateChatModeratorsCommands.execute(xupdate);

                            break;

                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void registreNewChatMembersAsDeliveries(List<User> newChatMembers) {
       
        for (User u : newChatMembers) {
            
            if (DataBase.TelegramUsers.getTelegramUserById(u.getId().toString()) == null) {
                DeliveryMan d = new DeliveryMan(u.getFirstName()+" "+u.getLastName(),"S/N");
                DataBase.Deliveries.insertNew(d);
                
                BalanceAccount ba= new BalanceAccount(d.getBalanceAccountNumber(),d.getAccountId());
                DataBase.BalancesAccounts.insertNewBalanceAccount(ba);
                
                TelegramUser tu = new TelegramUser(u.getId().toString(),Messenger.getDefaultBot().getBotUsername(),
                false,TelegramUser.AccountType.DELIVERYMAN,d.getAccountId());
                DataBase.TelegramUsers.insertNew(tu);
            }
            
        }
        
       
    
    }

    public static class globalFunctions {

        public static void deleteMsg(Xupdate xupdate) {

            TelegramAction ta = new TelegramAction();
            ta.setChatId(xupdate.getFromId());
            ta.setMessageId(xupdate.getMessageId());
            ta.setAction(TelegramAction.Action.DELETE_MESSAGE);
            ta.execute();

        }

        public static String getTransacctionsOf(String accountNumber) {
            try {
                ArrayList<Transacction> transacctionsOf = (ArrayList<Transacction>) DataBase.BalancesAccounts
                        .getTransacctionsOf(accountNumber);

                if (transacctionsOf.size() > Global.Global().max_users_transacctions_view) {
                    transacctionsOf = (ArrayList<Transacction>) transacctionsOf.subList(0, Global.Global().max_users_transacctions_view);
                }

                Collections.reverse(transacctionsOf);

                String tlist = "";

                for (Transacction t : transacctionsOf) {

                    if (t.getFrom().equals(accountNumber)) {
                        tlist += "➖" + t.getMount() + " " + t.getConcept() + ": " + t.getDate() + "\n";
                    } else {
                        tlist += "➕" + t.getMount() + " " + t.getConcept() + ": " + t.getDate() + "\n";
                    }

                }

                return tlist;

            } catch (Exception e) {
                return e.getMessage();
            }

        }

    }

    public static class privateChatBussinesCommands {

        private static void execute(Xupdate xupdate) {

            TelegramAction ta = new TelegramAction();
            ta.setChatId(xupdate.getSenderId());
            TelegramUser.Bussines bussines = xupdate.getTelegramUser().getBussines();

            if (bussines == null) {
                
                ta.setText("No se econtro cuenta asociada a este Usuario de telegram!\n\n"
                        + xupdate.getTelegramUser().toString());
                ta.setMenu(MessageMenu.okAndDeleteMessage());

                ta.execute();
                return;
            }
            
            if(!bussines.isActive()){
                ta.setText("⚠️Tu cuenta esta inactiva posibles razones:"
                        + "\nInactividad"
                        + "\nSaldo pendiente"
                        + "\npara mas informacion contacte a un moderador.");
                
                ta.execute();
                return;
            
            }

            switch (xupdate.getCommand().command()) {

                case "/main":
                case "/menu":

                    ta.setChatId(xupdate.getSenderId());
                    if (xupdate.getCommand().getParam(1).equals("refresh")) {
                        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                        ta.setEditMessageId(xupdate.getMessageId());
                    }

                    ta.setText("Telegram ID:" + xupdate.getSenderId() + "\nCuenta ID: " + bussines.getAccountId() + "\n" + bussines.getName());
                    ta.setMenu(mainMenu());
                    ta.execute();

                    break;

                case "/neworder":

                    if (!QuizesControl.hasQuiz(xupdate.getSenderId())) {

                        switch (xupdate.getCommand().getParam(1)) {

                            case "1":

                                QuizesControl.add(new QuizNewOrderAtm(xupdate.getSenderId(),
                                        xupdate.getTelegramUser().getBussines(), false));

                                break;

                            case "2":

                                QuizesControl.add(new QuizNewOrderManual(xupdate.getSenderId(),
                                        xupdate.getTelegramUser().getBussines(), false));

                                break;

                        }

                        QuizesControl.execute(xupdate);

                    } else {

                        QuizesControl.sendAlreadyInProcessMsg(xupdate.getSenderId());

                    }

                    break;
                case "/calcdeliverycost":

                    if (!QuizesControl.hasQuiz(xupdate.getSenderId())) {
                        QuizesControl.add(new QuizNewOrderAtm(xupdate.getSenderId(),
                                xupdate.getTelegramUser().getBussines(), true));
                        QuizesControl.execute(xupdate);

                    } else {

                        ta.setText("Ya tiene un proceso abierto, continue o ingrese /salir");
                        ta.execute();

                    }

                    break;
                    
                case "/deliverytable":
                    
                    ta.setText("Tarifas de entrega por Km");
                    ta.setMenu(bussines.getDeliveryTable().addOkAndDeleteMessage());
                    ta.execute();
                    
                    break;

                case "/mywallet":

                    TelegramUser.BalanceAccount balanceAccount = bussines.getBalanceAccount();
                    String text = "No. de Cartera: " + balanceAccount.getAccountNumber()
                            + "\nSaldo: " + balanceAccount.getBalance();

                    if (balanceAccount.getBalance() < -15) {
                        text += "\n\n" + bussines.getPaymentMenssage().replace("[USERID]", balanceAccount.getAccountNumber());
                    }

                    ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                    ta.setEditMessageId(xupdate.getMessageId());
                    ta.setText(text);

                    MessageMenu menu = new MessageMenu();
                    menu.addAbutton(new Button("🔀 Movimientos", "/movements&" + balanceAccount.getAccountNumber()), true);
                    menu.addBackButton("/menu&refresh");

                    ta.setMenu(menu);
                    ta.execute();

                    break;

                case "/support":

                    TelegramUser.Moderator adminAccount = bussines.getArea().getAdminAccount();

                    text = "Favor de comunicarte con el moderador de tu area:\n"
                            + adminAccount.toString();
                    ta.setMenu(MessageMenu.okAndDeleteMessage());
                    ta.setText(text);
                    ta.execute();

                    break;

                case "/myorders":
                    Predicate<Order> predicate = (c -> c.getBusssines().getAccountId().equals(bussines.getAccountId()));
                    ArrayList<Order> orderOf = OrdersControl.getOrderOf(predicate);

                    menu = OrdersControl.getOrderOfAsMenu(orderOf, "/vieworder&");

                    ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                    ta.setEditMessageId(xupdate.getMessageId());

                    ta.setText("Ordenes en curso " + orderOf.size());
                    ta.setMenu(menu.addBackButton("/menu&refresh"));
                    ta.execute();
                    break;

                case "/mytodayhistory":
                    predicate = (c -> c.getBusssines().getAccountId().equals(bussines.getAccountId()));
                    orderOf = OrdersControl.getFinishedOrderOf(predicate);
                    menu = OrdersControl.getOrderOfAsMenu(orderOf, "/viewhistoryorder&");
                    ta.setText("Ordenes terminadas" + orderOf.size());
                    ta.setMenu(menu.addBackButton("/menu&refresh"));
                    ta.execute();
                    break;

                case "/vieworder":
                    Order order = OrdersControl.getOrderById(xupdate.getCommand().getParam(1));
                    if (order != null) {
                        ta.setEditMessageId(xupdate.getMessageId());
                        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                        ta.setText(order.toString());
                        ta.setMenu(order.getCurrentButtonsForBussines());

                        ta.execute();
                    } else {
                        ta.setText("Esta orden no existe");
                        ta.execute();
                    }

                    break;

                case "/viewhistoryorder":
                    order = OrdersControl.getHistoryOrderById(xupdate.getCommand().getParam(1));
                    if (order != null) {
                        ta.setEditMessageId(xupdate.getMessageId());
                        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                        ta.setText(order.toString());
                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.execute();
                    } else {
                        ta.setText("Esta orden no existe");
                        ta.execute();
                    }

                    break;

                case "/setready":

                    String param = xupdate.getCommand().getParam(1);
                    Order o = OrdersControl.getOrderById(param);

                    if (o != null & o.getStatus().equals(Order.DeliveryStatus.PREPARACION)) {
                        OrdersControl.setStatus(o, Order.DeliveryStatus.LISTO);

                        o.addLog(bussines.getName() + " - > LISTO");
                        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                        ta.setEditMessageId(xupdate.getMessageId());
                        ta.setText(o.toString());
                        ta.setMenu(o.getCurrentButtonsForBussines());
                        ta.execute();

                        /*Notificamos al grupo mods*/
                        o.getArea().sendMessageToModsGroup("La orden "
                                + o.getShortId() + " de " + bussines.getName()
                                + " ya esta lista para recolectar!.", MessageMenu.okAndDeleteMessage());
                    }

                    break;

                case "/movements":

                    BalanceAccount ba = DataBase.BalancesAccounts.getBalanceAccount(xupdate.getCommand().getParam(1));

                    ta.setText("Ultimos movimientos de la cuenta " + ba.getAccountNumber() + "\n\n" + ba.getTransacctionsToString());
                    ta.setChatId(xupdate.getSenderId());
                    ta.setMenu(MessageMenu.okAndDeleteMessage());
                    ta.execute();

                    break;

                case "/deletemsg":

                    globalFunctions.deleteMsg(xupdate);

                    break;

            }

        }

        private static Messenger.MessageMenu mainMenu() {

            Messenger.MessageMenu menu = new Messenger.MessageMenu();

            if (!Global.Global().manual_order_only) {
                menu.addButton("✳ Nueva Orden(ATM)", "/neworder&1", true);
            }

            menu.addButton("✴️ Nueva Orden(Manual)", "/neworder&2", true);
            menu.addButton("🛵 Cotizar envio(ATM)", "/calcdeliverycost", true);
            menu.addButton("🛵 Tabla KM", "/deliverytable", true);
            menu.addButton("📑 Mis ordenes", "/myorders", true);
            menu.addButton("🕰 Historial (Hoy)", "/mytodayhistory", true);
            menu.addButton("💳 Mi cartera", "/mywallet", true);
            //menu.addButton("📢 Canal official", "/bussineschannel",true);
            menu.addButton("❓ Soporte", "/support");
            menu.addButton("♻ Actualizar", "/menu&refresh", true);

            return menu;

        }

    }

    public static class privateChatDeliveriesCommands {

        private static void execute(Xupdate xupdate) {

            TelegramAction ta = new TelegramAction();
            TelegramUser.DeliveryMan deliveryMan = xupdate.getTelegramUser().getDeliveryMan();
            TelegramUser telegramUser = xupdate.getTelegramUser();
            ta.setChatId(xupdate.getSenderId());

            if (deliveryMan == null) {

                ta.setText("No se econtro cuenta asociada a este Usuario de telegram!\n\n"
                        + telegramUser.toString());
                
                ta.execute();
                return;
            }
            
             if(!deliveryMan.isActive()){
                ta.setText("⚠️Tu cuenta esta inactiva posibles razones:"
                        + "\nInactividad"
                        + "\nSaldo pendiente"
                        + "\npara mas informacion contacte a un moderador.");
               
                ta.execute();
                return;
            
            }

            switch (xupdate.getCommand().command()) {

                case "/main":
                case "/menu":

                    ta.setSendMessage(xupdate.getFromId(),
                            "Telegram ID: " + xupdate.getSenderId()
                            + "\nCuenta ID: " + deliveryMan.getAccountId()
                            + "\n" + deliveryMan.getName() + " - " + telegramUser.getAccountType()
                            + "\nNivel: " + deliveryMan.getDeliveryManLevel());
                    ta.setEditMessageId(xupdate.getMessageId());
                    ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                    ta.setMenu(mainMenu());
                    ta.execute();

                    break;

                case "/mywallet":

                    TelegramUser.BalanceAccount balanceAccount = deliveryMan.getBalanceAccount();
                    String text = "No. de Cartera: " + balanceAccount.getAccountNumber()
                            + "\nSaldo: " + balanceAccount.getBalance();

                    MessageMenu menu = new MessageMenu();
                    menu.addAbutton(new Button("🔀 Movimientos", "/movements&" + balanceAccount.getAccountNumber()), true);
                    menu.addBackButton("/menu&refresh");

                    ta.setEditMessageId(xupdate.getMessageId());
                    ta.setAction(TelegramAction.Action.EDIT_MESSAGE);

                    ta.setMenu(menu);
                    ta.setText(text);
                    ta.execute();

                    break;

                case "/myorders":

                    try {
                        Predicate<Order> predicate = (c -> c.getDeliveryMan() != null && c.getDeliveryMan().getAccountId().equals(deliveryMan.getAccountId()));
                        ArrayList<Order> orderOf = OrdersControl.getOrderOf(predicate);

                        menu = OrdersControl.getOrderOfAsMenu(orderOf, "/vieworder&");

                        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                        ta.setEditMessageId(xupdate.getMessageId());

                        ta.setText("Ordenes en curso " + orderOf.size());
                        ta.setMenu(menu.addBackButton("/menu"));
                        ta.execute();
                    } catch (java.lang.NullPointerException e) {

                        ta.setText("Ordenes en curso 0");
                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.execute();

                    }

                    break;

                case "/mytodayhistory":
                    Predicate<Order> predicate = (c -> c.getDeliveryMan() != null && c.getDeliveryMan().getAccountId().equals(deliveryMan.getAccountId()));
                    ArrayList<Order> orderOf = OrdersControl.getFinishedOrderOf(predicate);

                    menu = OrdersControl.getOrderOfAsMenu(orderOf, "/viewhistoryorder&");
                    ta.setEditMessageId(xupdate.getMessageId());
                    ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                    ta.setText("Ordenes terminadas " + orderOf.size());
                    ta.setMenu(menu.addBackButton("/menu"));
                    ta.execute();
                    break;

                case "/vieworder":
                    Order order = OrdersControl.getOrderById(xupdate.getCommand().getParam(1));
                    if (order != null) {
                        ta.setEditMessageId(xupdate.getMessageId());
                        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                        ta.setText(order.toString());
                        ta.setMenu(order.getCurrentButtonsForDeliveryMan()
                                .addNewLine()
                                .addRefreshButton("/vieworder&" + order.getId()));

                        ta.execute();
                    } else {
                        ta.setText("Esta orden no existe");
                        ta.execute();
                    }

                    break;

                case "/viewhistoryorder":
                    order = OrdersControl.getHistoryOrderById(xupdate.getCommand().getParam(1));
                    if (order != null) {
                        ta.setEditMessageId(xupdate.getMessageId());
                        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                        ta.setText(order.toString());
                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.execute();
                    } else {
                        ta.setText("Esta orden no existe");
                        ta.execute();
                    }

                    break;

                case "/support":
                    text = "Favor de comunicarte con un moderador";
                    if (xupdate.getCommand().getParam(1).isEmpty()) {

                        Order orderById = OrdersControl.getOrderById(xupdate.getCommand().getParam(1));
                        if (orderById != null) {

                            TelegramUser.Moderator adminAccount = orderById.getArea().getAdminAccount();
                            text = "Comunicate con el moderador de esta orden " + orderById.getShortId() + " "
                                    + orderById.getCustomer().getName() + "\n" + adminAccount.toString();
                        }
                    }

                    ta.setText(text);
                    ta.setMenu(MessageMenu.okAndDeleteMessage());
                    ta.execute();

                    break;

                case "/accept":
                    String param = xupdate.getCommand().getParam(1);
                    Order o = OrdersControl.getOrderById(param);

                    if (deliveryMan.ordersCount() >= Global.Global().delivery_max_order_count) {

                        deliveryMan.sendMessage("Ya no puedes aceptar mas ordenes!", MessageMenu.okAndDeleteMessage());
                        break;
                    }

                    if (o == null) {
                        deliveryMan.sendMessage("Esta orden ya no esta disponible!", MessageMenu.okAndDeleteMessage());
                        break;
                    }

                    if (o.isRecolectable()) {
                        OrdersControl.setDeliveryMan(o, deliveryMan);

                    }

                    text = "La orden " + o.getShortId() + " ha sido aceptada por "
                            + deliveryMan.getName();

                    /*Notificamos al grupo de moderacion de area*/
                    OperationAreaMagnament area = o.getArea();
                    area.sendMessageToModsGroup(text, MessageMenu.okAndDeleteMessage());

                    /*Notificamos al restaurant area*/
                    o.sendNotificationToBussiness(text, MessageMenu.okAndDeleteMessage());

                    ta.setEditMessage(xupdate.getMessageId(), xupdate.getSenderId(), o.toString());
                    ta.setMenu(o.getCurrentButtonsForDeliveryMan());
                    ta.execute();

                    break;

                case "/updatestatus":

                    String status = xupdate.getCommand().getParam(1);
                    String orderId = xupdate.getCommand().getParam(2);
                    o = OrdersControl.getOrderById(orderId);

                    if (o == null) {

                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.setText("Esta orden ya no existe o fue completada! " + orderId);
                        ta.execute();

                        break;
                    }

                    if (!o.hasDeliveryMan()) {

                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.setText("Esta orden no cuenta con repartidor, necesitas aceptarla.");
                        ta.execute();

                        break;

                    }

                    if (!o.isMine(deliveryMan)) {
                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.setText("No tienes asignada esta orden.");
                        ta.execute();
                        break;
                    }

                    String modNotificationText = "";

                    switch (status) {

                        case "ARRIVED_TO_BUSSINES":

                            OrdersControl.deliveryManArrivedToBussines(o, true);
                            modNotificationText = "El repartidor " + o.deliveryMan.getName() + " ha llegado al restaurante.\n"
                                    + "Orden " + o.getShortId() + " " + o.customer.getName() + "\n"
                                    + "";

                            o.addLog(deliveryMan.getName() + " - > ARRIVED_TO_BUSSINES");

                            break;

                        case Order.DeliveryStatus.EN_CAMINO:

                            OrdersControl.setStatus(o, Order.DeliveryStatus.EN_CAMINO);

                            modNotificationText = "El repartidor " + o.deliveryMan.getName() + " esta en camino.\n"
                                    + "Orden " + o.getShortId() + " " + o.customer.getName() + "\n"
                                    + "";

                            o.addLog(deliveryMan.getName() + " - > EN_CAMINO");

                            break;

                        case Order.DeliveryStatus.EN_DOMICILIO:

                            OrdersControl.setStatus(o, Order.DeliveryStatus.EN_DOMICILIO);
                            modNotificationText = "El repartidor " + o.deliveryMan.getName() + " ha llegado con el cliente.\n"
                                    + "Orden " + o.getShortId() + " " + o.customer.getName() + "\n"
                                    + "";

                            o.addLog(deliveryMan.getName() + " - > EN_DOMICILIO");

                            break;

                        case Order.DeliveryStatus.ENTREGADO:

                            OrdersControl.setStatus(o, Order.DeliveryStatus.ENTREGADO);

                            modNotificationText = "El repartidor " + o.deliveryMan.getName() + " ha entregado la orden.\n"
                                    + "Orden " + o.getShortId() + " " + o.customer.getName() + "\n"
                                    + "";

                            o.addLog(deliveryMan.getName() + " - > ENTREGADO");
                            o.getBusssines().getBalanceAccount().chargeAmount(o.getBusssines().getServiceCost(), "Servicio de entrega", o.getShortId());

                            OrdersControl.addFinishedOrder(o);
                            OrdersControl.remove(o);
                            DataBase.Orders.saveOrder(o);

                            break;

                    }
                    ta.setEditMessage(xupdate.getMessageId(), xupdate.getSenderId(), o.toString());
                    ta.setMenu(o.getCurrentButtonsForDeliveryMan());
                    ta.execute();

                    /*Notificamos al grupo de moderacion de area*/
                    area = o.getArea();
                    area.sendMessageToModsGroup(modNotificationText, MessageMenu.okAndDeleteMessage());

                    /*Notificamos al restaurant area*/
                    o.sendNotificationToBussiness(modNotificationText, MessageMenu.okAndDeleteMessage());

                    break;

                case "/movements":

                    BalanceAccount ba = DataBase.BalancesAccounts.getBalanceAccount(xupdate.getCommand().getParam(1));

                    ta.setText("Ultimos movimientos de la cuenta " + ba.getAccountNumber() + "\n\n" + ba.getTransacctionsToString());
                    ta.setChatId(xupdate.getSenderId());
                    ta.setMenu(MessageMenu.okAndDeleteMessage());
                    ta.execute();

                    break;

                case "/deletemsg":

                    globalFunctions.deleteMsg(xupdate);

                    break;

            }

        }

        private static Messenger.MessageMenu mainMenu() {

            Messenger.MessageMenu menu = new Messenger.MessageMenu();
            menu.addButton("🗒 Mis ordenes", "/myorders", true);
            menu.addButton("🕰 Historial (Hoy)", "/mytodayhistory", true);
            menu.addButton("💳 Mi cartera", "/mywallet", true);
            menu.addButton("❓ Soporte", "/support", true);
            menu.addButton("♻ Actualizar", "/main", true);

            return menu;

        }

    }

    private static class privateChatNotRegistredCommands {

        private static void execute(Xupdate xupdate) {

            switch (xupdate.getCommand().command()) {

                case "/menu":
                case "/start":

                    String text = "ID: " + xupdate.getSenderId()
                            + "\nBienvenido a DeliveryExpress para registrarte comparte este ID con un moderador.";

                    TelegramAction ta = new TelegramAction();
                    ta.setSendMessage(xupdate.getFromId(), text);
                    ta.execute();

                    break;

                case "/deletemsg":

                    globalFunctions.deleteMsg(xupdate);

                    break;

            }

        }

    }

    private static class privateChatModeratorsCommands {

        private static void execute(Xupdate xupdate) {

            try {
                TelegramAction ta = new TelegramAction();
                TelegramUser.Moderator moderator = xupdate.getTelegramUser().getModerator();
                ta.setChatId(xupdate.getSenderId());

                if (moderator == null) {
                    ta.setText("No se econtro cuenta asociada a este Usuario de telegram!\n\n"
                            + xupdate.getSenderId().toString());

                    ta.execute();
                    return;
                }

                if (!moderator.isActive()) {

                    ta.setText("⚠️Tu cuenta esta inactiva posibles razones:"
                            + "\nInactividad"
                            + "\nSaldo pendiente"
                            + "\npara mas informacion contacte a un moderador.");

                    ta.execute();
                    return;

                }

                switch (xupdate.getCommand().command()) {
                    case "/main":
                    case "/menu":

                        ta.setSendMessage(xupdate.getFromId(), "Telegram ID:" + xupdate.getSenderId() + "\nCuenta ID: " + moderator.getAccountId() + "\n" + moderator.getName());
                        ta.setEditMessageId(xupdate.getMessageId());
                        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                        ta.setMenu(mainMenu());
                        ta.execute();

                        break;

                    case "/register":

                        if (xupdate.getCommand().getParam(1).isEmpty()) {
                            ta.setText("Que tipo de usuario quieres registrar?");
                            MessageMenu menu = new MessageMenu();
                            menu.addAbutton(new Button("🛵 Repartidor", "/register&" + TelegramUser.AccountType.DELIVERYMAN), true);
                            menu.addAbutton(new Button("🍔 Negocio", "/register&" + TelegramUser.AccountType.BUSSINES), true);
                            menu.addAbutton(new Button("⭐ Moderador", "/register&" + TelegramUser.AccountType.MODERATOR), true);
                            ta.setMenu(menu);
                            ta.execute();

                        } else {

                            if (!QuizesControl.hasQuiz(xupdate.getSenderId())) {
                                switch (xupdate.getCommand().getParam(1)) {

                                    case TelegramUser.AccountType.DELIVERYMAN:
                                        QuizesControl.add(new QuizNewDeliveryMan(xupdate.getSenderId()));
                                        break;
                                    case TelegramUser.AccountType.BUSSINES:
                                        QuizesControl.add(new QuizNewBussines(xupdate.getSenderId()));
                                        break;
                                    case TelegramUser.AccountType.MODERATOR:
                                        QuizesControl.add(new QuizNewModerator(xupdate.getSenderId()));
                                        break;

                                }
                                QuizesControl.execute(xupdate);
                            } else {
                                QuizesControl.sendAlreadyInProcessMsg(xupdate.getSenderId());

                            }

                        }

                        break;

                    case "/myorders":

                        ArrayList<Order> orderOf = new ArrayList<>();

                        if (moderator.getArea().equals("GENERAL")) {
                            orderOf = OrdersControl.getOrderOf(null);
                        } else {
                            Predicate<Order> predicate = (c -> c.getArea().getId().equals(moderator.getArea()));
                            orderOf = OrdersControl.getOrderOf(predicate);
                        }

                        Messenger.MessageMenu menu = OrdersControl.getOrderOfAsMenu(orderOf, "/vieworder&");

                        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                        ta.setEditMessageId(xupdate.getMessageId());

                        ta.setText("Ordenes en curso " + orderOf.size() + " Area: " + moderator.getArea());
                        ta.setMenu(menu.addBackButton("/menu"));
                        ta.execute();
                        break;

                    case "/mytodayhistory":

                        orderOf = new ArrayList<>();
                        if (moderator.getArea().equals("GENERAL")) {
                            orderOf = OrdersControl.getFinishedOrderOf(null);
                        } else {
                            Predicate<Order> predicate = (c -> c.getArea().getId().equals(moderator.getArea()));
                            orderOf = OrdersControl.getFinishedOrderOf(predicate);

                        }

                        menu = OrdersControl.getOrderOfAsMenu(orderOf, "/viewhistoryorder&");
                        ta.setEditMessageId(xupdate.getMessageId());
                        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                        ta.setText("Ordenes terminadas " + orderOf.size());
                        ta.setMenu(menu.addBackButton("/menu"));
                        ta.execute();
                        break;

                    case "/vieworder":
                        Order order = OrdersControl.getOrderById(xupdate.getCommand().getParam(1));
                        if (order != null) {
                            ta.setEditMessageId(xupdate.getMessageId());
                            ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                            ta.setText(order.toString());
                            ta.setMenu(order.getCurrentButtonsForModerators()
                                    .addNewLine()
                                    .addRefreshButton("/vieworder&" + order.getId()));

                        } else {
                            ta.setText("Esta orden no existe");

                        }
                        ta.execute();
                        break;

                    case "/viewhistoryorder":
                        order = OrdersControl.getHistoryOrderById(xupdate.getCommand().getParam(1));
                        if (order != null) {

                            ta.setText(order.toString());
                            ta.setMenu(MessageMenu.okAndDeleteMessage());

                        } else {
                            ta.setText("Esta orden no existe");

                        }
                        ta.execute();
                        break;

                    case "/users":
                        try {
                            if (xupdate.getCommand().getParam(1).isEmpty()) {

                                ta.setEditMessageId(xupdate.getMessageId());
                                ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                                ta.setText("Seleccione");
                                ta.setMenu(menuUsers().addBackButton("/menu"));
                                ta.execute();

                            } else {

                                /*formato [/users][AccountType][status][pageCount]
            example /users&DELIVERYMAN&2&0
                                 */
                                
                                String accountType = xupdate.getCommand().getParam(1);
                                int status = Integer.parseInt(xupdate.getCommand().getParam(2));
                                int pageCount = Integer.parseInt(xupdate.getCommand().getParam(3));
                                if(pageCount<1){
                                pageCount = 1;
                                }
                                
                                String decode ="&"+ xupdate.getText().replace("/", "_").replace("&", "-");

                                ArrayList<TelegramUser> list = new ArrayList<>();
                                ArrayList<Button>buttonsUsersList = new ArrayList<>();
                                menu = new MessageMenu();

                                switch (accountType) {

                                    case TelegramUser.AccountType.DELIVERYMAN:

                                        switch (status) {

                                            /*inactive*/
                                            case 0:
                                                list = DataBase.TelegramUsers.getTelegramUsersByAccountType(TelegramUser.AccountType.DELIVERYMAN)
                                                        .stream().filter(c -> c.getAccountStatus().equals(TelegramUser.AccountStatus.INACTIVE))
                                                        .collect(Collectors.toCollection(ArrayList::new));
                                                break;

                                            /*active*/
                                            case 1:

                                                list = DataBase.TelegramUsers.getTelegramUsersByAccountType(TelegramUser.AccountType.DELIVERYMAN)
                                                        .stream().filter(c -> c.getAccountStatus().equals(TelegramUser.AccountStatus.ACTIVE))
                                                        .collect(Collectors.toCollection(ArrayList::new));

                                                break;

                                            /*conected*/
                                            case 2:

                                                list = DataBase.TelegramUsers.getTelegramUsersByAccountType(TelegramUser.AccountType.DELIVERYMAN)
                                                        .stream().filter(c -> c.getDeliveryMan().getSession().isSharingLocation())
                                                        .collect(Collectors.toCollection(ArrayList::new));

                                                break;

                                        }
                                        
                                       
                                        for (TelegramUser tu : list) {
                                        buttonsUsersList.add(new Button(tu.getDeliveryMan().getName(), "/viewaccount&" + tu.getId()+decode));    
                                        }

                                        break;

                                    case TelegramUser.AccountType.BUSSINES:

                                        switch (status) {

                                            case 0:

                                                list = DataBase.TelegramUsers.getTelegramUsersByAccountType(TelegramUser.AccountType.BUSSINES)
                                                        .stream().filter(c -> c.getAccountStatus().equals(TelegramUser.AccountStatus.INACTIVE))
                                                        .collect(Collectors.toCollection(ArrayList::new));

                                                break;

                                            case 1:

                                                list = DataBase.TelegramUsers.getTelegramUsersByAccountType(TelegramUser.AccountType.BUSSINES)
                                                        .stream().filter(c -> c.getAccountStatus().equals(TelegramUser.AccountStatus.ACTIVE))
                                                        .collect(Collectors.toCollection(ArrayList::new));

                                                break;

                                        }
                                        
                                        
                                        
                                        for (TelegramUser tu : list) {
                                        buttonsUsersList.add(new Button(tu.getBussines().getName(), "/viewaccount&" + tu.getId()+decode));    
                                        }

                                        

                                        break;


                                    case TelegramUser.AccountType.MODERATOR:
                                        switch (status) {
                                            case 0:

                                                list = DataBase.TelegramUsers.getTelegramUsersByAccountType(TelegramUser.AccountType.MODERATOR)
                                                        .stream().filter(c -> c.getAccountStatus().equals(TelegramUser.AccountStatus.INACTIVE))
                                                        .collect(Collectors.toCollection(ArrayList::new));

                                                break;

                                            case 1:

                                                list = DataBase.TelegramUsers.getTelegramUsersByAccountType(TelegramUser.AccountType.MODERATOR)
                                                        .stream().filter(c -> c.getAccountStatus().equals(TelegramUser.AccountStatus.ACTIVE))
                                                        .collect(Collectors.toCollection(ArrayList::new));

                                                break;

                                        }
                                        
                                         for (TelegramUser tu : list) {
                                        buttonsUsersList.add(new Button(tu.getModerator().getName(), "/viewaccount&" + tu.getId()+decode));    
                                        }


                                        

                                        break;

                                }
                                
                                
                                System.out.println("TelegramUsers :"+list.size());
                                System.out.println("current page :"+pageCount);
                                System.out.println("total buttons :"+buttonsUsersList.size());
                                
                                PageListViewer<Button> book = new PageListViewer<>(buttonsUsersList, 10);
                                buttonsUsersList = book.getPage(pageCount);


                                /*en teoria se esta visualizando la lista de la seleccion
                                /exam. users&DELIVERYMAN*/
                                menu.addButtons(buttonsUsersList,true);
                                
                                String callbacks = xupdate.getCommand().command()+"&"+accountType+"&"+status+"&";
                                
                                 if (pageCount > 1) {
                                    menu.addButton("⬅ Previo", callbacks+String.valueOf(pageCount-1));
                                   
                                }

                                if (pageCount < book.pageCount()) {
                                    menu.addButton("Siguiente ➡", callbacks+String.valueOf(pageCount+1));
                                   
                                }

                                
                                
                                
                                
                                menu.addNewLine();
                                menu.addBackButton("/users");

                                ta.setEditMessageId(xupdate.getMessageId());
                                ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                                ta.setMenu(menu);
                                ta.setText(accountType+" -> "+status+" -> "+
                                        
                                        "Seleccione:");
                                ta.execute();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;

                    case "/viewaccount":
                        String acid = xupdate.getCommand().getParam(1);
                        String route = xupdate.getCommand().getParam(2);
                        
                        String decode = route.replace("_", "/").replace("-", "&");
                        
                        TelegramUser telegramUserById = DataBase.TelegramUsers.getTelegramUserById(acid);

                        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                        ta.setEditMessageId(xupdate.getMessageId());
                        ta.setText(telegramUserById.toStringDetailed());
                        MessageMenu telegramUserMenuForMod = telegramUserById.getTelegramUserMenuForMod();
                        telegramUserMenuForMod.addRefreshButton("/viewaccount&" + acid);
                        telegramUserMenuForMod.addBackButton(decode);

                        ta.setMenu(telegramUserMenuForMod);
                        ta.execute();

                        break;

                    case "/mywallet":

                        TelegramUser.BalanceAccount balanceAccount = moderator.getBalanceAccount();
                        String text = "No. de Cartera: " + balanceAccount.getAccountNumber()
                                + "\nSaldo: " + balanceAccount.getBalance();
                        ta.setSendMessage(xupdate.getFromId(), text);
                        ta.setEditMessageId(xupdate.getMessageId());
                        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                        menu = new MessageMenu();
                        ta.setMenu(menu.addBackButton("/menu"));
                        ta.execute();

                        break;

                    case "/deletemsg":

                        globalFunctions.deleteMsg(xupdate);

                        break;

                    case "/assign":

                        OrdersControl.modAssignOrder(ta, xupdate);

                        break;

                    case "/cancelorder":

                        String param = xupdate.getCommand().getParam(1);
                        Order o = OrdersControl.getOrderById(param);

                        if (o != null) {
                            o.setStatus(OrdersControl.Order.DeliveryStatus.CANCELADO);
                            o.addLog(moderator.getName() + " - > CANCELAR");
                            DataBase.Orders.saveOrder(o);

                            text = "Orden cancelada " + o.getShortId();

                            o.sendNotificationToBussiness(text, MessageMenu.okAndDeleteMessage());

                            o.sendNotificationToDelivery(text, MessageMenu.okAndDeleteMessage());

                            o.getBusssines().getArea().sendMessageToModsGroup(text, MessageMenu.okAndDeleteMessage());

                            TelegramAction.sendMessage(xupdate.getSenderId(), text, MessageMenu.okAndDeleteMessage());

                        } else {
                            TelegramAction.sendMessage(xupdate.getSenderId(), "Esta orden ya no existe!", MessageMenu.okAndDeleteMessage());

                        }

                        break;

                    case "/events":

                        param = xupdate.getCommand().getParam(1);
                        o = OrdersControl.getOrderById(param);

                        if (o != null) {
                            ta.setChatId(xupdate.getSenderId());
                            ta.setText(o.getShortId() + "\n" + o.getLogs());
                            ta.setMenu(MessageMenu.okAndDeleteMessage());
                            ta.execute();
                        }

                        break;

                    case "/sendtoexternal":

                        param = xupdate.getCommand().getParam(1);
                        o = OrdersControl.getOrderById(param);

                        if (o != null) {
                            /**
                             * borramos mensaje del grupo de internos*
                             */
                            o.deleteTMRs(Order.TMR.OrderMessageTypes.INTERNAL_DELIVERIES_GROUP);

                            menu = new MessageMenu();
                            menu.addButton(new Button("🟢 Aceptar", "/accept&" + o.getId()), true);
                            /*sen envia orden al grupo de externos*/
                            TelegramAction sendMessageToDeliveiesGroup = o.getArea().sendMessageToDeliveiesGroup(o.toString(), menu);
                            o.addTMR(xupdate.getFromId(), xupdate.getMessageId(), Order.TMR.OrderMessageTypes.EXTERNAL_DELIVERIES_GROUP);
                            /*respondemos al caller o al moderador*/
                            ta.setChatId(xupdate.getSenderId());
                            ta.setText(o.getShortId() + " enviado al grupo de externos.");
                            ta.setMenu(MessageMenu.okAndDeleteMessage());
                            ta.execute();
                        }

                        break;

                    case "/blacklist":

                        try {
                            int parseBooleanToInt = Utils.parseBoolean(xupdate.getCommand().getParam(2));
                            TelegramUser tu = DataBase.TelegramUsers.getTelegramUserById(xupdate.getCommand().getParam(1));
                            DataBase.TelegramUsers.setBlackList(tu.getId(), parseBooleanToInt);
                            /*reload TelegramUser*/
                            tu = DataBase.TelegramUsers.getTelegramUserById(xupdate.getCommand().getParam(1));

                            ta.setChatId(xupdate.getSenderId());
                            ta.setMenu(tu.getTelegramUserMenuForMod());
                            ta.setEditMessageId(xupdate.getMessageId());
                            ta.setText(tu.toStringDetailed());
                            ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                            ta.execute();

                        } catch (Exception e) {
                            TelegramAction.sendMessage(xupdate.getSenderId(), e.getMessage(), MessageMenu.okAndDeleteMessage());

                        }

                        break;

                    case "/setstatus":

                        try {
                            String status = xupdate.getCommand().getParam(2);
                            TelegramUser tu = DataBase.TelegramUsers.getTelegramUserById(xupdate.getCommand().getParam(1));
                            ta.setChatId(xupdate.getSenderId());

                            if (DataBase.TelegramUsers.setAccountStatus(tu.getId(), status)) {

                                ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                                ta.setEditMessageId(xupdate.getMessageId());
                                ta.setText(tu.toStringDetailed());
                                ta.setMenu(tu.getTelegramUserMenuForMod());
                            } else {

                                ta.setText("No se pudo cambiar el estado de esta cuenta " + xupdate.getCommand().getParam(1));
                                ta.setMenu(MessageMenu.okAndDeleteMessage());

                            }

                            ta.execute();

                        } catch (Exception e) {
                            TelegramAction.sendMessage(xupdate.getSenderId(), e.getMessage(), MessageMenu.okAndDeleteMessage());

                        }

                        break;

                    case "/changearea":
                        String bussinesAccountId = xupdate.getCommand().getParam(1);
                        if (xupdate.getCommand().getParam(2).isEmpty()) {

                            ArrayList<String> collect = DataBase.Areas.getAll().stream().map(c -> c.getId()).collect(Collectors.toCollection(ArrayList::new));

                            menu = new MessageMenu();
                            for (String s : collect) {
                                menu.addAbutton(new Button(s, "/changearea&" + bussinesAccountId + "&" + s), true);

                            }
                            menu.addOkAndDeleteMessage();

                            ta.setText("Seleccione Area");
                            ta.setMenu(menu);
                            ta.execute();

                        } else {

                            boolean updateBussinesField = DataBase.Bussiness.updateBussinesField(bussinesAccountId, "areaId", xupdate.getCommand().getParam(2));
                            if (updateBussinesField) {

                                TelegramAction.sendMessage(xupdate.getSenderId(), "Area actualizada!!", MessageMenu.okAndDeleteMessage());

                            } else {
                                TelegramAction.sendMessage(xupdate.getSenderId(), "No se pudo cambiar el area", MessageMenu.okAndDeleteMessage());

                            }
                        }

                        break;

                    case "/movements":

                        BalanceAccount ba = DataBase.BalancesAccounts.getBalanceAccount(xupdate.getCommand().getParam(1));

                        ta.setText("Ultimos movimientos de la cuenta " + ba.getAccountNumber() + "\n\n" + ba.getTransacctionsToString());
                        ta.setChatId(xupdate.getSenderId());
                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.execute();

                        break;

                    case "/transfer":

                        String param1 = xupdate.getCommand().getParam(1);

                        if (!QuizesControl.hasQuiz(xupdate.getSenderId())) {
                            QuizesControl.add(new QuizModDirectTransfer(xupdate.getSenderId(),
                                    param1));
                            QuizesControl.execute(xupdate);

                        } else {
                            QuizesControl.sendAlreadyInProcessMsg(xupdate.getSenderId());

                        }

                        break;

                    case "/database":

                        if (xupdate.getCommand().getParam(1).isEmpty()) {

                            ta.setEditMessageId(xupdate.getMessageId());
                            ta.setAction(TelegramAction.Action.EDIT_MESSAGE);

                            ta.setText("Seleccione");
                            menu = new MessageMenu();
                            menu.addAbutton(new Button("Limpiar cache", "/database&1"), true);
                            menu.addAbutton(new Button("Executar SQL", "/database&2"), true);
                            ta.setMenu(menu.addBackButton("/menu"));

                            ta.execute();

                        } else {

                            switch (xupdate.getCommand().getParam(1)) {

                                case "1":
                                    DataBase.clearCache();
                                    TelegramAction.sendMessage(xupdate.getSenderId(), "Limpiar cache", MessageMenu.okAndDeleteMessage());

                                    break;

                                case "2":
                                    String sqlInstruccitions = "Para executar un sql statement ingrese /sql&[sql statement]."
                                            + "\n" + DataBase.getDataBaseInfo();
                                    TelegramAction.sendMessage(xupdate.getSenderId(), sqlInstruccitions, MessageMenu.okAndDeleteMessage());

                                    break;

                            }

                        }

                        break;

                    case "/testing":

                        if (xupdate.getCommand().getParam(1).isEmpty()) {

                            ta.setText("Seleccione");
                            menu = new MessageMenu();
                            menu.addAbutton(new Button("Test Orders", "/testing&1"), true);
                            ta.setMenu(menu);
                            ta.execute();

                        } else {

                            switch (xupdate.getCommand().getParam(1)) {

                                case "1":
                                    OrdersControl.testOrders();
                                    TelegramAction.sendMessage(xupdate.getSenderId(), "test Orders executed", MessageMenu.okAndDeleteMessage());

                                    break;
                            }

                        }

                        break;

                    case "/config":

                        ta.setText("Confirguracion Global\n\n " + Utils.toJsonString(Global.Global()) + " \n\n para cambiar un valor"
                                + " ingrese /set&[nombre de la variable]&[valor]");

                        ta.setMenu(MessageMenu.refreshButton("/config"));
                        ta.execute();

                        break;

                    case "/set":

                        ta.setText(Global.Global().setValue(xupdate.getCommand().getParam(1), xupdate.getCommand().getParam(2)));
                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.execute();

                        break;

                    case "sql":
                        try {

                            String sql = xupdate.getCommand().getParam(1);
                            boolean executeUpdate = DataBase.executeUpdate(sql);
                            if (executeUpdate) {
                                TelegramAction.sendMessage(xupdate.getSenderId(), "SQL SUCCESS!", MessageMenu.okAndDeleteMessage());
                            } else {
                                TelegramAction.sendMessage(xupdate.getSenderId(), "SQL ERROR!\n"
                                        + sql, MessageMenu.okAndDeleteMessage());
                            }

                        } catch (Exception e) {

                            TelegramAction.sendMessage(xupdate.getSenderId(), e.getMessage(), MessageMenu.okAndDeleteMessage());
                        }

                        break;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private static Messenger.MessageMenu menuUsers() {

            /*formato [/users][AccountType][status][pageCount]
            example /users&DELIVERYMAN&2&0
             */
            Messenger.MessageMenu menu = new Messenger.MessageMenu();
            menu.addButton("🛵 Repartidores (Conectados)", "/users&" + TelegramUser.AccountType.DELIVERYMAN + "&2&1", true);
            menu.addButton("🛵 Repartidores", "/users&" + TelegramUser.AccountType.DELIVERYMAN + "&1&0", true);
            menu.addButton("🛵 Repartidores (Inactivos)", "/users&" + TelegramUser.AccountType.DELIVERYMAN + "&0&1", true);
            menu.addButton("🍔 Restaurantes", "/users&" + TelegramUser.AccountType.BUSSINES + "&1&0", true);
            menu.addButton("🍔 Restaurantes(Inactivos)", "/users&" + TelegramUser.AccountType.BUSSINES + "&0&1", true);
            menu.addButton("⭐ Moderadores", "/users&" + TelegramUser.AccountType.MODERATOR + "&1&0", true);
            menu.addButton("⭐ Moderadores(Inactivos)", "/users&" + TelegramUser.AccountType.MODERATOR + "&0&1", true);

            return menu;

        }

        private static Messenger.MessageMenu mainMenu() {

            Messenger.MessageMenu menu = new Messenger.MessageMenu();
            menu.addButton("📗 Registrar", "/register", true);
            menu.addButton("🗒 Mis ordenes", "/myorders", true);
            menu.addButton("🕰 Historial (Hoy)", "/mytodayhistory", true);
            menu.addButton("👤 Usuarios", "/users", true);
            menu.addButton("💳 Mi cartera", "/mywallet", true);
            menu.addButton("💻 Base de Datos", "/database", true);
            menu.addButton("⚙ Configuracion", "/config", true);
            menu.addButton("🧪 Pruebas", "/testing", true);
            menu.addButton("❇ Extras", "/extra", true);

            return menu;

        }

    }

    private static class groupChatDeliveriesCommands {

        private static void execute(Xupdate xupdate) {

            TelegramAction ta = new TelegramAction();

            Group group = xupdate.getTelegramUser().getGroup();
            OperationAreaMagnament oam = group.getOam();
            DeliveryMan deliveryMan = xupdate.getCallerTelegramUser().getDeliveryMan();
            
              if (deliveryMan == null) {

                ta.setChatId(xupdate.getFromId());
                ta.setText("No se econtro cuenta asociada a este Usuario de telegram!\n\n"
                        + xupdate.getSenderId().toString());
               
                ta.execute();
                return;
            }
            
            
             if (!deliveryMan.isActive()) {
                ta.setChatId(xupdate.getFromId());
                ta.setText("⚠️Tu cuenta esta inactiva posibles razones:"
                        + "\nInactividad"
                        + "\nSaldo pendiente"
                        + "\npara mas informacion contacte a un moderador.");
                
                ta.execute();
                return;
                
            }

            switch (xupdate.getCommand().command()) {

                case "/accept":

                    try {

                        if (Global.Global().request_delivery_location) {
                            if (!deliveryMan.getSession().isSharingLocation()) {
                                oam.sendMessage(xupdate.getFromId(),
                                        deliveryMan.getName() + " comparte tu ubicacion",
                                        MessageMenu.okAndDeleteMessage());
                                break;
                            }
                        }

                        String param = xupdate.getCommand().getParam(1);
                        Order o = OrdersControl.getOrderById(param);

                        if (deliveryMan.ordersCount() >= Global.Global().delivery_max_order_count) {

                            deliveryMan.sendMessage("Ya no puedes aceptar mas ordenes!", MessageMenu.okAndDeleteMessage());
                            break;
                        }

                        if (o == null) {

                            oam.sendMessage(xupdate.getFromId(),
                                    "Esta orden ya no existe o ya fue terminada!",
                                    MessageMenu.okAndDeleteMessage());

                            break;
                        }

                        if (o.isRecolectable()) {
                            OrdersControl.setDeliveryMan(o, deliveryMan);

                            oam.editMessage(xupdate.getMessageId(),
                                    xupdate.getFromId(),
                                    o.toStringAceptedInGroup(),
                                    null);

                            deliveryMan.sendMessage(o.toString(), o.getCurrentButtonsForDeliveryMan());

                            String text = "La orden " + o.getShortId() + " ya fue aceptada por " + deliveryMan.getName();
                            /*Notificamos al grupo de moderacion de area*/
                            OperationAreaMagnament area = o.getArea();
                            area.sendMessageToModsGroup(text, MessageMenu.okAndDeleteMessage());

                            /*Notificamos al restaurant area*/
                            o.sendNotificationToBussiness(text, MessageMenu.okAndDeleteMessage());

                        } else {
                            oam.sendMessage(xupdate.getFromId(),
                                    "Esta orden ya fue aceptada por otro repartidor!",
                                    MessageMenu.okAndDeleteMessage());

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        // oam.sendMessage(xupdate.getFromId(), e.getMessage(), MessageMenu.okAndDeleteMessage());

                    }

                    break;

                case "/vieworder":
                    Order order = OrdersControl.getOrderById(xupdate.getCommand().getParam(1));
                    if (order != null) {
                        ta.setChatId(xupdate.getFromId());
                        ta.setEditMessageId(xupdate.getMessageId());
                        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                        ta.setText(order.toStringAceptedInGroup());
                        ta.setMenu(MessageMenu.refreshButton("/vieworder&" + order.id));
                        ta.execute();
                    } else {
                        ta.setText("Esta orden no existe");
                        ta.execute();
                    }

                    break;
                case "/info":
                    TelegramAction.sendMessage(xupdate.getFromId(), "ID del grupo:" + xupdate.getFromId(), MessageMenu.okAndDeleteMessage());
                    break;

                case "/deletemsg":

                    globalFunctions.deleteMsg(xupdate);

                    break;

            }

        }

    }

    private static class groupChatModeratorsCommands {

        private static void execute(Xupdate xupdate) {

        }

    }

}
