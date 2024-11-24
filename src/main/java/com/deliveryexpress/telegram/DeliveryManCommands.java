/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.de.database.DbBalancer;
import com.deliveryexpress.objects.users.AccountStatus;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.objects.users.TelegramUser;
import com.deliveryexpress.quizes.QuizNewDeliveryMan;
import com.deliveryexpress.quizes.QuizesControl;

/**
 *
 * @author DeliveryExpress
 */
class DeliveryManCommands {

    static void execute(Xupdate xupdate) {
        
        TelegramUser user = xupdate.getSenderTelegramUser();
        DeliveryMan deliveryMan = user.getDeliveryMan();
        
        if (deliveryMan.getAccountStatus().equals(AccountStatus.PAUSED)) {
            Response.sendMessage(user, "Tu cuenta esta en proceso de aprovacion."
                    + "\n contacte a un moderador para mas informacion.", null);
            return;

        }
        
          if (deliveryMan.getAccountStatus().equals(AccountStatus.INACTIVE)) {
            Response.sendMessage(user, "Tu cuenta esta Inactiva!", null);
            return;

        }
   
        Command command = xupdate.getCommand();
        switch(command.command()){
        
            case "/menu":
            case "/start":
                BalanceAccount read = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(deliveryMan.getBalanceAccountNumber());
                Response.editMessage(xupdate.getSenderTelegramUser(), xupdate.getMessageId(),
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
