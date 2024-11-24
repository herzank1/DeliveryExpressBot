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
import com.deliveryexpress.objects.users.TelegramUser;
import com.deliveryexpress.telegram.Bot;
import com.deliveryexpress.telegram.Xupdate;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author DeliveryExpress a SQLITE balancer avoid frezzen
 */
public class DbBalancer {

    static DbConection accounts;
    static DbConection contability;
    static DbConection ordersHistory;

    public static void init() {

        accounts = new DbConection("db_accounts.sqlite");
        accounts.addDao(TelegramUser.class);
        accounts.addDao(Moderator.class);
        accounts.addDao(Bussines.class);
        accounts.addDao(Customer.class);
        accounts.addDao(DeliveryMan.class);
        accounts.addDao(GroupArea.class);
        accounts.addDao(Bot.class);

        contability = new DbConection("db_contability.sqlite");
        contability.addDao(Transacction.class);
        contability.addDao(BalanceAccount.class);

        ordersHistory = new DbConection("db_ordersHistory.sqlite");

    }

    public static class Accounts {
        
      public static TelegramUser getTelegramUser(String id, String node) {
        TelegramUser read = null;

  
            read = TelegramUsers().read(id);
  

        /*si no existe creamos uno*/
        if (read == null) {
            read = new TelegramUser(id,node,false,AccountType.NOT_REGISTRED,null);
            DataBase.Accounts.TelegramUsers().create(read);

        }else{
            /*si si existe pero el node es diferente, actualizamos el node*/
            if (!read.getLastNodeBot().equals(node)) {
                read.setLastNodeBot(node);
                DataBase.Accounts.TelegramUsers().update(read);

            }
        
        }
        return read;
    }

        public static GenericDao<TelegramUser, String> TelegramUsers() {

            GenericDao<TelegramUser, String> dao = accounts.getDao(TelegramUser.class);
            return dao;

        }

        public static class Deliveries {

            public static GenericDao<DeliveryMan, String> Deliveries() {

                GenericDao<DeliveryMan, String> dao = accounts.getDao(DeliveryMan.class);
                return dao;

            }
        }

        public static class Moderators {

            public static GenericDao<Moderator, String> Moderators() {

                GenericDao<Moderator, String> dao = accounts.getDao(Moderator.class);
                return dao;

            }

        }

        public static class Bussiness {

            public static GenericDao<Bussines, String> Bussiness() {

                GenericDao<Bussines, String> dao = accounts.getDao(Bussines.class);
                return dao;

            }
        }

        public static class Customers {

            public static GenericDao<Customer, String> Customers() {

                GenericDao<Customer, String> dao = accounts.getDao(Customer.class);
                return dao;

            }
        }

        public static class Bots {

            public static GenericDao<Bot, String> Bots() {

                GenericDao<Bot, String> dao = accounts.getDao(Bot.class);
                return dao;

            }
        }
        
        public static class GroupAreas {

