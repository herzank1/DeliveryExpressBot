/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui;

import com.deliveryexpress.de.Global;
import com.deliveryexpress.de.OrdersControl;
import com.monge.tbotboot.gui.GuiElement;
import com.monge.tbotboot.gui.GuiItem;
import com.monge.tbotboot.gui.GuiMenu;
import com.monge.tbotboot.gui.GuiScreen;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.Quiz;

/**
 *
 * @author DeliveryExpress
 */
public class GUIScreenConfig extends Quiz {

    GuiScreen screen;
    String screenMsgId;

    public GUIScreenConfig(String userId) {
        super(userId);

        GuiMenu mainMenu = new GuiMenu(null, "Configuracion");

        mainMenu.addItem(new GUIConfigSystem(null, "Sistema"));

        screen = new GuiScreen(mainMenu);
    }

    @Override
    public void execute(Xupdate xupdate) {

        switch (super.getStep()) {

            case 0:
                Response sendMessage = Response.sendMessage(xupdate.getTelegramUser(), screen.draw(), screen.getMenu());
                screenMsgId = sendMessage.getMessageId();
                super.next();
                break;

            case 1:
                screen.execute(xupdate.getText());

                if (!xupdate.isCallBack()) {
                    Response.deleteMessage(xupdate);
                }

                Response.editMessage(xupdate.getTelegramUser(), this.screenMsgId, screen.draw(), screen.getMenu());

                break;

        }

    }

    class GUIConfigSystem extends GuiItem {

        public GUIConfigSystem(GuiElement parent, String text) {
            super(parent, text);
        }

        @Override
        public void execute(String data) {

            String[] split = data.split("=");
            String command = split[0].toLowerCase().replace(" ", "");

            String value = "";
            try {
                value = split[1];
            } catch (java.lang.IndexOutOfBoundsException e) {
                /**/
            }

            switch (command) {

                case "checkDeliveryPositionOnChangeStatus":
                case "--cdpocs":
                    Global.getInstance().getOrdersConfig() .setCheckDeliveryPositionOnChangeStatus(BooleanOf(value)) ;

                    break;
            }

        }

        @Override
        public MessageMenu getMenu() {
            return new MessageMenu();
        }

        @Override
        public String draw() {

            return "checkDeliveryPositionOnChangeStatus: " + Global.getInstance().getOrdersConfig() .isCheckDeliveryPositionOnChangeStatus();

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

}
