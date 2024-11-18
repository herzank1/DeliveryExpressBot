/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects;

import com.deliveryexpress.de.DataBase;
import com.deliveryexpress.de.DeliveriesControl;
import com.deliveryexpress.de.Global;
import com.deliveryexpress.de.OperationsMagnament;
import com.deliveryexpress.de.OrdersControl;
import com.deliveryexpress.de.OrdersControl.Order;
import com.deliveryexpress.telegram.Messenger;
import com.deliveryexpress.telegram.Messenger.MessageMenu;
import com.deliveryexpress.telegram.Messenger.MessageMenu.Button;
import com.deliveryexpress.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author DeliveryExpress
 */
public class TelegramUser {

    String id, bot;
    boolean blackList;
    String accountType;
    String accountId;

    public TelegramUser() {
    }

    public TelegramUser(String id, String bot) {
        this.id = id;
        this.bot = bot;
        this.blackList = false;
        this.accountType = null;
        this.accountId = null;
    }

    public TelegramUser(String id, String bot, boolean blackList, String accountType, String accountId) {
        this.id = id;
        this.bot = bot;
        this.blackList = blackList;
        this.accountType = accountType;
        this.accountId = accountId;
    }

    public String getId() {
        return id;
    }

    public String getBot() {
        return bot;
    }

