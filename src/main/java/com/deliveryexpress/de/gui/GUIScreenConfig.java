/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui;

import com.deliveryexpress.de.Global;
import com.monge.tbotboot.commands.Command;
import com.monge.tbotboot.gui.GuiElement;
import com.monge.tbotboot.gui.GuiItem;
import com.monge.tbotboot.gui.GuiMenu;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.QuizGui;

/**
 *
 * @author DeliveryExpress
 */
public class GUIScreenConfig extends QuizGui {

    public GUIScreenConfig(Xupdate xupdate) {
        super(xupdate.getTelegramUser());

        GuiMenu mainMenu = new GuiMenu(null, "Configuracion");

        mainMenu.addItem(new GUIConfigSystem(null, "Sistema"));

        super.setMainMenu(mainMenu);
    }

}

class GUIConfigSystem extends GuiItem {

    public GUIConfigSystem(GuiElement parent, String text) {
        super(parent, text);
        super.setCommandSplitter("=");
    }

    @Override
    public void execute(Xupdate xupdate) {

        Command command = new Command(xupdate.getText(), super.getCommandSplitter());

        switch (command.command()) {

            /*setCheckDeliveryPositionOnChangeStatus*/
            case "--cdpocs":
                Global.getInstance()
                        .getOrdersConfig()
                        .setCheckDeliveryPositionOnChangeStatus(command.getParamAsBoolean(1));

                break;
            /*setDelivery_max_order_count*/
            case "--dmod":
                Global.getInstance()
                        .getOrdersConfig()
                        .setDelivery_max_order_count(command.getParamAsInteger(1));
                break;
            /*setDinamic_rate*/
            case "--dr":
                Global.getInstance()
                        .getOrdersConfig()
                        .setDinamic_rate(command.getParamAsInteger(1));
                break;

            /*setRequest_delivery_location*/
            case "--rdl":
                Global.getInstance()
                        .getOrdersConfig()
                        .setRequest_delivery_location(command.getParamAsBoolean(1));
                break;

            /*setManual_order_only*/
            case "--moo":
                Global.getInstance()
                        .getOrdersConfig()
                        .setManual_order_only(command.getParamAsBoolean(1));
                break;
        }

    }

    @Override
    public MessageMenu getMenu() {
        return new MessageMenu();
    }

    @Override
    public String draw() {

        return Global.getInstance().toStringForTelegram();
                
    }


}
