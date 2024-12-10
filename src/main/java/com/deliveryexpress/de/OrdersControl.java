/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de;

import com.deliveryexpress.de.orders.OrderLog;
import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.de.orders.Order;
import com.deliveryexpress.de.orders.OrderStatus;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.Customer;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.objects.users.Moderator;
import com.deliveryexpress.objects.users.Tuser;
import com.deliveryexpress.telegram.BussinesCommands;
import com.deliveryexpress.telegram.DeliveryManCommands;
import com.google.gson.GsonBuilder;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.ResponseAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author DeliveryExpress
 */
public class OrdersControl {

    public static List<Order> currentOrders = new ArrayList<>();
    static Map<String, DeliveryMan> deliveries;

    public static void init() {
        deliveries = DataBase.Accounts.Deliveries.Deliveries().getCache();
        loadTestOrder();
        initAtmAsignator();
    }

    public static Order getOrder(String id) {
        return currentOrders.stream().filter(c -> c.getId().equals(id))
                .findFirst().orElse(null);

    }

    private static void loadTestOrder() {
        Bussines bussines = Bussines.read(Bussines.class, "874db077-2f21-4090-ba9b-7dd211331a09");

        Customer customer = new Customer("686", "Cecy", "Av San Luis Potosí 3138", "casa");
        customer.setLastLoction("32.65112614097471, -115.5260718172052");

        Customer customer2 = new Customer("686", "bubis", "colosio", "casa");

        Order o1 = new Order(bussines, false);
        o1.setCustomer(customer);
        o1.setPreparationTimeMinutes(10);
        o1.setOrderCost(400);
        o1.setDeliveryCost(45);

        currentOrders.add(o1);

        // sendOrderToItsDeliveriesGroup(o1);
        for (int i = 0; i < 2; i++) {
            Order o2 = new Order(bussines, false);
            o2.setCustomer(customer2);
            o2.setPreparationTimeMinutes(20);
            o2.setOrderCost(100 + new Random().nextInt(401));
            o2.setDeliveryCost(150);
            addNewOrder(o2);
        }
    }

    public static void addNewOrder(Order order) {
        currentOrders.add(order);
    }

    public static void onNewOrderReceivedEvent(Order order) {

    }

    /**
     * *
     * Envia una orden al grupo secundario de repartidores
     *
     * @param o
     */
    public static void sendOrderToItsDeliveriesGroup(Order o) {
        Response.sendMessage(o.getArea().getDeliveriesGroup().getReceptor(), o.toTelegramStringBeforeTake(),
                new MessageMenu("✅ Aceptar", "/take&" + o.getId()));
    }

    public static void onNewOrderAsignedEvent(Order order) {
        if (order.getDeliveryMan() == null) {
            return;
        }

        order.setWaitingDeliveryConfirmation(true);
        Tuser tuser = Tuser.read(Tuser.class, order.getDeliveryMan().getTelegramId());
        MessageMenu menu = new MessageMenu();
        menu.addButton("✅ Aceptar", "/take&" + order.getId());
        menu.addButton("❌ Recahzar", "/reject&" + order.getId());
        Response sendMessage = Response.sendMessage(tuser.getReceptor(),
                "⚠ Nueva orden recibida (10 seg. para aceptar!)\n\n"
                + order.toTelegramString(), menu);

        startCountDown(sendMessage, order);
    }

    private static void onOrderDelivered(Order order) {

        order.getStorableOrder().create();
        order.getBusssines().getContract().executeOnOrderDelivered(order);
    }

