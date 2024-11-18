/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.quizes;

import com.deliveryexpress.de.DataBase;
import com.deliveryexpress.objects.TelegramUser;
import com.deliveryexpress.objects.TelegramUser.BalanceAccount;
import com.deliveryexpress.objects.Transacction;
import com.deliveryexpress.telegram.Messenger;
import com.deliveryexpress.telegram.Messenger.MessageMenu;
import com.deliveryexpress.telegram.Messenger.MessageMenu.Button;
import com.deliveryexpress.telegram.Xupdate;
import com.deliveryexpress.utils.Utils;

/**
 *
 * @author DeliveryExpress
 */
public class QuizModDirectTransfer extends Quiz{
    
    BalanceAccount balanceAccount;
    
    Transacction t = new Transacction();
    String userBalanceAccountId;

     public QuizModDirectTransfer(String userId,String BalanceAccountId) {

         
        
        super(userId);
        balanceAccount= DataBase.BalancesAccounts.getBalanceAccount("BA-DM-0");
        this.userBalanceAccountId = BalanceAccountId;
    }

    @Override
    void execute(Xupdate xupdate) {
        
        Messenger.TelegramAction ta = new Messenger.TelegramAction();
        ta.setAction(Messenger.TelegramAction.Action.SEND_MESSAGE);
        ta.setChatId(xupdate.getSenderId());
        
        switch (super.step) {
            
            case 0:
                ta.setText("Seleccione un opcion:");
                ta.setMenu(transferActionMenu());
                ta.execute();
                next();
                
                break;
            
            case 1:
                switch (xupdate.getText()) {

                    case "deposit":
                        t.setFrom(balanceAccount.getAccountNumber());
                        t.setTo(userBalanceAccountId);
                         ta.setText("Ingrese el monto");
                        ta.execute();
                        super.next();
                        break;

                    case "withdraw":
                        t.setFrom(userBalanceAccountId);
                        t.setTo(balanceAccount.getAccountNumber());
                         ta.setText("Ingrese el monto");
                        ta.execute();
                        super.next();
                        break;

                    case "liquid":

                        DataBase.BalancesAccounts.liquidAccount(balanceAccount.getAccountNumber(),userBalanceAccountId);
                        ta.setText("Se ha liquidado esta cuenta");
                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.execute();
                        super.destroy();
                        

                        break;

                }
                
                

                break;
                
            case 2:
                try {
                    t.setMount(Float.parseFloat(xupdate.getText()));
                    ta.setText("Ingrese el concepto o escribalo");
                    ta.setMenu(getConceptMenu());
                    ta.execute();
                    next();

                } catch (java.lang.NumberFormatException e) {

                    ta.setText("Error de formato, ingrese el monto sin signos ni letras.");
                    ta.execute();

                }
                
                break;

            case 3:
                t.setConcept(xupdate.getText());
                ta.setText("Ingrese la referencia");
                ta.setMenu(new MessageMenu().addAbutton(new Button("N/A","N/A"),false));
                ta.execute();
                next();

                break;
                
            case 4:
                t.setReference(xupdate.getText());
                ta.setText("Confirme la transaccion"
                        + "\n" + t.toString());
                ta.setMenu(MessageMenu.yesNo());
                ta.execute();
                next();

                break;

                
            case 5:
                if (Utils.isPositiveAnser(xupdate.getText())) {
                    boolean transfer = DataBase.BalancesAccounts.transfer(t);

                    if (transfer) {
                        ta.setText("Se ha transferido exitosamente!");
                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.execute();
                        super.destroy();

                    }else{
                        
                         ta.setText("Error!");
                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.execute();
                        super.destroy();
                    
                    }

                }else{
                ta.setText("Trasnferencia cancelada!");
                        ta.setMenu(MessageMenu.okAndDeleteMessage());
                        ta.execute();
                        super.destroy();
                
                }

                break;    

        }

    }
    
    private  MessageMenu transferActionMenu(){
        
        MessageMenu menu = new MessageMenu();
        
        menu.addAbutton(new Button("➕ Depositar","deposit"), true);
        menu.addAbutton(new Button("➖ Retirar/Cargar","withdraw"), true);
        menu.addAbutton(new Button("🟢 Liquidar","liquid"), true);
        
        
        return menu;
    
    }

    private MessageMenu getConceptMenu() {
     MessageMenu menu = new MessageMenu();
     /*si el usuario es el que envia o se le carga*/
        if(t.getFrom().equals(this.userBalanceAccountId)){
            
            menu.addAbutton(new Button("Ajuste","Ajuste"), true);
            menu.addAbutton(new Button("Cambio de direccion","Cambio de direccion"), true);
            menu.addAbutton(new Button("Cargo manual de servicio","Cargo manual de servicio"), true);
        
        }else{
            
            menu.addAbutton(new Button("Pago de Servicio","Pago de Servicio"), true);
            menu.addAbutton(new Button("Compensacion","Compensacion"), true);
            menu.addAbutton(new Button("Reembolso orden","Reembolso orden"), true);
            menu.addAbutton(new Button("Deposito de Saldo","Deposito de Saldo"), true);
        
        }
    
        
        return menu;
    }
    

    
}
