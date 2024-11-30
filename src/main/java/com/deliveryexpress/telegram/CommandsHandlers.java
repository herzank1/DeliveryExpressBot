/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.objects.users.AccountType;
import com.deliveryexpress.objects.users.Tuser;
import com.monge.tbotboot.messenger.Response;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.QuizesControl;

/**
 *
 * @author DeliveryExpress
 */
class CommandsHandlers {

    static void execute(Xupdate xupdate) {

        if (xupdate.isGroupMessage()) {
            Response.sendMessage(xupdate.getTelegramUser(), xupdate.getText(), null);
        } else {
            
            if (QuizesControl.hasQuiz(xupdate.getSenderId())) {
                QuizesControl.execute(xupdate);
            }
            
            Tuser user = Tuser.read(Tuser.class, xupdate.getSenderId());

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

                    break;
            }
        }

    }

}
