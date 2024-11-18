/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects;

/**
 *
 * @author DeliveryExpress
 */
public class Tags {

    String data;

    public Tags(String data) {
        this.data = data;
    }

    public boolean contains(String tagName) {

        return data.contains(tagName);

    }

    public static interface DeliveryTags {

        String IS_EMPLOYED = "IS_EMPLOYED";
        String BURNED = "BURNED";
    }

    public static interface BussinesTags {

        String IS_PREFERENTIAL = "IS_PREFERENTIAL";
    }
    
     public static interface BotTags {

        String IS_TEST_BOT = "IS_TEST_BOT";
    }
     
     public static interface AreaTags {

        String IS_TEST_AREA = "IS_TEST_AREA";
    }

}
