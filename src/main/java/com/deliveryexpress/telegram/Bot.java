/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.DataBase;
import com.deliveryexpress.de.SystemSecurity;
import com.deliveryexpress.de.UserInteracionHandlers;
import com.deliveryexpress.objects.Tags;
import com.deliveryexpress.objects.TelegramUser;
import com.deliveryexpress.utils.Utils;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 *
 * @author DeliveryExpress
 */
public class Bot extends TelegramLongPollingBot {

    String userName, apiKey,status,tags;
    public static int MAX_MSG_PER_SECOND = 30; //telegram Api allow 30 msg per seconds
    public static int MAX_MSG_PER_SECOND_TO_SAME_GROUP = 20;//telegram Api allow 20 msg per minute / one msg each 3 seconds

    public Bot(String userName, String apiKey) {
        this.userName = userName;
        this.apiKey = apiKey;

    }

    public Bot(String userName, String apiKey, String status, String tags) {
        this.userName = userName;
        this.apiKey = apiKey;
        this.status = status;
        this.tags = tags;
    }
    

    public boolean init() {
        TelegramBotsApi telegramBotsApi;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            BotSession registerBot = telegramBotsApi.registerBot(this);
            System.out.println(this.userName+" running:"+registerBot.isRunning());
            return true;
        } catch (TelegramApiException ex) {
            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;

    }
    


    @Override
    public void onUpdateReceived(Update update) {
        
       
        Xupdate xupdate = new Xupdate(update, this.getBotUsername());
        
        System.out.println("[UPDATE]\n"+Utils.toJsonString(update));
        System.out.println("[TELEGRAM_USER]\n"+Utils.toJsonString(xupdate.getTelegramUser()));

        
        if (SystemSecurity.allowUpdate(xupdate)) {
            //System.out.println("User:"+xupdate.getSenderId());
            UserInteracionHandlers.execute(xupdate);
        }else{
        
       // System.out.println("Blocked user:"+xupdate.getSenderId());
        }

    }

    @Override
    public String getBotUsername() {
        return this.userName;
    }

    @Override
    public String getBotToken() {
        return this.apiKey;
    }
    
    public Tags getTags() {
            return new Tags(this.tags);
        }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public boolean isTestBot(){
    return getTags().contains(Tags.BotTags.IS_TEST_BOT);
    }

    
    public static interface BotStatus{
    
        String ACTIVE = "ACTIVE";
        String INACTIVE = "INACTIVE";
    
    }
    
}
