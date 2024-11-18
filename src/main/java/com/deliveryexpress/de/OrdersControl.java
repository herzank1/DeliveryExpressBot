/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de;

import com.deliveryexpress.objects.OperationAreaMagnament;
import com.deliveryexpress.objects.TelegramUser.Bussines;
import com.deliveryexpress.objects.TelegramUser.Customer;
import com.deliveryexpress.objects.TelegramUser.DeliveryMan;
import com.deliveryexpress.telegram.Messenger;
import com.deliveryexpress.telegram.Messenger.MessageMenu;
import com.deliveryexpress.telegram.Messenger.MessageMenu.Button;
import com.deliveryexpress.telegram.Messenger.TelegramAction;
import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;
import com.deliveryexpress.utils.Utils.DateUtils;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author DeliveryExpress
 */
public class OrdersControl {
    
    static  ArrayList<Order> currentOrders = new ArrayList<>();
    static ArrayList<Order> finishedOrders = new ArrayList<>();

    public static void addNewOrder(Order order) {
     currentOrders.add(order);
    }
    
    /***
     * 
     * @param orders
     * @param precallBackData ingresa el callback antes de el id de la orden
     * ejemplo /vieworder&orderId /viewhistoryorder&orderId
     * @return 
     */
    public static MessageMenu getOrderOfAsMenu(ArrayList<Order> orders,String precallBackData) {

        
        MessageMenu menu = new MessageMenu();
        for (Order o : orders) {
            menu.addButton("📄 "+o.getShortId() + " " + o.getCustomer().getName()+" "+o.getCurrentStatusSymbol(), precallBackData + o.getId());
            menu.addNewLine();
        }

        return menu;

    }
    
    /***
     * 
     * @param areaId
     * @return all order of an area
     */
    public static ArrayList<Order> getCurrentAreasOrder(String areaId){
    
    return (ArrayList<Order>) currentOrders
            .stream()
            .filter(c-> c.getArea().getId().equals(areaId))
            .collect(Collectors.toCollection(ArrayList::new));

    
    }
    
    public static ArrayList<Order> getOrderOf(Predicate predicate) {

        if (predicate == null) {

            return currentOrders;
        } else {
            return (ArrayList<Order>) currentOrders.stream().filter(predicate).collect(Collectors.toCollection(ArrayList::new));

        }

    }

    public static ArrayList<Order> getFinishedOrderOf(Predicate predicate) {

        if (predicate == null) {

            return finishedOrders;
        } else {
            return (ArrayList<Order>) finishedOrders.stream().filter(predicate).collect(Collectors.toCollection(ArrayList::new));

        }
        
        
        
    }

    
    /***
     * 
     * @param order 
     * Eventos al momento de recibir una nueva orden del restuarante
     * 
     */
    public static void newOrderReceivedEvent(Order o) {

        try {
            OperationAreaMagnament area = o.getBusssines().getArea();
            Messenger.TelegramAction ta = new Messenger.TelegramAction();
            ta.setToGroup(true);

            /*Enviamos la orden al grupo de internos*/
            ta.setChatId(area.getMainDeliveriesGroupId());
            ta.setText(o.toString());
            MessageMenu menu = new MessageMenu();
            menu.addButton(new Button("🟢 Aceptar", "/accept&" + o.getId()), true);
            ta.setMenu(menu);
            ta.execute();

            /*agregamos la referencia del mensaje a la orden*/
            o.addTMR(ta.getChatId(), ta.getMessageId(), Order.TMR.OrderMessageTypes.INTERNAL_DELIVERIES_GROUP);

            /*Enviamos la orden al grupo de mods*/
            ta.setChatId(area.getAreaModsGroupId());
            ta.setText(o.toStringForModsNewOrder());
            ta.setThreadId(area.getOrdersThreadModsGroupId());
            ta.setMenu(null);
            ta.execute();
        } catch (Exception e) {
            
            e.printStackTrace();
        }

    }

    static Order getOrderById(String orderId) {
     
        return currentOrders.stream().filter(c-> c.getId().equals(orderId)).findFirst().orElse(null);
    
    }

    static Order getHistoryOrderById(String orderId) {
    
         return finishedOrders.stream().filter(c-> c.getId().equals(orderId)).findFirst().orElse(null);
    
    }

