/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;
import java.util.HashMap;
import java.util.Map;



public class QueryParams {
    private final Map<String, String> params;

    public QueryParams(String query) {
        params = parseQuery(query);
    }

    /**
     * Analiza la cadena de consulta y la convierte en un mapa de pares clave-valor.
     */
    private Map<String, String> parseQuery(String query) {
        Map<String, String> paramMap = new HashMap<>();

        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    String key = decode(keyValue[0]);
                    String value = decode(keyValue[1]);
                    paramMap.put(key, value);
                }
            }
        }

        return paramMap;
    }

    /**
     * Decodifica una cadena para asegurar que los parámetros estén correctamente interpretados.
     */
    private String decode(String value) {
        try {
            return java.net.URLDecoder.decode(value, "UTF-8");
        } catch (Exception e) {
            return value; // Devolver el valor sin decodificar si algo falla
        }
    }

    /**
     * Obtiene el valor de un parámetro de la cadena de consulta.
     * @param name Nombre del parámetro a buscar.
     * @return Valor del parámetro, o null si no existe.
     */
    public String getParam(String name) {
        return params.getOrDefault(name, null);
    }
    
       /**
     * Devuelve el valor del parámetro "action".
     */
    public String getAction() {
        return getParam("action");
    }

    /**
     * Devuelve todos los parámetros como un mapa (para escenarios avanzados).
     */
    public Map<String, String> getAllParams() {
        return params;
    }

    
}
