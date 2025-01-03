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
 * @author DeliveryExpress ingreso de ordenes de forma manual
 */
public class QuizNewOrderManual extends Quiz {

    Order order;
    Customer customer = new Customer();

    public QuizNewOrderManual(String userId, Bussines bussines, boolean fromCotizacion) {

        super(userId);
        this.order = new Order(bussines, true);
        this.order.setBussinesUserId(userId);

    }

    @Override
    public void execute(Xupdate xupdate) {

        Response response = new Response(xupdate.getTelegramUser());

        switch (super.getStep()) {

            case 0:

                response.setText("📞 Ingrese el telefono del cliente, sin guines ni espacios (10 digitos)");
                response.execute();
                next();

                break;

            case 1:

                String phone = xupdate.getText().replaceAll("[^0-9]", "");
                if (phone.length() != 10) {

                    response.setText("el telefono debe ser de 10 digitos, ingrese de nuevo.");
                    response.execute();
                } else {
                    this.customer.setPhone(phone);
                    response.setText("👤 Ingrese Nombre");
                    response.execute();
                    next();
                }

                break;

            case 2:

                customer.setName(xupdate.getText().replaceAll("[^a-zA-Z\\s]", ""));

                response.setText("📍 Ingrese la direccion o link de google maps.");
                response.execute();
                next();

                break;

            case 3:
                /*case Cotizacion*/
                customer.setLastAddress(xupdate.getText());
                response.setText("🗓 Ingrese la nota, codigo de acceso, entre calles, etc.");
                response.setMenu(MessageMenu.noNoteButton());
                response.execute();
                next();

                break;

            case 4:

                customer.setLastNote(xupdate.getText());
                response.setText("🛵 Seleccione o Ingrese manualmente la tarifa de entrega, "
                        + "redondear los Km ejem: 4.5 = 5 km, basarse en la --> ruta <-- de google maps");
                // String navigateToUrl = Utils.GoogleMapsUtils.navigateToUrl(this.order.getBusssines().getAddress(), customer.getLastAddress());

                MessageMenu menu = new MessageMenu();
                // menu.addUrlButton("📌 ver Maps", navigateToUrl);
                menu.addRowsButtonsList(getDeliveryCostTable().getRowList());
                response.setMenu(menu);
                response.execute();
                next();

                break;

            case 5:

                try {
                    order.setCustomer(customer);
                    order.setDeliveryCost(Float.parseFloat(xupdate.getText()));

                    if (order.getDeliveryCost() < order.getBusssines().getContract().getKmBaseCost()) {
                        response.setText("⚠️ La tarifa no puede ser menor a " + order.getBusssines().getContract().getKmBaseCost()
                                + ", ingrese la tarifa de envio que corresponda.");
                        response.execute();
                        break;
                    }

                    response.setText("💴 Ingrese el Costo de orden (no signos, ni decimales) o indique 0 si ya esta pagada.");
                    response.setMenu(new MessageMenu("Ya Pagada! ", "0"));
                    response.execute();
                    next();
                } catch (Exception e) {

                    e.printStackTrace();
                    response.setText("❌ formato incorrecto, verifique. he intente de nuevo"
                            + "\n 💴 Ingrese el Costo de orden (no signos, ni decimales) o indique 0 si ya esta pagada.");

                    response.execute();

                }

                break;

            case 6:

                try {
                    order.setOrderCost(Float.parseFloat(xupdate.getText()));
                    response.setText("⏰ Ingrese tiempo de preparacion");
                    response.setMenu(preparationTimeMenu());
                    response.execute();
                    next();
                } catch (Exception e) {

                    e.printStackTrace();
                    response.setText("❌ formato incorrecto, verifique. he intente de nuevo"
                            + "\n 💴 Ingrese el Costo de orden (no signos, ni decimales) o indique 0 si ya esta pagada.");

                    response.execute();

                }

                break;

            case 7:

                try {
                    order.setPreparationTimeMinutes(Integer.parseInt(xupdate.getText()));
                    response.setText("Esta correcta la informacion❓\n\n" + order.toTelegramString());
                    response.setMenu(MessageMenu.yesNo());
                    response.execute();
                    next();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case 8:

                if (Utils.isPositiveAnswer(xupdate.getText())) {

                    this.order.setCustomer(customer);
                    OrdersControl.addNewOrder(this.order);
                    OrdersControl.onNewOrderReceivedEvent(this.order);
                    response.setAction(ResponseAction.EDIT_MESSAGE);
                    response.setEditMessageId(xupdate.getMessageId());
                    response.setText("✅ Orden recibida correctamente..."
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

    MessageMenu preparationTimeMenu() {

        MessageMenu menu = new MessageMenu();
        menu.addButton("⏱ 10 min", "10", true);
        menu.addButton("⏱ 15 min", "15", true);
        menu.addButton("⏱ 30 min", "30", true);
        menu.addButton("⏱ 45 min", "45", true);
        menu.addButton("⏱ 60 min", "60", true);

        return menu;

    }

    MessageMenu getDeliveryCostTable() {

        Bussines busssines = this.order.getBusssines();
        return busssines.getDeliveryTable();

    }

}
