/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DeliveryExpress
 */
public class Utils {

    public static String toJsonString(Object obj) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);

    }
    
    public static String generateOrderId() {

        return generateUniqueId().substring(0, 6);
    }

    public static String generateUniqueId() {

        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }
    
   public static String getShortName(String name) {

            String[] parts = name.split(" ");
            String capitals = "";
            for (String s : parts) {
                capitals += s.replaceAll("[^a-zA-Z0-9_-]", "").substring(0, 1);
            }
            return capitals.toUpperCase();

        }

    

    public static boolean isBoolean(String value) {

        return "true".equals(value.toLowerCase()) || "false".equals(value.toLowerCase())
                || value.equals("0") || value.equals("1");

    }

    public static boolean isNumeric(String str) {
        try {
            double parseDouble = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    public static boolean isUrl(String text) {
        String regex = "\\b(https?|ftp)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        return text.matches(regex);

    }

    public static String getString(String value) {
        //get value to String with null safe 
        if (value == null) {
            return "null";
        } else {
            return value;
        }

    }

    public static boolean isCoordenate(String text) {

        String regex = "^-? ?(90|[0-8]?\\d)(\\.\\d+)?, *-?(180|1[0-7]\\d|\\d?\\d)(\\.\\d+)?$";
        return text.matches(regex);

    }

    public static boolean isPositiveAnswer(String value) {

        return value.toLowerCase().equals("si")
                || value.toLowerCase().equals("yes")
                || value.toLowerCase().equals("y")
                || value.toLowerCase().equals("1");
    }

    public static String toSHA256(String string) {

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    string.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(encodedhash);

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static boolean isPositiveAnser(String text) {
    
        
        return text.toLowerCase().equals("si")||
                text.toLowerCase().equals("yes")||
                text.toLowerCase().equals("1")||
                text.toLowerCase().equals("y")
                ;
    }

    public static String generateUniqued() {
       
        return generateUniqueId();
    
    }

    public static int parseBoolean(String value) {

        if (value.toLowerCase().equals("true") 
                || value.equals("1")
                ||value.toLowerCase().equals("t")) {
            return 1;
        } else {
            return 0;
        }

    }
    
    public static String generateRandomAlphanumeric(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }

        return result.toString();
    }

    

    public interface Stickers {

        //🔍📝💻❓🛵❌👍⏱⏰💵🍔🟡🟢🔴📞
        String BIEN = "👍";
        String OK = "✅";
        String CANCELAR = "❌";
        String EXCLAMACION = "‼";
        String UBICACION = "📌";
        String TELEFONO = "📞";
        String DURACION = "⏱";
        String FECHA = "⏰";
        String PESOS = "💵";
        String ORDEN = "📝";
        String BUSCAR = "🔍";
        String COMANDO = "💻";
        String AYUDA = "❓";
        String CLIENTE = "👦";
        String REPARTIDOR = "🛵";
        String RESTAURANTE = "🍔";
        String CIRCULO_VERDE = "🟢";
        String CIRCULO_AMARILLO = "🟡";
        String CIRCULO_ROJO = "🔴";
        String ESTRELLA = "⭐";

    }

  
 
    

}
