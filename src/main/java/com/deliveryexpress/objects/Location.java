/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects;

import com.deliveryexpress.utils.Utils;
import com.google.maps.model.LatLng;

/**
 *
 * @author DeliveryExpress
 */
public class Location {
     Double latitude;
     Double longitude;
     String address;
        
    

    public Location(org.telegram.telegrambots.meta.api.objects.Location location) {
         this.latitude = location.getLatitude();
         this.longitude = location.getLongitude();

    }

    public Location(String coor) {
      
        String parts[]=coor.split(",");
        this.latitude = Double.valueOf(parts[0]) ;
        this.longitude = Double.valueOf(parts[1]);
        
    
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return latitude + ", "+longitude ;
    }

    String getUrlLocation() {
        
    return Utils.GoogleMapsUtils.positionMarkUrl(this.toString());
    }
    
    public String getAddress() {

        if (this.address == null) {
            this.address = Utils.GoogleMapsUtils.geoCodeLocation(toGoogleModelLocation());
            return this.address;
        } else {
            return "null";
        }

    }
    
    private LatLng toGoogleModelLocation() {

        return new LatLng(this.latitude, this.longitude);
    }
    
    
    
}
