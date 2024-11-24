/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change o license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit o template
 */
package com.deliveryexpress.utils;

import com.deliveryexpress.de.orders.Order;
import com.deliveryexpress.de.orders.OrderStatus;
import com.deliveryexpress.telegram.MessageMenu;

/**
 *
 * @author DeliveryExpress
 */
public class MenuUtils {
    
     MessageMenu getCurrentButtonsForDeliveryMan(Order o) {
             MessageMenu menu = new MessageMenu();
        
             switch (o.getStatus()) {

                case OrderStatus.LISTO:
                case OrderStatus.PREPARACION:
                    
                    if (!o.isDeliveryManArrivedToBussines()) {
                        menu.addUrlButton("📌 Navegar 🍔", o.getBusssines().getPosition().getUrlNavigateTo());
                        menu.addButton("🍔 Llegue ✅", "/updatestatus&ARRIVED_TO_BUSSINES&"+o.getId());
                    } else {

                        menu.addButton("📦 Recolectar", "/updatestatus&"+OrderStatus.EN_CAMINO+"&"+o.getId());
                    }

                    break;

                case OrderStatus.EN_CAMINO:
                    menu.addUrlButton("📌 Navegar 🏡", o.getCustomer().getPosition().getUrlNavigateTo());
                    menu.addButton("🏡 Llegue ✅", "/updatestatus&"+OrderStatus.EN_DOMICILIO+"&"+o.getId());

                    break;
                case OrderStatus.EN_DOMICILIO:

                    menu.addButton("📦 Entregar 👤", "/updatestatus&"+OrderStatus.ENTREGADO+"&"+o.getId());

                    break;

                case OrderStatus.ENTREGADO:
                case OrderStatus.CANCELADO:

                    break;

            }
        
            return menu;
        
        }
        
        MessageMenu getCurrentButtonsForModerators(Order o) {
            
                 MessageMenu menu = new MessageMenu();
        
             switch (o.getStatus()) {

                case OrderStatus.LISTO:
                case OrderStatus.PREPARACION:
                case OrderStatus.EN_CAMINO:
                case OrderStatus.EN_DOMICILIO:
                    
                    if (o.getDeliveryMan()==null && o.isSentToExternal() == false) {
                        menu.addButton(new MessageMenu.Button("➡ a Externos", "/sendtoexternal&" + o.getId()), true);
                    }

                    menu.addButton("🛵 Asignar", "/assign&"+o.getId());
                    menu.addButton("🛑 Cancelar", "/cancelorder&"+o.getId());
                    menu.addNewLine();
                    menu.addButton("⌛ Eventos", "/events&"+o.getId());
                    
                    if (o.getDeliveryMan()==null && o.getDeliveryMan().isSharingLocation()) {
                        menu.addUrlButton("🛵📌 Repartidor", o.getDeliveryMan().getPosition().getUrlGoogleMapsMark());
                    }
                    
                    menu.addNewLine();
                    menu.addBackButton("/myorders");

                    break;

                case OrderStatus.ENTREGADO:
                case OrderStatus.CANCELADO:
                    
                    menu.addButton("⌛ Eventos", "/events&"+o.getId());
                    menu.addBackButton("/myorders");

                    break;

            }
        
            return menu;
        
        }
    
}
