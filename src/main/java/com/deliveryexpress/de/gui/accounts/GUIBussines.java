/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui.accounts;

import com.deliveryexpress.objects.users.AccountStatus;
import com.deliveryexpress.objects.users.Bussines;
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
public class GUIBussines extends GuiItem {

    ArrayList<Bussines> bussines;
    PageListViewer list;
    int currentPage = 1;
    Bussines current;
    String botoomMsg = "";

    public GUIBussines(GuiElement parent, String text) {
        super(parent, text);
        bussines = Bussines.readAll(Bussines.class);
        list = new PageListViewer(bussines, 10);

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

                    current = Bussines.read(Bussines.class, command.getParam(1));
                    if (current == null) {
                        System.out.println("value " + command.getParam(1));
                        botoomMsg = "no se encontro este negocio, intente de nuevo";
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

                    String phone = command.getParam(1).replaceAll("[^0-9]", "");
                    if (phone.length() != 10) {

                        current.setPhone(phone);
                        current.update();
                        botoomMsg = "Se han guardado los datos!";
                    } else {
                        botoomMsg = "formato incorrecto!";
                    }

                    break;

                case "address":
                    current.setAddress(command.getParam(1));
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;
                case "areaid":
                    current.setAreaId(command.getParam(1));
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

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
            return "Ingrese  --view=id del negocio para ver..."
                    + "\n\n" + botoomMsg;
        } else {
            return "Ingresa --clear para limpiar pantalla.\n\n"
                    + current.toStringForTelegram()
                    + "\n\npara editar un valor ingrese el nombre del valor  = nuevo valor"
                    + " por ejemplo: name = fulano."
                    + "\n"
                    + "Valores editables\n"
                    + "-> name = value\n"
                    + "-> phone = value\n"
                    + "-> address = value\n"
                    + "-> areaid = value\n"
                    + "-> accountstatus = value\n"
                    + "\n\n" + botoomMsg;
        }
    }

    private MessageMenu getElementListMenu() {
        ArrayList<Bussines> page = this.list.getPage(currentPage);
        MessageMenu menu = new MessageMenu();
        for (Bussines b : page) {
            menu.addButton(b.getName(), "--view=" + b.getAccountId(), true);

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
