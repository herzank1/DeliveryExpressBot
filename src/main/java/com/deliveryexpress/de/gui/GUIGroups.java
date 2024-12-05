/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui;

import com.deliveryexpress.objects.GroupArea;
import com.deliveryexpress.objects.users.Bussines;
import com.monge.tbotboot.gui.GuiElement;
import com.monge.tbotboot.gui.GuiItem;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.utils.PageListViewer;
import java.util.ArrayList;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress
 */
@EqualsAndHashCode(callSuper = false)
public class GUIGroups extends GuiItem {

    ArrayList<GroupArea> areas;
    PageListViewer list;
    int currentPage = 1;
    GroupArea current;
    String botoomMsg = "";

    public GUIGroups(GuiElement parent, String text) {
        super(parent, text);
        areas = GroupArea.readAll(GroupArea.class);
        list = new PageListViewer(areas, 10);

    }

    private void reload() {
        areas = GroupArea.readAll(GroupArea.class);
        list = new PageListViewer(areas, 10);
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

                case "--new":
                    this.current = new GroupArea();
                    this.current.create();
                    reload();
                    break;

                case "--clear":
                    this.current = null;
                    break;

                case "--page":
                    currentPage = list.getValueOf(value);
                    break;

                case "--view":

                    current = GroupArea.read(GroupArea.class, value);
                    if (current == null) {
                        System.out.println("value " + value);
                        botoomMsg = "no se encontro esta area, intente de nuevo";
                    } else {
                        botoomMsg = "";
                    }

                    break;

                case "grouid":
                case "id":
                    String prevId = current.getId();
                    current.setId(value);
                    current.update();

                    updateAllBussinesDependencies(prevId,value);
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "name":
                case "nombre":
                    current.setName(value);
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "details":
                case "detalles":
                    current.setDescription(value);
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "admin":
                case "adminid":
                    current.setAdminTelegramId(value);
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "modsgroup":
                case "groupmod":
                    current.setAreaModsGroupId(value);
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "hilo":
                case "othread":
                    current.setOrdersThreadModsGroupId(value);
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "deliveriesmaingroup":
                case "maindggroup":
                    current.setMainDeliveriesGroupId(value);
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "deliveriesgroup":
                case "dgroup":
                    current.setDeliveriesGroupId(value);
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
            return "Ingrese  --view=id del area para ver..."
                    + "\n\n" + botoomMsg;
        } else {
            return "Ingresa --clear para limpiar pantalla.\n\n"
                    + current.toStringForTelegram()
                    + "\n\npara editar un valor ingrese el nombre del valor  = nuevo valor"
                    + " por ejemplo: name = fulano."
                    + "\n" + "\n"
                    + "Valores editables\n"
                    + "-> id = text (cambiar el id afectara los negocios relacionados a esta area!)\n"
                    + "-> name = text\n"
                    + "-> details = text\n"
                    + "-> admin = Id de la cuenta del admin\n"
                    + "-> groupmod = id del grupo de admins de esta area\n"
                    + "-> othread = id del hilo de conversacion del grupo de admins\n"
                    + "-> maindggroup = id del grupo principal(internos) del area\n"
                    + "-> dgroup = id del grupo secundario\n"
                    + "\n\n" + botoomMsg;
        }
    }

    private MessageMenu getElementListMenu() {
        ArrayList<GroupArea> page = this.list.getPage(currentPage);
        MessageMenu menu = new MessageMenu();
        for (GroupArea a : page) {
            menu.addButton(a.getName(), "--view=" + a.getId(), true);

        }

        return menu;
    }

    @Override
    public MessageMenu getMenu() {
        /*merge nav buttons menu*/
        MessageMenu navMenu = list.getNavMenu("--page=", this.currentPage);
        navMenu.addButton("✴ Nuevo grupo", "--new");
        /*add deliveries list buttons menu*/
        navMenu.merge(getElementListMenu());
        return navMenu;

    }

    /***
     * Actualiza el area id de todos los negocios igual a _prev
     * @param _prev aread id
     * @param _new new area id
     */
    private void updateAllBussinesDependencies(String _prev, String _new) {
        ArrayList<Bussines> readAll = Bussines.readAll(Bussines.class);
        for (Bussines b : readAll) {

            if (b.getAreaId().equals(_prev)) {
                b.setAreaId(_new);
                b.update();
            }
        }

    }

}
