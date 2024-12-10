/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.server;

import com.deliveryexpress.de.Global;
import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.de.contability.BussinesContract;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.Tuser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

/**
 *
 * @author DeliveryExpress
 */
public class GetBussinesDataAction implements GetBussinesAction {

    public GetBussinesDataAction() {
    }

    @Override
    public void handle(HttpExchange exchange, QueryParams params) throws IOException {

        Tuser tuser = Tuser.read(Tuser.class, params.getParam("userid"));
        Bussines bussines = tuser.getBussines();
        BalanceAccount balanceAccount = bussines.getBalanceAccount();
        BussinesContract contract = bussines.getContract();
        Global.liveServerData liveData = Global.getInstance().getLiveData();

        // Crear el objeto JSON con Gson para convertir los objetos
        JsonObject jsonResponse = new JsonObject();

        Gson gson = new Gson();

        // Crear el objeto JSON
        // Convertir los objetos completos en JSON
        jsonResponse.add("tuser", gson.toJsonTree(tuser));
        jsonResponse.add("bussines", gson.toJsonTree(bussines));
        jsonResponse.add("balanceAccount", gson.toJsonTree(balanceAccount));
        jsonResponse.add("contract", gson.toJsonTree(contract));
        jsonResponse.add("liveData", gson.toJsonTree(liveData));

        String jsonResponseString = new Gson().toJson(jsonResponse);

        ResponseHelper.sendJsonResponse(exchange, jsonResponseString);

    }

}
