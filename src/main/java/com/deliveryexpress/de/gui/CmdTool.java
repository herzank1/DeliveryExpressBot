/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui;

import com.monge.tbotboot.commands.Command;
import com.monge.tbotboot.gui.CMD;
import com.monge.tbotboot.gui.GuiElement;
import com.monge.tbotboot.gui.GuiMenu;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.objects.Receptor;
import com.monge.tbotboot.quizes.QuizGui;

/**
 *
 * @author DeliveryExpress
 */
public class CmdTool extends QuizGui {

    public CmdTool(Xupdate xupdate) {
        super(xupdate.getTelegramUser());
        GuiMenu mainMenu = new GuiMenu(null, "CMD");
        mainMenu.addItem(new CmdSystem(null, "Mini CMD"));
        super.setMainMenu(mainMenu);
    }

    class CmdSystem extends CMD {

        public CmdSystem(GuiElement parent, String title) {
            super(parent, title);
        }

        
        @Override
        public String executeCmd(Command command) {
            String outPut = "comando invalido";
        
            return outPut;
        
        
        }

    }

}
