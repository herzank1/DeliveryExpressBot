/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.orders;

import com.deliveryexpress.utils.DateUtils;
import com.deliveryexpress.utils.Utils;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */

@Data
public class OrderLog {
    
    String date;
    String eventDetails;
    String madeby;

    public OrderLog(String eventDetails, String madeby) {
        this.date = DateUtils.getNowDate();
        this.eventDetails = eventDetails;
        this.madeby = madeby;
    }
    
}
