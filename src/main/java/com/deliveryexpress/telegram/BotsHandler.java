/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.de.database.DbBalancer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DeliveryExpress
 */
public class BotsHandler {

    static Map<String, Bot> botsList = new HashMap<>();

    public static void init() {
        List<Bot> readAll = DataBase.Accounts.Bots.Bots().readAll();
        for (Bot b : readAll) {
            System.out.println("Cargando bot "+b.getBotUsername()+" "+b.getApiKey());
           
            b.init();
            add(b);
        }

    }

    private static void add(Bot bot) {
        if (!botsList.containsKey(bot.getBotUsername())) {
            botsList.put(bot.getBotUsername(), bot);
        }
    }

}
