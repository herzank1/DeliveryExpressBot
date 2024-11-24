/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;


import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.objects.GroupArea;
import com.deliveryexpress.objects.users.AccountType;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.objects.users.TelegramUser;
import com.deliveryexpress.telegram.MessageAction;
import com.deliveryexpress.telegram.MessageMenu;
import com.deliveryexpress.telegram.TelegramAction;
import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;
import java.util.ArrayList;

/**
 *
 * @author DeliveryExpress
 */
public class QuizNewDeliveryMan extends Quiz {

    String telegramId;
    String name, phone;

    DeliveryMan deliveryMan;
    TelegramUser telegramUser;

    public QuizNewDeliveryMan(String userId) {
        super(userId);
    }

    @Override
    void execute(Xupdate xupdate) {

        TelegramAction ta = new TelegramAction();
        ta.setAction(MessageAction.SEND_MESSAGE);
        ta.setChatId(xupdate.getSenderId());

        switch (super.step) {

            case 0:

                ta.setText("Ingrese id de telegram");
                ta.execute();
                next();

                break;

            case 1:

                telegramId = xupdate.getText();
                
                
                /*revisamos si ya existe el TelegramUser**/
                TelegramUser telegramUser = DataBase.Accounts.TelegramUsers().read(telegramId);
                if (telegramUser == null) {

                    ta.setText("⚠️ Este Usuario " + telegramId + " de telegram no exite, el usuario debe mandar mensaje primero a un nodo o robot official, "
                            + "para continuar el registro. si ya lo hizo presione ingrese el Id de nuevo o de lo contrario"
                            + " /salir");
                    ta.setMenu(MessageMenu.ExitButton());
                    ta.execute();
                    break;
                }


                ta.setText("Ingrese nombre del Repartidor.");
                ta.execute();
                next();

                break;

            case 2:
                name = xupdate.getText();
                ta.setText("Ingrese celular del Repartidor.");
                ta.execute();
                next();

                break;

            case 3:
                phone = xupdate.getText();

                deliveryMan = new DeliveryMan(name, phone);

                ta.setText("Es correcta la informacion?\n" + deliveryMan.toString());
                ta.setMenu(MessageMenu.yesNo());
                ta.execute();
                next();

                break;

            case 4:
                
                
            

                if (Utils.isPositiveAnswer(xupdate.getText())) {

                        telegramUser = new TelegramUser(telegramId, "", false, AccountType.DELIVERYMAN, deliveryMan.getAccountId());
                        DataBase.Accounts.TelegramUsers().update(telegramUser);
                        
                        BalanceAccount balanceAccount = new BalanceAccount();
                        
                        DataBase.Contability.BalancesAccounts.BalancesAccounts().create(balanceAccount);
                        deliveryMan.setAccountId(balanceAccount.getAccountNumber());
                        DataBase.Accounts.Deliveries.Deliveries().create(deliveryMan);
                        
                       
                        ta.setText("Registro completo.");
                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.execute();
                        destroy();
                   

                } else {

                
                    ta.setText("Proceso cancelado.");
                    ta.execute();
                    destroy();
                }

                break;

        }
        
      
    }
    
   

}
