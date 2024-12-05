/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.deliveryexpress.de.orders;

/**
 *
 * @author DeliveryExpress
 */
public class OrderStatus {

    public static final String PREPARACION = "PREPARACION";
    public static final String LISTO = "LISTO";
    public static final String EN_CAMINO = "EN_CAMINO";
    public static final String EN_DOMICILIO = "EN_DOMICILIO";
    public static final String ENTREGADO = "ENTREGADO";
    public static final String CANCELADO = "CANCELADO";

    public interface OrderStatusSymbol {

        String PREPARACION = "[⏰🍳]";  // Representa preparación (cocinando o listo para cocinar)
        String LISTO = "[✅]";         // Representa listo para enviar
        String EN_CAMINO = "[🚚]";     // Representa en camino
        String EN_DOMICILIO = "[⏰🏠]";  // Representa que está en el domicilio del cliente
        String ENTREGADO = "[✅📦]";     // Representa entregado
        String CANCELADO = "[❌]";     // Representa cancelado

    }
    
        public static String getEmoji(String status) {
        switch (status) {
            case PREPARACION:
                return OrderStatusSymbol.PREPARACION;
            case LISTO:
                return OrderStatusSymbol.LISTO;
            case EN_CAMINO:
                return OrderStatusSymbol.EN_CAMINO;
            case EN_DOMICILIO:
                return OrderStatusSymbol.EN_DOMICILIO;
            case ENTREGADO:
                return OrderStatusSymbol.ENTREGADO;
            case CANCELADO:
                return OrderStatusSymbol.CANCELADO;
            default:
                return "❓"; // Emoji para estado desconocido
        }
    }


}
