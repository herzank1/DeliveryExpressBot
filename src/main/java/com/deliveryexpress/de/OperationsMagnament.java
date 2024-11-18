/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de;

import com.deliveryexpress.objects.OperationAreaMagnament;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 *
 * @author DeliveryExpress
 */
public class OperationsMagnament {

    static ArrayList< OperationAreaMagnament> oamList = new ArrayList<>();
    
    public static void init(boolean testAreas) {
        
      
        if (testAreas) {
            oamList = DataBase.Areas.getAll().stream().filter(c -> c.isTestArea()).collect(Collectors.toCollection(ArrayList::new));
        } else {
            oamList = DataBase.Areas.getAll().stream().filter(c -> !c.isTestArea()).collect(Collectors.toCollection(ArrayList::new));
        }
        
        verifiAreasIdReferences();
      
        for(OperationAreaMagnament oam:oamList){
        System.out.println("Area encontrada: "+oam.toString());
        }

    }
    
    private static void verifiAreasIdReferences(){
    
       /*
        for(OperationAreaMagnament o:oamList){
        
        if(DataBase.TelegramUsers.getTelegramUserById(o.getAreaModsGroup())==null){
        DataBase.TelegramUsers.insertNew(new TelegramUser(o.getAreaModsGroup(),"",false,"",""));
        }
        DataBase.TelegramUsers.getTelegramUserById(o.getMainDeliveriesGroupId());
        DataBase.TelegramUsers.getTelegramUserById(o.getDeliveriesGroupId());
        DataBase.TelegramUsers.getTelegramUserById(o.getAdminTelegramId());
        }
    */
    }
    
    public static OperationAreaMagnament getOAMbyDeliveriesGroupId(String id){
    
    return oamList.stream().filter(c-> c.getDeliveriesGroupId().equals(id)).findFirst().orElse(null);
    
    }
    
     public static OperationAreaMagnament getOAMbyId(String areaId) {

        return oamList.stream().filter(c -> c.getId().equals(areaId)).findFirst().orElse(null);

    }

    public static OperationAreaMagnament findOAM(String telegramId) {
        
        return oamList.stream().filter(c -> c.getAreaModsGroupId().equals(telegramId)
                || c.getMainDeliveriesGroupId().equals(telegramId)
                || c.getDeliveriesGroupId().equals(telegramId)).findFirst().orElse(null);
        
    }

    public static ArrayList<OperationAreaMagnament> getOamList() {
        return oamList;
    }
    
    

}
