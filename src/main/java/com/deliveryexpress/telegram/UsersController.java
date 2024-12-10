/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.objects.users.AccountType;
import com.deliveryexpress.objects.users.Tuser;
import com.google.gson.GsonBuilder;
import com.monge.tbotboot.commands.CommandsHandlers;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.QuizesControl;

/**
 *
 * @author DeliveryExpress
 */
public class UsersController implements CommandsHandlers {

    @Override
    public void execute(Xupdate xupdate) {

        try {
            // System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(xupdate));
            System.out.println(xupdate.toStringDetails());
            System.out.println("------------------------------------------------------");
            if (xupdate.isGroupMessage()) {
                Tuser group = DataBase.Accounts.getTelegramGroup(xupdate);
                Tuser user = DataBase.Accounts.getTelegramUser(xupdate);

                switch (user.getAccountType()) {

                    case AccountType.DELIVERYMAN:

                        GroupDeliveryManCommands.execute(xupdate);

                        break;

                    case AccountType.MODERATOR:
                        GroupModeratorCommands.execute(xupdate);
                        break;
                }
            } else {

                Tuser user = DataBase.Accounts.getTelegramUser(xupdate);

                if (QuizesControl.hasQuiz(xupdate.getSenderId())) {
                    QuizesControl.execute(xupdate);
                }

                switch (user.getAccountType()) {

                    case AccountType.NOT_REGISTRED:

                        NotRegistredCommands.execute(xupdate);

                        break;

                    case AccountType.DELIVERYMAN:

                        DeliveryManCommands.execute(xupdate);

                        break;

                    case AccountType.BUSSINES:

                        BussinesCommands.execute(xupdate);

                        break;

                    case AccountType.MODERATOR:
                        ModeratorCommands.execute(xupdate);
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
