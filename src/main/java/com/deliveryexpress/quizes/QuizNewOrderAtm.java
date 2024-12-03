/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import com.deliveryexpress.de.OrdersControl;
import com.deliveryexpress.de.orders.Order;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.Customer;
import com.deliveryexpress.telegram.BussinesCommands;
import com.deliveryexpress.utils.Utils;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.ResponseAction;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.Quiz;

/**
 *
 * @author DeliveryExpress
 */
public class QuizNewOrderAtm extends Quiz {

    Order order;
    Quiz subQuiz;

    /**
     * *
     *
     * @param userId
     * @param bussines
     * @param fromCotizacion ingresar tu si este quiz es para cotizar y false,
     * para cargar la informacion del cliente
     */
    public QuizNewOrderAtm(String userId, Bussines bussines, boolean fromCotizacion) {

        super(userId);
        this.order = new Order(bussines, false);

        if (fromCotizacion) {
            subQuiz = new SubQuizCotization(this);
        } else {
            subQuiz = new SubQuizGetCustomer(this);
        }

    }

    @Override
    public void execute(Xupdate xupdate) {

        Response response = new Response(xupdate.getTelegramUser());
        
        System.out.println("parent step = "+this.getStep()+" son step = "+subQuiz.getStep());

        if (!subQuiz.isFinalized()) {
            subQuiz.execute(xupdate);
        } else {

            switch (super.getStep()) {

                case 0:

                    order.setOrderCost(Float.parseFloat(xupdate.getText()));
                    response.setText("Ingrese tiempo de preparacion");
                    response.setMenu(preparationTimeMenu());
                    response.execute();
                    next();

                    break;

                case 1:

                    order.setPreparationTimeMinutes(Integer.parseInt(xupdate.getText()));
                    response.setText("Esta correcta la informacion?\n\n" + order.toTelegramString());
                    response.setMenu(MessageMenu.yesNo());
                    response.execute();
                    next();

                    break;

                case 2:

                    if (Utils.isPositiveAnswer(xupdate.getText())) {

                        OrdersControl.addNewOrder(this.order);
                        OrdersControl.onNewOrderReceivedEvent(this.order);
                        response.setAction(ResponseAction.EDIT_MESSAGE);
                        response.setEditMessageId(xupdate.getMessageId());
                        response.setText("Orden recibida correctamente..."
                                + "\n\n" + this.order.toTelegramString());
                        response.setMenu(BussinesCommands.getCurrentOrdersBussinesMenu(order));
                        response.execute();

                        this.destroy();

                    } else {
                        response.setAction(ResponseAction.EDIT_MESSAGE);
                        response.setEditMessageId(xupdate.getMessageId());
                        response.setText("🚫 Orden cancelada");
                        response.setMenu(MessageMenu.okAndDeleteMessage());
                        response.execute();

                        this.destroy();
                    }

                    break;

            }

        }

    }

    MessageMenu preparationTimeMenu() {

        MessageMenu menu = new MessageMenu();
        menu.addButton("10 min", "10", true);
        menu.addButton("15 min", "15", true);
        menu.addButton("30 min", "30", true);
        menu.addButton("45 min", "45", true);
        menu.addButton("60 min", "60", true);

        return menu;

    }

    void setCustomer(Customer customer) {

        order.setCustomer(customer);

    }

    void setDeliveryCost(float deliveryCost) {
        this.order.setDeliveryCost(deliveryCost);
    }

    MessageMenu getDeliveryCostTable() {

        Bussines busssines = this.order.getBusssines();
        return busssines.getDeliveryTable();

    }

}
