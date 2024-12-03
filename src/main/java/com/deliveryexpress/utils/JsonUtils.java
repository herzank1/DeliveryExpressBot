/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author DeliveryExpress
 */
public class JsonUtils {


        /**
         * *
         *
         * @param <T>
         * @param jsonArray
         * @param clazz
         * @return a Array of Objects from JsonArray
         */
        public static <T> ArrayList<T> JsonArrayToObject(String jsonArray, ArrayList<T> clazz) {

            Gson gson = new Gson();
            TypeToken type = new TypeToken<ArrayList<T>>() {
            };

            ArrayList<T> array = gson.fromJson(jsonArray, type.getType());

            return array;

        }

        /**
         * *
         *
         * @param <T>
         * @param jsonArray
         * @param clazz
         * @return a Array of Objects from JsonArray
         */
        public static <T> ArrayList<T> JsonArrayToObject(JsonArray jsonArray, ArrayList<T> clazz) {

            Gson gson = new Gson();
            TypeToken type = new TypeToken<ArrayList<T>>() {
            };

            ArrayList<T> array = gson.fromJson(jsonArray, type.getType());

            return array;

        }

        /**
         * *
         *
         * @param <T>
         * @param clazz
         * @return a JsonElement of custom arraylist of object
         */
        public static <T> JsonElement toJsonArray(ArrayList<T> clazz) {

            Gson gson = new Gson();
            TypeToken type = new TypeToken<ArrayList<T>>() {
            };
            JsonElement element = gson.toJsonTree(clazz, type.getType());
            return element;

        }

        /**
         *
         * @param fileName
         * @return a JsonObject of a file
         */
        public static JsonObject convertFileToJSON(String fileName) {

            // Read from File to String
            JsonObject jsonObject = new JsonObject();

            try {
                JsonParser parser = new JsonParser();
                JsonElement jsonElement = parser.parse(new FileReader(fileName));
                jsonObject = jsonElement.getAsJsonObject();
            } catch (FileNotFoundException e) {

                return null;

            }

            return jsonObject;
        }

    }

    