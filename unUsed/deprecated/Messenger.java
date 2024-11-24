/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deprecated;

import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.telegram.Bot;
import com.deliveryexpress.telegram.MessageAction;
import com.deliveryexpress.telegram.TelegramAction;
import com.deliveryexpress.utils.Utils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author DeliveryExpress
 * Esta clase executa el Telegram action o envia el mensaje
 */
public class Messenger {
    
    static ArrayList<Bot> bots = new ArrayList<>();
    
    public static void init() {
        /*Cargamos e iniamos los bots de la base de datos*/
        bots = (ArrayList<Bot>) DataBase.Accounts.Bots.Bots().readAll();
        for (Bot b : bots) {
            b.init();
        }
        
        System.out.println("Messenger Ok, total bots loaded:" + bots.size());
    }
    
    public static Bot getBotByName(String botUserName) {
        Bot orElse = bots.stream().filter(c -> c.getBotUsername().equals(botUserName)).findFirst().orElse(null);
        if(orElse==null ||botUserName==null){
        
        return bots.getFirst();
        }
     return orElse;   
    }
    
    public static TelegramAction execute(TelegramAction ta) {
        System.out.println(Utils.toJsonString(ta));
        
        try {
            
            
            try {
                if (ta.toGroup) {
                    Thread.sleep(3000);
                } else {
                    Thread.sleep(1000 / 30);
                }
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Messenger.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Bot botOut = ta.getBotOut();
            
            System.out.println("TelegramAction - bot "+botOut.getBotUsername());
            
            switch (ta.getAction()) {
                
                case MessageAction.SEND_MESSAGE:
                    
                    SendMessage sm = new SendMessage();
                    sm.setChatId(ta.getChatId());
                    sm.setText(ta.getText());
                    if (ta.isHtml()) {
                        sm.enableHtml(true);
                    }
                    if (ta.getThreadId() != null) {
                        sm.setMessageThreadId(Integer.valueOf(ta.getThreadId()));
                        
                    }
                    
                    if (ta.getMenu() != null) {
                        sm.setReplyMarkup(ta.getMenu().getReplyKeyboard());
                        
                    }
                    
                     {
                        try {
                            Message execute = botOut.execute(sm);
                            
                            ta.setMessageId(String.valueOf(execute.getMessageId()));
                            
                        } catch (TelegramApiException ex) {
                            Logger.getLogger(Messenger.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Error al ejecutar el TelegramAction:\n" + Utils.toJsonString(ta));
                        }
                    }
                    
                    break;
                
                case MessageAction.EDIT_MESSAGE:
                    try {
                        EditMessageText emt = new EditMessageText();
                        emt.setMessageId(Integer.valueOf(ta.getEditMessageId()));
                        emt.setChatId(ta.getChatId());
                        emt.setText(ta.getText());
                        if (ta.isHtml()) {
                            emt.enableHtml(true);
                        }

                        if (ta.getMenu() != null) {
                            emt.setReplyMarkup((InlineKeyboardMarkup) ta.getMenu().getReplyKeyboard());

                        }

                        Serializable execute = botOut.execute(emt);

                        ta.setMessageId(String.valueOf(execute.toString()));

                    } catch (TelegramApiException ex) {
                        String message = ex.getMessage();
                        Logger.getLogger(Messenger.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Error al ejecutar el TelegramAction:\n" + Utils.toJsonString(ta));
                        System.out.println("Message Id:" + ta.getEditMessageId());

                        if (message.equals(TelegramApiExecptionsMessage.CANT_EDIT_MSG)) {
                            ta.setAction(MessageAction.SEND_MESSAGE);
                            ta.execute();

                        }
                    }

                    
                    break; 
                    
                 case MessageAction.DELETE_MESSAGE:
                    
                    DeleteMessage dm = new DeleteMessage();
                    dm.setMessageId(Integer.parseInt(ta.getMessageId()));
                    dm.setChatId(ta.getChatId());
                    
                   
                    
                     {
                        try {
                            Boolean execute = botOut.execute(dm);
                            
                            ta.setMessageId(String.valueOf(execute.toString()));
                            
                        } catch (TelegramApiException ex) {
                            Logger.getLogger(Messenger.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Error al ejecutar el TelegramAction:\n" + Utils.toJsonString(ta));
                        }
                    }
                    
                    break;    
                
            }
        } catch (Exception e) {
            System.out.println("Error al ejecutar el TelegramAction:\n" + Utils.toJsonString(ta));
            e.printStackTrace();
        }
        
        return ta;
    }

    public static Bot getDefaultBot() {
      return bots.get(0);
    
    }
    
    public static interface TelegramApiExecptionsMessage {

        String CANT_EDIT_MSG = "Error executing org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText query: [400] Bad Request: message can't be edited";

    }
    
  
   
    public static class HtmlText {
        
        @Override
        public String toString() {
            return "HtmlText{" + '}';
        }
        
    }
    
}
