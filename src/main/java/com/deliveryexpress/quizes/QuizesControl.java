/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import com.deliveryexpress.telegram.Command;
import com.deliveryexpress.telegram.MessageMenu;
import com.deliveryexpress.telegram.Response;
import com.deliveryexpress.telegram.Xupdate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author DeliveryExpress
 */
public class QuizesControl {

    static Map<String, Quiz> quizes = new HashMap<>();

    public static boolean hasQuiz(String userId) {

        return quizes.containsKey(userId);
    }

    public static void add(Quiz e) {
        quizes.put(e.getUserId(), e);

    }

    public static Quiz getQuiz(String userId) {
        return quizes.get(userId);
    }

    public static void execute(Xupdate xupdate) {

        Quiz quiz = getQuiz(xupdate.getSenderId());
      
        if (quiz != null&&!xupdate.getCommand().isCommand()) {

            switch (xupdate.getText()) {

                case "/close":
                case "/cerrar":
                case "/salir":
                case "/exit":
                case "/esc":

                    quiz.destroy();
                    Response.sendMessage(xupdate.getSenderTelegramUser(), "Proceso finalizado!", null);

                    break;

                default:
                    quiz.execute(xupdate);
                    break;

            }

        } else {
            System.out.println("No se encontro questionario o estas usando un //comando en un questionario!");

        }

    }

    static void destroy(Quiz aThis) {
        quizes.remove(aThis.getUserId());

    }

    public static void sendAlreadyInProcessMsg(String senderId) {
        Response.sendMessage(senderId, "Ya tiene un proceso abierto, continue o ingrese /salir", null);

    }

}
