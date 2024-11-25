/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;


import com.deliveryexpress.de.contability.Transacction;
import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.de.database.DbBalancer;
import com.deliveryexpress.telegram.MessageMenu;
import com.deliveryexpress.telegram.Response;
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
        
           Response response = new Response(xupdate.getSenderTelegramUser());
        
        switch (super.step) {
            
            case -1:
                response.setText("Ingrese la cuenta de origen.");
                response.execute();
                next();
                
                break;
            
            case 0:
                t.setFrom(xupdate.getText());
                response.setText("Ingrese la cuenta de destino.");
                response.execute();
                next();
                
                break;
                
            case 1:
                t.setTo(xupdate.getText());
                response.setText("Ingrese el monto");
                response.execute();
                next();

                break;

            case 2:
                t.setMount(Float.parseFloat(xupdate.getText()));
                response.setText("ingrese el concepto");
                response.execute();
                next();

                break;
                
                case 3:
                t.setConcept(xupdate.getText());
                response.setText("Ingrese la referencia");
                response.execute();
                next();

                break;

            case 4:
                t.setReference(xupdate.getText());
                response.setText("Confirme la transaccion"
                        + "\n"+t.toString());
                response.setMenu(MessageMenu.yesNo());
                response.execute();
                next();

                break;
                
            case 5:
                if (Utils.isPositiveAnser(xupdate.getText())) {

                    DataBase.Contability.Transacctions.execute(t);
                    response.setText("Transaccion exitosa!");
                    response.setMenu(MessageMenu.okAndDeleteMessage());
                    response.execute();

                    destroy();

                }

                break;    

        }

    }
    

    
}
