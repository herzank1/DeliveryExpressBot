/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.DataBase;
import com.deliveryexpress.utils.Utils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 *
 * @author DeliveryExpress
 */
public class Messenger {
    
    static ArrayList<Bot> bots = new ArrayList<>();
    
    public static void init(boolean test) {
        
        bots = DataBase.Bots.getBots(test);
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
                
                case TelegramAction.Action.SEND_MESSAGE:
                    
                    SendMessage sm = new SendMessage();
                    sm.setChatId(ta.getChatId());
                    sm.setText(ta.getText());
                    if (ta.isIsHtml()) {
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
                
                case TelegramAction.Action.EDIT_MESSAGE:
                    try {
                        EditMessageText emt = new EditMessageText();
                        emt.setMessageId(Integer.valueOf(ta.getEditMessageId()));
                        emt.setChatId(ta.getChatId());
                        emt.setText(ta.getText());
                        if (ta.isIsHtml()) {
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
                            ta.setAction(TelegramAction.Action.SEND_MESSAGE);
                            ta.execute();

                        }
                    }

                    
                    break; 
                    
                 case TelegramAction.Action.DELETE_MESSAGE:
                    
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
    
    public static class TelegramAction {

        String chatId;
        String threadId;
        String text;
        boolean isHtml;
        String action;
        String editMessageId;
        String reference;
        MessageMenu menu;
        String fileId;
        String fileType;

        /*returned messageId*/
        String messageId;
        boolean toGroup;
        
        public TelegramAction() {
            
            this.setAction(TelegramAction.Action.SEND_MESSAGE);
        }
        
        public void setEditMessage(String messageId, String chatId, String text) {
            
            setParams(chatId, null, text, false, Action.EDIT_MESSAGE, messageId, null, null, null, null);
            
        }
        
        public void setEditMessage(String messageId, String chatId, String text, boolean html) {
            
            setParams(chatId, null, text, isHtml, Action.EDIT_MESSAGE, messageId, null, null, null, null);
            
        }
        
        public void setSendMessageToThread(String chatId, String text, String threadId) {
            
            setParams(chatId, threadId, text, false, Action.SEND_MESSAGE, null, null, null, null, null);
        }
        
        public void setSendMessage(String chatId, String text) {
            
            setParams(chatId, null, text, false, Action.SEND_MESSAGE, null, null, null, null, null);
        }
        
        public void setSendMessage(String chatId, String text, boolean html) {
            
            setParams(chatId, null, text, html, Action.SEND_MESSAGE, null, null, null, null, null);
        }
        
        public void setParams(String chatId, String threadId, String text, boolean isHtml, String action, String messageId, String reference, MessageMenu menu, String fileId, String fileType) {
            this.chatId = chatId;
            this.threadId = threadId;
            this.text = text;
            this.isHtml = isHtml;
            this.action = action;
            this.editMessageId = messageId;
            this.reference = reference;
            this.menu = menu;
            this.fileId = fileId;
            this.fileType = fileType;
        }
        
        public String getChatId() {
            return chatId;
        }
        
        public void setChatId(String chatId) {
            this.chatId = chatId;
        }
        
        public String getThreadId() {
            return threadId;
        }
        
        public void setThreadId(String threadId) {
            this.threadId = threadId;
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
        
        public boolean isIsHtml() {
            return isHtml;
        }
        
        public void setIsHtml(boolean isHtml) {
            this.isHtml = isHtml;
        }
        
        public String getAction() {
            return action;
        }
        
        public void setAction(String action) {
            this.action = action;
        }
        
        public String getEditMessageId() {
            return editMessageId;
        }
        
        /*establece el id del mensaje para editar*/
        public void setEditMessageId(String messageId) {
            this.editMessageId = messageId;
        }
        
        public String getReference() {
            return reference;
        }
        
        public void setReference(String reference) {
            this.reference = reference;
        }
        
        public MessageMenu getMenu() {
            return menu;
        }
        
        public void setMenu(MessageMenu menu) {
            this.menu = menu;
        }
        
        public String getFileId() {
            return fileId;
        }
        
        public void setFileId(String fileId) {
            this.fileId = fileId;
        }
        
        public String getFileType() {
            return fileType;
        }
        
        public void setFileType(String fileType) {
            this.fileType = fileType;
        }
        
        public String getMessageId() {
            return messageId;
        }
        
        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }
        
        public TelegramAction execute() {
            return Messenger.execute(this);
        }
        
        public boolean actionSuccess() {
            return this.messageId != null;
        }
        
        public boolean isToGroup() {
            return toGroup;
        }

        /**
         * *
         * si es true Protege y garantiza el envio del mensage al grupo,
         * limitando el max_msg_per_second_to_group 20/min segun Api de Telegram
         *
         * @param toGroup
         */
        public void setToGroup(boolean toGroup) {
            this.toGroup = toGroup;
        }
        
        /***
         * envia un mensaje a cada ID relacionado a una cuenta
         *
         * @param parentsIds
         * @param text
         * @param menu 
         */
        public static void sendMessageToAccount(ArrayList<String> parentsIds, String text, MessageMenu menu) {

            TelegramAction ta = new TelegramAction();
            ta.setText(text);
            ta.setMenu(menu);
            for (String s : parentsIds) {
                ta.setChatId(s);
                ta.execute();

            }

        }
        
                public static void sendMessage(String senderId, String text, MessageMenu menu) {
         TelegramAction ta = new TelegramAction();
         ta.setChatId(senderId);
         ta.setText(text);
         ta.setMenu(menu);
         ta.execute();
        
        }

        /***
         * 
         * @return Bot correspondiente del chat ID desde la base de datos en la tabla telegram Users
         */
        private Bot getBotOut() {
         
            String botOutName = DataBase.TelegramUsers.getTelegramUserById(chatId).getBot();
            return Messenger.getBotByName(botOutName);
        
        }
        
        public static interface Action {

            String DELETE_MESSAGE = "DELETE_MESSAGE";
            String SEND_MESSAGE = "SEND_MESSAGE";
            String EDIT_MESSAGE = "EDIT_MESSAGE";
            String SEND_FILE = "SEND_FILE";
            
        }
        
    }
    
    public static class MessageMenu {
        
      
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
            menu.addButton("✅ Ok", "/deletemsg");
            
            return menu;
            
        }
        
        public MessageMenu addAbutton(Button button, boolean newLine) {
            
            addButton(button, newLine);
            return this;
        }
        
        public MessageMenu addOkAndDeleteMessage() {
            
            this.addButton("✅ Ok", "/deletemsg");
            
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
    
    public static class HtmlText {
        
        @Override
        public String toString() {
            return "HtmlText{" + '}';
        }
        
    }
    
}
