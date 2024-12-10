/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui.accounts;

import com.deliveryexpress.de.contability.BussinesContract;
import com.monge.tbotboot.commands.Command;
import com.monge.tbotboot.gui.GuiElement;
import com.monge.tbotboot.gui.GuiItem;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.utils.PageListViewer;
import java.util.ArrayList;

/**
 *
 * @author DeliveryExpress
 */
public class GUIBussinesContracts extends GuiItem {

    ArrayList<BussinesContract> contracts;
    PageListViewer list;
    int currentPage = 1;
    BussinesContract current;
    String botoomMsg = "";

    public GUIBussinesContracts(GuiElement parent, String text) {
        super(parent, text);
        contracts = BussinesContract.readAll(BussinesContract.class);
        list = new PageListViewer(contracts, 10);
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

                    current = BussinesContract.read(BussinesContract.class, command.getParam(1));
                    if (current == null) {
                        System.out.println("value " + command.getParam(1));
                        botoomMsg = "no se encontro este contrato, intente de nuevo";
                    } else {
                        botoomMsg = "";
                    }

                    break;

                case "setKmBase":

                    int kmbase =command.getParamAsInteger(1);
                    current.setKmBase(kmbase);
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "setKmBaseCost":

                    int setKmBaseCost =command.getParamAsInteger(1);
                    current.setKmBaseCost(setKmBaseCost);
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "setKmExtraCost":

                    int setKmExtraCost =command.getParamAsInteger(1);
                    current.setKmExtraCost(setKmExtraCost);
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "setServiceType":

                    current.setServiceType(command.getParam(1));
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "setServiceCost":
                    int setServiceCost = Integer.parseInt(command.getParam(1));
                    current.setServiceCost(setServiceCost);
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "setPayCuota":
                    boolean setPayCuota = command.getParamAsBoolean(1);
                    current.setPayCuota(setPayCuota);
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "setPayCuotaMount":
                    int setPayCuotaMount = command.getParamAsInteger(1);
                    current.setPayCuotaMount(setPayCuotaMount);
                    current.update();
                    botoomMsg = "Se han guardado los datos!";

                    break;

                case "setMaxDebLimit":
                    int setMaxDebLimit = command.getParamAsInteger(1);
                    current.setMaxDebLimit(setMaxDebLimit);
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
            return "Ingrese  --view=id del contrato para ver..."
                    + "\n\n" + botoomMsg;
        } else {
            return "Ingresa --clear para limpiar pantalla.\n\n"
                    + current.toStringForTelegram()
                    + "\n\npara editar un valor ingrese el nombre del valor  = nuevo valor"
                    + " por ejemplo: name = fulano."
                    + "\n"
                    + "Valores editables\n"
                    + "-> setKmBase = value\n"
                    + "-> setKmBaseCost = value\n"
                    + "-> setKmExtraCost = value\n"
                    + "-> setServiceType = value\n"
                    + "-> setServiceCost = value\n"
                    + "-> setPayCuota boolean = value\n"
                    + "-> setPayCuotaMount = value\n"
                    + "-> setMaxDebLimit = value\n"
                    + "\n\n" + botoomMsg;
        }
    }

    private MessageMenu getElementListMenu() {
        ArrayList<BussinesContract> page = this.list.getPage(currentPage);
        MessageMenu menu = new MessageMenu();
        for (BussinesContract c : page) {
            menu.addButton(c.getId(), "--view=" + c.getId(), true);

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

    private boolean BooleanOf(String input) {

        if (input == null) {
            return false; // Por defecto, null se considera false
        }
        // Convertimos el string a minúsculas y eliminamos espacios
        input = input.trim().toLowerCase();
        // Comprobamos las posibles entradas para true
        return input.equals("true") || input.equals("1")
                || input.equals("s") || input.equals("y")
                || input.equals("yes") || input.equals("si");

    }

}
