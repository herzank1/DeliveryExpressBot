/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public  class TMR{
        
            String chatId;
            String messageId;
            String reference;

            public TMR(String chatId, String messageId, String reference) {
                this.chatId = chatId;
                this.messageId = messageId;
                this.reference = reference;
            }

      
            public void edit(String newText,MessageMenu menu){
                TelegramAction ta =  new TelegramAction();
                ta.setAction(MessageAction.EDIT_MESSAGE);
                ta.setChatId(chatId);
                ta.setMessageId(messageId);
                ta.setText(newText);
                ta.setMenu(menu);
                ta.execute();
            
            }
            
            
            public void delete(){
                TelegramAction ta =  new TelegramAction();
                ta.setAction(MessageAction.DELETE_MESSAGE);
                ta.setChatId(chatId);
                ta.setMessageId(messageId);
                ta.execute();

            }
            
            public static interface OrderMessageTypes{
            
            String PRIVATE_CHAT_DELIVERY = "PRIVATE_CHAT_DELIVERY";
            String EXTERNAL_DELIVERIES_GROUP = "EXTERNAL_DELIVERIES_GROUP";
            String INTERNAL_DELIVERIES_GROUP = "INTERNAL_DELIVERIES_GROUP";
            
            }
        
        }
        