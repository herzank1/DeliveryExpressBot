/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.quizes.QuizNewBussines;
import com.deliveryexpress.quizes.QuizNewDeliveryMan;
import com.monge.tbotboot.commands.Command;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.QuizesControl;
import com.monge.tbotboot.utils.Symbols;


/**
 *
 * @author DeliveryExpress
 */
class NotRegistredCommands {

    static void execute(Xupdate xupdate) {
     
        Command command = xupdate.getCommand();
        switch(command.command()){
        
            case "/menu":
            case "/start":
                
                Response.editMessage(xupdate.getTelegramUser(),xupdate.getMessageId(), "No registrado: ID "+xupdate.getSenderId(), getMenu());
                
                break;
            
            case "/dsingin":
                
                QuizesControl.add(new QuizNewDeliveryMan(xupdate.getSenderId()));
                QuizesControl.execute(xupdate);
                
                break;
                
               case "/bsingin":
                
                QuizesControl.add(new QuizNewBussines(xupdate.getSenderId()));
                QuizesControl.execute(xupdate);
                
                break;    
                
            case "/delete_msg":
                Response.deleteMessage(xupdate);
                
                break;
        
        }
    
    }
    
    private static MessageMenu getMenu(){
        
        MessageMenu menu = new MessageMenu();
        menu.addButton(Symbols.OK+" Registrar como Repartidor", "/dsingin",true);
        menu.addButton(Symbols.OK+" Registrar como Negocio", "/bsingin",true);
        menu.addButton(Symbols.INFO+" Informacion", "/info");
        
        return menu;
    
    }
    
}
