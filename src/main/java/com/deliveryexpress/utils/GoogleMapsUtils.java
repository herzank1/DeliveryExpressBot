/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.utils;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

/**
 *
 * @author DeliveryExpress
 */
public class GoogleMapsUtils {
   

        static String GeocodingAPI = "AIzaSyBybOUg1fBCWK5DP1tgLYbgBgDguJFHRDo";

        public static GeoApiContext getContext() {

            GeoApiContext context;

            context = new GeoApiContext.Builder()
                    .apiKey(GeocodingAPI)
                    .build();
            return context;

        }

        public static String navigateToUrl(String address) {

            String base = "https://www.google.com/maps/dir/?api=1&";
            String params =String.join("&", new String[]{"destination=" + codeUrl(address),"travelmode=driving"}) ;

            return base + params;
        }
        
         public static String navigateToUrl(String from,String to) {

            String base = "https://www.google.com/maps/dir/?api=1&";
            String params =String.join("&", new String[]{"origin="+codeUrl(from),"destination=" + codeUrl(to),"travelmode=driving"}) ;

            return base + params;
        }
        /*codifica un texto*/
        private static String codeUrl(String text){
        
        return text.replace("|", "%7C").replace(",", "%2C").replace(" ", "+");
        }

        public static String positionMarkUrl(String coordenates) {
          String base = "https://www.google.com/maps/search/?api=1&query=";
          return base+codeUrl(coordenates.replace(" ", ""));
        
        }
        
        /****
         * 
         * @param input
         * @return la direccion formateada de google
         */
        public static String geoCodeAddress(String input) {

            GeocodingApiRequest geocode;

            try {

                geocode = GeocodingApi.geocode(getContext(), input);

                GeocodingResult[] await = geocode.await();
                String formattedAddress = await[0].formattedAddress;
                getContext().shutdown();

                return formattedAddress;

            } catch (Exception ex) {
                return  input;

            }
            
            

    }
        
        public static String geoCodeLocation(LatLng location) {

            GeocodingApiRequest geocode;
            String formattedAddress = "null";
            try {

                geocode = GeocodingApi.reverseGeocode(getContext(), location);

                GeocodingResult[] await = geocode.await();
                formattedAddress = await[0].formattedAddress;
                getContext().shutdown();

            } catch (Exception ex) {

            }
            return formattedAddress;
        }

}
