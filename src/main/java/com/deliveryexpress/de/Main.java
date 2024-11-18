/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.deliveryexpress.de;

import com.deliveryexpress.telegram.Messenger;
import com.deliveryexpress.utils.Utils;
import java.util.Scanner;

/**
 *
 * @author DeliveryExpress
 */
public class Main {

    public static void main(String[] args) {

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Seleccione una Opcion"
                + "\n[A] Run producction mode"
                + "\n[B] Run test mode"
                + "\n[C] Run google Maps tester"
                );

        String input = myObj.nextLine();

        switch (input.toLowerCase()) {
             case "a":

                Global.load();

                DataBase.init();
                Messenger.init(false);
                OperationsMagnament.init(false);
                SystemSecurity.init(false);

                break;

            case "b":
                boolean testMode = true;
                Global.load();

                DataBase.init();
                Messenger.init(testMode);
                OperationsMagnament.init(testMode);
                SystemSecurity.init(testMode);

                break;

           

            case "c":
               Utils.GoogleMapsUtils.test();

                break;
                
            

        }

    }
}
