/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import com.deliveryexpress.de.Global;
import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.Customer;
import com.deliveryexpress.utils.Cotization;
import com.deliveryexpress.utils.Utils;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.Quiz;

/**
 *
 * @author DeliveryExpress Sub questionario para seleccionar al cliente
 */
public class SubQuizGetCustomer extends Quiz {

    QuizNewOrderAtm parent;

    Customer customer = new Customer();
    Cotization cotization;

    float deliveryCost;
    boolean finalized;

    public SubQuizGetCustomer(QuizNewOrderAtm father) {

        super(Utils.generateUniqued());
        this.parent = father;

    }

    @Override
    public void execute(Xupdate xupdate) {

        Response response = new Response(xupdate.getTelegramUser());

        switch (super.getStep()) {

            case 0:

                response.setText("Ingrese el telefono del cliente, sin guiones ni espacios (10 digitos)");
                response.execute();
                next();

                break;

            case 1:
                String phone = xupdate.getText().replaceAll("[^0-9]", "");
                if (phone.length() != 10) {

                    response.setText("el telefono debe ser de 10 digitos, ingrese de nuevo.");
                    response.execute();
                } else {
                    Customer customerByPhone = Customer.read(Customer.class, xupdate.getText());
                    if (customerByPhone != null) {
                        this.customer = customerByPhone;

                        //Cotizacion automatica//
                        sendCotization(response);

                        /*Cliente no existe*/
                    } else {
                        this.customer.setPhone(xupdate.getText());
                        response.setText("Ingrese Nombre");
                        response.execute();
                        next();

                    }

                }

                break;

            case 2:

                customer.setName(xupdate.getText().replaceAll("[^a-zA-Z\\s]", ""));

                response.setText("Ingrese la direccion");
                response.execute();
                next();

                break;

            case 3:
                /*case Cotizacion*/
                customer.setLastAddress(xupdate.getText().replaceAll("[^a-zA-Z\\s]", ""));
                //Cotizacion automatica//
                sendCotization(response);

                break;

            case 4:
                customer.setLastNote(xupdate.getText());
                this.finalized(response);

                break;

            /* (10) Confirmacion de cliente encontrado*/
            case 10:

                switch (xupdate.getText()) {

                    case "confirm":
                        
                        customer.create();

                        response.setText("Ingrese la nota, codigo de acceso, entre calles, etc.");
                        response.setMenu(MessageMenu.noNoteButton());
                        response.execute();

                        goTo(4);

                        break;

                    case "changeaddress":
                        response.setText("Ingrese la direccion");
                        response.execute();

                        goTo(3);

                        break;

                    case "putdeliverycost":

                        response.setText("Ingrese manualmente la tarifa.");
                        response.setMenu(parent.getDeliveryCostTable());
                        response.execute();
                        goTo(11);

                        break;

                }

                break;

            case 11:
                this.deliveryCost = Float.parseFloat(xupdate.getText());

                if (this.deliveryCost < parent.order.getBusssines().getContract().getKmBaseCost()) {
                    response.setText("⚠️ La tarifa no puede ser menor a " + parent.order.getBusssines().getContract().getKmBaseCost()
                            + ", ingrese la tarifa de envio que corresponda.");
                    response.execute();
                    break;
                }

                response.setText("Ingrese la nota, codigo de acceso, entre calles, etc.");
                response.setMenu(MessageMenu.noNoteButton());
                response.execute();

                goTo(4);

                break;

        }

    }

    public float getDeliveryCost() {
        return this.deliveryCost;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void finalized(Response response) {

        parent.setCustomer(customer);
        parent.setDeliveryCost(deliveryCost);
        response.setText("Ingrese el costo o valor de la Orden");
        response.execute();
        this.finalized = true;

    }

    @Override
    public boolean isFinalized() {
        return finalized;
    }

    private void sendCotization(Response response) {
        try{
         cotization = new Cotization(
                  Global.Global().city,getBussines().getAddress(), this.customer.getLastAddress());

        
        this.deliveryCost = cotization.getDeliveryCost(getBussines());

        response.setText(cotization.toStringDetails(this.parent.order.getBusssines())
                + "\nQue desea hacer?");

        response.setMenu(addressConfirmationMenu());

        response.execute();

        /* ir paso (10) Confirmacion de cliente encontrado*/
        goTo(10);
        }catch(Exception e){e.printStackTrace();}

       

    }

    private Bussines getBussines() {
        return parent.order.getBusssines();
    }

    MessageMenu addressConfirmationMenu() {

        MessageMenu menu = new MessageMenu();
        menu.addButton("✅ Confirmar", "confirm");
        menu.addNewLine();
        menu.addButton("🔀 Cambiar direccion", "changeaddress");
        menu.addNewLine();
        menu.addButton("#️⃣ Selc. Tarifa Manual", "putdeliverycost");
        menu.addNewLine();
        menu.addAbutton(new MessageMenu.Button("❌ Cerrar", "/close"), true);

        return menu;

    }

}
