/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

/**
 *
 * @author DeliveryExpress
 */
public class UsersCommands {

    interface DeliveryCommands {

        String MENU = "/menu";
        String START = "/start";
        String CONECTAR_DESCONECTAR = "/switchConection";
        String MIS_ORDENES = "/myOrders";
        String HISTORIAL_ORDENES = "/myOrdersHistory";
        String MOVIMIENTOS = "/walletMovements";
        String CARTERA = "/wallet";
        String SOPORTE = "/support";

    }

    interface BussinesCommands {

        String MENU = "/menu";
        String START = "/start";
        String NUEVA_ORDEN = "/newOrder";
        String NUEVA_ORDEN_ATM = "/newOrderAtm";
        String TARIFAS = "/fees";
        String MIS_ORDENES = "/myOrders";
        String HISTORIAL_ORDENES = "/myOrdersHistory";
        String MOVIMIENTOS = "/walletMovements";
        String CARTERA = "/wallet";
        String SOPORTE = "/support";

    }
    
        interface ModeratorCommands {


    }

}
