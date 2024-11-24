/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.objects.GroupArea;
import java.util.List;

/**
 *
 * @author DeliveryExpress
 */
public class Methods {
    
     public static  MessageMenu getAreas() {
        MessageMenu menu = new MessageMenu();
        List<GroupArea> readAll = DataBase.Accounts.GroupAreas.GroupAreas().readAll();
        menu.addButton(new MessageMenu.Button("GENERAL", "GENERAL"), true);
        for (GroupArea o : readAll) {
            menu.addButton(new MessageMenu.Button(o.getName(), o.getId()), true);
        }
        
        return menu;
        
    }
    
    
    
}
