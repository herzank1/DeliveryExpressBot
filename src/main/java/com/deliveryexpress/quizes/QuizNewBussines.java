/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.objects.users.AccountStatus;
import com.deliveryexpress.objects.users.AccountType;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.TelegramUser;

import com.deliveryexpress.telegram.MessageMenu;
import com.deliveryexpress.telegram.Methods;
import com.deliveryexpress.telegram.Response;

import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;
import java.util.ArrayList;

/**
 *
 * @author DeliveryExpress
 */
public class QuizNewBussines extends Quiz {

    String name, phone, address, area;

    Bussines bussines;

    public QuizNewBussines(String userId) {
        super(userId);
    }

    @Override
    void execute(Xupdate xupdate) {

        Response response = new Response(xupdate.getSenderTelegramUser());

        switch (super.step) {

            case -1:
                response.setText("Ingrese Nombre del negocio.");
                response.execute();
                next();

                break;

            case 0:
                name = xupdate.getText();
                response.setText("Ingrese telefono del Negocio.");
                response.execute();
                next();

                break;

            case 1:
                phone = xupdate.getText();
                response.setText("Ingrese direccion o ubicacion del Negocio."
                        + "Si es posible tomarla de Google.");

                response.execute();
                next();

                break;

            case 2:

                if (xupdate.hasLocation()) {
                    bussines.setLocation(xupdate.getLocation().toString());
                } else {
                    address = xupdate.getText();
                }

                response.setText("Seleccione Area de operacion.");
                response.setMenu(Methods.getAreas());
                response.execute();
                next();

                break;

            case 3:
                area = xupdate.getText();

                bussines = new Bussines(name, phone, address, area);

                response.setText("Es correcta la informacion?\n" + bussines.toString());
                response.setMenu(MessageMenu.yesNo());
                response.execute();
                next();

                break;

            case 4:

                if (Utils.isPositiveAnswer(xupdate.getText())) {

                    try {
                        TelegramUser tu = xupdate.getSenderTelegramUser();

                        tu.setAccountType(AccountType.BUSSINES);
                        tu.setAccountId(bussines.getAccountId());

                        DataBase.Accounts.TelegramUsers().update(tu);

                        BalanceAccount balanceAccount = new BalanceAccount();

                        DataBase.Contability.BalancesAccounts.BalancesAccounts().create(balanceAccount);
                        bussines.setBalanceAccountNumber(balanceAccount.getAccountNumber());
                        bussines.setAccountStatus(AccountStatus.PAUSED);
                        DataBase.Accounts.Bussiness.Bussiness().create(bussines);

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

                    destroy();
                    response.setText("Proceso cancelado.");
                    response.execute();
                }

                break;

        }

    }

}
