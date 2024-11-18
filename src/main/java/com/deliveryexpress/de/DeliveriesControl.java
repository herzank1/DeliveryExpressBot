/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de;

import com.deliveryexpress.objects.Location;
import com.deliveryexpress.objects.TelegramUser;
import com.deliveryexpress.objects.TelegramUser.DeliveryMan;
import com.deliveryexpress.telegram.Xupdate;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 *
 * @author DeliveryExpress
 * Esta clase contiene todo para administrar las sessiones de los repartidores
 * los metodos de la classe repartidores debera llamar a  esta clase segun corresponda
 */
public class DeliveriesControl {
    
   /***
    * 
    * @return la lista cahce de la base de datos de los repartidores
    */
    private static ArrayList<DeliveryMan>deliveries(){
        return DataBase.Deliveries.deliveries;
    }
 
    public static DeliveryMan getDeliveryManById(String deliveryId) {
        return DataBase.Deliveries.getDeliveryManByAccountId(deliveryId);
    }

    static ArrayList<DeliveryMan> getSharingLocationDeliveries() {
        return deliveries().stream().filter(c -> c.getSession().isSharingLocation()).collect(Collectors.toCollection(ArrayList::new));
    }

    static ArrayList<DeliveryMan> getConnectedDeliveries() {
        return deliveries().stream().filter(c -> c.getSession().isConected()).collect(Collectors.toCollection(ArrayList::new));
    }
    
    static synchronized void setDeliveryStatus(String deliveryId,String status){
        getDeliveryManById(deliveryId).getSession().setStatus(status);
    }

    /***
     * En teoria ya esta condicionado el null pointer de la location y el deliveryMan desde el metodo que lo llamante
     * @param xupdate 
     */
    static synchronized void updateDeliveryManLocation(Xupdate xupdate) {
    
        TelegramUser telegramUser = xupdate.getCallerTelegramUser();
        Location location = xupdate.getLocation();
        
        DeliveryMan deliveryManById = getDeliveryManById(telegramUser.getAccountId());
        DeliveryMan.Session session = deliveryManById.getSession();
        session.setLocation(location);
        
        System.out.print(session.toString());
    
    }
    
}
