/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.database;

import com.deliveryexpress.de.contability.BalanceAccount;
import com.deliveryexpress.de.contability.Transacction;
import com.deliveryexpress.de.orders.StorableOrder;
import com.deliveryexpress.objects.GroupArea;
import com.deliveryexpress.objects.users.AccountType;
import com.deliveryexpress.objects.users.Bussines;
import com.deliveryexpress.de.contability.BussinesContract;
import com.deliveryexpress.de.contability.Payment;
import com.deliveryexpress.objects.users.Customer;
import com.deliveryexpress.objects.users.DBot;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.objects.users.Moderator;
import com.deliveryexpress.objects.users.Tuser;
import com.monge.tbotboot.messenger.Xupdate;

import com.monge.xsqlite.xsqlite.ConnectionPoolManager;
import com.monge.xsqlite.xsqlite.GenericDao;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DeliveryExpress a SQLITE balancer avoid frezzen
 */
public class DataBase {

    public static void init() {

        System.out.println("Init DataBase...");

        ConnectionPoolManager.addConnection("db_accounts.sqlite",
                Tuser.class,
                Moderator.class,
                Bussines.class,
                BussinesContract.class,
                Customer.class,
                DeliveryMan.class,
                GroupArea.class,
                DBot.class
        );

        ConnectionPoolManager.addConnection("db_contability.sqlite",
                Transacction.class,
                BalanceAccount.class,
                Payment.class
        );

        ConnectionPoolManager.addConnection("db_ordersHistory.sqlite",
                StorableOrder.class
        );

        System.out.println("success!");
        
        

    }

    public static class Accounts {

        public static Tuser getTelegramGroup(Xupdate xupdate) {
            if (!xupdate.isGroupMessage()) {
                return null;
            }
            return processTelegramUser(xupdate.getFromId(),xupdate.getBotUserName(), AccountType.IS_GROUP);
        }

        public static Tuser getTelegramUser(Xupdate xupdate) {
            
            return processTelegramUser(xupdate.getSenderId(),xupdate.getBotUserName(), AccountType.NOT_REGISTRED);
        }

        public static Tuser processTelegramUser(String id,String bot ,String accountType) {
            Tuser read = TelegramUsers().read(id);

            if (read == null) {
                // Si no existe, creamos uno nuevo
                read = new Tuser(
                        id,
                        bot,
                        false,
                        accountType,
                        null
                );
                read.create();
            }

            // Si el nodo ha cambiado, actualizamos
            if (!bot.equals(read.getLastNodeBot())) {
                read.setLastNodeBot(bot);
                read.update();
            }

            return read;
        }

        public static GenericDao<Tuser, String> TelegramUsers() {

            return ConnectionPoolManager.getDao(Tuser.class);

        }

        public static class Deliveries {

            public static GenericDao<DeliveryMan, String> Deliveries() {

                return ConnectionPoolManager.getDao(DeliveryMan.class);

            }
        }

        public static class Moderators {

            public static GenericDao<Moderator, String> Moderators() {

                return ConnectionPoolManager.getDao(Moderator.class);

            }

        }

    }

    public static class Contability {

        public static class BalancesAccounts {

            public interface MainBalancesAccountsIDs {

                String CARGO = "@CARGO";
                String DELIVERY_EXPRESS = "@DELIVERY_EXPRESS";
                String SERVICIOS_REPARTOS = "@SERVICIOS_REPARTOS";
                String SERGUROS_ORDENES = "@SERGUROS_ORDENES";
                String SEGUROS_REPARTIDORES = "@SEGUROS_REPARTIDORES";
                String CAJA_CHICA = "@CAJA_CHICA";
                String EMERGENCIAS = "@EMERGENCIAS";
                String CUOTAS_REPARTIDORES = "@CUOTAS_REPARTIDORES";
                String CUOTAS_NEGOCIOS = "@CUOTAS_NEGOCIOS";

            }

