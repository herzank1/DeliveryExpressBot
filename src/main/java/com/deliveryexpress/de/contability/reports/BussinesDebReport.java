/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.contability.reports;

import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.utils.DateUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
public class BussinesDebReport {

    String fecha;
    String cobrador;
    ArrayList<DebListElement> lista;

    public BussinesDebReport() {
        this.fecha = DateUtils.getNowDate();
        this.cobrador = " ";
        lista = new ArrayList<>();
        fill();
    }

    private void fill() {

        ArrayList<Bussines> readAll = Bussines.readAll(Bussines.class);
        for (Bussines b : readAll) {

            float balance = b.getBalanceAccount().getBalance();
            if (balance < 0) {
                lista.add(new DebListElement(
                        b.getName(),
                        b.getAddress(),
                        b.getPhone(),
                        String.valueOf(balance)));
            }
        }

    }

    public String save() {
        return createCsvFile("reporte_deudas_negocios.csv");
    }

    private String createCsvFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Fecha," + this.fecha + "\n");
            writer.write("cobrador," + this.cobrador + "\n");
            // Escribir encabezados
            writer.write("Business Name,Address,Phone,Debt Amount,Received Amount\n");

            // Escribir los datos de la lista
            for (DebListElement element : lista) {
                writer.write(element.toCsvRow());
            }

            System.out.println("Archivo CSV creado exitosamente en: " + filePath);
            return filePath;

        } catch (IOException e) {
            System.err.println("Error al crear el archivo CSV: " + e.getMessage());
        }
        
        return null;
    }

    @Data
    private class DebListElement {

        String bussinesName;
        String address;
        String phone;
        String debAmmount;
        String receivedAmmount;

        public DebListElement(String bussinesName, String address, String phone, String debAmmount) {
            this.bussinesName = bussinesName;
            this.address = address;
            this.phone = phone;
            this.debAmmount = debAmmount;
            this.receivedAmmount = " ";
        }

        public String toCsvRow() {
            return escapeCsv(bussinesName) + ","
                    + escapeCsv(address) + ","
                    + escapeCsv(phone) + ","
                    + escapeCsv(debAmmount) + ","
                    + escapeCsv(receivedAmmount) + "\n";
        }

        private String escapeCsv(String value) {
            if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                // Escapar comillas dobles en el texto
                value = value.replace("\"", "\"\"");
                // Envolver el texto entre comillas dobles
                return "\"" + value + "\"";
            }
            return value;
        }

    }

}
