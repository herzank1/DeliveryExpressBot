/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.utils;

import com.deliveryexpress.de.Global;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.de.contability.BussinesContract;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
@Data
public class Cotization {

    String cityContext;
    String from;
    String to;
    String duration;
    float distance;
    boolean notFound;

    /**
     * *
     *
     * @param cityContext
     * @param from
     * @param to
     */
    public Cotization(String cityContext, String from, String to) {
        this.cityContext = cityContext;
        this.from = GoogleMapsUtils.geoCodeAddress(this.cityContext + " " + from);
        this.to = GoogleMapsUtils.geoCodeAddress(this.cityContext + " " + to);
        fetch();
    }

    private void fetch() {
        try {

            String[] origins = {this.from};
            String[] destinations = {this.to};

            DistanceMatrix await = DistanceMatrixApi.getDistanceMatrix(GoogleMapsUtils.getContext(), origins, destinations).await();
            DistanceMatrixRow[] rows = await.rows;

            this.duration = "" + rows[0].elements[0].distance + " " + rows[0].elements[0].duration;
            this.distance = (rows[0].elements[0].distance.inMeters / 1000f);

            // Invoke .shutdown() after your application is done making requests
            GoogleMapsUtils.getContext().shutdown();
        } catch (java.lang.NullPointerException ex) {
            this.distance = 5;
            this.duration = "15 min";
            notFound = true;

        } catch (ApiException | InterruptedException | IOException ex) {
            Logger.getLogger(Cotization.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /***
     * Obtenemos el costo de envio basados en el crontrato
     * @param bussines
     * @return 
     */
    public float getDeliveryCost(BussinesContract contract) {

        int kmBase = contract.getKmBase();
        float kmBaseCost = contract.getKmBaseCost();
        float kmExtraCost = contract.getKmExtraCost();

        float sum = 0;
        float kmsExtras = 0;

        if (this.getDistance() > kmBase) {
            kmsExtras = this.getDistance() - (float) kmBase;
        }

        sum = kmBaseCost + (kmsExtras * kmExtraCost);

        if (this.getDistance() > 10) {
            sum += Global.Global().execed_delivery_distance;
        } else if (this.getDistance() > 20) {
            sum += Global.Global().execed_delivery_distance;
        }

        return sum * Global.Global().dinamic_rate;

    }

    public String toStringDetails(BussinesContract contract) {

        StringBuilder sb = new StringBuilder();
        sb.append("Direccion: ").append(this.getTo()).append("\n");
        sb.append("Distancia desde el negocio: ")
                .append(String.format("%.2f", this.getDistance())).append(" km").append("\n");
        sb.append("Costo de envio: ")
                .append(String.format("%.2f", this.getDeliveryCost(contract))).append(" pesos.").append("\n");

        return sb.toString();
    }

}
