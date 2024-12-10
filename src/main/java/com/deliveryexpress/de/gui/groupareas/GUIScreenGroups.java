/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui.groupareas;

import com.monge.tbotboot.gui.GuiMenu;
import com.monge.tbotboot.gui.GuiScreen;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.Quiz;
import com.monge.tbotboot.quizes.QuizGui;

/**
 *
 * @author DeliveryExpress
 */
public class GUIScreenGroups extends QuizGui {

  
    public GUIScreenGroups(Xupdate xupdate) {
        super(xupdate.getTelegramUser());

        GuiMenu mainMenu = new GuiMenu(null, "Grupos");
        mainMenu.addItem(new GUIGroups(null, "Grupos y areas"));

         super.setMainMenu(mainMenu);
    }

 

 

}
