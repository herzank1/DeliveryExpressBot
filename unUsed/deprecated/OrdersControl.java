/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deprecated;

import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.de.orders.Order;
import com.deliveryexpress.objects.GroupArea;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.Customer;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.telegram.MessageMenu;
import com.deliveryexpress.telegram.MessageMenu.Button;
import deprecated.Messenger;
import com.deliveryexpress.telegram.TelegramAction;

import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.OrdersUtils;

import java.util.ArrayList;

import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author DeliveryExpress
 */
public class OrdersControl {

    static ArrayList<Order> currentOrders = new ArrayList<>();
    static ArrayList<Order> finishedOrders = new ArrayList<>();

    public static void addNewOrder(Order order) {
        currentOrders.add(order);
    }

    /**
     * *
     *
     * @param orders
     * @param precallBackData ingresa el callback antes de el id de la orden
     * ejemplo /vieworder&orderId /viewhistoryorder&orderId
     * @return
     */
    public static MessageMenu getOrderOfAsMenu(ArrayList<Order> orders, String precallBackData) {

        MessageMenu menu = new MessageMenu();
        for (Order o : orders) {
            String currentStatusSymbol = OrdersUtils.getCurrentStatusSymbol(o);
            menu.addButton("📄 " + o.getShortId() + " " + o.getCustomer().getName() + " " + currentStatusSymbol, precallBackData + o.getId());
            menu.addNewLine();
        }

        return menu;

    }

    /**
     * *
     *
     * @param areaId
     * @return all order of an area
     */
    public static ArrayList<Order> getCurrentAreasOrder(String areaId) {

        return (ArrayList<Order>) currentOrders
                .stream()
                .filter(c -> c.getBusssines().getAreaId().equals(areaId))
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

    /**
     * *
     *
     * @param order Eventos al momento de recibir una nueva orden del
     * restuarante
     *
     */
    public static void newOrderReceivedEvent(Order o) {

        try {
            GroupArea area = o.getBusssines().getGrouArea();
            TelegramAction ta = new TelegramAction();
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

        return currentOrders.stream().filter(c -> c.getId().equals(orderId)).findFirst().orElse(null);

    }

    static Order getHistoryOrderById(String orderId) {

        return finishedOrders.stream().filter(c -> c.getId().equals(orderId)).findFirst().orElse(null);

    }

    /**
     * *
     * Asigna la orden al repartidor indicado, si la orden ya cuenta con uno, se
     * le notifica
     *
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

    static void modAssignOrder(TelegramAction ta, Xupdate xupdate) {

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
        o.setDeliveryManArrivedToBussines(b);
    }

    static synchronized void remove(Order o) {

        currentOrders.remove(o);

    }

    static synchronized void addFinishedOrder(Order o) {

        finishedOrders.add(o);
    }

}