    private static void startCountDown(Response response, Order o) {

        Timer timer = new Timer();

        // Tarea a ejecutar cuando el tiempo se haya completado
        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                if (!o.isConfirmTake()) {

                    if (o.getDeliveryMan() != null) {
                        o.getRejectedList().add(o.getDeliveryMan().getAccountId());
                    }

                    o.setDeliveryMan(null);
                    o.setWaitingDeliveryConfirmation(false);
                    response.setAction(ResponseAction.DELETE_MESSAGE);
                    response.execute();

                }

            }
        };

        // Programar la tarea para que se ejecute después de 30 segundos
        timer.schedule(tarea, 1000 * 15); // 10 segundos

    }

    public static Map<String, DeliveryMan> getDeliveries() {
        return deliveries;
    }

    private static Predicate<Order> completeOrderPredicate() {

        return c -> (c.getStatus().equals(OrderStatus.CANCELADO)
                || c.getStatus().equals(OrderStatus.ENTREGADO));

    }

    private static Predicate<Order> unCompleteOrderPredicate() {

        return c -> (!c.getStatus().equals(OrderStatus.CANCELADO)
                && !c.getStatus().equals(OrderStatus.ENTREGADO));

    }

    /**
     * *
     *
     * @param deliveryMan
     * @param finished
     * @return
     */
    public static ArrayList<Order> getOrdersOf(DeliveryMan deliveryMan, boolean finished) {

        if (finished) {
            return currentOrders.stream()
                    .filter(completeOrderPredicate())
                    .filter(c -> c.getDeliveryMan() != null)
                    .filter(c -> c.getDeliveryMan().getAccountId().equals(deliveryMan.getAccountId()))
                    .collect(Collectors.toCollection(ArrayList::new));

        } else {
            return currentOrders.stream()
                    .filter(unCompleteOrderPredicate())
                    .filter(c -> c.getDeliveryMan() != null)
                    .filter(c -> c.getDeliveryMan().getAccountId().equals(deliveryMan.getAccountId()))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

    }

    public static ArrayList<Order> getOrdersOf(Bussines bussines, boolean finished) {

        if (finished) {
            return currentOrders.stream()
                    .filter(completeOrderPredicate())
                    .filter(c -> c.getBusssines().getAccountId().equals(bussines.getAccountId()))
                    .collect(Collectors.toCollection(ArrayList::new));

        } else {
            return currentOrders.stream()
                    .filter(unCompleteOrderPredicate())
                    .filter(c -> c.getBusssines().getAccountId().equals(bussines.getAccountId()))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

    }

    /**
     * *
     *
     * @param moderator
     * @param finished
     * @return
     */
    public static ArrayList<Order> getOrdersOf(Moderator moderator, boolean finished) {

        if (finished) {
            return currentOrders.stream()
                    .filter(completeOrderPredicate())
                    .collect(Collectors.toCollection(ArrayList::new));

        } else {
            return currentOrders.stream()
                    .filter(unCompleteOrderPredicate())
                    .collect(Collectors.toCollection(ArrayList::new));
        }

    }

    private static void initAtmAsignator() {

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Ordenes en curso: " + currentOrders.size());
                System.out.println("Repartidores activos: " + getConnectedDeliveries().size());

                ordersFor:
                for (Order o : currentOrders) {
                    if (o.getDeliveryMan() == null) {

                        deliveriesFor:
                        for (DeliveryMan d : getConnectedDeliveries()) {

                            if (!o.getRejectedList().contains(d.getAccountId())
                                    && deliveryOrderCount(d) < 2
                                    && d.isConnected()) {
                                o.setDeliveryMan(d);
                                System.out.println("a " + d.getName()
                                        + " se le asigno la orden de "
                                        + o.getCustomer()
                                                .getName());
                                onNewOrderAsignedEvent(o);

                                break deliveriesFor;
                            }

                        }

                    }
                }
                
                System.out.println(new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(Global.getInstance()
                                .getLiveData()));


            }
            
            
        };

        timer.schedule(timerTask, 1000, 1000 * 30);
        
        
    }

    public static ArrayList<DeliveryMan> getConnectedDeliveries() {

        // Convertir a un ArrayList de DeliveryMan
        ArrayList<DeliveryMan> deliveryManList = new ArrayList<>();
        for (Map.Entry<String, DeliveryMan> entry : deliveries.entrySet()) {
            DeliveryMan value = entry.getValue();
            if (value.isConnected()) {
                deliveryManList.add(entry.getValue());
            }

        }

        return deliveryManList;

    }

    private static int deliveryOrderCount(DeliveryMan delivery) {
        return getOrdersOf(delivery, false).size();

    }

    public static boolean changeOrderStatus(Bussines bussines, String statusCode, String orderId) {
        try {

            Order order = getOrder(orderId);

            System.out.println("changeOrderStatus by " + bussines.getName() + " to " + orderId
                    + " set to " + statusCode);

            if (order == null) {
                throw new NullPointerException("La orden " + orderId + " es nulla.");
            }

            boolean succes = false;

            switch (statusCode) {

                case BussinesCommands.BussinesOrderChangeStatusCode.SET_READY:
                    if (order.getStatus().equals(OrderStatus.PREPARACION)) {
                        order.setStatus(OrderStatus.LISTO);
                        succes = true;
                    }

                    break;
            }

            /*registramos el evento en el log de la orden*/
            if (succes) {
                OrderLog log = new OrderLog("change status by delivery = " + statusCode, bussines.getAccountId());
                order.getLogs().add(log.toString());
            }

            return succes;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * *
     *
     * @param deliveryMan
     * @param statusCode
     * @param orderId
     */
    public static boolean changeOrderStatus(DeliveryMan deliveryMan, String statusCode, String orderId) {

        try {
            Order order = getOrder(orderId);

            System.out.println("changeOrderStatus by " + deliveryMan.getName() + " to " + orderId
                    + " set to " + statusCode);

            if (order == null) {
                throw new NullPointerException("La orden " + orderId + " es nulla.");
            }

            boolean succes = false;

            switch (statusCode) {

                case DeliveryManCommands.DeliveryOrderChangeStatusCode.ARRIVED_TO_BUSSINES:
                    if (order.getStatus().equals(OrderStatus.LISTO)
                            || order.getStatus().equals(OrderStatus.PREPARACION)) {

                        if (order.deliveryIsNearFromBussines()) {
                            order.setDeliveryManArrivedToBussines(true);
                            succes = true;
                        } else {
                            Response.sendMessage(deliveryMan.getReceptor(),
                                    "No has llegado al negocio!",
                                    MessageMenu.okAndDeleteMessage());
                            succes = false;

                        }

                    } else {
                        succes = false;
                    }

                    break;

                case DeliveryManCommands.DeliveryOrderChangeStatusCode.PICK_UP:

                    if (order.getStatus().equals(OrderStatus.LISTO)
                            || order.getStatus().equals(OrderStatus.PREPARACION)
                            && order.isDeliveryManArrivedToBussines()) {

                        if (order.deliveryIsNearFromBussines()) {
                            order.setStatus(OrderStatus.EN_CAMINO);
                            succes = true;
                        } else {
                            Response.sendMessage(deliveryMan.getReceptor(),
                                    "No estas en el negocio!",
                                    MessageMenu.okAndDeleteMessage());
                            succes = false;

                        }

                    } else {
                        succes = false;
                    }
                    break;
                case DeliveryManCommands.DeliveryOrderChangeStatusCode.ARRIVED_TO_CUSTOMER:

                    if (order.getStatus().equals(OrderStatus.EN_CAMINO)) {

                        if (order.deliveryIsNearFromCustomer()) {
                            order.setStatus(OrderStatus.EN_DOMICILIO);
                            succes = true;
                        } else {
                            Response.sendMessage(deliveryMan.getReceptor(),
                                    "No has llegado con el cliente!",
                                    MessageMenu.okAndDeleteMessage());
                            succes = false;

                        }

                    } else {

                        succes = false;
                    }
                    break;
                case DeliveryManCommands.DeliveryOrderChangeStatusCode.DELIVERED:
                    if (order.getStatus().equals(OrderStatus.EN_DOMICILIO)) {

                        if (order.deliveryIsNearFromCustomer()) {
                            order.setStatus(OrderStatus.ENTREGADO);
                            /*false ya que en esta posicion la orden no esta en currents*/
                            succes = true;
                            onOrderDelivered(order);

                        } else {
                            Response.sendMessage(deliveryMan.getReceptor(),
                                    "No estas con el cliente!",
                                    MessageMenu.okAndDeleteMessage());
                            succes = false;

                        }

                    } else {

                        succes = false;
                    }
                    break;
            }

            /*registramos el evento en el log de la orden*/
            if (succes) {
                OrderLog log = new OrderLog("change status by delivery = " + statusCode, deliveryMan.getAccountId());
                order.getLogs().add(log.toString());
            }

            return succes;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * *
     * Repartidor acepta la orden
     *
     * @param o
     * @param deliveryMan
     * @return
     */
    public static boolean deliveryManTakeOrder(Order o, DeliveryMan deliveryMan) {

        if (o.getDeliveryMan() == null || o.getDeliveryMan() == deliveryMan) {
            o.setDeliveryMan(deliveryMan);
            o.setConfirmTake(true);
            o.setWaitingDeliveryConfirmation(false);

            return true;
        } else {
            return false;
        }

    }

    /**
     * *
     * Repartidor rechaza la orden
     *
     * @param o
     * @param deliveryMan
     * @return
     */
    public static boolean deliveryManRejectOrder(Order o, DeliveryMan deliveryMan) {
        o.setDeliveryMan(null);
        o.setWaitingDeliveryConfirmation(false);
        o.setConfirmTake(false);
        o.getRejectedList().add(deliveryMan.getAccountId());

        return true;
    }

    public static Order extractAndGetOrderFromText(String text) {
        String regex = "Id: (\\w+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String id = matcher.group(1);
            for (Order o : currentOrders) {
                if (o.getShortId().equals(id)) {
                    return o;
                }
            }

        } else {
            return null;

        }

        return null;

    }

}
