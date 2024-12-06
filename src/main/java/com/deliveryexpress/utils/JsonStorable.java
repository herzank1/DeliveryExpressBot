/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.deliveryexpress.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DeliveryExpress
 */
public interface JsonStorable {

    @Retention(RetentionPolicy.RUNTIME) // Retener en tiempo de ejecución
    @Target(ElementType.FIELD) // Usar solo en campos
    public @interface fileName {
    }

    @Retention(RetentionPolicy.RUNTIME) // Retener en tiempo de ejecución
    @Target(ElementType.TYPE) 
    public @interface path {

        String path() default "Jsons";
    }

    public static <T> T load(Class<T> clazz, String fileName) {

        String path = getPath(clazz);
        if (path == null || fileName == null) {
            throw new IllegalArgumentException("No se puede cargar: ruta o id no definidos.");
        }

        // Leer el archivo JSON basado en la ruta y el id
        File file = new File(path + "/" + fileName + ".json");

        // Deserializar el archivo JSON a la clase actual
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            T loadedObject = gson.fromJson(reader, clazz);
            return loadedObject;

            // Aquí puedes transferir los valores del objeto cargado al objeto actual si es necesario
            // Este paso es opcional dependiendo de tu implementación.
        } catch (FileNotFoundException ex) {
       
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException ex1) {
                Logger.getLogger(JsonStorable.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (SecurityException ex1) {
                Logger.getLogger(JsonStorable.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (InstantiationException ex1) {
                Logger.getLogger(JsonStorable.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (IllegalAccessException ex1) {
                Logger.getLogger(JsonStorable.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (IllegalArgumentException ex1) {
                Logger.getLogger(JsonStorable.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (InvocationTargetException ex1) {
                Logger.getLogger(JsonStorable.class.getName()).log(Level.SEVERE, null, ex1);
            }
        
        } catch (IOException ex) {
            Logger.getLogger(JsonStorable.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    default void save() {
        String path = getPath();
        String fileName = getFileName();

        if (path == null || fileName == null) {
            throw new IllegalArgumentException("No se puede guardar: ruta o id no definidos.");
        }

        // Crear el directorio si no existe
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();  // Crear directorio
        }

        // Crear el archivo de destino con el id como nombre de archivo
        File file = new File(path + "/" + fileName + ".json");

        // Serializar el objeto a JSON y guardarlo en el archivo
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(this.toJson());
        } catch (IOException ex) {
            Logger.getLogger(JsonStorable.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    default String getPath() {
        Class<?> clazz = this.getClass();  // Obtener la clase actual

        return getPath(clazz);
    }

    default String getFileName() {
        Class<?> clazz = this.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            fileName annotation = field.getAnnotation(fileName.class);

            if (annotation != null) {
                try {

                    // Obtener el valor del campo con la anotación @fileName
                    return (String) field.get(this);  // Retorna el valor del campo
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  // Manejo de excepciones si no se puede acceder al campo
                }
            }
        }

        return null;

    }

    default String toJson() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    /**
     * *
     * Return exclude Fields Without Expose Annotation
     *
     * @return
     */
    default String toJsonEFWEA() {
        return new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation()
                .create().toJson(this);
    }

    public static String toJson(Object o) {

        return new GsonBuilder().setPrettyPrinting()
                .create().toJson(o);

    }

    /**
     * *
     *
     * @param o
     * @return exclude Fields Without Expose Annotation
     */
    public static String toJsonEFWEA(Object o) {

        return new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation()
                .create().toJson(o);

    }

    public static String getPath(Class<?> clazz) {

        // Comprobar si la clase tiene la anotación @path
        path pathAnnotation = clazz.getAnnotation(path.class);

        if (pathAnnotation != null && !pathAnnotation.path().isEmpty()) {
            // Si se ha definido un valor en la anotación @path, usarlo
            return pathAnnotation.path();
        } else {
            // Si no se ha definido un valor, devolver el nombre de la clase
            return clazz.getSimpleName();  // Obtener el nombre de la clase sin el paquete
        }

    }

    public static <T> ArrayList<T> readAll(Class<?> clazz) {
        ArrayList<T> resultList = new ArrayList<>();
        // Obtener la ruta desde la anotación
        String path = getPath(clazz);
        if (path == null) {
            throw new IllegalArgumentException("No se encontró la ruta para la clase " + clazz.getName());
        }

        // Crear un archivo apuntando a la ruta
        File folder = new File(path);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("La ruta proporcionada no es válida o no es un directorio.");
        }

        // Obtener todos los archivos .json del directorio
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            Gson gson = new Gson();

            // Leer cada archivo y deserializar su contenido
            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    // Deserializar el archivo JSON a un objeto de la clase T
                    T obj = (T) gson.fromJson(reader, clazz);
                    resultList.add(obj);
                } catch (IOException e) {
                    e.printStackTrace();  // Manejo de errores en caso de problemas al leer el archivo
                }
            }
        }
        return resultList;

    }

}
