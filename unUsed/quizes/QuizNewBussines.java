/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.objects.users.AccountType;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.TelegramUser;
import com.deliveryexpress.telegram.MessageAction;
import com.deliveryexpress.telegram.MessageMenu;
import deprecated.Messenger;
import com.deliveryexpress.telegram.TelegramAction;
import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;
import java.util.ArrayList;

/**
 *
 * @author DeliveryExpress
 */
public class QuizNewBussines extends Quiz {

    String telegramId;
    String name, phone, address, area;

    Bussines bussines;
    TelegramUser telegramUser;

    public QuizNewBussines(String userId) {
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
                telegramUser = DataBase.Accounts.TelegramUsers().read(telegramId);
                if (telegramUser == null) {

                    ta.setText("⚠️ Este Usuario " + telegramId + " de telegram no exite, el usuario debe mandar mensaje primero a un nodo o robot official,"
                            + " para continuar el registro. si ya lo hizo presione ingrese el Id de nuevo o de lo contrario"
                            + " /salir");
                    ta.setMenu(MessageMenu.ExitButton());
                    ta.execute();
                    break;
                }
                
                
                
                ta.setText("Ingrese nombre del Negocio.");
                ta.execute();
                next();

                break;

            case 2:
                name = xupdate.getText();
                ta.setText("Ingrese telefono del Negocio.");
                ta.execute();
                next();

                break;

            case 3:
                phone = xupdate.getText();
                ta.setText("Ingrese direccion o ubicacion del Negocio.");
                ta.execute();
                next();

                break;

            case 4:
                
                if (xupdate.hasLocation()) {
                  //  address = xupdate.getLocation().getAddress();
                } else {
                    address = xupdate.getText();
                }
                
                
                ta.setText("Seleccione Area de operacion.");
                ta.setMenu(getAreas());
                ta.execute();
                next();

                break;

            case 5:
                area = xupdate.getText();

                bussines = new Bussines(name, phone, address, area);

                ta.setText("Es correcta la informacion?\n" + bussines.toString());
                ta.setMenu(MessageMenu.yesNo());
                ta.execute();
                next();

                break;

            case 6:
                
           
            
            

                if (Utils.isPositiveAnswer(xupdate.getText())) {
   
                    telegramUser = new TelegramUser(telegramId, "", false, AccountType.BUSSINES, bussines.getAccountId());
                        DataBase.Accounts.TelegramUsers().update(telegramUser);
                        
                        BalanceAccount balanceAccount = new BalanceAccount();
                        
                        DataBase.Contability.BalancesAccounts.BalancesAccounts().create(balanceAccount);
                        bussines.setBalanceAccountNumber(balanceAccount.getAccountNumber());
                        DataBase.Accounts.Bussiness.Bussiness().create(bussines);
                        ta.setText("Registro completo.");
                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.execute();
                        destroy();
                    

                } else {

                    destroy();
                    ta.setText("Proceso cancelado.");
                    ta.execute();
                }

                break;


        }
        
      
    }
    
 

}
