/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import com.deliveryexpress.de.DataBase;
import com.deliveryexpress.objects.Transacction;
import com.deliveryexpress.telegram.Messenger;
import com.deliveryexpress.telegram.Messenger.MessageMenu;
import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;

/**
 *
 * @author DeliveryExpress
 */
public class QuizTransfer extends Quiz{
    
    Transacction t = new Transacction();

     public QuizTransfer(String userId,String BalanceAccountId) {

        super(userId);
        
    }

    @Override
    void execute(Xupdate xupdate) {
        
        Messenger.TelegramAction ta = new Messenger.TelegramAction();
        ta.setAction(Messenger.TelegramAction.Action.SEND_MESSAGE);
        ta.setChatId(xupdate.getSenderId());
        
        switch (super.step) {
            
            case -1:
                ta.setText("Ingrese la cuenta de origen.");
                ta.execute();
                next();
                
                break;
            
            case 0:
                t.setFrom(xupdate.getText());
                ta.setText("Ingrese la cuenta de destino.");
                ta.execute();
                next();
                
                break;
                
            case 1:
                t.setTo(xupdate.getText());
                ta.setText("Ingrese el monto");
                ta.execute();
                next();

                break;

            case 2:
                t.setMount(Float.parseFloat(xupdate.getText()));
                ta.setText("ingrese el concepto");
                ta.execute();
                next();

                break;
                
                case 3:
                t.setConcept(xupdate.getText());
                ta.setText("Ingrese la referencia");
                ta.execute();
                next();

                break;

            case 4:
                t.setReference(xupdate.getText());
                ta.setText("Confirme la transaccion"
                        + "\n"+t.toString());
                ta.setMenu(MessageMenu.yesNo());
                ta.execute();
                next();

                break;
                
            case 5:
                if(Utils.isPositiveAnser(xupdate.getText())){
                
                }

                break;    

        }

    }
    

    
}
