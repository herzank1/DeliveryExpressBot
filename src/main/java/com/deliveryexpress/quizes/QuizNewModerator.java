/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import com.deliveryexpress.de.DataBase;
import com.deliveryexpress.de.OperationsMagnament;
import com.deliveryexpress.objects.OperationAreaMagnament;
import com.deliveryexpress.objects.TelegramUser;
import com.deliveryexpress.objects.TelegramUser.Moderator;
import com.deliveryexpress.telegram.Messenger;
import com.deliveryexpress.telegram.Messenger.MessageMenu;
import com.deliveryexpress.telegram.Messenger.MessageMenu.Button;
import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;
import java.util.ArrayList;

/**
 *
 * @author DeliveryExpress
 */
public class QuizNewModerator extends Quiz {

    String telegramId;
    String name, phone, area;

    Moderator moderator;

    public QuizNewModerator(String userId) {
        super(userId);
    }

    @Override
    void execute(Xupdate xupdate) {

        Messenger.TelegramAction ta = new Messenger.TelegramAction();
        ta.setAction(Messenger.TelegramAction.Action.SEND_MESSAGE);
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
                TelegramUser telegramUserById = DataBase.TelegramUsers.getTelegramUserById(telegramId);
                if (telegramUserById == null) {

                    ta.setText("⚠️ Este Usuario " + telegramId + " de telegram no exite, el usuario debe mandar mensaje primero a un nodo o robot official,"
                            + " para continuar el registro. si ya lo hizo presione ingrese el Id de nuevo o de lo contrario"
                            + " /salir");
                    ta.setMenu(MessageMenu.ExitButton());
                    ta.execute();
                    break;
                }
                
                
                ta.setText("Ingrese nombre del Moderador.");
                ta.execute();
                next();

                break;

            case 2:
                name = xupdate.getText();
                ta.setText("Ingrese telefono del Moderador.");
                ta.execute();
                next();

                break;

            case 3:
                phone = xupdate.getText();
                ta.setText("Ingrese el Area de Operacion");
                ta.setMenu(getAreas());
                ta.execute();
                next();

                break;

            case 4:
 
                area = xupdate.getText();

                moderator = new Moderator(name, phone);
                moderator.setArea(area);

                ta.setText("Es correcta la informacion?\n" + moderator.toString());
                ta.setMenu(MessageMenu.yesNo());
                ta.execute();
                next();

                break;

            case 5:
                
      
            
            
                

                try {
                    if (Utils.isPositiveAnswer(xupdate.getText())) {
                  


                        Moderator insertNew = DataBase.Moderators.insertNew(moderator);
                        DataBase.TelegramUsers.updateTelegramUserField(telegramId, "accountType", TelegramUser.AccountType.MODERATOR);
                        DataBase.TelegramUsers.updateTelegramUserField(telegramId, "accountId", moderator.getAccountId());

                        DataBase.BalancesAccounts.insertNewBalanceAccount(moderator.getBalanceAccountNumber(), moderator.getAccountId());

                        ta.setText("Registro completo.");
                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.execute();
                        destroy();

                    } else {

                        destroy();
                        ta.setText("Proceso cancelado.");
                        ta.execute();
                    }

                } catch (Exception e) {

                    destroy();
                    ta.setText(e.getMessage());
                    ta.execute();

                }
                
                break;

        }
        
      
    }
    
     MessageMenu getAreas() {
        MessageMenu menu = new MessageMenu();
        ArrayList<OperationAreaMagnament> oamList = OperationsMagnament.getOamList();
        menu.addButton(new Button("GENERAL","GENERAL"), true);
        for (OperationAreaMagnament o : oamList) {
            menu.addButton(new Button(o.getName(), o.getId()), true);
        }

        return menu;

    }

}
