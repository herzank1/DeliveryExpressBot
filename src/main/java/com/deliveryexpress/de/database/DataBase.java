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
import com.deliveryexpress.objects.users.Customer;
import com.deliveryexpress.objects.users.DeliveryMan;
import com.deliveryexpress.objects.users.Moderator;
import com.deliveryexpress.objects.users.Tuser;
import com.monge.tbotboot.messenger.Bot;

import com.monge.xsqlite.xsqlite.ConnectionPoolManager;
import com.monge.xsqlite.xsqlite.GenericDao;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DeliveryExpress a SQLITE balancer avoid frezzen
 */
public class DataBase {

    public static void init() {

        ConnectionPoolManager.addConnection("db_accounts.sqlite",
                Tuser.class,
                Moderator.class,
                Bussines.class,
                Customer.class,
                DeliveryMan.class,
                GroupArea.class,
                Bot.class
        );

        ConnectionPoolManager.addConnection("db_contability.sqlite",
                Transacction.class,
                BalanceAccount.class
        );
        
           ConnectionPoolManager.addConnection("db_ordersHistory.sqlite",
                StorableOrder.class
        );
        

     

       

    }

    public static class Accounts {

        public static Tuser getTelegramUser(String id, String node) {
            Tuser read = null;

            read = TelegramUsers().read(id);


            /*si no existe creamos uno*/
            if (read == null) {
                read = new Tuser(id, node, false, AccountType.NOT_REGISTRED, null);
               ConnectionPoolManager.getDato(Tuser.class).create(read);
                
                

            } else {
                /*si si existe pero el node es diferente, actualizamos el node*/
                if (!read.getLastNodeBot().equals(node)) {
                    read.setLastNodeBot(node);
                    ConnectionPoolManager.getDato(Tuser.class).update(read);

                }

            }
            return read;
        }

        public static GenericDao<Tuser, String> TelegramUsers() {

           
            return ConnectionPoolManager.getDato(Tuser.class);

        }

        public static class Deliveries {

            public static GenericDao<DeliveryMan, String> Deliveries() {

                return ConnectionPoolManager.getDato(DeliveryMan.class);

            }
        }

        public static class Moderators {

            public static GenericDao<Moderator, String> Moderators() {

                return ConnectionPoolManager.getDato(Moderator.class);

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
                BalanceAccount cargos = (BalanceAccount) ConnectionPoolManager.getDato(BalanceAccount.class).read(MainBalancesAccountsIDs.CARGO);
                if (cargos == null) {
                    cargos = new BalanceAccount(MainBalancesAccountsIDs.CARGO, 100f);
                    ConnectionPoolManager.getDato(BalanceAccount.class).create(cargos);
                }

                /* Verificar y crear cuenta principal */
                BalanceAccount cuentaPrincipal = (BalanceAccount) ConnectionPoolManager.getDato(BalanceAccount.class).read(MainBalancesAccountsIDs.DELIVERY_EXPRESS);
                if (cuentaPrincipal == null) {
                    cuentaPrincipal = new BalanceAccount(MainBalancesAccountsIDs.DELIVERY_EXPRESS, 100f);
                    ConnectionPoolManager.getDato(BalanceAccount.class).create(cuentaPrincipal);
                }

                /* Verificar y crear cuenta de Servicios Repartos */
                BalanceAccount cuentaServiciosRepartos = (BalanceAccount) ConnectionPoolManager.getDato(BalanceAccount.class).read(MainBalancesAccountsIDs.SERVICIOS_REPARTOS);
                if (cuentaServiciosRepartos == null) {
                    cuentaServiciosRepartos = new BalanceAccount(MainBalancesAccountsIDs.SERVICIOS_REPARTOS, 0f);
                    ConnectionPoolManager.getDato(BalanceAccount.class).create(cuentaServiciosRepartos);
                }

                /* Verificar y crear cuenta de Seguros Órdenes */
                BalanceAccount cuentaSegurosOrdenes = (BalanceAccount) ConnectionPoolManager.getDato(BalanceAccount.class).read(MainBalancesAccountsIDs.SERGUROS_ORDENES);
                if (cuentaSegurosOrdenes == null) {
                    cuentaSegurosOrdenes = new BalanceAccount(MainBalancesAccountsIDs.SERGUROS_ORDENES, 0f);
                   ConnectionPoolManager.getDato(BalanceAccount.class).create(cuentaSegurosOrdenes);
                }

                /* Verificar y crear cuenta de Seguros Repartidores */
                BalanceAccount cuentaSegurosRepartidores = (BalanceAccount) ConnectionPoolManager.getDato(BalanceAccount.class).read(MainBalancesAccountsIDs.SEGUROS_REPARTIDORES);
                if (cuentaSegurosRepartidores == null) {
                    cuentaSegurosRepartidores = new BalanceAccount(MainBalancesAccountsIDs.SEGUROS_REPARTIDORES, 0f);
                    ConnectionPoolManager.getDato(BalanceAccount.class).create(cuentaSegurosRepartidores);
                }

                /* Verificar y crear cuenta de Caja Chica */
                BalanceAccount cajachica = (BalanceAccount) ConnectionPoolManager.getDato(BalanceAccount.class).read(MainBalancesAccountsIDs.CAJA_CHICA);
                if (cajachica == null) {
                    cajachica = new BalanceAccount(MainBalancesAccountsIDs.CAJA_CHICA, 0f);
                  ConnectionPoolManager.getDato(BalanceAccount.class).create(cajachica);
                }

                /* Verificar y crear cuenta de Emergencias */
                BalanceAccount emergencias = (BalanceAccount) ConnectionPoolManager.getDato(BalanceAccount.class).read(MainBalancesAccountsIDs.EMERGENCIAS);
                if (emergencias == null) {
                    emergencias = new BalanceAccount(MainBalancesAccountsIDs.EMERGENCIAS, 0f);
                    ConnectionPoolManager.getDato(BalanceAccount.class).create(emergencias);
                }

                /* Verificar y crear cuenta de Cuotas Repartidores */
                BalanceAccount cuotasRepas =(BalanceAccount) ConnectionPoolManager.getDato(BalanceAccount.class).read(MainBalancesAccountsIDs.CUOTAS_REPARTIDORES);
                if (cuotasRepas == null) {
                    cuotasRepas = new BalanceAccount(MainBalancesAccountsIDs.CUOTAS_REPARTIDORES, 0f);
                    ConnectionPoolManager.getDato(BalanceAccount.class).create(cuotasRepas);
                }

                /* Verificar y crear cuenta de Cuotas Negocios */
                BalanceAccount cuotasBussines = (BalanceAccount) ConnectionPoolManager.getDato(BalanceAccount.class).read(MainBalancesAccountsIDs.CUOTAS_NEGOCIOS);
                if (cuotasBussines == null) {
                    cuotasBussines = new BalanceAccount(MainBalancesAccountsIDs.CUOTAS_NEGOCIOS, 0f);
                    ConnectionPoolManager.getDato(BalanceAccount.class).create(cuotasBussines);
                }
            }

            public static GenericDao<BalanceAccount, String> BalancesAccounts() {

                return ConnectionPoolManager.getDato(BalanceAccount.class);

            }

        }

        public static class Transacctions {

            public static GenericDao<Transacction, String> Transacctions() {

                return ConnectionPoolManager.getDato(Transacction.class);

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

                return ConnectionPoolManager.getDato(StorableOrder.class);

            }
        }

    }

}