    static void testOrders() {

        
        String bussinesAccout = "AID-THDP-0";
        String telegramBussinesId = "6426120337";
        Bussines b1 = DataBase.Bussiness.getBussinesByAccountId(bussinesAccout);
        Bussines b2 = DataBase.Bussiness.getBussinesByAccountId(bussinesAccout);

        Order o1 = new Order(b1,false);
        o1.setBussinesUserId(telegramBussinesId);
        o1.setCustomer(new Customer("6863095448", "Mimosha", "Av san luis potosi 3129 fracc. aztecas", ""));

        Order o2 = new Order(b1,false);
        o2.setBussinesUserId(telegramBussinesId);
        o2.setCustomer(new Customer("6863095448", "Moy", "Bahia de los angelez", "casa esquina"));

        Order o3 = new Order(b2,false);
        o3.setBussinesUserId(telegramBussinesId);
        o3.setCustomer(new Customer("686101112", "Ivan", "Av san luis potosi 3129 fracc. aztecas", ""));

        Order o4 = new Order(b2,false);
        o4.setBussinesUserId(telegramBussinesId);
        o4.setCustomer(new Customer("686546213", "Morral", "El condor", "arbol en patio"));

        OrdersControl.addNewOrder(o1);
        OrdersControl.addNewOrder(o2);
        OrdersControl.addNewOrder(o3);
        OrdersControl.addNewOrder(o4);
        
        OrdersControl.newOrderReceivedEvent(o3);
       // OrdersControl.newOrderReceivedEvent(o4);

        System.out.println("test order run...");
       // System.out.println(OrdersControl.currentOrders);

    }

    /***
     * Asigna la orden al repartidor indicado, si la orden ya cuenta con uno, se le notifica
     * @param orderById
     * @param deliveryMan 
     */
    static void modAssignOrderToDelivery(Order orderById, DeliveryMan deliveryMan) {
        
        
        if (orderById.hasDeliveryMan() && !orderById.getDeliveryMan().equals(deliveryMan)) {
            deliveryMan.sendMessage("⚠️ Tu orden " + orderById.getShortId() + " se ha asignado a otro repartidor!", MessageMenu.okAndDeleteMessage());
        }
        
        orderById.setDeliveryMan(deliveryMan);

        /*Notificamos al repartidor*/
        String text = "⚠️Se te asigno una orden⚠️\n" + orderById.toString();
        MessageMenu menu = orderById.getCurrentButtonsForDeliveryMan();
        deliveryMan.sendMessage(text, menu);
       

    }

    static void modAssignOrder(TelegramAction ta,Xupdate xupdate) {
        
      
      String orderId = xupdate.getCommand().getParam(1);
                        Order orderById = OrdersControl.getOrderById(orderId);

                        if (orderById == null) {
                            ta.setText("Esta orden ya no existe! " + orderId);
                            ta.setMenu(MessageMenu.okAndDeleteMessage());
                            ta.execute();

                            return;
                        }
                        

                        if (!xupdate.getCommand().getParam(2).isEmpty()) {

                            String deliveryId = xupdate.getCommand().getParam(2);
                            DeliveryMan deliveryMan = DeliveriesControl.getDeliveryManById(deliveryId);

                            if (deliveryMan != null) {

                                OrdersControl.modAssignOrderToDelivery(orderById, deliveryMan);

                                ta.setText("Se asigno la orden correctamente! " + orderId);
                                ta.setMenu(MessageMenu.okAndDeleteMessage());
                                ta.execute();

                            } else {
                                ta.setText("Repartidor no existe!" + deliveryId);
                                ta.setMenu(getSelectDeliveryMenu("/assign&" + orderId + "&"));
                                ta.execute();

                            }

                        } else {

                            ta.setText("Seleccione al repartidor");
                            ta.setMenu(getSelectDeliveryMenu("/assign&" + orderId + "&"));
                            ta.execute();

                        } 
    
    
    }
    
          private static MessageMenu getSelectDeliveryMenu(String preCallBackData) {

            ArrayList<DeliveryMan> connectedDeliveries = DeliveriesControl.getSharingLocationDeliveries();
            MessageMenu menu = new MessageMenu();
            for (DeliveryMan d : connectedDeliveries) {
                menu.addButton(new Button("🛵 " + d.getName(), preCallBackData + d.getAccountId()), true);
            }

            return menu;

        }

    static synchronized void setStatus(Order o, String status) {
        o.setStatus(status);
    }

    static synchronized void setDeliveryMan(Order o, DeliveryMan deliveryMan) {
        o.setDeliveryMan(deliveryMan);
    }

    static synchronized void deliveryManArrivedToBussines(Order o, boolean b) {
        o.deliveryManArrivedToBussines = b;
    }

    static synchronized void remove(Order o) {

        currentOrders.remove(o);

    }

    static synchronized void addFinishedOrder(Order o) {

        finishedOrders.add(o);
    }
    
