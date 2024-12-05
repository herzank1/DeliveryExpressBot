/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;


import com.deliveryexpress.de.Global;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.de.contability.BussinesContract;
import com.deliveryexpress.objects.users.Customer;
import com.deliveryexpress.utils.Cotization;
import com.deliveryexpress.utils.Utils;


import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.MessageMenu.Button;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.Quiz;

/**
 *
 * @author DeliveryExpress
 */
public class SubQuizCotization extends Quiz {

    QuizNewOrderAtm parent;

    Customer customer = new Customer();

    float deliveryCost;
    private Cotization cotization;
    

    public SubQuizCotization(QuizNewOrderAtm father) {

        super(Utils.generateUniqued());
        this.parent = father;

    }

    @Override
    public void execute(Xupdate xupdate) {

       Response response = new Response(xupdate.getTelegramUser());

        switch (super.getStep()) {

            case 0:

                response.setText("Ingrese la direccion");
                response.execute();
                next();

                break;

            case 1:

                /*case Cotizacion*/
                customer.setLastAddress(xupdate.getText().replaceAll("[^a-zA-Z\\s]", ""));
                //Cotizacion automatica//
                sendCotization(response);

                break;

            case 2:

                response.setText("❌ Ingrese el telefono del cliente, sin guines ni espacion (10 digitos)");
                response.execute();
                next();

                break;

            case 3:
                
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

            case 4:

                customer.setName(xupdate.getText().replaceAll("[^a-zA-Z\\s]", ""));

                response.setText("Ingrese la nota, codigo de acceso, entre calles, etc.");
                response.setMenu(MessageMenu.noNoteButton());
                response.execute();
                next();

                break;
            case 5:
                customer.setLastNote(xupdate.getText());

                finalized(response);
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
                        response.setText("Ingrese Celular");
                        response.execute();

                        goTo(3);

                        break;

                    case "changeaddress":
                        response.setText("Ingrese la direccion");
                        response.execute();

                        goTo(1);

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
                    response.setText("⚠️ La tarifa no puede ser menor a "+parent.order.getBusssines().getContract().getKmBaseCost()+
                    ", ingrese la tarifa de envio que corresponda.");
                    response.execute();
                    break;
                }
                
                
                
                response.setText("Ingrese el telefono del cliente, sin guines ni espacion (10 digitos)");
                response.execute();
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

    public void finalized(Response response) {

        parent.setCustomer(customer);
        parent.setDeliveryCost(deliveryCost);
        response.setText("Ingrese el costo o valor de la Orden");
        response.execute();
        this.setFinalized(true);

    }

    

    private void sendCotization(Response response) {
        BussinesContract contract = this.parent.order.getBusssines().getContract();

        cotization = new Cotization(
                getBussines().getAddress(), customer.getLastAddress(),Global.Global().city);

        this.deliveryCost = cotization.getDeliveryCost(contract);

       response.setText(cotization.toStringDetails(contract)
                + "\nQue desea hacer?");

        response.setMenu(addressConfirmationMenu());

        response.execute();

        /* ir paso (10) Confirmacion de cliente encontrado*/
        goTo(10);

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
        menu.addAbutton(new Button("❌ Cerrar","/close"), true);

        return menu;

    }

}