            public static GenericDao<GroupArea, String> GroupAreas() {

                GenericDao<GroupArea, String> dao = accounts.getDao(GroupArea.class);
                return dao;

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
                BalanceAccount cargos = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(MainBalancesAccountsIDs.CARGO);
                if (cargos == null) {
                    cargos = new BalanceAccount(MainBalancesAccountsIDs.CARGO, 100f);
                    DataBase.Contability.BalancesAccounts.BalancesAccounts().create(cargos);
                }

                /* Verificar y crear cuenta principal */
                BalanceAccount cuentaPrincipal = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(MainBalancesAccountsIDs.DELIVERY_EXPRESS);
                if (cuentaPrincipal == null) {
                    cuentaPrincipal = new BalanceAccount(MainBalancesAccountsIDs.DELIVERY_EXPRESS, 100f);
                    DataBase.Contability.BalancesAccounts.BalancesAccounts().create(cuentaPrincipal);
                }

                /* Verificar y crear cuenta de Servicios Repartos */
                BalanceAccount cuentaServiciosRepartos = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(MainBalancesAccountsIDs.SERVICIOS_REPARTOS);
                if (cuentaServiciosRepartos == null) {
                    cuentaServiciosRepartos = new BalanceAccount(MainBalancesAccountsIDs.SERVICIOS_REPARTOS, 0f);
                    DataBase.Contability.BalancesAccounts.BalancesAccounts().create(cuentaServiciosRepartos);
                }

                /* Verificar y crear cuenta de Seguros Órdenes */
                BalanceAccount cuentaSegurosOrdenes = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(MainBalancesAccountsIDs.SERGUROS_ORDENES);
                if (cuentaSegurosOrdenes == null) {
                    cuentaSegurosOrdenes = new BalanceAccount(MainBalancesAccountsIDs.SERGUROS_ORDENES, 0f);
                    DataBase.Contability.BalancesAccounts.BalancesAccounts().create(cuentaSegurosOrdenes);
                }

                /* Verificar y crear cuenta de Seguros Repartidores */
                BalanceAccount cuentaSegurosRepartidores = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(MainBalancesAccountsIDs.SEGUROS_REPARTIDORES);
                if (cuentaSegurosRepartidores == null) {
                    cuentaSegurosRepartidores = new BalanceAccount(MainBalancesAccountsIDs.SEGUROS_REPARTIDORES, 0f);
                    DataBase.Contability.BalancesAccounts.BalancesAccounts().create(cuentaSegurosRepartidores);
                }

                /* Verificar y crear cuenta de Caja Chica */
                BalanceAccount cajachica = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(MainBalancesAccountsIDs.CAJA_CHICA);
                if (cajachica == null) {
                    cajachica = new BalanceAccount(MainBalancesAccountsIDs.CAJA_CHICA, 0f);
                    DataBase.Contability.BalancesAccounts.BalancesAccounts().create(cajachica);
                }

                /* Verificar y crear cuenta de Emergencias */
                BalanceAccount emergencias = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(MainBalancesAccountsIDs.EMERGENCIAS);
                if (emergencias == null) {
                    emergencias = new BalanceAccount(MainBalancesAccountsIDs.EMERGENCIAS, 0f);
                    DataBase.Contability.BalancesAccounts.BalancesAccounts().create(emergencias);
                }

                /* Verificar y crear cuenta de Cuotas Repartidores */
                BalanceAccount cuotasRepas = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(MainBalancesAccountsIDs.CUOTAS_REPARTIDORES);
                if (cuotasRepas == null) {
                    cuotasRepas = new BalanceAccount(MainBalancesAccountsIDs.CUOTAS_REPARTIDORES, 0f);
                    DataBase.Contability.BalancesAccounts.BalancesAccounts().create(cuotasRepas);
                }

                /* Verificar y crear cuenta de Cuotas Negocios */
                BalanceAccount cuotasBussines = DataBase.Contability.BalancesAccounts.BalancesAccounts().read(MainBalancesAccountsIDs.CUOTAS_NEGOCIOS);
                if (cuotasBussines == null) {
                    cuotasBussines = new BalanceAccount(MainBalancesAccountsIDs.CUOTAS_NEGOCIOS, 0f);
                    DataBase.Contability.BalancesAccounts.BalancesAccounts().create(cuotasBussines);
                }
            }

            public static GenericDao<BalanceAccount, String> BalancesAccounts() {

                GenericDao<BalanceAccount, String> dao = contability.getDao(BalanceAccount.class);
                return dao;

            }

        }

        public static class Transacctions {

            public static GenericDao<Transacction, String> Transacctions() {

                GenericDao<Transacction, String> dao = contability.getDao(Transacction.class);
                return dao;

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

                GenericDao<StorableOrder, String> dao = ordersHistory.getDao(StorableOrder.class);
                return dao;

            }
        }

    }

}