    public static class Order{
        
        String id;
        String status;
        int preparationTimeMinutes;
        String creationDate;
        
        Bussines busssines;
        Customer customer;
        
        float orderCost;
        float deliveryCost;
        
        DeliveryMan deliveryMan;
        
        boolean deliveryManArrivedToBussines;
        boolean sentToExternal;
        
        
        String bussinesUserId;
        
        ArrayList<String>logs = new ArrayList<>();
        
        ArrayList<TMR> tmrs = new ArrayList<>();

        public Order(Bussines busssines,boolean IsManual) {
            
            if (IsManual) {
                this.id = "M-" + Utils.generateOrderId();
            } else {
                this.id = "A-" + Utils.generateOrderId();
            }

            this.status = DeliveryStatus.PREPARACION;
            this.preparationTimeMinutes = 0;
            this.creationDate = Utils.DateUtils.getNowDate();
            this.busssines = busssines;
            this.customer = null;
            this.orderCost = 0;
            this.deliveryCost = 0;
            this.deliveryMan = null;
            
            this.addLog(busssines.getName()+" - > CREATE_ORDER");
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public String getBussinesUserId() {
            return bussinesUserId;
        }

        public void setBussinesUserId(String bussinesUserId) {
            this.bussinesUserId = bussinesUserId;
        }

        public boolean isSentToExternal() {
            return sentToExternal;
        }

        public void setSentToExternal(boolean sentToExternal) {
            this.sentToExternal = sentToExternal;
        }
        
        
        
        
        public boolean isReady() {
            if (this.status.equals(DeliveryStatus.LISTO)) {
                return true;
            }

            int minutesLeft = this.preparationTimeMinutes - getElapsedMinutesFromCreation();
            //order is ready to pick up when it is 10 minutes left
            if (minutesLeft <= 10 && this.status.equals(DeliveryStatus.PREPARACION)) {
                this.status = DeliveryStatus.LISTO;
                return true;
            }
            return false;

        }
        
        public void addLog(String data) {
            this.logs.add(Utils.DateUtils.getNowDate() + " " + data);
        }
        
        public String getLogs() {

            return String.join("\n", this.logs);
        }
        
        
        public int getElapsedMinutesFromCreation() {

            long timeStamp = DateUtils.dateToUnix(this.creationDate);
            // return elapsed minutes from creation
            return (int) ((DateUtils.getUnixTimeStamp() - timeStamp) / 60);

        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getPreparationTimeMinutes() {
            return preparationTimeMinutes;
        }

        public void setPreparationTimeMinutes(int preparationTimeMinutes) {
            this.preparationTimeMinutes = preparationTimeMinutes;
        }

        public String getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(String creationDate) {
            this.creationDate = creationDate;
        }

        public Bussines getBusssines() {
            return busssines;
        }
        
        public OperationAreaMagnament getArea(){
        
            return getBusssines().getArea();
        }

        public Customer getCustomer() {
            return customer;
        }
        
        

        public void setBusssines(Bussines busssines) {
            this.busssines = busssines;
        }

        public float getOrderCost() {
            return orderCost;
        }

        public void setOrderCost(float orderCost) {
            this.orderCost = orderCost;
        }

        public float getDeliveryCost() {
            return deliveryCost;
        }

        public void setDeliveryCost(float deliveryCost) {
            this.deliveryCost = deliveryCost;
        }

        public DeliveryMan getDeliveryMan() {
            return deliveryMan;
        }

        public void setDeliveryMan(DeliveryMan deliveryMan) {
            this.deliveryMan = deliveryMan;
        }
        
        
        public boolean hasDeliveryMan() {
            return this.deliveryMan != null;
        }
        
       

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }

        public String getShortId() {
           
            return this.id.substring(this.id.length()-5, this.id.length());
        
        }
        
        public String getCurrentStatusSymbol(){
            
            String status="";
            
            switch (this.status) {

                case DeliveryStatus.PREPARACION:

                    status = "⏰🍔";
                    if (this.hasDeliveryMan()) {
                        status += "🛵";
                    }
                    break;

                case DeliveryStatus.LISTO:
                    status = "✅🍔";

                    if (this.hasDeliveryMan()) {
                        status += "🛵";
                    }
                    break;

                case DeliveryStatus.EN_CAMINO:
                    status = "🛵➡🏠️";
                    break;
                case DeliveryStatus.EN_DOMICILIO:

                    status = "🛵⏰🏠️";
                    break;

                case DeliveryStatus.ENTREGADO:
                    status = "🏠✅️";
                    break;
                case DeliveryStatus.CANCELADO:

                    status = "🚫️";
                    break;

            }

            return "";

        }

        MessageMenu getCurrentButtonsForBussines() {
            MessageMenu menu = new MessageMenu();
            switch (this.status) {

                case DeliveryStatus.PREPARACION:
                    menu.addAbutton(new Button("👍 Listo", "/setready&" + this.getId()), true);
                    menu.addRefreshButton("/vieworder&" + this.getId());
                    break;

                case DeliveryStatus.LISTO:
                case DeliveryStatus.EN_CAMINO:
                case DeliveryStatus.EN_DOMICILIO:

                    menu.addRefreshButton("/vieworder&" + this.getId());
                    menu.addSupportButton("/support&" + this.getId());

                    break;

                case DeliveryStatus.ENTREGADO:
                case DeliveryStatus.CANCELADO:

                    break;

            }
        
            return menu;
        
        }

        MessageMenu getCurrentButtonsForDeliveryMan() {
             MessageMenu menu = new MessageMenu();
        
             switch (this.status) {

                case DeliveryStatus.LISTO:
                case DeliveryStatus.PREPARACION:
                    
                    if (!deliveryManArrivedToBussines) {
                        menu.addUrlButton("📌 Navegar 🍔", this.busssines.getNavigateUrl());
                        menu.addButton("🍔 Llegue ✅", "/updatestatus&ARRIVED_TO_BUSSINES&"+this.getId());
                    } else {

                        menu.addButton("📦 Recolectar", "/updatestatus&"+Order.DeliveryStatus.EN_CAMINO+"&"+this.id);
                    }

                    break;

                case DeliveryStatus.EN_CAMINO:
                    menu.addUrlButton("📌 Navegar 🏡", this.customer.getNavigateUrl());
                    menu.addButton("🏡 Llegue ✅", "/updatestatus&"+Order.DeliveryStatus.EN_DOMICILIO+"&"+this.id);

                    break;
                case DeliveryStatus.EN_DOMICILIO:

                    menu.addButton("📦 Entregar 👤", "/updatestatus&"+Order.DeliveryStatus.ENTREGADO+"&"+this.id);

                    break;

                case DeliveryStatus.ENTREGADO:
                case DeliveryStatus.CANCELADO:

                    break;

            }
        
            return menu;
        
        }
        
        MessageMenu getCurrentButtonsForModerators() {
            
                 MessageMenu menu = new MessageMenu();
        
             switch (this.status) {

                case DeliveryStatus.LISTO:
                case DeliveryStatus.PREPARACION:
                case DeliveryStatus.EN_CAMINO:
                case DeliveryStatus.EN_DOMICILIO:
                    
                    if (!this.hasDeliveryMan() && this.sentToExternal == false) {
                        menu.addButton(new Button("➡ a Externos", "/sendtoexternal&" + this.id), true);
                    }

                    menu.addButton("🛵 Asignar", "/assign&"+this.id);
                    menu.addButton("🛑 Cancelar", "/cancelorder&"+this.id);
                    menu.addNewLine();
                    menu.addButton("⌛ Eventos", "/events&"+this.id);
                    
                    if (this.hasDeliveryMan() && this.getDeliveryMan().getSession().isSharingLocation()) {
                        menu.addUrlButton("🛵📌 Repartidor", this.getDeliveryMan().getUrlLocation());
                    }
                    
                    menu.addNewLine();
                    menu.addBackButton("/myorders");

                    break;

                case DeliveryStatus.ENTREGADO:
                case DeliveryStatus.CANCELADO:
                    
                    menu.addButton("⌛ Eventos", "/events&"+this.id);
                    menu.addBackButton("/myorders");

                    break;

            }
        
            return menu;
        
        }

        boolean isMine(DeliveryMan deliveryMan) {
            return this.deliveryMan!=null&&this.deliveryMan.getAccountId().equals(deliveryMan.getAccountId());        
        }

        boolean isRecolectable() {
           
            return !hasDeliveryMan()&&( this.status.equals(Order.DeliveryStatus.PREPARACION)
                    ||this.status.equals(Order.DeliveryStatus.LISTO));
        
        }
        
         public String getHeader() {

            return String.join("\n", new String[]{
                "ID: " + this.id,
                 "Fecha de creacion: " + this.creationDate,
                 "Preparacion en: " + String.valueOf(this.preparationTimeMinutes),
                 "Estado: " + this.status});
        }
        
        public String getFooter() {

            return String.join("\n", new String[]{"Costo de orden: "+String.valueOf(this.getOrderCost())
                    ,"Costo de Envio: "+ String.valueOf(this.deliveryCost)
            ,"Total a cobrar:"+this.getTotal()});
        }

        @Override
         public String toString(){
        
            return String.join("\n\n", new String[]{
            getHeader(),this.getBusssines().toString(),this.getCustomer().toString(),Objects.toString(this.getDeliveryMan(), "🛵 Buscando repartidor..."),getFooter()});
        }

         /*visualizar orden para el grupo de repartidores [ORDEN ACEPTADA POR UN REPARTIDOR]*/
        String toStringAceptedInGroup() {
            return String.join("\n\n", new String[]{
                getHeader(),
                 this.getBusssines().toString(),
                 Objects.toString(this.getDeliveryMan().getName(),
                 "🛵 Sin repartidor"),
                 getFooter()});

        }

        

        /*visualizar orden para el grupo de mods [EVENTO NUEVA ORDEN RECIBIDA]*/
        private String toStringForModsNewOrder() {

            return String.join("\n", new String[]{
                 "🟢Nueva orden recibida!",
                 "Id: " + this.getShortId(),
                 "Restaurante: " + this.getBusssines().getName(),
                 "Cliente: " + this.getCustomer().getName(),
                 "Area: " + this.getArea().getId(),
                 getFooter()});

        }

        void sendNotificationToBussiness(String text, MessageMenu menu) {
            
            TelegramAction ta = new TelegramAction();
            ta.setChatId(this.bussinesUserId);
            ta.setText(text);
            ta.setMenu(menu);
            ta.execute();
            
        }

        void sendNotificationToDelivery(String text, MessageMenu menu) {
            if (this.hasDeliveryMan()) {
                this.getDeliveryMan().sendMessage(text, menu);
            }

        }

        private Float getTotal() {

            return this.getOrderCost() + this.getDeliveryCost();
        }

      

        
        
        public static interface DeliveryStatus {

            String PREPARACION = "PREPARACION";
            String LISTO = "LISTO";
            String EN_CAMINO = "EN_CAMINO";
            String EN_DOMICILIO = "EN_DOMICILIO";
            String ENTREGADO = "ENTREGADO";
            String CANCELADO = "CANCELADO";

        }
        
        public void addTMR(String chatId, String messageId, String reference) {
            tmrs.add(new TMR(chatId, messageId, reference));
        }
        
        public void editTMRs(String reference,String newText,MessageMenu menu){
            
            for (TMR tmr : tmrs) {
                if (tmr.getReference().equals(reference)) {
                    tmr.edit(newText, menu);
                }
            }
        
        }
        
        public void deleteTMRs(String reference) {

            ArrayList<TMR> found = new ArrayList<>();
            
            for (TMR tmr : tmrs) {
                if (tmr.getReference().equals(reference)) {
                    tmr.delete();
                    found.add(tmr);
                }
            }
            
            tmrs.removeAll(found);

        }
        
        /*Telegram Message reference
         Objecto que representa un mensaje de telegram el cual se relaciona con objectos a travez de la referencia
        
        */
        public static class TMR{
        
            String chatId;
            String messageId;
            String reference;

            public TMR(String chatId, String messageId, String reference) {
                this.chatId = chatId;
                this.messageId = messageId;
                this.reference = reference;
            }

            public String getChatId() {
                return chatId;
            }

            public void setChatId(String chatId) {
                this.chatId = chatId;
            }

            public String getMessageId() {
                return messageId;
            }

            public void setMessageId(String messageId) {
                this.messageId = messageId;
            }

            public String getReference() {
                return reference;
            }

            public void setReference(String reference) {
                this.reference = reference;
            }
            
            public void edit(String newText,MessageMenu menu){
                TelegramAction ta =  new TelegramAction();
                ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
                ta.setChatId(chatId);
                ta.setMessageId(messageId);
                ta.setText(newText);
                ta.setMenu(menu);
                ta.execute();
            
            }
            
            
            public void delete(){
                TelegramAction ta =  new TelegramAction();
                ta.setAction(TelegramAction.Action.DELETE_MESSAGE);
                ta.setChatId(chatId);
                ta.setMessageId(messageId);
                ta.execute();

            }
            
            public static interface OrderMessageTypes{
            
            String PRIVATE_CHAT_DELIVERY = "PRIVATE_CHAT_DELIVERY";
            String EXTERNAL_DELIVERIES_GROUP = "EXTERNAL_DELIVERIES_GROUP";
            String INTERNAL_DELIVERIES_GROUP = "INTERNAL_DELIVERIES_GROUP";
            
            }
        
        }
        
    
    }
    
}
