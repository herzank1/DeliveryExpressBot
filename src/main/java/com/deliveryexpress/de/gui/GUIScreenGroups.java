/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui;

import com.monge.tbotboot.gui.GuiMenu;
import com.monge.tbotboot.gui.GuiScreen;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.Quiz;

/**
 *
 * @author DeliveryExpress
 */
public class GUIScreenGroups extends Quiz {

    GuiScreen screen;
    String screenMsgId;

    public GUIScreenGroups(String userId) {
        super(userId);

        GuiMenu mainMenu = new GuiMenu(null, "Grupos");
        mainMenu.addItem(new GUIGroups(null, "Grupos y areas"));

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

}
