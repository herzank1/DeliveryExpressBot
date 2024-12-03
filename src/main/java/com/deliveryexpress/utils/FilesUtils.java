/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.utils;

import com.deliveryexpress.de.Main;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 *
 * @author DeliveryExpress
 */
public class FilesUtils {
   

        public static void checkFile(String fileName, String initial_data) {

            File file = new File(fileName);
            if (!file.exists()) {
                try {
                    System.out.println(fileName + " not Exist");
                    file.createNewFile();
                    // write code for saving data to the file

                    FileWriter myWriter = new FileWriter(fileName);
                    myWriter.write(initial_data);
                    myWriter.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }

        public static String readJsonFile(String fileName) {

            if (!fileName.contains(".json")) {

                fileName += ".json";
            }
            String content = "";
            try {
                content = new String(Files.readAllBytes(Paths.get(fileName)));
            } catch (IOException ex) {
                System.out.println("No se pudo leer el archvo json " + fileName);
                ex.printStackTrace();
            }

            return content;
        }

        public static void writeJson(String filename, final Object s) {

            String toJson = Utils.toJsonString(s);

            if (filename.contains(".json")) {
                write(filename, "", toJson, false);
            } else {
                write(filename, ".json", toJson, false);
            }

        }

        private static void write(String filename, String extension, final String data, boolean appendLine) {

            try {

                FileWriter fw = new FileWriter(filename + extension, appendLine); //the true will append the new data
                if (appendLine) {
                    fw.write(data + "\n");
                } else {
                    fw.write(data);
                }

                fw.close();

                System.out.println(filename + " saved!");
            } catch (IOException ioe) {
                System.err.println("IOException: " + ioe.getMessage());
            }

        }

        public static String getPath(String filename) throws URISyntaxException, IOException {
            final ProtectionDomain domain;
            final CodeSource source;
            final URL url;
            final URI uri;
            String DirectoryPath;
            String separador_directorios = System.getProperty("file.separator");
            String JarURL;
            File auxiliar;
            domain = Main.class.getProtectionDomain();
            source = domain.getCodeSource();
            url = source.getLocation();
            uri = url.toURI();
            JarURL = uri.getPath();
            auxiliar = new File(JarURL);
            //Si es un directorio es que estamos ejecutando desde el IDE. En este caso
            // habrá que buscar el fichero en la carperta  abuela(junto a las carpetas "src" y "target·
            if (auxiliar.isDirectory()) {
                auxiliar = new File(auxiliar.getParentFile().getParentFile().getPath());
                DirectoryPath = auxiliar.getCanonicalPath() + separador_directorios;
            } else {
                JarURL = auxiliar.getCanonicalPath();
                DirectoryPath = JarURL.substring(0, JarURL.lastIndexOf(separador_directorios) + 1);

            }

            System.out.println(DirectoryPath + filename);
            return DirectoryPath + filename;
        }

    }

