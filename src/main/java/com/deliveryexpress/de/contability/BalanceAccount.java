/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.contability;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.monge.xsqlite.xsqlite.BaseDao;
import com.monge.xsqlite.xsqlite.GenericDao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BalanceAccount extends BaseDao {

    @DatabaseField(id = true)
    String accountNumber;
    @DatabaseField
    float balance;

    public BalanceAccount() {
        this.accountNumber = UUID.randomUUID().toString();
        this.balance = 0f;
    }

    public BalanceAccount(String accountNumber, float balance) {
        this.accountNumber = UUID.randomUUID().toString();
        this.balance = balance;
    }

    /**
     * Obtiene las transacciones relacionadas con esta cuenta.
     *
     * @param count el número máximo de transacciones a retornar.
     * @return un ArrayList con las transacciones encontradas, limitado por
     * count.
     */
    public ArrayList<Transacction> getTransacctions(int maxElements) {
        ArrayList<Transacction> transactions = new ArrayList<>();

        try {
            // Validar que count sea positivo
            if (maxElements <= 0) {
                Logger.getLogger(BalanceAccount.class.getName())
                        .log(Level.WARNING, "El parámetro count debe ser mayor a cero.");
                return transactions; // Retorna una lista vacía
            }

            // Obtener el DAO
            GenericDao<Transacction, ?> dao = Transacction.getDao(Transacction.class);
            Dao<Transacction, ?> finalDao = dao.getFinalDao();

            // Construir la consulta
            QueryBuilder<Transacction, ?> queryBuilder = finalDao.queryBuilder();
            queryBuilder.where().eq("from", this.accountNumber).or().eq("to", this.accountNumber);
            queryBuilder.limit((long) maxElements); // Limitar el número de resultados a 'count'

            // Ejecutar la consulta y convertir a ArrayList
            List<Transacction> queryResult = queryBuilder.query();
            transactions.addAll(queryResult);
        } catch (SQLException ex) {
            Logger.getLogger(BalanceAccount.class.getName())
                    .log(Level.SEVERE, "Error al obtener transacciones para la cuenta: " + this.accountNumber, ex);
        } catch (Exception ex) {
            Logger.getLogger(BalanceAccount.class.getName())
                    .log(Level.SEVERE, "Error inesperado al obtener transacciones.", ex);
        }

        return transactions;
    }

    public String toTelegramString() {
        StringBuilder sb = new StringBuilder();
        sb.append("🆔 " + this.accountNumber).append("\n");
        sb.append("💵 Saldo: " + this.balance).append("\n")
                .append("\n");

        return sb.toString();
    }

    public String getReference() {
        return this.accountNumber.toUpperCase().replace("-", "").substring(0, 7);
    }
    
    /***
     * Agrega fondos a la cuenta y actualiza en DB
     * @param amount
     * @param concept
     * @param ref 
     */
    public void addFounds(float amount,String concept,String ref){
      /*registramos trasnaccion*/
                Transacction t = new Transacction();
                t.setFrom(ContabilityControl.MAIN_BALANCE_ACCOUNT);
                t.setTo(this.accountNumber);
                t.setMount(amount);
                t.setConcept(concept);
                t.setReference(ref);
                t.create();
                /*actualizamos balance*/
                this.balance += amount;
                this.update();
    
    }
    
    /***
     * Extrae fondos  a la cuent ay actualiza db
     * @param amount
     * @param concept
     * @param ref 
     */
        public void subFounds(float amount,String concept,String ref){
      /*registramos trasnaccion*/
                Transacction t = new Transacction();
                t.setTo(ContabilityControl.MAIN_BALANCE_ACCOUNT);
                t.setFrom(this.accountNumber);
                t.setMount(amount);
                t.setConcept(concept);
                t.setReference(ref);
                t.create();
                /*actualizamos balance*/
                this.balance -= amount;
                this.update();
    
    }

}
