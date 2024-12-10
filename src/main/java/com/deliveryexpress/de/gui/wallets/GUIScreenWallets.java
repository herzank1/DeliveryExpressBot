/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.gui.wallets;

import com.deliveryexpress.de.contability.reports.BussinesDebReport;
import com.deliveryexpress.objects.users.AccountType;
import com.deliveryexpress.utils.DateUtils;
import com.monge.tbotboot.gui.GuiItemAction;
import com.monge.tbotboot.gui.GuiMenu;
import com.monge.tbotboot.messenger.Methods;
import com.monge.tbotboot.messenger.Xupdate;
import com.monge.tbotboot.quizes.QuizGui;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author DeliveryExpress
 */
public class GUIScreenWallets extends QuizGui {

    public GUIScreenWallets(Xupdate xupdate) {
        super(xupdate.getTelegramUser());

        GuiMenu mainMenu = new GuiMenu(null, "Balances");
        mainMenu.addItem(new GUIWallets(null, "Repartidores", AccountType.DELIVERYMAN));
        mainMenu.addItem(new GUIWallets(null, "Negocios", AccountType.BUSSINES));
        mainMenu.addItem(new GuiItemAction(null, "Reporte de deudas",
                "Corre el reporte de deudas pendientes del los negocios "
                + "en un archivo CSV.") {
            @Override
            public void execute(Xupdate xupdate) {

                CompletableFuture.runAsync(() -> {
                    try {
                        BussinesDebReport report = new BussinesDebReport();
                        String filepath = report.save();

                        Methods.sendLocalDocFile(xupdate.getTelegramUser(), filepath, super.getText(), null);

                        super.setPostMessage(DateUtils.getNowDate()
                                + "\nSe han guardado el reporte!");
                    } catch (Exception e) {
                        super.setPostMessage(e.getMessage());
                    }
                });

            }

        });

        super.setMainMenu(mainMenu);

    }

}
