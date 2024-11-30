/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.objects.users.AccountStatus;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.objects.users.Tuser;
import com.monge.tbotboot.commands.Command;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;


/**
 *
 * @author DeliveryExpress
 */
class DeliveryManCommands {

    static void execute(Xupdate xupdate) {
        
        Tuser user = Tuser.read(Tuser.class, xupdate.getSenderId());
        DeliveryMan deliveryMan = user.getDeliveryMan();
        
        if (deliveryMan.getAccountStatus().equals(AccountStatus.PAUSED)) {
            Response.sendMessage(xupdate.getTelegramUser(), "Tu cuenta esta en proceso de aprovacion."
                    + "\n contacte a un moderador para mas informacion.", null);
            return;

        }
        
          if (deliveryMan.getAccountStatus().equals(AccountStatus.INACTIVE)) {
            Response.sendMessage(xupdate.getTelegramUser(), "Tu cuenta esta Inactiva!", null);
            return;

        }
   
        Command command = xupdate.getCommand();
        switch(command.command()){
        
            case "/menu":
            case "/start":
                BalanceAccount read = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(deliveryMan.getBalanceAccountNumber());
                Response.editMessage(xupdate.getTelegramUser(), xupdate.getMessageId(),
                        "Repartidor " + deliveryMan.getName()
                        + "\nID:" + user.getId()
                        + "\nCartera:" + read.getBalance() + "$", getMenu());

                break;


                
            case "/delete_msg":
                Response.deleteMessage(xupdate);
                
                break;
        
        }
    
    }
    
    private static MessageMenu getMenu(){
        
        MessageMenu menu = new MessageMenu();
            menu.addButton("🗒 Mis ordenes", "/myorders", true);
            menu.addButton("🕰 Historial (Hoy)", "/mytodayhistory", true);
            menu.addButton("💳 Mi cartera", "/mywallet", true);
            menu.addButton("❓ Soporte", "/support", true);
            menu.addButton("♻ Actualizar", "/menu", true);
        
        return menu;
    
    }
    
    

}
