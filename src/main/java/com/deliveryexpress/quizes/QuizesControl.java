/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import com.deliveryexpress.telegram.Messenger.MessageMenu;
import com.deliveryexpress.telegram.Messenger.TelegramAction;
import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;
import java.util.ArrayList;

/**
 *
 * @author DeliveryExpress
 */
public class QuizesControl {
    
    static ArrayList<Quiz> quizes = new ArrayList<>();
    
    public static boolean hasQuiz(String userId) {
        
        if (quizes.isEmpty()) {
            return false;
        }

        return quizes.stream().filter(c -> c.userId.equals(userId)).findFirst().orElse(null) != null;
    }
    
    public synchronized static void add(Quiz e) {
        quizes.add(e);
        
    }
    
    public static Quiz getQuiz(String userId) {
        if (quizes.isEmpty()) {
            return null;
        }

        return quizes.stream().filter(c -> c.userId.equals(userId)).findFirst().orElse(null);
    }
    
    public static void execute(Xupdate xupdate) {

        Quiz quiz = getQuiz(xupdate.getSenderId());
        if (quiz != null) {
            
            System.out.println(quiz.toString());

            switch (xupdate.getText()) {

                case "/close":
                case "/cerrar":
                case "/salir":
                case "/exit":
                case "/esc":

                    quiz.destroy();
                    TelegramAction ta = new TelegramAction();
                    ta.setSendMessage(xupdate.getSenderId(), "Proceso finalizado!");
                    ta.execute();

                    break;
                    
                default:
                quiz.execute(xupdate);
                break;
                        

            }

            
        }else{
            System.out.println("quiz not found for:"+xupdate.getSenderId());
        
        }

    }

    static void destroy(Quiz aThis) {
        quizes.remove(aThis);

    }

    public static void sendAlreadyInProcessMsg(String senderId) {
        TelegramAction ta = new TelegramAction();
        ta.setText("Ya tiene un proceso abierto, continue o ingrese /salir");
        ta.setMenu(new MessageMenu().addExitButton());
        ta.execute();

    }

}
