/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de;

import com.deliveryexpress.objects.TelegramUser;
import com.deliveryexpress.telegram.Xupdate;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author DeliveryExpress
 */
public class SystemSecurity {
    
    static Map<String, Integer> requestCounter = new HashMap<>();
    static int MAX_REQUEST_COUNTER = 7;

    public static void init(boolean testMode) {
        if(testMode){
        MAX_REQUEST_COUNTER = 30;
        }
        
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                
                requestCounter.clear();
            
            }
        };

        timer.schedule(timerTask, 1000,1000);
        
        System.out.println("System security Activate!");

    }
    
    
    public static boolean allowUpdate(Xupdate xupdate) {
        /*si proviene de un grupo*/
        if(xupdate.isGroupMessage()){
        return true;
        }
        
        /*si el xupdate esta expirado o ya tiene tiempo que se recibio*/
        if(xupdate.isExpired()){
        
        return false;
        }
        
        

        try {
            
            

            increaseCounter(xupdate.getSenderId(),1);

            if (requestCounter.get(xupdate.getSenderId()) > MAX_REQUEST_COUNTER) {
                DataBase.TelegramUsers.setBlackList(xupdate.getSenderId(), 1);

                return false;
            }

            return !xupdate.getTelegramUser().isBlackList();
        } catch (Exception e) {

            e.printStackTrace();
            return true;

        }

    }
    
    
    private static void increaseCounter(String telegramId,int qty) {

        try{
         requestCounter.put(telegramId, requestCounter.get(telegramId) + qty);
        }catch(Exception e){
         requestCounter.put(telegramId, 0);
        }
       

    }

}
