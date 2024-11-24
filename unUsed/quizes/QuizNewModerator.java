/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.objects.GroupArea;
import com.deliveryexpress.objects.users.AccountType;
import com.deliveryexpress.objects.users.Moderator;
import com.deliveryexpress.objects.users.TelegramUser;
import com.deliveryexpress.telegram.MessageAction;
import com.deliveryexpress.telegram.MessageMenu;
import com.deliveryexpress.telegram.MessageMenu.Button;
import com.deliveryexpress.telegram.TelegramAction;
import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DeliveryExpress
 */
public class QuizNewModerator extends Quiz {
    
    String telegramId;
    String name, phone, area;
    
    Moderator moderator;
    TelegramUser telegramUser;
    
    public QuizNewModerator(String userId) {
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
                        
                        telegramUser = new TelegramUser(telegramId, "", false, AccountType.MODERATOR, moderator.getAccountId());
                        DataBase.Accounts.TelegramUsers().update(telegramUser);
                        
                        BalanceAccount balanceAccount = new BalanceAccount();
                        
                        DataBase.Contability.BalancesAccounts.BalancesAccounts().create(balanceAccount);
                        moderator.setBalanceAccountNumber(balanceAccount.getAccountNumber());
                        DataBase.Accounts.Moderators.Moderators().create(moderator);
                        
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
    
   
}
