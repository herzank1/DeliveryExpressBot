/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui.accounts;

import com.deliveryexpress.objects.users.Tuser;
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
public class GUITuser extends GuiItem {

    ArrayList<Tuser> tusers;
    PageListViewer list;
    int currentPage = 1;
    Tuser current;
    String botoomMsg = "";

    public GUITuser(GuiElement parent, String text) {
        super(parent, text);
        tusers = Tuser.readAll(Tuser.class);
        list = new PageListViewer(tusers, 10);

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

                    current = Tuser.read(Tuser.class, command.getParam(1));
                    if (current == null) {
                        System.out.println("value " + command.getParam(1));
                        botoomMsg = "no se encontro este repartidor, intente de nuevo";
                    } else {
                        botoomMsg = "";
                    }

                    break;

                case "blacklist":
                    current.setBlackList(Boolean.parseBoolean(command.getParam(1)));
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;
                case "accounttype":

                    current.setAccountType(
                            command.getParam(1));
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;
                case "accountid":
                    current.setAccountId(command.getParam(1));
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

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
            return "Ingrese  --view=id del usuario para ver..."
                    + "\n\n" + botoomMsg;
        } else {
            return "Ingresa --clear para limpiar pantalla.\n\n"
                    + current.toStringForTelegram()
                    + "\n\npara editar un valor ingrese el nombre del valor  = nuevo valor"
                    + " por ejemplo: name = fulano."
                    + "\n"
                    + "Valores editables\n"
                    + "-> blacklist= value\n"
                    + "-> accounttype= value\n"
                    + "-> accountid= value\n"
                    + "\n\n" + botoomMsg;
        }
    }

    private MessageMenu getElementListMenu() {
        ArrayList<Tuser> page = this.list.getPage(currentPage);
        MessageMenu menu = new MessageMenu();
        for (Tuser u : page) {
            menu.addButton(u.getId(), "--view=" + u.getId(), true);

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
