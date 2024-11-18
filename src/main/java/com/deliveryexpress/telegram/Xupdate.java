/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.DataBase;
import com.deliveryexpress.objects.Location;
import com.deliveryexpress.objects.TelegramUser;
import com.deliveryexpress.objects.TelegramUser.Group;

import com.deliveryexpress.quizes.QuizesControl;
import com.deliveryexpress.utils.Utils;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 *
 * @author DeliveryExpress
 */
public class Xupdate {

    Update update;
    String botUserName;
    
    

    Xupdate(Update update,String botUserName) {
        this.update = update;
        this.botUserName = botUserName;
        
        /*
        String senderId = "";
        
           if (this.update.hasCallbackQuery()) {
            senderId = update.getCallbackQuery().getFrom().getId().toString();

        } else if (this.update.hasMessage()) {
            senderId = update.getMessage().getFrom().getId().toString();

        } else if (this.update.hasEditedMessage()) {
            senderId = update.getEditedMessage().getFrom().getId().toString();
        }
        */
        

    }
    
    public boolean isGroupMessage() {

        if (this.update.hasCallbackQuery()) {
            if (this.update.getCallbackQuery().getMessage().getChat().isGroupChat()
                    || this.update.getCallbackQuery().getMessage().getChat().isSuperGroupChat()) {

                return true;
            }

        } else if (this.update.hasMessage()) {
            if (this.update.getMessage().getChat().isGroupChat()
                    || this.update.getMessage().getChat().isSuperGroupChat()) {

                return true;
            }
        } else if (this.update.hasEditedMessage()) {

            if (this.update.getEditedMessage().getChat().isGroupChat()
                    || this.update.getEditedMessage().getChat().isSuperGroupChat()) {

                return true;

            }

        }

        return false;

    }
    
   
        
    /**
     * 
     * @return suer or group ids
     */
    public String getFromId() {

        if (isGroupMessage()) {

            if (this.update.hasCallbackQuery()) {
                return this.update.getCallbackQuery().getMessage().getChat().getId().toString();

            } else if (this.update.hasMessage()) {
                return this.update.getMessage().getChat().getId().toString();

            } else if (this.update.hasEditedMessage()) {
                return this.update.getEditedMessage().getChat().getId().toString();
            }

        } else {

            return getSenderId();
        }

        return "";
    }
    
    /***
     * 
     * @return sender or user Id the origin
     */
    public String getSenderId() {

        if (this.update.hasCallbackQuery()) {
            return this.update.getCallbackQuery().getFrom().getId().toString();

        } else if (this.update.hasMessage()) {
            return this.update.getMessage().getFrom().getId().toString();

        } else if (this.update.hasEditedMessage()) {
            return this.update.getEditedMessage().getFrom().getId().toString();
        }

        return "null";

    }
    
    public String getText() {

        if (this.update.hasCallbackQuery()) {
            return this.update.getCallbackQuery().getData();
        } else if (this.update.hasMessage() && this.update.getMessage().hasText()) {
            return this.update.getMessage().getText();
        } else if (this.update.hasEditedMessage() && this.update.getEditedMessage().hasText()) {
            return this.update.getEditedMessage().getText();
        }
        
        return "null";

    }
    
    
    public Command getCommand() {
        return new Command(getText());
    }
    
    public boolean hasLocation() {
        return getLocation() != null;
    }
    
    /***
     * 
     * @return works for private and groups chats
     */
    public Location getLocation(){
        
      
        
        if (this.update.hasMessage()&&this.update.getMessage().hasLocation()) {
            return new Location(this.update.getMessage().getLocation()) ;

        } else if (this.update.hasEditedMessage()&&this.update.getEditedMessage().hasLocation()) {
            return new Location(this.update.getEditedMessage().getLocation());
        }

        return null;
    
    }

    public TelegramUser getTelegramUser() {
            return DataBase.TelegramUsers.getTelegramUser(this);
    }
    
    public TelegramUser getCallerTelegramUser() {
        return DataBase.TelegramUsers.getTelegramUserById(this.getSenderId());
    }

    public boolean hasQuiz() {
    return QuizesControl.hasQuiz(getSenderId());
    }

    
       /***
     * 
     * @return works for private and groups chats
     */
    public String getMessageId() {

        if (this.update.hasMessage()) {
            return String.valueOf(this.update.getMessage().getMessageId());

        } else if (this.update.hasEditedMessage()) {
            return String.valueOf(this.update.getEditedMessage().getMessageId());
        } else if (update.hasCallbackQuery()) {

            return String.valueOf(update.getCallbackQuery().getMessage().getMessageId());
        }

        return null;

    }

   
    public String getBotName() {
        return this.botUserName;

    }

    public boolean isExpired() {
       
    return false;
    }

    public boolean hasNewChatMember() {
      
        List<User> newChatMembers = this.update.getMessage().getNewChatMembers();
        return newChatMembers!=null||!newChatMembers.isEmpty();
    
    }
    
    public List<User> getNewChatMembers(){

       return this.update.getMessage().getNewChatMembers();

    }



    
    
    public static class Command{
    
        
        String data;
        String[] parts;
        
        public static final String COMMAND_SEPARATOR = "&";

        public Command(String data) {
            this.data = data;
            if (data.startsWith("/")) {
                this.parts = data.split(COMMAND_SEPARATOR);
            }

        }

        public String getParam(int index) {

            try {

                return parts[index];

            } catch (Exception e) {
                return "";
            }

        }

        public String command() {

            if (getParam(0).isEmpty()) {
                return this.data;
            } else {
                return getParam(0);
            }

        }

        
        
        
    
    }

    
    
}
