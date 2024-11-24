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
import com.deliveryexpress.objects.users.TelegramUser;

import com.deliveryexpress.telegram.MessageMenu;
import com.deliveryexpress.telegram.Response;

import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;
import com.google.gson.GsonBuilder;

/**
 *
 * @author DeliveryExpress Quiz realizado por el usuario que desea registrarse
 */
public class QuizNewDeliveryMan extends Quiz {

    String name, phone;

    DeliveryMan deliveryMan;

    public QuizNewDeliveryMan(String userId) {
        super(userId);
    }

    @Override
    void execute(Xupdate xupdate) {

        Response response = new Response(xupdate.getSenderTelegramUser());

        switch (super.step) {

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

                deliveryMan = new DeliveryMan(name, phone);

                response.setText("Es correcta la informacion?\n" + deliveryMan.toString());
                response.setMenu(MessageMenu.yesNo());
                response.execute();
                next();

                break;

            case 3:

                if (Utils.isPositiveAnswer(xupdate.getText())) {
                  
                    try {
                        TelegramUser tu = xupdate.getSenderTelegramUser();

                        tu.setAccountType(AccountType.DELIVERYMAN);
                        tu.setAccountId(deliveryMan.getAccountId());
                        
                       
                        DataBase.Accounts.TelegramUsers().update(tu);

                        BalanceAccount balanceAccount = new BalanceAccount();
                        
                       
                        DataBase.Contability.BalancesAccounts.BalancesAccounts().create(balanceAccount);
                        deliveryMan.setBalanceAccountNumber(balanceAccount.getAccountNumber());
                        deliveryMan.setAccountStatus(AccountStatus.PAUSED);
                        DataBase.Accounts.Deliveries.Deliveries().create(deliveryMan);
                        
          
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
