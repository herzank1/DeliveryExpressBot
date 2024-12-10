/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui.wallets;

import com.deliveryexpress.de.contability.reports.BussinesDebReport;
import com.deliveryexpress.objects.users.Tuser;
import com.monge.tbotboot.commands.Command;
import com.monge.tbotboot.gui.GuiElement;
import com.monge.tbotboot.gui.GuiItem;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.utils.PageListViewer;
import com.monge.xsqlite.xsqlite.GenericDao;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DeliveryExpress
 */
public class GUIWallets extends GuiItem {

    ArrayList<Wallet> wallets;
    PageListViewer list;
    int currentPage = 1;
    Wallet current;
    String botoomMsg = "";

    /**
     * *
     *
     * @param parent
     * @param text buttonText
     * @param walletsType AccountType
     */
    public GUIWallets(GuiElement parent, String text, String walletsType) {
        super(parent, text);
        super.setCommandSplitter(" ");

        try {
            wallets = new ArrayList<>();
            GenericDao dao = Tuser.getDao(Tuser.class);
            List findByColumnList = dao.findByColumnList("accountType", walletsType);
            ArrayList<Tuser> users = (ArrayList<Tuser>) findByColumnList;

            for (Tuser user : users) {
                wallets.add(new Wallet(user));
            }
            list = new PageListViewer(wallets, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void execute(Xupdate xupdate) {
        try {

            Command command = new Command(xupdate.getText(), super.getCommandSplitter());

            switch (command.command()) {

                case "--clear":
                    this.current = null;
                    break;

                case "--page":
                    currentPage = list.getValueOf(command.getParam(1));
                    break;

                case "--view":

                    try {
                        current = new Wallet(Tuser.read(Tuser.class, command.getParam(1)));
                        if (current == null) {
                            System.out.println("value " + command.getParam(1));
                            botoomMsg = "no se encontro esta usuario, intente de nuevo";
                        } else {
                            botoomMsg = "";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        botoomMsg = e.getMessage();
                    }

                    break;

                case "send":
                case "add":
                case "sum":
                case "enviar":

                    float amount = command.getParamAsFloat(1);
                    current.balance.addFounds(amount, command.getParam(2), command.getParam(3));

                    botoomMsg = "Se han guardado los datos!";
                    break;

                case "charge":
                case "sub":
                case "cobrar":

                    amount = command.getParamAsFloat(1);
                    current.balance.subFounds(amount, command.getParam(2), command.getParam(3));

                    botoomMsg = "Se han guardado los datos!";
                    break;

             

            }

            botoomMsg = "";
        } catch (Exception e) {
            botoomMsg = e.getMessage();
        }

    }

    private MessageMenu getElementListMenu() {
        ArrayList<Wallet> page = this.list.getPage(currentPage);
        MessageMenu menu = new MessageMenu();
        for (Wallet w : page) {
            menu.addButton(w.getOwnerName(), "--view" + super.getCommandSplitter() + w.getUser().getId(), true);

        }

        return menu;
    }

    @Override
    public MessageMenu getMenu() {
        /*merge nav buttons menu*/
        MessageMenu navMenu = list.getNavMenu("--page" + super.getCommandSplitter(), this.currentPage);
        navMenu.merge(getElementListMenu());
        return navMenu;
    }

    @Override
    public String draw() {
        if (current == null) {
            return "Ingrese  --view" + super.getCommandSplitter() + "id(Telegram) del usuario para ver..."
                    + "\n\n" + botoomMsg;
        } else {
            return "Ingresa --clear para limpiar pantalla.\n\n"
                    + current.toStringForTelegram()
                    + "\n\npara editar un valor ingrese el nombre del valor" + super.getCommandSplitter() + " nuevo valor"
                    + " por ejemplo: name" + super.getCommandSplitter() + "fulano."
                    + "\n" + "\n"
                    + "Comando para transferencia - Enviar y Cobrar\n"
                    + "-> send" + super.getCommandSplitter() + "amount" + super.getCommandSplitter() + "concept" + super.getCommandSplitter() + "refe\n"
                    + "-> charge" + super.getCommandSplitter() + "amount" + super.getCommandSplitter() + "concept" + super.getCommandSplitter() + "refe\n"
          
                    + "\n\n" + botoomMsg;
        }

    }

}
