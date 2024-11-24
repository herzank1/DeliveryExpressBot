/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.telegram;

import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.de.database.DataBase;
import com.deliveryexpress.objects.users.AccountStatus;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.objects.users.TelegramUser;

/**
 *
 * @author DeliveryExpress
 */
class BussinesCommands {

    static void execute(Xupdate xupdate) {

        TelegramUser user = xupdate.getSenderTelegramUser();
        Bussines bussines = user.getBussines();

        if (bussines.getAccountStatus().equals(AccountStatus.PAUSED)) {
            Response.sendMessage(user, "Tu cuenta esta en proceso de aprovacion."
                    + "\n contacte a un moderador para mas informacion.", null);
            return;

        }

        if (bussines.getAccountStatus().equals(AccountStatus.INACTIVE)) {
            Response.sendMessage(user, "Tu cuenta esta Inactiva!", null);
            return;

        }

        Command command = xupdate.getCommand();
        switch (command.command()) {

            case "/menu":
            case "/start":
                BalanceAccount read = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(bussines.getBalanceAccountNumber());
                Response.editMessage(xupdate.getSenderTelegramUser(), xupdate.getMessageId(),
                        bussines.getName()
                        + "\nID:" + user.getId()
                        + "\nCartera:" + read.getBalance() + "$", getMenu());

                break;

            case "/delete_msg":
                Response.deleteMessage(xupdate);

                break;

        }

    }

    private static MessageMenu getMenu() {
        MessageMenu menu = new MessageMenu();

        menu.addButton("✳ Nueva Orden(ATM)", "/neworder&1", true);

        menu.addButton("✴️ Nueva Orden(Manual)", "/neworder&2", true);
        menu.addButton("🛵 Cotizar envio(ATM)", "/calcdeliverycost", true);
        menu.addButton("🛵 Tabla KM", "/deliverytable", true);
        menu.addButton("📑 Mis ordenes", "/myorders", true);
        menu.addButton("🕰 Historial (Hoy)", "/mytodayhistory", true);
        menu.addButton("💳 Mi cartera", "/mywallet", true);
        //menu.addButton("📢 Canal official", "/bussineschannel",true);
        menu.addButton("❓ Soporte", "/support");
        menu.addButton("♻ Actualizar", "/menu&refresh", true);

        return menu;

    }

}
