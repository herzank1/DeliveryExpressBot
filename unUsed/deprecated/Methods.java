/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deprecated;

import com.deliveryexpress.telegram.MessageMenu;
import com.deliveryexpress.telegram.TelegramAction;

/**
 *
 * @author DeliveryExpress
 */
public class Methods {
    
    
    public void sendMessageToModsGroup(String text, MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setChatId(this.areaModsGroupId);
        ta.setText(text);
        ta.setMenu(menu);
        ta.execute();

    }

    public TelegramAction sendMessageToDeliveiesMainGroup(String text, MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setChatId(this.mainDeliveriesGroupId);
        ta.setText(text);
        ta.setMenu(menu);
        return ta.execute();

    }

    public TelegramAction sendMessageToDeliveiesGroup(String text, Messenger.MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setChatId(this.deliveriesGroupId);
        ta.setText(text);
        ta.setMenu(menu);
        return ta.execute();

    }
    
      public TelegramAction editMessageToDeliveiesMainGroup(String messageID,String text, Messenger.MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
        ta.setChatId(this.mainDeliveriesGroupId);
        ta.setMessageId(messageID);
        ta.setText(text);
        ta.setMenu(menu);
        return ta.execute();

    }

    public TelegramAction editMessageToDeliveiesGroup(String messageID,String text, Messenger.MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setChatId(this.deliveriesGroupId);
        ta.setMessageId(messageID);
         ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
        ta.setText(text);
        ta.setMenu(menu);
        return ta.execute();

    }
    
        
    public void sendMessage(String chatId, String text, Messenger.MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setChatId(chatId);
        ta.setText(text);
        ta.setMenu(menu);
        ta.execute();

    }
    
    public void editMessage(String messageID, String chatId, String text, Messenger.MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
        ta.setChatId(chatId);
        ta.setEditMessageId(messageID);
        ta.setText(text);
        ta.setMenu(menu);
        ta.execute();

    }

    
    
    public MessageMenu getTelegramUserMenuForMod() {
        
        String accountNumber = this.getBalanceAccount().getAccountNumber();
        MessageMenu menu = new MessageMenu();

        if (this.isBlackList()) {
            menu.addAbutton(new Button("✅ Quitar de la Lista negra", "/blacklist&" + this.id + "&0"), true);
        } else {
            menu.addAbutton(new Button("🚫 Agregar a la Lista negra", "/blacklist&" + this.id + "&1"), true);
        }
       // menu.addAbutton(new Button("🤖 Set Bot", "/setbot&" + this.id), true);
        menu.addAbutton(new Button("💵 Transferir", "/transfer&" + accountNumber), true);
        menu.addAbutton(new Button("🔀 Movimientos", "/movements&" + accountNumber), true);

        String status = this.getAccountStatus();
        switch (status) {
            case TelegramUser.AccountStatus.ACTIVE:
                menu.addAbutton(new Button("❌ DESACTIVAR", "/setstatus&" + this.id + "&" + TelegramUser.AccountStatus.INACTIVE), true);
                break;
            case TelegramUser.AccountStatus.PAUSED:
            case TelegramUser.AccountStatus.INACTIVE:
                menu.addAbutton(new Button("✅ ACTIVAR", "/setstatus&" + this.id + "&" + TelegramUser.AccountStatus.ACTIVE), true);
                break;
        }
        
        if(this.isBussines()){
         Bussines bussines = this.getBussines();
         menu.addAbutton(new Button("🌐 Cambiar Area", "/changearea&" + bussines.accountId), true);

        }
        
        
        return menu;
    }


    
    
}
