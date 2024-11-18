/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects;

import com.deliveryexpress.de.DataBase;
import com.deliveryexpress.objects.TelegramUser.Moderator;
import com.deliveryexpress.telegram.Messenger;
import com.deliveryexpress.telegram.Messenger.TelegramAction;
import com.deliveryexpress.utils.Utils;

/**
 *
 * @author DeliveryExpress
 * representa un area de operaciones
 */
public class OperationAreaMagnament {

    String id; //example DE_AREA_SANMARCOS
    String name; //Nombre del grupo
    String description; //descripcion
    String adminTelegramId; //id del administrador del grupo
    String areaModsGroupId; //id de grupo de moderadores de esta area
    String ordersThreadModsGroupId;// hilo de conversacion o ingreso de ordenes del grupo de moderadores de esta area
    String mainDeliveriesGroupId;//id telegram grupo principal para repartidores, moderadores, internos y de bases
    String deliveriesGroupId; //id telegram grupo secundario de repartidores

    /*new params*/
    String status; //estado del grupo
    String tag; //etiquetas
    String bussinesPaymentMessage;//informacion bancaria para pagos de los restaurantes

    public OperationAreaMagnament() {
    }

    public OperationAreaMagnament(
            String id, 
            String name, 
            String description, 
            String adminTelegramId, 
            String areaModsGroupId, 
            String ordersThreadModsGroupId, 
            String mainDeliveriesGroupId, 
            String deliveriesGroupId, 
            String status, 
            String tag,
            String bussinesPaymentMessage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.adminTelegramId = adminTelegramId;
        this.areaModsGroupId = areaModsGroupId; //telegram Id
        this.ordersThreadModsGroupId = ordersThreadModsGroupId;
        this.mainDeliveriesGroupId = mainDeliveriesGroupId;
        this.deliveriesGroupId = deliveriesGroupId;
        this.status = status;
        this.tag = tag;
        this.bussinesPaymentMessage = bussinesPaymentMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdminTelegramId() {
        return adminTelegramId;
    }

    public void setAdminTelegramId(String adminTelegramId) {
        this.adminTelegramId = adminTelegramId;
    }
    
    public Moderator getModerator(){
        
       TelegramUser telegramUserById = DataBase.TelegramUsers.getTelegramUserById(this.adminTelegramId);
       Moderator mod =  telegramUserById.getModerator();

       return mod;
    
    }

    public String getAreaModsGroupId() {
        return areaModsGroupId;
    }

    public void setAreaModsGroupId(String areaModsGroupId) {
        this.areaModsGroupId = areaModsGroupId;
    }

    public String getBussinesPaymentMessage() {
        return bussinesPaymentMessage;
    }

    public void setBussinesPaymentMessage(String bussinesPaymentMessage) {
        this.bussinesPaymentMessage = bussinesPaymentMessage;
    }
    
    


    public String getOrdersThreadModsGroupId() {
        return ordersThreadModsGroupId;
    }

    public void setOrdersThreadModsGroupId(String ordersThreadModsGroupId) {
        this.ordersThreadModsGroupId = ordersThreadModsGroupId;
    }

    public String getMainDeliveriesGroupId() {
        return mainDeliveriesGroupId;
    }

    public void setMainDeliveriesGroupId(String mainDeliveriesGroupId) {
        this.mainDeliveriesGroupId = mainDeliveriesGroupId;
    }

    public String getDeliveriesGroupId() {
        return deliveriesGroupId;
    }

    public void setDeliveriesGroupId(String deliveriesGroupId) {
        this.deliveriesGroupId = deliveriesGroupId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTag() {
        if (tag != null) {
            return tag;
        } else {
            return "";
        }

    }

    public Tags getTags() {
            return new Tags(getTag());
        }

    public void setTag(String tag) {
        this.tag = tag;
    }
    
    public boolean isTestArea() {
        return getTags().contains(Tags.AreaTags.IS_TEST_AREA);
    }

    public Moderator getAdminAccount() {
    
        
        TelegramUser telegramUserById = DataBase.TelegramUsers.getTelegramUserById(this.adminTelegramId);
      return DataBase.Moderators.getModeratorByAccountId(telegramUserById.accountId);
    
    }
    
    public void sendMessage(String chatId, String text, Messenger.MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setChatId(chatId);
        ta.setText(text);
        ta.setMenu(menu);
        ta.execute();

    }
    
    public void editMessage(String messageID, String chatId, String text, Messenger.MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
        ta.setChatId(chatId);
        ta.setEditMessageId(messageID);
        ta.setText(text);
        ta.setMenu(menu);
        ta.execute();

    }

    public void sendMessageToModsGroup(String text, Messenger.MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setChatId(this.areaModsGroupId);
        ta.setText(text);
        ta.setMenu(menu);
        ta.execute();

    }

    public TelegramAction sendMessageToDeliveiesMainGroup(String text, Messenger.MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setChatId(this.mainDeliveriesGroupId);
        ta.setText(text);
        ta.setMenu(menu);
        return ta.execute();

    }

    public TelegramAction sendMessageToDeliveiesGroup(String text, Messenger.MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setChatId(this.deliveriesGroupId);
        ta.setText(text);
        ta.setMenu(menu);
        return ta.execute();

    }
    
      public TelegramAction editMessageToDeliveiesMainGroup(String messageID,String text, Messenger.MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
        ta.setChatId(this.mainDeliveriesGroupId);
        ta.setMessageId(messageID);
        ta.setText(text);
        ta.setMenu(menu);
        return ta.execute();

    }

    public TelegramAction editMessageToDeliveiesGroup(String messageID,String text, Messenger.MessageMenu menu) {

        TelegramAction ta = new TelegramAction();
        ta.setChatId(this.deliveriesGroupId);
        ta.setMessageId(messageID);
         ta.setAction(TelegramAction.Action.EDIT_MESSAGE);
        ta.setText(text);
        ta.setMenu(menu);
        return ta.execute();

    }
    
    public static interface AreaStatus{
    
        String ACTIVE = "ACTIVE";
        String INACTIVE = "INACTIVE";
    
    }

    @Override
    public String toString() {
        return Utils.toJsonString(this);
    }
    
    

}