    public boolean isBlackList() {
        return blackList;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBot(String bot) {
        this.bot = bot;
    }

    public void setBlackList(boolean blackList) {
        this.blackList = blackList;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountType() {

        if (accountType != null) {
            return accountType;
        } else {
            return AccountType.NOT_REGISTRED;
        }

    }

    public String getAccountId() {
        return accountId;
    }
    
   

    public Bussines getBussines() {
        if (this.accountType.equals(AccountType.BUSSINES)) {
            return DataBase.Bussiness.getBussinesByAccountId(this.accountId);
        } else {
            return null;
        }

    }

    public DeliveryMan getDeliveryMan() {
        if (this.accountType.equals(AccountType.DELIVERYMAN)) {
            return DeliveriesControl.getDeliveryManById(this.accountId);
        } else {
            return null;
        }

    }

    public Moderator getModerator() {

        if (this.accountType.equals(AccountType.MODERATOR)) {
            return DataBase.Moderators.getModeratorByAccountId(this.accountId);
        } else {
            return null;
        }
    }

    public ArrayList<String> toSqlArrayListValues() {

        ArrayList<String> values = new ArrayList<>();
        values.add("'" + this.id + "'");
        values.add("'" + this.bot + "'");
        /*cast boolean to int  0||1*/
        int myInt = this.blackList ? 1 : 0;
        values.add("" + String.valueOf(myInt) + "");
        values.add("'" + this.accountType + "'");
        values.add("'" + this.accountId + "'");

        return values;

    }

    @Override
    public String toString() {
        return "TelegramUser" 
                + "\n📃 id = " + id 
                + "\n🤖 bot = " + bot 
                + "\n📓 blackList = " + blackList 
                + "\n🏷 accountType = " + accountType 
                + "\n🗒 accountId = " + accountId;
    }

    public Group getGroup() {
    return new Group(this.id);
    
    }

    public String toStringDetailed() {
        
        String telegramUser = this.toString();
        String account="";
        String balanceAccount = "";
        
        
        switch(this.accountType){
        
        case TelegramUser.AccountType.DELIVERYMAN:
                                DeliveryMan deliveryMan = this.getDeliveryMan();
                             
                                account = deliveryMan.toString();
                                balanceAccount = deliveryMan.getBalanceAccount().toString();
                              
                                break;
                                
        case TelegramUser.AccountType.BUSSINES:
                                Bussines bussines = this.getBussines();
                                account = bussines.toString();
                                balanceAccount = bussines.getBalanceAccount().toString();
                                break;
                                
        case TelegramUser.AccountType.MODERATOR:
                                Moderator moderator = this.getModerator();
                                account = moderator.toString();
                                balanceAccount = moderator.getBalanceAccount().toString();
                                break;                        
        
        }
     
        
        return String.join("\n\n", new String[]{telegramUser,account,balanceAccount});

    
    }
    
     public BalanceAccount getBalanceAccount() {

        BalanceAccount balanceAccount = null;

        switch (this.accountType) {

            case TelegramUser.AccountType.DELIVERYMAN:
                DeliveryMan deliveryMan = this.getDeliveryMan();
                balanceAccount = deliveryMan.getBalanceAccount();

                break;

            case TelegramUser.AccountType.BUSSINES:
                Bussines bussines = this.getBussines();

                balanceAccount = bussines.getBalanceAccount();
                break;

            case TelegramUser.AccountType.MODERATOR:
                Moderator moderator = this.getModerator();

                balanceAccount = moderator.getBalanceAccount();
                break;

        }

        return balanceAccount;

    }
    
    /***
     * 
     * @return 
     */

    public MessageMenu getTelegramUserMenuForMod() {
        
        String accountNumber = this.getBalanceAccount().getAccountNumber();
        MessageMenu menu = new MessageMenu();

        if (this.isBlackList()) {
            menu.addAbutton(new Button("✅ Quitar de la Lista negra", "/blacklist&" + this.id + "&0"), true);
        } else {
            menu.addAbutton(new Button("🚫 Agregar a la Lista negra", "/blacklist&" + this.id + "&1"), true);
        }
       // menu.addAbutton(new Button("🤖 Set Bot", "/setbot&" + this.id), true);
        menu.addAbutton(new Button("💵 Transferir", "/transfer&" + accountNumber), true);
        menu.addAbutton(new Button("🔀 Movimientos", "/movements&" + accountNumber), true);

        String status = this.getAccountStatus();
        switch (status) {
            case TelegramUser.AccountStatus.ACTIVE:
                menu.addAbutton(new Button("❌ DESACTIVAR", "/setstatus&" + this.id + "&" + TelegramUser.AccountStatus.INACTIVE), true);
                break;
            case TelegramUser.AccountStatus.PAUSED:
            case TelegramUser.AccountStatus.INACTIVE:
                menu.addAbutton(new Button("✅ ACTIVAR", "/setstatus&" + this.id + "&" + TelegramUser.AccountStatus.ACTIVE), true);
                break;
        }
        
        if(this.isBussines()){
         Bussines bussines = this.getBussines();
         menu.addAbutton(new Button("🌐 Cambiar Area", "/changearea&" + bussines.accountId), true);

        }
        
        
        return menu;
    }

    public String getAccountStatus() {

        String status = TelegramUser.AccountStatus.INACTIVE;

        switch (this.accountType) {

            case TelegramUser.AccountType.DELIVERYMAN:
                status = this.getDeliveryMan().getAccountStatus();
                break;

            case TelegramUser.AccountType.BUSSINES:

                status = this.getBussines().getAccountStatus();
                break;

            case TelegramUser.AccountType.MODERATOR:

                status = this.getModerator().getAccountStatus();

                break;

        }
        return status;

    }

    private boolean isBussines() {
        return this.accountType.equals(AccountType.BUSSINES);
    }

    public static interface AccountStatus {

        String ACTIVE = "ACTIVE";
        String INACTIVE = "INACTIVE";
        String PAUSED = "PAUSED";

    }

    public static interface AccountType {

        String IS_GROUP = "IS_GROUP";
        String BUSSINES = "BUSSINES";
        String DELIVERYMAN = "DELIVERYMAN";
        String MODERATOR = "MODERATOR";
        String NOT_REGISTRED = "NOT_REGISTRED";

    }

    public static interface Fees {

        float DEFAULT_BUSSINES_SERVICE_COST = 15F;
        float DEFAULT_BUSSINES_KM_BASE_COST = 40f;
        int DEFAULT_BUSSINES_KM_BASE = 4;
        float DEFAULT_BUSSINES_KM_EXTRA_COST = 7f;
        String DEFAULT_BUSSINES_SERVICE_TYPE = Bussines.ServiceType.PER_ORDER;

    }

    /**
     *
     * @author DeliveryExpress
     */
    public static class BalanceAccount {

        String accountNumber, ownerAccountId, alias;
        float balance;

        public BalanceAccount(String accountNumber) {
            this.accountNumber = accountNumber;
            this.ownerAccountId = "EMPTY";
            this.alias = "Cuenta";
            this.balance = 0f;
        }
        
         public BalanceAccount(String accountNumber, String ownerAccount) {

            this.accountNumber = accountNumber;
            this.ownerAccountId = ownerAccount;
            this.alias = "Cuenta";
            this.balance = 0f;

        }

        public BalanceAccount(String accountNumber, String ownerAccountId, String alias) {
            this.accountNumber = accountNumber;
            this.ownerAccountId = ownerAccountId;
            this.alias = alias;
            this.balance = 0f;
        }

        public BalanceAccount(String accountNumber, String ownerAccountId, String alias, float balance) {
            this.accountNumber = accountNumber;
            this.ownerAccountId = ownerAccountId;
            this.alias = alias;
            this.balance = balance;
        }

      

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getOwnerAccountId() {
            return ownerAccountId;
        }

        public String getAlias() {
            return alias;
        }

        public float getBalance() {
            return balance;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public void setOwnerAccountId(String ownerAccountId) {
            this.ownerAccountId = ownerAccountId;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public void setBalance(float balance) {
            this.balance = balance;
        }
        
        

        public ArrayList<String> toSqlArrayListValues() {

            ArrayList<String> values = new ArrayList<>();
            values.add("'" + this.accountNumber + "'");
            values.add("'" + this.ownerAccountId + "'");
            values.add("'" + this.alias + "'");
            values.add("'" + String.valueOf(this.balance) + "'");

            return values;

        }

        @Override
        public String toString() {
            return  "📄 accountNumber = " + accountNumber 
                    + "\n📄 ownerAccountId = " + ownerAccountId 
                    + "\n📄 alias = " + alias 
                    + "\n💵 balance = " + balance;
                    
        }
        
        public ArrayList<Transacction> getTransacctions() {
            return DataBase.BalancesAccounts.getTransacctionsOf(this.accountNumber);
        }
        
        public String getTransacctionsToString() {
            ArrayList<Transacction> transacctionsOf = DataBase.BalancesAccounts.getTransacctionsOf(this.accountNumber);

            ArrayList<String> collect = transacctionsOf.stream().map(c -> c.toString()).collect(Collectors.toCollection(ArrayList::new));
            Collections.reverse(collect);

            if (collect.size() > Global.Global().max_users_transacctions_view) {
                List<String> subList = collect.subList(0, Global.Global().max_users_transacctions_view);
                return String.join("\n\n", subList);
            } else {
                return String.join("\n\n", collect);
            }

        } 

        public boolean chargeAmount(float mount, String concept, String ref) {
         
            Transacction t = new Transacction();
            t.setFrom(this.accountNumber);
            t.setTo(Global.Global().mainBalanceAccount);
            t.setMount(mount);
            t.setConcept(concept);
            t.setReference(ref);
            return t.execute();
            
            
        
        }

    }

    public static class Customer {

        String phone, name, lastAddress;
        String lastNote;

        public Customer() {
        }

        public Customer(String phone, String name, String lastAddress, String lastNote) {
            this.phone = phone;
            this.name = name;
            this.lastAddress = lastAddress;
            this.lastNote = lastNote;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastAddress() {
            return lastAddress;
        }

        public void setLastAddress(String lastAddress) {
            this.lastAddress = lastAddress;
        }

        public String getLastNote() {
            return lastNote;
        }

        public void setLastNote(String lastNote) {
            this.lastNote = lastNote;
        }

        @Override
        public String toString() {
            return String.join("\n", new String[]{"👦 Cliente " + this.name, "📞 " + this.phone, "📌 " + this.lastAddress, "📝 Nota:" + this.lastNote});

        }

        public String getNavigateUrl() {

            return Utils.GoogleMapsUtils.navigateToUrl(this.lastAddress);
        }

    }

    public static class Bussines {

        String accountId, name, phone, address;
        String balanceAccountNumber;
        String accountStatus;
        String areaId;

        String serviceType;

        float serviceCost, kmBaseCost, kmExtraCost;
        int kmBase; //Banderazo

        String ownerName, schedule;
        String tags;

        public Bussines() {
        }

        private String getShortName() {

            return Utils.getShortName(this.name);

        }

        public Bussines(String name, String phone, String address, String area) {
            this.name = name;
            this.accountId = "AID-" + getShortName() + "-" + DataBase.Bussiness.countBussines();
            this.phone = phone;
            this.address = address;
            this.balanceAccountNumber = "BA-" + getShortName() + "-" + DataBase.BalancesAccounts.countBalancesAccounts();
            this.accountStatus = AccountStatus.ACTIVE;
            this.areaId = area;
            this.serviceCost = Fees.DEFAULT_BUSSINES_SERVICE_COST;
            this.kmBaseCost = Fees.DEFAULT_BUSSINES_KM_BASE_COST;
            this.kmExtraCost = Fees.DEFAULT_BUSSINES_KM_EXTRA_COST;
            this.kmBase = Fees.DEFAULT_BUSSINES_KM_BASE;
            this.ownerName = "NULL";
            this.schedule = "NULL";
            this.tags = "NULL";
            this.serviceType = Fees.DEFAULT_BUSSINES_SERVICE_TYPE;
        }

        public Bussines(String accountId, String name, String phone, String address, String balanceAccountNumber, String accountStatus, String area, String serviceType, float serviceCost, float kmBaseCost, float kmExtraCost, int kmBase, String ownerName, String schedule, String flags) {
            this.accountId = accountId;
            this.name = name;
            this.phone = phone;
            this.address = address;
            this.balanceAccountNumber = balanceAccountNumber;
            this.accountStatus = accountStatus;
            this.areaId = area;
            this.serviceType = serviceType;
            this.serviceCost = serviceCost;
            this.kmBaseCost = kmBaseCost;
            this.kmExtraCost = kmExtraCost;
            this.kmBase = kmBase;
            this.ownerName = ownerName;
            this.schedule = schedule;
            this.tags = flags;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public BalanceAccount getBalanceAccount() {

            return DataBase.BalancesAccounts.getBalanceAccount(this.balanceAccountNumber);

        }

        public String getBalanceAccountNumber() {
            return balanceAccountNumber;
        }

        public void setBalanceAccountNumber(String balanceAccountNumber) {
            this.balanceAccountNumber = balanceAccountNumber;
        }

        public String getAccountStatus() {
            return accountStatus;
        }

        public void setAccountStatus(String accountStatus) {
            this.accountStatus = accountStatus;
        }

        public String getAreaId() {
            return areaId;
        }

        public void setAreaId(String area) {
            this.areaId = area;
        }

        public float getServiceCost() {
            return serviceCost;
        }

        public void setServiceCost(float serviceCost) {
            this.serviceCost = serviceCost;
        }

        public float getKmBaseCost() {
            return kmBaseCost;
        }

        public void setKmBaseCost(float kmBaseCost) {
            this.kmBaseCost = kmBaseCost;
        }

        public float getKmExtraCost() {
            return kmExtraCost;
        }

        public void setKmExtraCost(float kmExtraCost) {
            this.kmExtraCost = kmExtraCost;
        }

        public int getKmBase() {
            return kmBase;
        }

        public void setKmBase(int kmBase) {
            this.kmBase = kmBase;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public void setOwnerName(String ownerName) {
            this.ownerName = ownerName;
        }

        public String getSchedule() {
            return schedule;
        }

        public void setSchedule(String schedule) {
            this.schedule = schedule;
        }

        public Tags getTags() {
            return new Tags(this.tags);
        }

        public void setTags(String flags) {
            this.tags = flags;
        }

        public OperationAreaMagnament getArea() {
            return OperationsMagnament.getOAMbyId(this.areaId);
        }

        public String getPaymentMenssage() {
            return getArea().getBussinesPaymentMessage();
        }

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

        public MessageMenu getDeliveryTable() {
            MessageMenu menu = new MessageMenu();
            menu.addAbutton(new Button(String.valueOf(this.kmBase) + " km - " + String.valueOf(this.kmBaseCost), String.valueOf(this.kmBaseCost)), true);
            for (int i = 1; i <= 10; i++) {
                String value = String.valueOf(this.kmBaseCost + (i * this.kmExtraCost));
                String text = String.valueOf(this.kmBase + i) + " km - " + value;

                menu.addAbutton(new Button(text, value), true);
            }
            return menu;

        }

        public String getNavigateUrl() {

            return Utils.GoogleMapsUtils.navigateToUrl(this.address);

        }

        public void sendMessage(String text, MessageMenu menu) {
            Messenger.TelegramAction.sendMessageToAccount(getParentsIds(), text, menu);

        }

        /**
         * *
         *
         * @return regresa los id de telegram relacionados a esta cuenta usarse
         * para enviar mensajes a este objecto desde otra cuenta indirectamente
         * o como notificacion
         */
        ArrayList<String> parentsIds;

        public ArrayList<String> getParentsIds() {
            if (parentsIds != null) {

                return parentsIds;
            } else {

                parentsIds = DataBase.TelegramUsers.getAccountParentsId(this.accountId);
                return parentsIds;
            }

        }

        public boolean isActive() {
         
            return this.accountStatus.equals(TelegramUser.AccountStatus.ACTIVE);
        
        }

        public static interface ServiceType {

            String WEEKLY = "WEEKLY";
            String MONTHLY = "MONTHLY";
            String PER_ORDER = "PER_ORDER";
        }

        @Override
        public String toString() {
            return String.join("\n", new String[]{"🍔 " + this.name, "📞 " + this.phone, "📌 " + this.address, "Area: " + this.areaId});

        }

        public ArrayList<String> toSqlArrayListValues() {

            ArrayList<String> values = new ArrayList<>();
            values.add("'" + this.accountId + "'");
            values.add("'" + this.name + "'");
            values.add("'" + this.phone + "'");
            values.add("'" + this.address + "'");
            values.add("'" + this.balanceAccountNumber + "'");
            values.add("'" + this.accountStatus + "'");
            values.add("'" + this.areaId + "'");
            values.add("'" + this.serviceType + "'");
            values.add("" + String.valueOf(this.serviceCost) + "");
            values.add("" + String.valueOf(this.kmBaseCost) + "");
            values.add("" + String.valueOf(this.kmExtraCost) + "");
            values.add("" + String.valueOf(this.kmBase) + "");
            values.add("'" + this.ownerName + "'");
            values.add("'" + this.schedule + "'");
            values.add("'" + this.tags + "'");

            return values;

        }

    }

    public static class DeliveryMan {

        String accountId, name, phone, deliveryManLevel, balanceAccountNumber, accountStatus, tags;
        Session session;

        public DeliveryMan() {
        }

        public DeliveryMan(String name, String phone) {
            this.name = name;
            this.accountId = "AID-" + getShortName() + "-" + DataBase.Deliveries.countDeliveries();
            this.phone = phone;
            this.deliveryManLevel = DeliveryMan.DeliveryManLevel.TUERCA;
            this.balanceAccountNumber = "BA-" + getShortName() + "-" + DataBase.BalancesAccounts.countBalancesAccounts();
            this.accountStatus = AccountStatus.ACTIVE;
            this.tags = "NULL";

        }

        public DeliveryMan(String accountId, String name, String phone, String deliveryManLevel, String balanceAccountNumber, String accountStatus) {
            this.accountId = accountId;
            this.name = name;
            this.phone = phone;
            this.deliveryManLevel = deliveryManLevel;
            this.balanceAccountNumber = balanceAccountNumber;
            this.accountStatus = accountStatus;

        }

        public DeliveryMan(String accountId, String name, String phone, String deliveryManLevel, String balanceAccountNumber, String accountStatus, String flags) {
            this.accountId = accountId;
            this.name = name;
            this.phone = phone;
            this.deliveryManLevel = deliveryManLevel;
            this.balanceAccountNumber = balanceAccountNumber;
            this.accountStatus = accountStatus;
            this.tags = flags;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getDeliveryManLevel() {
            return deliveryManLevel;
        }

        public void setDeliveryManLevel(String deliveryManLevel) {
            this.deliveryManLevel = deliveryManLevel;
        }

        public String getBalanceAccountNumber() {
            return balanceAccountNumber;
        }

        public void setBalanceAccountNumber(String balanceAccountNumber) {
            this.balanceAccountNumber = balanceAccountNumber;
        }

        public String getAccountStatus() {
            return accountStatus;
        }

        public void setAccountStatus(String accountStatus) {
            this.accountStatus = accountStatus;
        }

        public Tags getTags() {
            return new Tags(this.tags);
        }

        public void setTags(String flags) {
            this.tags = flags;
        }

        public Session getSession() {
            if (this.session == null) {
                this.session = new Session();
            }
            return session;

        }

        public void setSession(Session session) {
            this.session = session;
        }

        @Override
        public String toString() {
            return String.join("\n", new String[]{"🛵 " + this.name, "📞 " + this.phone, "⬆️ " + this.deliveryManLevel});

        }

        public BalanceAccount getBalanceAccount() {
            return DataBase.BalancesAccounts.getBalanceAccount(this.balanceAccountNumber);
        }

        private String getShortName() {

            return Utils.getShortName(this.name);
        }

        /**
         * *
         *
         * @return regresa los id de telegram relacionados a esta cuenta usarse
         * para enviar mensajes a este objecto desde otra cuenta indirectamente
         * o como notificacion
         */
        ArrayList<String> parentsIds;

        public ArrayList<String> getParentsIds() {
            if (parentsIds != null) {

                return parentsIds;
            } else {

                parentsIds = DataBase.TelegramUsers.getAccountParentsId(this.accountId);
                return parentsIds;
            }

        }
        
      
        public boolean equals(DeliveryMan obj) {
           
        return this.getAccountId().equals(obj.accountId);
    }

        public void sendMessage(String text, MessageMenu menu) {
            Messenger.TelegramAction.sendMessageToAccount(getParentsIds(), text, menu);

        }

        public String getUrlLocation() {
            return this.getSession().getLocation().getUrlLocation();
        }

        public ArrayList<Order> getCurrentOrders() {
            Predicate<Order> predicate = (c -> c.getDeliveryMan() != null && c.getDeliveryMan().getAccountId().equals(this.getAccountId()));
            return OrdersControl.getOrderOf(predicate);

        }
        
        public int ordersCount() {
            return getCurrentOrders().size();
        }

        public boolean isActive() {
           return this.accountStatus.equals(TelegramUser.AccountStatus.ACTIVE);
         }

        public static interface DeliveryManLevel {

            String TUERCA = "TUERCA";
            String HERRAMIENTA = "HERRAMIENTA";
            String MAQUINA = "MAQUINA";
            String ROBOT = "ROBOT";
            String ANDROIDE = "ANDROIDE";
            String CYBORG = "CYBORG";
            String HUMANO = "HUMANO";
            String SEMIDIOS = "SEMIDIOS";
            String DIOS = "DIOS";

        }

        public ArrayList<String> toSqlArrayListValues() {

            ArrayList<String> values = new ArrayList<>();
            values.add("'" + this.accountId + "'");
            values.add("'" + this.name + "'");
            values.add("'" + this.phone + "'");
            values.add("'" + this.deliveryManLevel + "'");
            values.add("'" + this.balanceAccountNumber + "'");
            values.add("'" + this.accountStatus + "'");
            values.add("'" + this.tags + "'");

            return values;

        }

        public class Session {

            Location location;
            long lastUpdate;
            String status;

            public static final long LAST_UPDATE_TOLERANCE = 60;

            public Session() {
                location = null;
                lastUpdate = Utils.DateUtils.getUnixTimeStamp();
                String status = Session.Status.DISCONNECTED;
            }

            public Location getLocation() {
                return location;
            }

            public void setLocation(Location location) {
                this.location = location;
                this.lastUpdate = Utils.DateUtils.getUnixTimeStamp();
            }

            public long getLastUpdate() {
              
                    return this.lastUpdate;
               
            }

            public void setLastUpdate(long lastUpdate) {
                
                
                this.lastUpdate = lastUpdate;
            }

            public String getStatus() {
                
                if(status==null){
                
                return Status.DISCONNECTED;
                }
                
                if (isConected()) {
                    return Status.CONNECTED;
                } else {
                    return Status.DISCONNECTED;
                }

            }

            public void setStatus(String status) {
                this.status = status;
            }

            public static interface Status {

                String CONNECTED = "CONNECTED";
                String DISCONNECTED = "DISCONNECTED";

            }

            public boolean isConected() {
              return this.status.equals(Status.CONNECTED) && isSharingLocation();
            }

            public boolean isSharingLocation() {
                
                long unixTimeStamp = Utils.DateUtils.getUnixTimeStamp();
                
                long diff = unixTimeStamp - this.getLastUpdate();
                
                String data = "now:"+unixTimeStamp
                        +"\nlast update:"+this.getLastUpdate()
                        +"\ndiff:"+diff
                        +"\ntolernce:"+LAST_UPDATE_TOLERANCE;
                
                System.out.println(data);
                
                

                try {
                    return this.getLocation()!= null && (diff < LAST_UPDATE_TOLERANCE);

                } catch (java.lang.NullPointerException e) {
                    return false;
                }

            }

            @Override
            public String toString() {
                return "Session{" + "location=" + location.toString() + ", lastUpdate=" + lastUpdate + ", status=" + getStatus() + '}'
                        +"\nisSharingLocation:"+isSharingLocation();
            }
            
            

        }
        
        

    }

    public static class Moderator {

        String accountId, name, phone, balanceAccountNumber, accountStatus, area, tags;

        public Moderator() {
        }

        public Moderator(String accountId, String name, String phone, String balanceAccountNumber, String accountStatus, String area, String tags) {
            this.accountId = accountId;
            this.name = name;
            this.phone = phone;
            this.balanceAccountNumber = balanceAccountNumber;
            this.accountStatus = accountStatus;
            this.area = area;
            this.tags = tags;
        }

        public Moderator(String name, String phone) {

            this.name = name;
            this.accountId = "AID-" + getShortName() + "-" + DataBase.Moderators.countModerators();
            this.phone = phone;
            this.balanceAccountNumber = "BA-" + getShortName() + "-" + DataBase.BalancesAccounts.countBalancesAccounts();
            this.accountStatus = TelegramUser.AccountStatus.ACTIVE;
            this.tags = "";
            this.area = "";

        }

        private String getShortName() {

            return Utils.getShortName(this.name);

        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public OperationAreaMagnament getOAM() {
            return OperationsMagnament.getOAMbyId(area);
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getBalanceAccountNumber() {
            return balanceAccountNumber;
        }

        public void setBalanceAccountNumber(String balanceAccountNumber) {
            this.balanceAccountNumber = balanceAccountNumber;
        }

        public String getAccountStatus() {
            return accountStatus;
        }

        public void setAccountStatus(String accountStatus) {
            this.accountStatus = accountStatus;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        @Override
        public String toString() {
            return String.join("\n", new String[]{"⭐ " + this.name, "📞 " + this.phone, "📌 " + this.area});

        }

        public BalanceAccount getBalanceAccount() {

            return DataBase.BalancesAccounts.getBalanceAccount(this.getBalanceAccountNumber());

        }

            public ArrayList<String> toSqlArrayListValues() {

            ArrayList<String> values = new ArrayList<>();
            values.add("'" + this.accountId + "'");
            values.add("'" + this.name + "'");
            values.add("'" + this.phone + "'");
            values.add("'" + this.balanceAccountNumber + "'");
            values.add("'" + this.accountStatus + "'");
            values.add("'" + this.area + "'");
            values.add("'" + this.tags + "'");

            return values;

        }

        public boolean isActive() {
           
        return this.getAccountStatus().equals(TelegramUser.AccountStatus.ACTIVE);
        }

    }

    public static class Group {

        String telegramId;

        public Group(String telegramId) {
            this.telegramId = telegramId;
            
        }

        public OperationAreaMagnament getOam() {
            return OperationsMagnament.findOAM(telegramId);
        }

    }

}
