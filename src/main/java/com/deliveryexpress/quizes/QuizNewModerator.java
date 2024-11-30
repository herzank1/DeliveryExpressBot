/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.objects.users.AccountStatus;
import com.deliveryexpress.objects.users.AccountType;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.objects.users.Moderator;
import com.deliveryexpress.objects.users.Tuser;

import com.deliveryexpress.utils.Utils;
import com.google.gson.GsonBuilder;
import com.monge.tbotboot.messenger.MessageMenu;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.Quiz;

/**
 *
 * @author DeliveryExpress Quiz realizado por el usuario que desea registrarse
 */
public class QuizNewModerator extends Quiz {

    String name, phone;

    Moderator moderator;

    public QuizNewModerator(String userId) {
        super(userId);
    }

    @Override
    public void execute(Xupdate xupdate) {

        Response response = new Response(xupdate.getTelegramUser());

        switch (super.getStep()) {

            case 0:

                response.setText("Ingrese tu nombre.");
                response.execute();
                next();

                break;

            case 1:
                name = xupdate.getText();
                response.setText("Ingrese celular del Repartidor."
                        + "Sin guiones, espacios, solo 10 digitos.");
                response.execute();
                next();

                break;

            case 2:
                phone = xupdate.getText();

                moderator = new Moderator(name, phone);

                response.setText("Es correcta la informacion?\n" + moderator.toString());
                response.setMenu(MessageMenu.yesNo());
                response.execute();
                next();

                break;

            case 3:

                if (Utils.isPositiveAnswer(xupdate.getText())) {
                  
                    try {
                        Tuser tu = new Tuser(xupdate);

                        tu.setAccountType(AccountType.DELIVERYMAN);
                        tu.setAccountId(moderator.getAccountId());
                        
                       
                        DataBase.Accounts.TelegramUsers().update(tu);

                        BalanceAccount balanceAccount = new BalanceAccount();
                        
                       
                        DataBase.Contability.BalancesAccounts.BalancesAccounts().create(balanceAccount);
                        moderator.setBalanceAccountNumber(balanceAccount.getAccountNumber());
                        moderator.setAccountStatus(AccountStatus.PAUSED);
                        DataBase.Accounts.Moderators.Moderators() .create(moderator);
                        
          
                        response.setText("Registro completo.");
                        response.setMenu(MessageMenu.okAndDeleteMessage());
                        response.execute();
                        destroy();

                    } catch (Exception e) {
                        e.printStackTrace();
                        response.setText(e.getLocalizedMessage());
                        response.setMenu(MessageMenu.okAndDeleteMessage());
                        response.execute();

                    }

                } else {

                    response.setText("Proceso cancelado.");
                    response.execute();
                    destroy();
                }

                break;

        }

    }

}
