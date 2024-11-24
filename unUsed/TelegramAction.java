/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import deprecated.Messenger;
import java.util.ArrayList;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 * Representa la accion o el mensaje que se enviara a TelegramUser
 */
@Data
public class TelegramAction {

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

        this.setAction(MessageAction.SEND_MESSAGE);
    }

    public void setEditMessage(String messageId, String chatId, String text) {

        setParams(chatId, null, text, false, MessageAction.EDIT_MESSAGE, messageId, null, null, null, null);

    }

    public void setEditMessage(String messageId, String chatId, String text, boolean html) {

        setParams(chatId, null, text, isHtml, MessageAction.EDIT_MESSAGE, messageId, null, null, null, null);

    }

    public void setSendMessageToThread(String chatId, String text, String threadId) {

        setParams(chatId, threadId, text, false, MessageAction.SEND_MESSAGE, null, null, null, null, null);
    }

    public void setSendMessage(String chatId, String text) {

        setParams(chatId, null, text, false, MessageAction.SEND_MESSAGE, null, null, null, null, null);
    }

    public void setSendMessage(String chatId, String text, boolean html) {

        setParams(chatId, null, text, html, MessageAction.SEND_MESSAGE, null, null, null, null, null);
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

    public TelegramAction execute() {
        return Messenger.execute(this);
    }

    public boolean actionSuccess() {
        return this.messageId != null;
    }

    /**
     * *
     * si es true Protege y garantiza el envio del mensage al grupo, limitando
     * el max_msg_per_second_to_group 20/min segun Api de Telegram
     *
     * @param toGroup
     */
    public void setToGroup(boolean toGroup) {
        this.toGroup = toGroup;
    }

    /**
     * *
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

}
