/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import deprecated.OrdersControl;
import com.deliveryexpress.de.orders.Order;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.Customer;
import com.deliveryexpress.telegram.MessageAction;
import com.deliveryexpress.telegram.MessageMenu;



import deprecated.Messenger;
import com.deliveryexpress.telegram.TelegramAction;

import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;

/**
 *
 * @author DeliveryExpress
 * ingreso de ordenes de forma manual
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
    void execute(Xupdate xupdate) {

        TelegramAction ta = new TelegramAction();
        ta.setAction(MessageAction.SEND_MESSAGE);
        ta.setChatId(xupdate.getSenderId());

        switch (super.step) {

            case 0:

                ta.setText("📞 Ingrese el telefono del cliente, sin guines ni espacion (10 digitos)");
                ta.execute();
                next();

                break;

            case 1:

                this.customer.setPhone(xupdate.getText().replace("-", "").replace(" ", ""));
                ta.setText("👤 Ingrese Nombre");
                ta.execute();
                next();

                break;

            case 2:

                customer.setName(xupdate.getText().replace("-", "").replace(" ", ""));

                ta.setText("📍 Ingrese la direccion o link de google maps.");
                ta.execute();
                next();

                break;

            case 3:
                /*case Cotizacion*/
                customer.setLastAddress(xupdate.getText().replace("#", "").replace(",", ""));
                ta.setText("🗓 Ingrese la nota, codigo de acceso, entre calles, etc.");
                ta.setMenu(MessageMenu.noNoteButton());
                ta.execute();
                next();

                break;

            case 4:

                customer.setLastNote(xupdate.getText());
                ta.setText("🛵 Seleccione o Ingrese manualmente la tarifa de entrega, "
                        + "redondear los Km ejem: 4.5 = 5 km, basarse en la --> ruta <-- de google maps");
                String navigateToUrl = Utils.GoogleMapsUtils.navigateToUrl(this.order.getBusssines().getAddress(), customer.getLastAddress());

                MessageMenu menu = new MessageMenu();
                menu.addUrlButton("📌 ver Maps", navigateToUrl);
                menu.addRowsButtonsList(getDeliveryCostTable().getRowList());
                ta.setMenu(menu);
                ta.execute();
                next();

                break;

            case 5:

                try {
                    order.setCustomer(customer);
                    order.setDeliveryCost(Float.parseFloat(xupdate.getText()));

                    if (order.getDeliveryCost() < order.getBusssines().getKmBaseCost()) {
                        ta.setText("⚠️ La tarifa no puede ser menor a " + order.getBusssines().getKmBaseCost()
                                + ", ingrese la tarifa de envio que corresponda.");
                        ta.execute();
                        break;
                    }

                    
                    ta.setText("💴 Ingrese el Costo de orden (no signos, ni decimales) o indique 0 si ya esta pagada.");
                    ta.setMenu(new MessageMenu("Ya Pagada! ", "0"));
                    ta.execute();
                    next();
                } catch (Exception e) {

                    e.printStackTrace();
                    ta.setText("❌ formato incorrecto, verifique. he intente de nuevo"
                            + "\n 💴 Ingrese el Costo de orden (no signos, ni decimales) o indique 0 si ya esta pagada.");

                    ta.execute();

                }

                break;

            case 6:

                try {
                    order.setOrderCost(Float.parseFloat(xupdate.getText()));
                    ta.setText("⏰ Ingrese tiempo de preparacion");
                    ta.setMenu(preparationTimeMenu());
                    ta.execute();
                    next();
                } catch (Exception e) {

                    e.printStackTrace();
                    ta.setText("❌ formato incorrecto, verifique. he intente de nuevo"
                            + "\n 💴 Ingrese el Costo de orden (no signos, ni decimales) o indique 0 si ya esta pagada.");

                    ta.execute();

                }

                break;

            case 7:

                try {
                    order.setPreparationTimeMinutes(Integer.parseInt(xupdate.getText()));
                    ta.setText("Esta correcta la informacion❓\n\n" + order.toString());
                    ta.setMenu(MessageMenu.yesNo());
                    ta.execute();
                    next();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case 8:

                if (Utils.isPositiveAnswer(xupdate.getText())) {

                    this.order.setCustomer(customer);
                    OrdersControl.addNewOrder(this.order);
                    OrdersControl.newOrderReceivedEvent(this.order);
                    ta.setAction(MessageAction.EDIT_MESSAGE);
                    ta.setEditMessageId(xupdate.getMessageId());
                    ta.setText("✅ Orden recibida correctamente..."
                            + "\n\n" + this.order.toString());
                    ta.setMenu(MessageMenu.refreshButton("/refresh&" + this.order.getId()));
                    ta.execute();

                    this.destroy();

                } else {
                    ta.setText("🚫 Orden cancelada");
                    ta.setMenu(MessageMenu.okAndDeleteMessage());
                    ta.execute();

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
