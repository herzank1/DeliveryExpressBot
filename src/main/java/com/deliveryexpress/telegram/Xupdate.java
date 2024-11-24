/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.objects.Position;
import com.deliveryexpress.objects.users.TelegramUser;
import java.util.List;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class Xupdate {

    Update update;
    String botUserName;

    /***
     * 
     * @param update
     * @param botUserName incoming bot
     */
    Xupdate(Update update, String botUserName) {
        this.update = update;
        this.botUserName = botUserName;

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

    /**
     * *
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

    /**
     * *
     *
     * @return works for private and groups chats
     */
    public Position getLocation() {

        if (this.update.hasMessage() && this.update.getMessage().hasLocation()) {
            return new Position(this.update.getMessage().getLocation().getLatitude(),
                    this.update.getMessage().getLocation().getLongitude());

        } else if (this.update.hasEditedMessage() && this.update.getEditedMessage().hasLocation()) {
            return new Position(this.update.getMessage().getLocation().getLatitude(),
                    this.update.getMessage().getLocation().getLongitude());
        }

        return null;

    }
    
    /***
     * 
     * @return explicitamente el usuario que envio este mensaje 
     */
    public TelegramUser getSenderTelegramUser() {
   
        return DataBase.Accounts.getTelegramUser(this.getSenderId(), this.getBotUserName());

    }

  /***
   * 
   * @return el grupo o el usuario de este update
   */
    public TelegramUser getTelegramUser() {
        TelegramUser read = null;

        if (this.isGroupMessage()) {
            read = DataBase.Accounts.TelegramUsers().read(this.getFromId());
            
        } else {
            
            read = getSenderTelegramUser();
        }

  
        return read;
    }

  
    /**
     * *
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

    public boolean hasNewChatMember() {

        List<User> newChatMembers = this.update.getMessage().getNewChatMembers();
        return newChatMembers != null || !newChatMembers.isEmpty();

    }

    public List<User> getNewChatMembers() {

        return this.update.getMessage().getNewChatMembers();

    }

    public boolean isExpired() {

        Integer date = 0;

        if (this.update.hasCallbackQuery()) {
            date = this.update.getCallbackQuery().getMessage().getDate();

        } else if (this.update.hasMessage()) {
            date = this.update.getMessage().getDate();
        } else if (this.update.hasEditedMessage()) {

            date = this.update.getEditedMessage().getDate();

        }

        // Obtener el tiempo actual en segundos
        long currentTimestamp = System.currentTimeMillis() / 1000;

        // Comprobar si han pasado más de 10 minutos (600 segundos)
        return (currentTimestamp - date) > 600;
    }

}
