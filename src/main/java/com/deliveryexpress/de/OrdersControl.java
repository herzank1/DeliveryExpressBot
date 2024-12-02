/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de;

import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.de.orders.Order;
import com.deliveryexpress.de.orders.OrderStatus;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.Customer;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.objects.users.Tuser;
import com.deliveryexpress.telegram.DeliveryManCommands;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.ResponseAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 *
 * @author DeliveryExpress
 */
public class OrdersControl {

    static List<Order> currentOrders = new ArrayList<>();
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
        Bussines bussines = new Bussines("La ahumadera BBQ", "+526862644096",
                "P.º de La Rumorosa 403, San Marcos, 21050 Mexicali, B.C.", "AREA1");
        bussines.setLocation("32.63350229254402, -115.49188553668682");

        Customer customer = new Customer("686", "Cecy", "Av San Luis Potosí 3138", "casa");
        customer.setLastLoction("32.65112614097471, -115.5260718172052");

        Customer customer2 = new Customer("686", "bubis", "colosio", "casa");

        Order o1 = new Order(bussines, false);
        o1.setCustomer(customer);
        o1.setPreparationTimeMinutes(10);
        o1.setOrderCost(400);
        o1.setDeliveryCost(45);

        Order o2 = new Order(bussines, false);
        o2.setCustomer(customer2);
        o2.setPreparationTimeMinutes(20);
        o2.setOrderCost(350);
        o2.setDeliveryCost(150);

        currentOrders.add(o1);
        currentOrders.add(o2);
    }

    public static void addNewOrder(Order order) {
        currentOrders.add(order);
    }

    public static void onNewOrderReceivedEvent(Order order) {

    }

    public static void onNewOrderAsignedEvent(Order order) {

        order.setWaitingDeliveryConfirmation(true);
        Tuser tuser = Tuser.read(Tuser.class, order.getDeliveryMan().getTelegramId());
        MessageMenu menu = new MessageMenu();
        menu.addButton("✅ Aceptar", "/take&" + order.getId());
        menu.addButton("❌ Recahzar", "/reject&" + order.getId());
        Response sendMessage = Response.sendMessage(tuser.getReceptor(), 
                "⚠ Nueva orden recibida (10 seg. para aceptar!)\n\n"
                        +order.toTelegramString(), menu);

        startCountDown(sendMessage,order);
    }

    private static void startCountDown(Response response, Order o) {

        Timer timer = new Timer();

        // Tarea a ejecutar cuando el tiempo se haya completado
        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                if (!o.isConfirmTake()) {

                    o.getRejectedList().add(o.getDeliveryMan().getAccountId());
                    o.setDeliveryMan(null);
                    o.setWaitingDeliveryConfirmation(false);
                    response.setAction(ResponseAction.DELETE_MESSAGE);
                    response.execute();

                }

            }
        };

        // Programar la tarea para que se ejecute después de 30 segundos
        timer.schedule(tarea, 1000*10); // 10 segundos

    }

    public static Map<String, DeliveryMan> getDeliveries() {
        return deliveries;
    }

    public static ArrayList<Order> getOrdersOf(DeliveryMan deliveryMan) {

        return currentOrders.stream()
                .filter(c -> c.getDeliveryMan() != null)
                .filter(c -> c.getDeliveryMan().getAccountId().equals(deliveryMan.getAccountId()))
                .collect(Collectors.toCollection(ArrayList::new));

    }

    private static void initAtmAsignator() {

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Ordenes en curso: " + currentOrders.size());
                System.out.println("Repartidores activos: " + deliveries.size());

                ordersFor:
                for (Order o : currentOrders) {
                    if (o.getDeliveryMan() == null) {

                        deliveriesFor:
                        for (DeliveryMan d : getConnectedDeliveries()) {

                            if (!o.getRejectedList().contains(d.getAccountId())&&deliveryOrderCount(d) < 2) {
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

            }
        };

        timer.schedule(timerTask, 1000, 1000 * 30);

    }

    private static ArrayList<DeliveryMan> getConnectedDeliveries() {

        // Convertir a un ArrayList de DeliveryMan
        ArrayList<DeliveryMan> deliveryManList = new ArrayList<>();
        for (Map.Entry<String, DeliveryMan> entry : deliveries.entrySet()) {
            // if(entry.getValue().isConnected()){
            deliveryManList.add(entry.getValue());
            //   }

        }

        return deliveryManList;

    }

    private static int deliveryOrderCount(DeliveryMan delivery) {
        return getOrdersOf(delivery).size();

    }

    /**
     * *
     *
     * @param deliveryMan
     * @param statusCode
     * @param orderId
     */
    public static boolean changeOrderStatus(DeliveryMan deliveryMan, String statusCode, String orderId) {

        Order order = getOrder(orderId);
        if (order == null) {
            return false;
        }

        switch (statusCode) {

            case DeliveryManCommands.DeliveryOrderChangeStatusCode.ARRIVED_TO_BUSSINES:
                if (order.getStatus().equals(OrderStatus.LISTO)
                        || order.getStatus().equals(OrderStatus.PREPARACION)) {
                    order.setDeliveryManArrivedToBussines(true);
                    return true;
                } else {
                    return false;
                }

            case DeliveryManCommands.DeliveryOrderChangeStatusCode.PICK_UP:

                if (order.getStatus().equals(OrderStatus.LISTO)
                        || order.getStatus().equals(OrderStatus.PREPARACION)
                        && order.isDeliveryManArrivedToBussines()) {
                    order.setStatus(OrderStatus.EN_CAMINO);

                    return true;
                } else {
                    return false;
                }

            case DeliveryManCommands.DeliveryOrderChangeStatusCode.ARRIVED_TO_CUSTOMER:

                if (order.getStatus().equals(OrderStatus.EN_CAMINO)) {
                    order.setStatus(OrderStatus.EN_DOMICILIO);
                    return true;
                } else {

                    return false;
                }

            case DeliveryManCommands.DeliveryOrderChangeStatusCode.DELIVERED:
                if (order.getStatus().equals(OrderStatus.EN_DOMICILIO)) {
                    order.setStatus(OrderStatus.ENTREGADO);
                    return true;
                } else {

                    return false;
                }

        }

        return false;

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

        o.setDeliveryMan(deliveryMan);
        o.setConfirmTake(true);
        o.setWaitingDeliveryConfirmation(false);

        return true;

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

}
