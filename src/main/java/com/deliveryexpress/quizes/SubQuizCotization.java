/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import com.deliveryexpress.de.DataBase;
import com.deliveryexpress.de.Global;

import com.deliveryexpress.objects.TelegramUser.Bussines;
import com.deliveryexpress.objects.TelegramUser.Customer;
import com.deliveryexpress.telegram.Messenger;
import com.deliveryexpress.telegram.Messenger.MessageMenu;
import com.deliveryexpress.telegram.Messenger.MessageMenu.Button;
import com.deliveryexpress.telegram.Messenger.TelegramAction;
import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;
import com.deliveryexpress.utils.Utils.GoogleMapsUtils.Route;

/**
 *
 * @author DeliveryExpress
 */
public class SubQuizCotization extends Quiz {

    QuizNewOrderAtm father;

    Customer customer = new Customer();

    float deliveryCost;
    private Route route;
    

    public SubQuizCotization(QuizNewOrderAtm father) {

        super(Utils.generateUniqued());
        this.father = father;

    }

    @Override
    void execute(Xupdate xupdate) {

        TelegramAction ta = new TelegramAction();
        ta.setAction(TelegramAction.Action.SEND_MESSAGE);
        ta.setChatId(xupdate.getSenderId());

        switch (super.step) {

            case 0:

                ta.setText("Ingrese la direccion");
                ta.execute();
                next();

                break;

            case 1:

                /*case Cotizacion*/
                customer.setLastAddress(xupdate.getText().replace("#", "").replace(",", " "));
                //Cotizacion automatica//
                sendCotization(ta);

                break;

            case 2:

                ta.setText("❌ Ingrese el telefono del cliente, sin guines ni espacion (10 digitos)");
                ta.execute();
                next();

                break;

            case 3:
                
                if (xupdate.getText().length() < 10) {
                    ta.setText("El telefono del cliente no puede ser menor a 10 digitos,"
                            + "ingrese el telefono del cliente");
                    ta.execute();
                    break;
                }
                
                this.customer.setPhone(xupdate.getText());
                ta.setText("Ingrese Nombre");
                ta.execute();
                next();

                break;

            case 4:

                customer.setName(xupdate.getText().replace("-", "").replace(" ", ""));

                ta.setText("Ingrese la nota, codigo de acceso, entre calles, etc.");
                ta.setMenu(MessageMenu.noNoteButton());
                ta.execute();
                next();

                break;
            case 5:
                customer.setLastNote(xupdate.getText());

                finalized(ta);
                break;

            /* (10) Confirmacion de cliente encontrado*/
            case 10:

                switch (xupdate.getText()) {

                    case "confirm":

                        /*
                        ta.setText("Ingrese Nombre");
                        ta.execute();

                        goTo(4);
                        */
                        ta.setText("Ingrese Celular");
                        ta.execute();

                        goTo(3);

                        break;

                    case "changeaddress":
                        ta.setText("Ingrese la direccion");
                        ta.execute();

                        goTo(1);

                        break;

                    case "putdeliverycost":

                        ta.setText("Ingrese manualmente la tarifa.");
                        ta.setMenu(father.getDeliveryCostTable());
                        ta.execute();
                        goTo(11);

                        break;

                }

                break;

            case 11:
                
                
                this.deliveryCost = Float.parseFloat(xupdate.getText());
                
                if (this.deliveryCost < father.order.getBusssines().getKmBaseCost()) {
                    ta.setText("⚠️ La tarifa no puede ser menor a "+father.order.getBusssines().getKmBaseCost()+
                    ", ingrese la tarifa de envio que corresponda.");
                    ta.execute();
                    break;
                }
                
                
                
                ta.setText("Ingrese el telefono del cliente, sin guines ni espacion (10 digitos)");
                ta.execute();
                goTo(3);

                break;

        }

    }

    public float getDeliveryCost() {
        return this.deliveryCost;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void finalized(TelegramAction ta) {

        father.setCustomer(customer);
        father.setDeliveryCost(deliveryCost);
        ta.setText("Ingrese el costo o valor de la Orden");
        ta.execute();
        super.finalized();

    }

    

    private void sendCotization(TelegramAction ta) {

        route = new Route(
                getBussines().getAddress(), customer.getLastAddress(),Global.Global().city);

        this.deliveryCost = route.getDeliveryCost(getBussines());

        String cotizacion = String.join("\n", new String[]{
            "Direccion ingresada: " + route.getTo(),
            "Direccion encontrada (GoogleMaps): " + route.getGeoCodeTo(),
            "Distancia: " + String.format("%.2f", route.getDistance()) + " km",
            "Costo de envio: " + String.format("%.2f", route.getDeliveryCost(getBussines()))});
        //********************//

        ta.setText(cotizacion
                + "\nQue desea hacer?");

        ta.setMenu(addressConfirmationMenu());

        ta.execute();

        /* ir paso (10) Confirmacion de cliente encontrado*/
        goTo(10);

    }

    private Bussines getBussines() {
        return father.order.getBusssines();
    }

    Messenger.MessageMenu addressConfirmationMenu() {

        Messenger.MessageMenu menu = new Messenger.MessageMenu();
        menu.addButton("✅ Confirmar", "confirm");
        menu.addNewLine();
        menu.addButton("🔀 Cambiar direccion", "changeaddress");
        menu.addNewLine();
        menu.addButton("#️⃣ Selc. Tarifa Manual", "putdeliverycost");
        menu.addNewLine();
        menu.addAbutton(new Button("❌ Cerrar","/close"), true);

        return menu;

    }

}