            public static void createMainBalancesAccounts() {

                /* Verificar y crear cuenta de Cargos */
                BalanceAccount cargos = (BalanceAccount) ConnectionPoolManager.getDao(BalanceAccount.class).read(MainBalancesAccountsIDs.CARGO);
                if (cargos == null) {
                    cargos = new BalanceAccount(MainBalancesAccountsIDs.CARGO, 100f);
                    ConnectionPoolManager.getDao(BalanceAccount.class).create(cargos);
                }

                /* Verificar y crear cuenta principal */
                BalanceAccount cuentaPrincipal = (BalanceAccount) ConnectionPoolManager.getDao(BalanceAccount.class).read(MainBalancesAccountsIDs.DELIVERY_EXPRESS);
                if (cuentaPrincipal == null) {
                    cuentaPrincipal = new BalanceAccount(MainBalancesAccountsIDs.DELIVERY_EXPRESS, 100f);
                    ConnectionPoolManager.getDao(BalanceAccount.class).create(cuentaPrincipal);
                }

                /* Verificar y crear cuenta de Servicios Repartos */
                BalanceAccount cuentaServiciosRepartos = (BalanceAccount) ConnectionPoolManager.getDao(BalanceAccount.class).read(MainBalancesAccountsIDs.SERVICIOS_REPARTOS);
                if (cuentaServiciosRepartos == null) {
                    cuentaServiciosRepartos = new BalanceAccount(MainBalancesAccountsIDs.SERVICIOS_REPARTOS, 0f);
                    ConnectionPoolManager.getDao(BalanceAccount.class).create(cuentaServiciosRepartos);
                }

                /* Verificar y crear cuenta de Seguros Órdenes */
                BalanceAccount cuentaSegurosOrdenes = (BalanceAccount) ConnectionPoolManager.getDao(BalanceAccount.class).read(MainBalancesAccountsIDs.SERGUROS_ORDENES);
                if (cuentaSegurosOrdenes == null) {
                    cuentaSegurosOrdenes = new BalanceAccount(MainBalancesAccountsIDs.SERGUROS_ORDENES, 0f);
                    ConnectionPoolManager.getDao(BalanceAccount.class).create(cuentaSegurosOrdenes);
                }

                /* Verificar y crear cuenta de Seguros Repartidores */
                BalanceAccount cuentaSegurosRepartidores = (BalanceAccount) ConnectionPoolManager.getDao(BalanceAccount.class).read(MainBalancesAccountsIDs.SEGUROS_REPARTIDORES);
                if (cuentaSegurosRepartidores == null) {
                    cuentaSegurosRepartidores = new BalanceAccount(MainBalancesAccountsIDs.SEGUROS_REPARTIDORES, 0f);
                    ConnectionPoolManager.getDao(BalanceAccount.class).create(cuentaSegurosRepartidores);
                }

                /* Verificar y crear cuenta de Caja Chica */
                BalanceAccount cajachica = (BalanceAccount) ConnectionPoolManager.getDao(BalanceAccount.class).read(MainBalancesAccountsIDs.CAJA_CHICA);
                if (cajachica == null) {
                    cajachica = new BalanceAccount(MainBalancesAccountsIDs.CAJA_CHICA, 0f);
                    ConnectionPoolManager.getDao(BalanceAccount.class).create(cajachica);
                }

                /* Verificar y crear cuenta de Emergencias */
                BalanceAccount emergencias = (BalanceAccount) ConnectionPoolManager.getDao(BalanceAccount.class).read(MainBalancesAccountsIDs.EMERGENCIAS);
                if (emergencias == null) {
                    emergencias = new BalanceAccount(MainBalancesAccountsIDs.EMERGENCIAS, 0f);
                    ConnectionPoolManager.getDao(BalanceAccount.class).create(emergencias);
                }

                /* Verificar y crear cuenta de Cuotas Repartidores */
                BalanceAccount cuotasRepas = (BalanceAccount) ConnectionPoolManager.getDao(BalanceAccount.class).read(MainBalancesAccountsIDs.CUOTAS_REPARTIDORES);
                if (cuotasRepas == null) {
                    cuotasRepas = new BalanceAccount(MainBalancesAccountsIDs.CUOTAS_REPARTIDORES, 0f);
                    ConnectionPoolManager.getDao(BalanceAccount.class).create(cuotasRepas);
                }

                /* Verificar y crear cuenta de Cuotas Negocios */
                BalanceAccount cuotasBussines = (BalanceAccount) ConnectionPoolManager.getDao(BalanceAccount.class).read(MainBalancesAccountsIDs.CUOTAS_NEGOCIOS);
                if (cuotasBussines == null) {
                    cuotasBussines = new BalanceAccount(MainBalancesAccountsIDs.CUOTAS_NEGOCIOS, 0f);
                    ConnectionPoolManager.getDao(BalanceAccount.class).create(cuotasBussines);
                }
            }

            public static GenericDao<BalanceAccount, String> BalancesAccounts() {

                return ConnectionPoolManager.getDao(BalanceAccount.class);

            }

        }

        public static class Transacctions {

            public static GenericDao<Transacction, String> Transacctions() {

                return ConnectionPoolManager.getDao(Transacction.class);

            }

            public static boolean execute(Transacction transacction) {
                try {

                    BalanceAccount bfrom = Contability.BalancesAccounts.BalancesAccounts().read(transacction.getFrom());
                    BalanceAccount bto = Contability.BalancesAccounts.BalancesAccounts().read(transacction.getTo());

                    bfrom.setBalance(bfrom.getBalance() - transacction.getMount());
                    bto.setBalance(bto.getBalance() + transacction.getMount());

                    Contability.BalancesAccounts.BalancesAccounts().update(bfrom);
                    Contability.BalancesAccounts.BalancesAccounts().update(bto);

                    Contability.Transacctions.Transacctions().create(transacction);

                    return true;

                } catch (Exception ex) {
                    Logger.getLogger(Transacction.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }

            }

        }

    }

    public static class OrdersHistory {

        public static ArrayList<StorableOrder> getHistoryRange(String[] args) {

            return null;

        }

        public static class Orders {

            public static GenericDao<StorableOrder, String> Orders() {

                return ConnectionPoolManager.getDao(StorableOrder.class);

            }
        }

    }

}
