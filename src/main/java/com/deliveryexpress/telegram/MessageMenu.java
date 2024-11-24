/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 *
 * @author DeliveryExpress
 */
 public  class MessageMenu {
        
      
        ReplyKeyboard replyMarkup;
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        
        public MessageMenu() {
            rowList = new ArrayList<>();
        }

        /**
         * *
         *
         * @param inEachLine add a button in new line
         * @param button
         */
        public MessageMenu(boolean inEachLine, Button... button) {
            
            for (Button b : button) {
                
                this.addButton(b);
                if (inEachLine) {
                    newLine();
                }
                
            }
        }
        
         public MessageMenu(String text, String data) {
            addButton(text,data);
        }
        
        
        
        public void addButton(String text, String callBack) {
            addButton(new Button(text, callBack));
        }
        
        public void addUrlButton(String text, String url) {
            addButton(new Button(text, url, true));
        }
        
        public void addButton(Button b, boolean newLine) {
            addButton(b);
            if (newLine) {
                newLine();
            }

        }
        
        private void addButton(Button b) {
            
            if (rowList.isEmpty()) {
                rowList.add(new ArrayList<>());
            }
            
            getLast().add(b.getIkb());
            
            
        }
        private  List<InlineKeyboardButton> getLast(){
            
            return rowList.get(rowList.size()-1);

        
        }
        
        public void newLine() {
            rowList.add(new ArrayList<>());
        }
        
        public MessageMenu addNewLine() {
            newLine();
            return this;
        }
        
        public ReplyKeyboard getReplyKeyboard() {
            
            if (replyMarkup != null) {
                
                return replyMarkup;
            } else {
                
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                inlineKeyboardMarkup.setKeyboard(rowList);
                return inlineKeyboardMarkup;
            }
            
        }
        
        public void addButton(String text, String callback, boolean b) {
            addButton(text, callback);
            if (b) {
                newLine();
            }
            
        }
        
          public static MessageMenu yesNo() {
            
            MessageMenu menu = new MessageMenu();
            menu.addButton("✅ Si", "si");
            menu.addButton("❌ No", "no");
            
            return menu;
            
        }
        
        public static MessageMenu noNoteButton() {
            
            MessageMenu menu = new MessageMenu();
            menu.addButton("Sin nota", "na");
            
            return menu;
            
        }
        
        public static MessageMenu okAndDeleteMessage() {
            
            MessageMenu menu = new MessageMenu();
            menu.addButton("✅ Ok", "/delete_msg");
            
            return menu;
            
        }
        
        public MessageMenu addAbutton(Button button, boolean newLine) {
            
            addButton(button, newLine);
            return this;
        }
        
        public MessageMenu addOkAndDeleteMessage() {
            
            this.addButton("✅ Ok", "/delete_msg");
            
            return this;
            
        }

        public List<List<InlineKeyboardButton>> getRowList() {
            return rowList;
        }
        
        public void addRowsButtonsList(List<List<InlineKeyboardButton>> rowList) {
            this.rowList.addAll(rowList);
        }

        /**
         * *
         *
         * @param data call back data to be executed
         * @return a new single menu button like ♻ Actualizar
         */
        public static MessageMenu refreshButton(String callback) {
            
            MessageMenu menu = new MessageMenu();
            menu.addButton("♻ Actualizar", callback);
            
            return menu;
            
        }
        
        public MessageMenu addRefreshButton(String callback) {
            
            this.addButton("♻ Actualizar", callback);
            
            return this;
            
        }
        
        public MessageMenu addSupportButton(String callback) {
            
            this.addButton("❓ Soporte", callback);
            
            return this;
        }
        
        public static MessageMenu backButton(String callback) {
            MessageMenu menu = new MessageMenu();
            menu.addButton(" 🔙 ", callback);
            
            return menu;
            
        }
        
        public MessageMenu addBackButton(String callback) {
            
            this.addButton(" 🔙 ", callback);
            
            return this;
            
        }
        
        public static MessageMenu ExitButton() {
            
            MessageMenu menu = new MessageMenu();
            menu.addButton("❎ Salir ", "/salir");
            
            return menu;
            
        }
        
        public MessageMenu addExitButton() {
            
            this.addButton("❎ Salir ", "/salir");
            
            return this;
        }

        public void addBakcAndForwardButtons(String backData, String FwrData) {
        this.newLine();
        this.addAbutton(new Button("⬅️",backData), false);
        this.addAbutton(new Button("️➡",FwrData), false);
         
        }

        public void addButtons(ArrayList<Button> list, boolean newline) {

            for (Button b : list) {

                if (newline) {
                    this.addAbutton(b, newline);

                } else {
                    this.addButton(b);
                }

            }

        }
        
        
        public static class Button {
            
            InlineKeyboardButton ikb;

            /**
             * @param text
             * @param callBackData (str | Any, optional) – Data to be sent in a
             * callback query to the bot when button is pressed, UTF-8 1-64
             * bytes. If the bot instance
             */
            public Button(String text, String callBackData) {
                ikb = new InlineKeyboardButton();
                this.ikb.setText(text);
                this.ikb.setCallbackData(callBackData);
            }
            
            private Button(String text, String callBackData, boolean isUrl) {
                ikb = new InlineKeyboardButton();
                this.ikb.setText(text);
                
                if (callBackData.isEmpty()) {
                    callBackData = "0";
                }
                
                if (isUrl) {
                    this.ikb.setUrl(callBackData);
                } else {
                    this.ikb.setCallbackData(callBackData);
                }
                
            }
            
            public InlineKeyboardButton getIkb() {
                return ikb;
            }
            
            public void setIkb(InlineKeyboardButton ikb) {
                this.ikb = ikb;
            }
            
        }
        
    }
    
