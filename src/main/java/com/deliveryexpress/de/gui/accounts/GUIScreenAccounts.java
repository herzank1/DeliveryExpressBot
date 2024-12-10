/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui.accounts;

import com.monge.tbotboot.gui.GuiMenu;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.QuizGui;

/**
 *
 * @author DeliveryExpress
 */
public class GUIScreenAccounts extends QuizGui {

  
    public GUIScreenAccounts(Xupdate xupdate) {
        super(xupdate.getTelegramUser(),true);

        GuiMenu mainMenu = new GuiMenu(null, "Accounts");

        mainMenu.addItem(new GUITuser(null, "Users"));
        mainMenu.addItem(new GUIDeliverys(null, "Repartidores"));
        
        GuiMenu bussinesMenu = new GuiMenu(null, "Accounts");
        bussinesMenu.addItem(new GUIBussines(null, "Negocios"));
        bussinesMenu.addItem(new GUIBussinesContracts(null, "Contratos"));
        
        mainMenu.addItem(bussinesMenu);

        mainMenu.addItem(new GUIModerators(null, "Moderadores"));

        super.setMainMenu(mainMenu);
    }


}
