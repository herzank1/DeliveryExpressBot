/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui.accounts;

import com.deliveryexpress.objects.users.AccountStatus;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.monge.tbotboot.commands.Command;
import com.monge.tbotboot.utils.PageListViewer;
import com.monge.tbotboot.gui.GuiElement;
import com.monge.tbotboot.gui.GuiItem;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Xupdate;
import java.util.ArrayList;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 */
@EqualsAndHashCode(callSuper = false)
public class GUIDeliverys extends GuiItem {

    ArrayList<DeliveryMan> deliveries;
    PageListViewer list;
    int currentPage = 1;
    DeliveryMan current;
    String botoomMsg = "";

    public GUIDeliverys(GuiElement parent, String text) {
        super(parent, text);
        deliveries = DeliveryMan.readAll(DeliveryMan.class);
        list = new PageListViewer(deliveries, 10);

    }

    @Override
    public void execute(Xupdate xupdate) {

        try {
            

             Command command = new Command(xupdate.getText(), "=");

            switch (command.command()) {
                case "--clear":
                    this.current = null;
                    break;

                case "--page":
                    currentPage = list.getValueOf(command.getParam(1));
                    break;

                case "--view":

                    current = DeliveryMan.read(DeliveryMan.class, command.getParam(1));
                    if (current == null) {
                        System.out.println("value " + command.getParam(1));
                        botoomMsg = "no se encontro este repartidor, intente de nuevo";
                    } else {
                        botoomMsg = "";
                    }

                    break;

                case "name":
                    current.setName(command.getParam(1));
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;
                case "phone":

                    String phone = xupdate.getText().replaceAll("[^0-9]", "");
                    if (phone.length() != 10) {

                        current.setPhone(command.getParam(1));
                        current.update();
                        botoomMsg = "Se han guardado los datos!";
                    } else {
                        botoomMsg = "formato incorrecto!";
                    }

                    break;
                case "accountstatus":
                    String status = command.getParam(1).toUpperCase();
                    if (status.equals(AccountStatus.ACTIVE)
                            || status.equals(AccountStatus.INACTIVE)
                            || status.equals(AccountStatus.PAUSED)) {

                        current.setAccountStatus(status);
                        current.update();
                        botoomMsg = "Se han guardado los datos!";

                    } else {

                        botoomMsg = "valor invalido! permitidos [active,inactive,paused]";

                    }

                    break;

            }

            botoomMsg = "";
        } catch (Exception e) {
            botoomMsg = e.getMessage();
        }

    }

    @Override
    public String draw() {
        if (current == null) {
            return "Ingrese  --view=id del repartidor para ver..."
                    + "\n\n" + botoomMsg;
        } else {
            return "Ingresa --clear para limpiar pantalla.\n\n"
                    + current.toStringForTelegram()
                    + "\n\npara editar un valor ingrese el nombre del valor  = nuevo valor"
                    + " por ejemplo: name = fulano."
                    + "\n"
                    + "Valores editables\n"
                    + "-> name= value\n"
                    + "-> phone = value\n"
                    + "-> accountstatus = value\n"
                    + "\n\n" + botoomMsg;
        }
    }

    private MessageMenu getElementListMenu() {
        ArrayList<DeliveryMan> page = this.list.getPage(currentPage);
        MessageMenu menu = new MessageMenu();
        for (DeliveryMan d : page) {
            menu.addButton(d.getName(), "--view=" + d.getAccountId(), true);

        }

        return menu;
    }

    @Override
    public MessageMenu getMenu() {
        /*merge nav buttons menu*/
        MessageMenu navMenu = list.getNavMenu("--page=", this.currentPage);
        /*add deliveries list buttons menu*/
        navMenu.merge(getElementListMenu());
        return navMenu;

    }

}
