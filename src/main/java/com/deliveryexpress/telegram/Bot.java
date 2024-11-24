/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.SystemSecurity;
import com.deliveryexpress.objects.Tags;
import com.deliveryexpress.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.field.DatabaseField;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
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
@Data
public class Bot extends TelegramLongPollingBot {

    @DatabaseField(id = true)
    String userName;
    @DatabaseField
    String apiKey;
    @DatabaseField
    String status;
    @DatabaseField
    String tags;

    public int MAX_MSG_PER_SECOND = 30; //telegram Api allow 30 msg per seconds
    public int MAX_MSG_PER_SECOND_TO_SAME_GROUP = 20;//telegram Api allow 20 msg per minute / one msg each 3 seconds

    public Bot() {
    }

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
            System.out.println(this.userName + " running:" + registerBot.isRunning());
            return true;
        } catch (TelegramApiException ex) {
            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    @Override
    public void onUpdateReceived(Update update) {
        
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(update));

        Xupdate xupdate = new Xupdate(update, this.getBotUsername());
        
        

        if (SystemSecurity.allowUpdate(xupdate)) {
            CommandsHandlers.execute(xupdate);
        }

    }

    public Tags getTags() {
        return new Tags(this.tags);
    }

    @Override
    public String getBotUsername() {
        return userName; // Devuelve el username del bot
    }

    @Override
    public String getBotToken() {
        return apiKey; // Devuelve el token del bot
    }

}
