/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de;

import com.deliveryexpress.de.OrdersControl.Order;
import com.deliveryexpress.objects.OperationAreaMagnament;
import com.deliveryexpress.objects.Tags;
import com.deliveryexpress.objects.TelegramUser;
import com.deliveryexpress.objects.TelegramUser.BalanceAccount;
import com.deliveryexpress.objects.TelegramUser.Bussines;
import com.deliveryexpress.objects.TelegramUser.Customer;
import com.deliveryexpress.objects.TelegramUser.DeliveryMan;
import com.deliveryexpress.objects.TelegramUser.Moderator;
import com.deliveryexpress.objects.Transacction;
import com.deliveryexpress.telegram.Bot;
import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author DeliveryExpress
 */
public class DataBase {

    static Connection connection = null;
    static String dbFileName = "DeDataBaseV2024";

    public static void init() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
            System.out.println("DataBase OK");

        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static boolean executeUpdate(String sql) {

        try {
            Statement statement = connection.createStatement();
            int executeUpdate = statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            System.out.println(sql);
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;

    }

    public static boolean insert(String tableName, ArrayList<String> values) {

        String join = String.join(",", values);
        String sql = "INSERT INTO " + tableName + " VALUES(" + join + ")";

        return executeUpdate(sql);
    }

    public static int rowCount(String tableName) {

        ResultSet resultSet = getResultSet("SELECT Count(*) FROM " + tableName);

        try {
            while (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException ex) {

        }

        return 0;
    }

    public static ResultSet getResultSet(String sql) {

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static void createInitialOwnerMod(String telegramId) {
        Moderator ownerModerator = new Moderator("DIEGO MG", "6863095448");
        ownerModerator.setArea("GENERAL");
        ownerModerator.setTags("IS_OWNER");
        BalanceAccount ownerBa = new BalanceAccount(ownerModerator.getBalanceAccountNumber(), ownerModerator.getAccountId());
        ownerBa.setBalance(10000f);
        
        DataBase.BalancesAccounts
                .insertNewBalanceAccount(ownerBa);
        
        DataBase.Moderators.insertNew(ownerModerator);
        TelegramUser owner = new TelegramUser(telegramId, "@herzankBot");
        owner.setAccountId(ownerModerator.getAccountId());
        owner.setAccountType(TelegramUser.AccountType.MODERATOR);
        
        DataBase.TelegramUsers.insertNew(owner);
        
    }
    
    
     public static void createInitialBussines(String telegramId) {
        Bussines bu = new Bussines("(test) Hot Dogs Padrino", "6531457009","Chiltepinos Wings Galerías, Blvd. Lázaro Cárdenas 2200, El Porvenir, 21220 Mexicali, B.C.","TEST");
        bu.setTags("IS_TEST");
        BalanceAccount buba = new BalanceAccount(bu.getBalanceAccountNumber(), bu.getAccountId());
        buba.setBalance(10000f);
        
        DataBase.BalancesAccounts
                .insertNewBalanceAccount(buba);
        
        DataBase.Bussiness.insertNew(bu);
        TelegramUser businessTest = new TelegramUser(telegramId, "@herzankBot");
        businessTest.setAccountId(bu.getAccountId());
        businessTest.setAccountType(TelegramUser.AccountType.BUSSINES);
        
        DataBase.TelegramUsers.insertNew(businessTest);
        
    }  
     
     
     static void createInitialDelivery(String telegramId) {
     
        DeliveryMan dm = new DeliveryMan("Diego Villarreal", "6531457009");
        dm.setTags("IS_TEST");
        BalanceAccount dmba = new BalanceAccount(dm.getBalanceAccountNumber(), dm.getAccountId());
        dmba.setBalance(10000f);
        
        DataBase.BalancesAccounts
                .insertNewBalanceAccount(dmba);
        
        DataBase.Deliveries.insertNew(dm);
        TelegramUser businessTest = new TelegramUser(telegramId, "@herzankBot");
        businessTest.setAccountId(dm.getAccountId());
        businessTest.setAccountType(TelegramUser.AccountType.DELIVERYMAN);
        
        DataBase.TelegramUsers.insertNew(businessTest);
     
     
     }


    /**
     * *
     * Utilizar esta funcion para migrar las cuentas de las base de datos
     * previas a esta version esto importa los datos de las cuentas y crea los
     * TelegramUsers con sus respectivas cuentas de balance para esto es
     * necesario copiar y pegar a esta base de datos las tablas accounts y
     * bussines
     *
     * @param clearTables
     */
    static void migrate(boolean clearTables) {

        if (clearTables) {
            clearAccounts();
        }

        
        System.out.println("Init migration accoutns...");
        String sql = "SELECT * FROM accounts";

        BalanceAccount wallet0 = DataBase.BalancesAccounts.insertNewBalanceAccount("0x0000", "null");

        ResultSet resultSet = getResultSet(sql);

        try {
            while (resultSet.next()) {

                try {

                    TelegramUser tu = new TelegramUser();
                    tu.setId(resultSet.getString(1));
                    tu.setBlackList(false);

                    BalanceAccount ba = null;

                    Transacction t = new Transacction();
                    float balance = resultSet.getFloat(7);
                    t.setConcept("Corte");
                    t.setConcept("Migracion");

                    switch (resultSet.getString(5)) {

                        case "DELIVERY":
                            tu.setBot("@demxlrepartidorbot");
                            tu.setAccountType(TelegramUser.AccountType.DELIVERYMAN);

                            DeliveryMan dm = new DeliveryMan(resultSet.getString(2), resultSet.getString(4));
                            dm.setAccountStatus(resultSet.getString(8));
                            tu.setAccountId(dm.getAccountId());

                            ba = DataBase.BalancesAccounts.insertNewBalanceAccount(dm.getBalanceAccountNumber(), dm.getAccountId());
                            DataBase.Deliveries.insertNew(dm);
                            break;

                        case "BUSSINES":
                            tu.setBot("@DeliveryExpress1Bot");
                            tu.setAccountType(TelegramUser.AccountType.BUSSINES);
                            Bussines bu = new Bussines(resultSet.getString(2),
                                    resultSet.getString(4),
                                    resultSet.getString(3),
                                    "SIN AREA");
                            bu.setAreaId(findArea(tu.getId()));
                            bu.setAccountStatus(resultSet.getString(8));

                            tu.setAccountId(bu.getAccountId());
                            ba = DataBase.BalancesAccounts.insertNewBalanceAccount(bu.getBalanceAccountNumber(), bu.getAccountId());

                            DataBase.Bussiness.insertNew(bu);

                            break;

                        case "ACCOUNT":
                        case "OWNER":
                        case "MODERATOR":
                            tu.setAccountType(TelegramUser.AccountType.MODERATOR);
                            tu.setBot("@DeliveryExpressBot");
                            Moderator mo = new Moderator(resultSet.getString(2),
                                    resultSet.getString(4));

                            tu.setAccountId(mo.getAccountId());

                            ba = DataBase.BalancesAccounts.insertNewBalanceAccount(mo.getBalanceAccountNumber(), mo.getAccountId());
                            DataBase.Moderators.insertNew(mo);
                            break;

                    }

                    if (balance < 0) {
                        t.setFrom(ba.getAccountNumber());
                        t.setTo(wallet0.getAccountNumber());
                        t.setMount(Math.abs(balance));
                    } else {
                        t.setFrom(wallet0.getAccountNumber());
                        t.setTo(ba.getAccountNumber());
                        t.setMount(balance);

                    }
                    DataBase.BalancesAccounts.transfer(t);
                    System.out.println(tu.toString());

                    DataBase.TelegramUsers.insertNew(tu);

                } catch (Exception e) {
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);

        }

    }
    
    static void zeroAllBalances(){
    
    executeUpdate("DELETE FROM transactions");
    }
    
        static void migrateOnlyBalances() {

        System.out.println("Init migration balances...");
        
        executeUpdate("DELETE FROM transactions");
        
        String sql = "SELECT * FROM accounts";

      //  BalanceAccount wallet0 = DataBase.BalancesAccounts.insertNewBalanceAccount("0x0000", "null");

        ResultSet resultSet = getResultSet(sql);

        try {
            while (resultSet.next()) {

                try {

                    BalanceAccount mainBalanceAccount = Global.Global().getMainBalanceAccount();
                    TelegramUser tu = DataBase.TelegramUsers.getTelegramUserById(resultSet.getString(1));
                    BalanceAccount balanceAccount = tu.getBalanceAccount();

                    Transacction t = new Transacction();
                    float balance = resultSet.getFloat(7);
                    t.setConcept("Corte");
                    t.setConcept("Migracion");

                    switch (resultSet.getString(5)) {

                        case "BUSSINES":
                            
                            if(balance==0){
                            break;
                            }

                            if (balance < 0) {
                                t.setFrom(balanceAccount.getAccountNumber());
                                t.setTo(mainBalanceAccount.getAccountNumber());
                                t.setMount(Math.abs(balance));
                            } else {
                                t.setFrom(mainBalanceAccount.getAccountNumber());
                                t.setTo(balanceAccount.getAccountNumber());
                                t.setMount(balance);

                            }
                            DataBase.BalancesAccounts.transfer(t);

                            break;

                    }


                   
                    System.out.println(tu.toString());


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);

        }

    }
    

    /**
     * *
     * usarse solo para la migracion de las cuentas
     *
     * @param telegramId
     * @return
     */
    private static String findArea(String telegramId) {

        ResultSet resultSet = getResultSet("SELECT * FROM bussines WHERE telegramId = '" + telegramId + "'");
        try {
            return resultSet.getString(4);
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "SIN AREA";

    }

    public static void clearAccounts() {
        executeUpdate("DELETE FROM bussiness");
        executeUpdate("DELETE FROM telegramUsers");
        executeUpdate("DELETE FROM deliveries");
        executeUpdate("DELETE FROM moderators");
        executeUpdate("DELETE FROM balances");
        executeUpdate("DELETE FROM transactions");

    }

    public static void clearCache() {
      TelegramUsers.cache.clear();
    
    }

    static String getDataBaseInfo() {
        
        String data = "[telegramUsers]\n";
        ResultSet resultSet = getResultSet("PRAGMA table_info(telegramUsers);");
        ResultSet resultSet1 = getResultSet("PRAGMA table_info(deliveries);");
        ResultSet resultSet2 = getResultSet("PRAGMA table_info(bussiness);");
        ResultSet resultSet3 = getResultSet("PRAGMA table_info(moderators);");
        ResultSet resultSet4 = getResultSet("PRAGMA table_info(areas);");
      
        try {
            while(resultSet.next()){
                data+=resultSet.getString(2)+" "+resultSet.getString(3)+"\n";
            }
            
            data += "\n[deliveries]\n";
            
            while(resultSet1.next()){
                data+=resultSet1.getString(2)+" "+resultSet1.getString(3)+"\n";
            }
            
            data += "\n[bussiness]\n";
            
            while(resultSet2.next()){
                data+=resultSet2.getString(2)+" "+resultSet2.getString(3)+"\n";
            }
            
            data += "\n[moderators]\n";
            
            while(resultSet3.next()){
                data+=resultSet3.getString(2)+" "+resultSet3.getString(3)+"\n";
            }
            
            data += "\n[areas]\n";
            
            while(resultSet4.next()){
                data+=resultSet4.getString(2)+" "+resultSet4.getString(3)+"\n";
            }

        
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return data;
    
    
    }

    
    public static class TelegramUsers {

        static ArrayList<TelegramUser> cache = new ArrayList<>();

        public static TelegramUser getTelegramUser(Xupdate xupdate) {

            TelegramUser orElse = null;
            //buscamos en cache
            if (!cache.isEmpty()) {
                orElse = cache.stream().filter(c -> c.getId().equals(xupdate.getFromId())).findFirst().orElse(null);
                if (orElse != null) {

                    if (!orElse.getBot().equals(xupdate.getBotName())) {

                        setBot(xupdate.getFromId(), xupdate.getBotName());
                        orElse.setBot(xupdate.getBotName());
                    }
                    return orElse;
                }
            }

            //buscamos en base de datos, si no exite se registrara uno
            orElse = getTelegramUserXupdateIdFromDB(xupdate);
            if (orElse != null) {
                cache.add(orElse);
                return orElse;
            }

            return null;

        }

        /***
         * Busca el TU directamente en la base de datos, si no existe regresa null
         */
       
        public static TelegramUser getTelegramUserById(String id) {

            TelegramUser orElse = null;
            //buscamos en cache
            if (!cache.isEmpty()) {
                orElse = cache.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
                if (orElse != null) {

                    return orElse;
                }
            }

            //buscamos en base de datos, si no exite la base de datos registrara uno
            orElse = getTelegramUserByIdFromDB(id);
            if (orElse != null) {
                cache.add(orElse);
                return orElse;
            }

            return null;

        }

        private static TelegramUser getTelegramUserByIdFromDB(String id) {

            ResultSet resultSet = getResultSet("SELECT * FROM TelegramUsers WHERE id = '" + id + "'");
            try {
                if (resultSet != null & resultSet.next()) {

                    TelegramUser obj = new TelegramUser(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getBoolean(3),
                            resultSet.getString(4),
                            resultSet.getString(5));

                    return obj;

                } else {

                    return null;
                }
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

        }

        private static TelegramUser getTelegramUserXupdateIdFromDB(Xupdate xupdate) {

            ResultSet resultSet = getResultSet("SELECT * FROM TelegramUsers WHERE id = '" + xupdate.getFromId() + "'");
            try {
                if (resultSet != null & resultSet.next()) {

                    TelegramUser obj = new TelegramUser(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getBoolean(3),
                            resultSet.getString(4),
                            resultSet.getString(5));

                    return obj;

                } else {

                    TelegramUser ntu = new TelegramUser();

                    ntu.setBot(xupdate.getBotName());
                    ntu.setBlackList(false);

                    if (xupdate.isGroupMessage()) {

                        ntu.setId(xupdate.getFromId());
                        ntu.setAccountType("IS_GROUP");
                    } else {
                        ntu.setId(xupdate.getSenderId());
                        ntu.setAccountType("NOT_REGISTRED");

                    }

                    return insertNew(ntu);
                }
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

        }

        public static TelegramUser insertNew(TelegramUser tu) {
            if (tu.getId() == null) {

                return null;
            }
            insert("telegramUsers", tu.toSqlArrayListValues());
            return tu;

        }

        public static boolean setBot(String tuserId, String botName) {

            return updateTelegramUserField(tuserId, "bot", botName);
        }

        public static boolean setAccountType(String tuserId, String accountType) {

            return updateTelegramUserField(tuserId, "accountId", accountType);
        }

        public static boolean setAccount(String tuserId, String accountId) {

            return updateTelegramUserField(tuserId, "accountId", accountId);
        }

        public static boolean setBlackList(String tuserId, int value) {

            return updateTelegramUserField(tuserId, "blackList", String.valueOf(value));
        }

        public static boolean updateTelegramUserField(String tuserId, String fieldName, String value) {
            try {
                if (executeUpdate("UPDATE telegramUsers SET " + fieldName + " = '" + value + "' WHERE id ='" + tuserId + "'")) {

                    try {
                        cache.removeIf(c -> c.getId().equals(tuserId));
                    } catch (java.lang.NullPointerException e) {

                    }

                    return true;
                }

            } catch (Exception e) {
                return false;
            }
            return false;

        }

        public static ArrayList<String> getAccountParentsId(String accountId) {

            String sql = "SELECT * FROM telegramUsers WHERE accountId = '" + accountId + "'";

            ResultSet resultSet = getResultSet(sql);
            ArrayList<String> list = new ArrayList<>();
            try {
                while (resultSet.next()) {
                    list.add(resultSet.getString(1));

                }

            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);

            }

            return list;

        }

        static ArrayList<TelegramUser> getTelegramUsersByAccountType(String accountType) {

            String sql = "SELECT * FROM telegramUsers WHERE accountType = '" + accountType + "'";

            ResultSet resultSet = getResultSet(sql);
            ArrayList<TelegramUser> list = new ArrayList<>();
            try {
                while (resultSet.next()) {

                    TelegramUser obj = new TelegramUser(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getBoolean(3),
                            resultSet.getString(4),
                            resultSet.getString(5));

                    list.add(obj);

                }
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);

            }
            return list;
        }

        static boolean setAccountStatus(String id, String status) {

            TelegramUser telegramUserById = getTelegramUserById(id);
            boolean result = false;
            switch (telegramUserById.getAccountType()) {

                case TelegramUser.AccountType.DELIVERYMAN:

                    result = DataBase.Deliveries.setAccountStatus(telegramUserById.getAccountId(), status);

                    break;

                case TelegramUser.AccountType.BUSSINES:
                    result = DataBase.Bussiness.setAccountStatus(telegramUserById.getAccountId(), status);

                    break;

                case TelegramUser.AccountType.MODERATOR:
                    result = DataBase.Moderators.setAccountStatus(telegramUserById.getAccountId(), status);

                    break;

            }

            return result;

        }

    }

    public static class Bots {

        public static ArrayList<Bot> getBots(boolean testBots) {

            String sql = "SELECT * FROM bots";

            if (testBots) {
                sql = "SELECT * FROM bots WHERE tags LIKE '%IS_TEST_BOT%'";
            }

            ResultSet resultSet = getResultSet(sql);
            ArrayList<Bot> list = new ArrayList<>();
            try {
                while (resultSet.next()) {
                    Bot obj = new Bot(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4));

                    if (obj.getStatus().equals(Bot.BotStatus.ACTIVE)) {
                        list.add(obj);
                    }

                }

            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);

            }

            return list;

        }

    }

    public static class BalancesAccounts {

        public static BalanceAccount getBalanceAccountByOwnerAccountId(String ownerAccountId) {

            ResultSet resultSet = getResultSet("SELECT * FROM balances WHERE ownerAccountId = '" + ownerAccountId + "'");
            try {
                if (resultSet != null & resultSet.next()) {

                    BalanceAccount obj = new BalanceAccount(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getFloat(4));

                    return obj;

                } else {
                    return null;
                }
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

        }

        public static BalanceAccount getBalanceAccount(String accountNumber) {

            ResultSet resultSet = getResultSet("SELECT * FROM balances WHERE accountNumber = '" + accountNumber + "'");
            try {
                if (resultSet != null & resultSet.next()) {

                    BalanceAccount obj = new BalanceAccount(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getFloat(4));

                    return obj;

                } else {
                    return insertNew(accountNumber);
                }
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

        }
        
        public static BalanceAccount insertNewBalanceAccount(BalanceAccount ba) {
            
            insert("balances", ba.toSqlArrayListValues());
            return ba;

        }

        public static BalanceAccount insertNewBalanceAccount(String accountNumber, String ownerAccount) {
            BalanceAccount ba = new BalanceAccount(accountNumber, ownerAccount);
            insert("balances", ba.toSqlArrayListValues());
            return ba;

        }

        private static BalanceAccount insertNew(String accountNumber) {
            BalanceAccount tu = new BalanceAccount(accountNumber);
            insert("balances", tu.toSqlArrayListValues());
            return tu;

        }

        /***
         * 
         * @param fromAccountNumber origen de los fondos
         * @param accountNumber cuenta a liquidar
         * @return 
         */
        public static boolean liquidAccount(String fromAccountNumber,String accountNumber) {

            float balance = getBalanceAccount(accountNumber).getBalance();
            if (balance < 0) {
                Transacction t = new Transacction(fromAccountNumber, accountNumber, Math.abs(balance), "Pago de Servicio", "null");
                return transfer(t);
            } else {

                return false;
            }

        }

        /**
         * *
         * Realiza la tranferencia entre cuentas y lo registra en las
         * transacciones
         *
         * @param t
         * @return
         */
        public static boolean transfer(Transacction t) {

            boolean updateBalance = updateBalance(t.getFrom(), t.getMount(), Operation.SUB);
            boolean updateBalance2 = updateBalance(t.getTo(), t.getMount(), Operation.SUM);

            boolean insertTransfer = insertTransfer(t);

            return (updateBalance && updateBalance2 && insertTransfer);

        }

        private static boolean insertTransfer(Transacction t) {

            return insert("transactions", t.toSqlArrayListValues());

        }

        private static boolean updateBalance(String accountNumber, float value, String op) {

            try {
                if (executeUpdate("UPDATE balances SET balance = balance " + op + value + " WHERE accountNumber ='" + accountNumber + "'")) {
                    return true;
                }

            } catch (Exception e) {
                return false;
            }
            return false;

        }

        public static ArrayList<Transacction> getTransacctionsOf(String accountNumber) {

            ArrayList<Transacction> list = new ArrayList<>();

            String sql = "SELECT * FROM transactions WHERE [from] ='" + accountNumber + "' OR [to] = '" + accountNumber + "'";
            ResultSet resultSet = getResultSet(sql);

            try {
                while (resultSet.next()) {
                    Transacction obj = new Transacction();
                    obj.setId(resultSet.getString(1));
                    obj.setFrom(resultSet.getString(2));
                    obj.setTo(resultSet.getString(3));
                    obj.setMount(resultSet.getFloat(4));
                    obj.setConcept(resultSet.getString(5));
                    obj.setReference(resultSet.getString(6));
                    obj.setDate(resultSet.getString(7));

                    list.add(obj);

                }
                return list;
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            }

            return list;

        }

        private static interface Operation {

            String SUM = "+";
            String SUB = "-";

        }

        public static int countBalancesAccounts() {
            return rowCount("balances");
        }

    }

    public static class Bussiness {

        public static Bussines getBussinesByAccountId(String accountId) {

            return getBussinessByIdFromDB(accountId);
        }

        private static Bussines getBussinessByIdFromDB(String accountId) {

            ResultSet resultSet = getResultSet("SELECT * FROM bussiness WHERE accountId = '" + accountId + "'");
            try {
                if (resultSet != null & resultSet.next()) {

                    Bussines obj = new Bussines(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getString(5),
                            resultSet.getString(6),
                            resultSet.getString(7),
                            resultSet.getString(8),
                            resultSet.getFloat(9),
                            resultSet.getFloat(10),
                            resultSet.getFloat(11),
                            resultSet.getInt(12),
                            resultSet.getString(13),
                            resultSet.getString(14),
                            resultSet.getString(15));

                    return obj;

                }
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

            return null;

        }

        public static Bussines insertNew(Bussines b) {

            insert("bussiness", b.toSqlArrayListValues());
            return b;

        }

        public static int countBussines() {
            return rowCount("bussiness");
        }

        public static boolean setAccountStatus(String accountId, String status) {

            return updateBussinesField(accountId, "accountStatus", status);

        }

        public static boolean updateBussinesField(String accountId, String fieldName, String value) {
            try {
                if (executeUpdate("UPDATE bussiness SET " + fieldName + " = '" + value + "' WHERE accountId ='" + accountId + "'")) {

                    try {
                        //cache.removeIf(c-> c.getId().equals(tuserId));
                    } catch (java.lang.NullPointerException e) {

                    }

                    return true;
                }

            } catch (Exception e) {
                return false;
            }
            return false;

        }

    }

    public static class Deliveries {
        
        public static ArrayList<DeliveryMan> deliveries = new ArrayList<>();

        public static DeliveryMan getDeliveryManByAccountId(String deliveryId) {

            DeliveryMan orElse = deliveries.stream().filter(c -> c.getAccountId().equals(deliveryId)).findFirst().orElse(null);

            if (orElse != null) {
                return orElse;
            } else {

                DeliveryMan deliveryFromDb = getDeliveryManByIdFromDB(deliveryId);
                if (deliveryFromDb != null) {

                    deliveries.add(deliveryFromDb);
                    return deliveryFromDb;

                }

            }
            return null;

        }

        private static DeliveryMan getDeliveryManByIdFromDB(String accountId) {

            ResultSet resultSet = getResultSet("SELECT * FROM deliveries WHERE accountId = '" + accountId + "'");
            try {
                if (resultSet != null & resultSet.next()) {

                    DeliveryMan obj = new DeliveryMan(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getString(5),
                            resultSet.getString(6),
                            resultSet.getString(7));

                    return obj;

                }
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

            return null;

        }

        public static DeliveryMan insertNew(DeliveryMan d) {

            insert("deliveries", d.toSqlArrayListValues());
            return d;

        }

        public static int countDeliveries() {
            return rowCount("deliveries");
        }

        static ArrayList<DeliveryMan> getDeliveries() {

            ArrayList<DeliveryMan> list = new ArrayList<>();

            String sql = "SELECT * FROM deliveries";
            ResultSet resultSet = getResultSet(sql);

            try {
                while (resultSet.next()) {
                    DeliveryMan obj = new DeliveryMan(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getString(5),
                            resultSet.getString(6),
                            resultSet.getString(7));

                    list.add(obj);

                }
                return list;
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
            }

            return list;

        }

        private static boolean setAccountStatus(String accountId, String status) {

            return updateDeliveryManField(accountId, "accountStatus", status);

        }

        public static boolean updateDeliveryManField(String accountId, String fieldName, String value) {
            try {
                if (executeUpdate("UPDATE deliveries SET " + fieldName + " = '" + value + "' WHERE accountId ='" + accountId + "'")) {

                    try {
                        //cache.removeIf(c-> c.getId().equals(tuserId));
                    } catch (java.lang.NullPointerException e) {

                    }

                    return true;
                }

            } catch (Exception e) {
                return false;
            }
            return false;

        }

    }

    public static class Customers {

        public static Customer getCustomerByPhone(String phone) {

            return getCustomerByIdFromPhone(phone);
        }

        private static Customer getCustomerByIdFromPhone(String phone) {

            ResultSet resultSet = getResultSet("SELECT * FROM customers WHERE phone = '" + phone + "'");
            try {
                if (resultSet != null & resultSet.next()) {

                    Customer obj = new Customer(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4));

                    return obj;

                }
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

            return null;

        }

        public boolean updateCustomer(Customer customer) {

            String sql = "update customers Set lastAddress = '" + customer.getLastAddress()
                    + "' set lastNote = '" + customer.getLastNote() + "' where phone = '" + customer.getPhone() + "'";

            return executeUpdate(sql);

        }

    }

    public static class Moderators {

        public static Moderator getModeratorByAccountId(String accountId) {

            return getModeratorByAccountIdFromDb(accountId);
        }

        private static Moderator getModeratorByAccountIdFromDb(String accountId) {

            ResultSet resultSet = getResultSet("SELECT * FROM moderators WHERE accountId = '" + accountId + "'");
            try {
                if (resultSet != null & resultSet.next()) {

                    Moderator obj = new Moderator(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getString(5),
                            resultSet.getString(6),
                            resultSet.getString(7));

                    return obj;

                }
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

            return null;

        }

        public static int countModerators() {
            return rowCount("moderators");
        }

        public static boolean setAccountStatus(String accountId, String status) {

            return updateModeratorField(accountId, "accountStatus", status);

        }

        public static boolean updateModeratorField(String accountId, String fieldName, String value) {
            try {
                if (executeUpdate("UPDATE moderators SET " + fieldName + " = '" + value + "' WHERE accountId ='" + accountId + "'")) {

                    try {
                        //cache.removeIf(c-> c.getId().equals(tuserId));
                    } catch (java.lang.NullPointerException e) {

                    }

                    return true;
                }

            } catch (Exception e) {
                return false;
            }
            return false;

        }

        public static Moderator insertNew(Moderator m) {

            insert("moderators", m.toSqlArrayListValues());
            return m;

        }

    }

    public static class Areas {

        public static OperationAreaMagnament getAreaById(String areaId) {

            ResultSet resultSet = getResultSet("SELECT * FROM areas WHERE id = '" + areaId + "'");
            try {
                if (resultSet != null & resultSet.next()) {

                    OperationAreaMagnament obj = new OperationAreaMagnament(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getString(5),
                            resultSet.getString(6),
                            resultSet.getString(7),
                            resultSet.getString(8),
                            resultSet.getString(9),
                            resultSet.getString(10),
                            resultSet.getString(11));

                    return obj;

                }
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

            return null;

        }

        static ArrayList<OperationAreaMagnament> getAll() {

            ArrayList<OperationAreaMagnament> list = new ArrayList<>();
            ResultSet resultSet = getResultSet("SELECT * FROM areas ");
            try {
                while (resultSet.next()) {

                    OperationAreaMagnament obj = new OperationAreaMagnament(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4),
                            resultSet.getString(5),
                            resultSet.getString(6),
                            resultSet.getString(7),
                            resultSet.getString(8),
                            resultSet.getString(9),
                            resultSet.getString(10),
                            resultSet.getString(11));

                    list.add(obj);

                }
            } catch (SQLException ex) {
                Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

            return list;

        }

    }

    public static class Orders {

        public static StorableOrder insertNew(StorableOrder so) {

            insert("ordersHistory", so.toSqlArrayListValues());
            return so;

        }

        static void saveOrder(Order o) {

            insertNew(new StorableOrder(o));

        }

        static class StorableOrder {

            String id, status, creationDate, closeDate, bussinesAid, deliveryAid, customerAid;
            float orderCost, deliveryCost;
            String logs;

            public StorableOrder(Order o) {

                this.id = o.getId();
                this.status = o.getStatus();
                this.creationDate = o.getCreationDate();
                this.closeDate = Utils.DateUtils.getNowDate();
                this.bussinesAid = o.getBusssines().getAccountId();
                this.deliveryAid = Objects.toString(o.getDeliveryMan().getAccountId(), "Sin repartidor");
                this.customerAid = o.getCustomer().getPhone();
                this.orderCost = o.getOrderCost();
                this.deliveryCost = o.getDeliveryCost();
                this.logs = o.getLogs();
            }

            public StorableOrder(String id, String status, String creationDate, String closeDate, String bussinesAid, String deliveryAid, String customerAid, float orderCost, float deliveryCost, String logs) {
                this.id = id;
                this.status = status;
                this.creationDate = creationDate;
                this.closeDate = closeDate;
                this.bussinesAid = bussinesAid;
                this.deliveryAid = deliveryAid;
                this.customerAid = customerAid;
                this.orderCost = orderCost;
                this.deliveryCost = deliveryCost;
                this.logs = logs;
            }

            public ArrayList<String> toSqlArrayListValues() {

                ArrayList<String> values = new ArrayList<>();
                values.add("'" + this.id + "'");
                values.add("'" + this.status + "'");
                values.add("'" + this.creationDate + "'");
                values.add("'" + this.closeDate + "'");
                values.add("'" + this.bussinesAid + "'");
                values.add("'" + this.deliveryAid + "'");
                values.add("'" + this.customerAid + "'");
                values.add("" + this.orderCost + "");
                values.add("" + this.deliveryCost + "");
                values.add("'" + this.logs + "'");

                return values;

            }

        }

    }

}
