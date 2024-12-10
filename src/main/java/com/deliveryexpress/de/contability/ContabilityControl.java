/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.contability;

import com.deliveryexpress.objects.users.Bussines;
import com.monge.xsqlite.xsqlite.BaseDao;
import java.util.ArrayList;
import lombok.Data;

/**
 *
 * @author DeliveryExpress
 */
public class ContabilityControl {

    public static final String MAIN_BALANCE_ACCOUNT = "@DeliveryExpress";

    public static BalanceAccount getMainBalanceAccount() {
        BalanceAccount b = BalanceAccount.read(BalanceAccount.class, MAIN_BALANCE_ACCOUNT);
        if (b == null) {
            b = new BalanceAccount();
            b.setAccountNumber(MAIN_BALANCE_ACCOUNT);
            b.create();
        }

        return b;
    }

    /**
     * *
     * Carga la cuota a todos los contratos
     */
    public static void runCuotasCharges() {
        ArrayList<BussinesContract> bcs = BussinesContract.readAll(BussinesContract.class);

        for (BussinesContract c : bcs) {
            c.chargeCuota();
        }

    }

    /**
     * *
     * Carga el cobro mensual a todos los contratos
     */
    public static void runMonthlyCharges() {
        ArrayList<BussinesContract> bcs = BussinesContract.readAll(BussinesContract.class);

        for (BussinesContract c : bcs) {
            c.chargeMothly();
        }

    }

    public static String paymentMessage(float amount, String reference) {

        String cardNumber1 = "Banco BBVA\n"
                + "No. Targeta: 4152313813297715\n"
                + "Beneficiario: Diego Vicente Villarreal Monge\n"
                + "Referencia:" + reference + "\n\n";

        String cardNumber2 = "Spin by OXXO\n"
                + "CLABE: 728969000052109631\n"
                + "Beneficiario: Diego Vicente Villarreal Monge\n"
                + "Referencia:" + reference + "\n\n";

        return "Favor de pagar " + amount + " pesos en..\n\n" + cardNumber1 + cardNumber1
                + "";

    }

    /*generar lista de cobro de negocios*/
    public void generateDebCollectionLista() {
        ArrayList<Bussines> readAll = Bussines.readAll(Bussines.class);
        

    }
    
   

}
