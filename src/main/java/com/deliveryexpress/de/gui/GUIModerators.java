/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui;

import com.deliveryexpress.objects.users.AccountStatus;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.objects.users.Moderator;
import com.monge.tbotboot.utils.PageListViewer;
import com.monge.tbotboot.gui.GuiElement;
import com.monge.tbotboot.gui.GuiItem;
import com.monge.tbotboot.messenger.MessageMenu;
import java.util.ArrayList;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 */
@EqualsAndHashCode(callSuper = false)
public class GUIModerators extends GuiItem {

    ArrayList<Moderator> moderator;
    PageListViewer list;
    int currentPage = 1;
    Moderator current;
    String botoomMsg = "";

    public GUIModerators(GuiElement parent, String text) {
        super(parent, text);
        moderator = Moderator.readAll(Moderator.class);
        list = new PageListViewer(moderator, 10);

    }

    @Override
    public void execute(String data) {

        try {
            String[] split = data.split("=");
            String command = split[0].toLowerCase().replace(" ", "");

            String value = "";
            try {
                value = split[1];
            } catch (java.lang.IndexOutOfBoundsException e) {
                /**/
            }

            switch (command) {

                case "--clear":
                    this.current = null;
                    break;

                case "--page":
                    currentPage = list.getValueOf(value);
                    break;

                case "--view":

                    current = Moderator.read(Moderator.class, value);
                    if (current == null) {
                        System.out.println("value " + value);
                        botoomMsg = "no se encontro este repartidor, intente de nuevo";
                    } else {
                        botoomMsg = "";
                    }

                    break;

                case "name":
                    current.setName(value);
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;
                case "phone":

                    String phone = data.replaceAll("[^0-9]", "");
                    if (phone.length() != 10) {

                        current.setPhone(value);
                        current.update();
                        botoomMsg = "Se han guardado los datos!";
                    } else {
                        botoomMsg = "formato incorrecto!";
                    }

                    break;

       
                case "accountstatus":
                    String status = value.toUpperCase();
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
            return "Ingrese  --view=id del Moderador para ver..."
                    + "\n\n" + botoomMsg;
        } else {
            return "Ingresa --clear para limpiar pantalla.\n\n"
                    + current.toStringForTelegram()
                    + "\n\npara editar un valor ingrese el nombre del valor  = nuevo valor"
                    + " por ejemplo: name = fulano."
                    + "\n"
                    + "Valores editables\n"
                    + "-> name\n"
                    + "-> phone\n"
                    + "-> accountstatus\n"
                    + "\n\n" + botoomMsg;
        }
    }

    private MessageMenu getElementListMenu() {
        ArrayList<Moderator> page = this.list.getPage(currentPage);
        MessageMenu menu = new MessageMenu();
        for (Moderator m : page) {
            menu.addButton(m.getName(), "--view=" + m.getAccountId(), true);

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
