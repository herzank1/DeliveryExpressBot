/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.de.contability;

import com.deliveryexpress.objects.users.Tuser;
import com.deliveryexpress.utils.Utils;
import com.google.gson.Gson;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.monge.tbotboot.objects.Receptor;
import com.monge.xsqlite.xsqlite.BaseDao;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Payment extends BaseDao {

    @DatabaseField(id = true)
    String id;
    @DatabaseField
    float amount;
    @DatabaseField
    String concept;
    @DatabaseField
    String balanceAccountNumber;
    @DatabaseField
    String status;
    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    byte[] img;
    @DatabaseField
    String metadata;/*json*/

    public Payment() {
    }

    /**
     * *.
     *
     * @param amount
     * @param concept
     * @param accountId
     */
    public Payment(float amount, String concept, String accountId) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.concept = concept;
        this.balanceAccountNumber = accountId;
        this.status = Status.PENDING;
        this.metadata = new Gson().toJson(new PaymentMetaData(null));
    }

    /**
     * *
     *
     * @param msg
     */
    public void reject(String msg) {
        this.status = Status.REJECT;
        this.concept = msg;
        this.update();

    }

    public void aprove() {

        aprove(this.amount);

    }

    public void aprove(float mount) {

        if (!this.status.equals(Status.PENDING)) {
            return;
        }

        this.setAmount(amount);
        /*registramos trasnaccion*/
        Transacction t = new Transacction();
        t.setFrom(ContabilityControl.MAIN_BALANCE_ACCOUNT);
        t.setTo(balanceAccountNumber);
        t.setMount(mount);
        t.setConcept(concept);
        t.setReference(getReference());
        t.create();
        /*actualizamos balance*/
        BalanceAccount balanceAccount = BalanceAccount.read(BalanceAccount.class, balanceAccountNumber);
        balanceAccount.setBalance(balanceAccount.getBalance() + mount);
        balanceAccount.update();

        this.status = Status.APROVED;
        this.update();

    }

    public String getReference() {
        return getBalanceAccount().getReference();
    }

    public BalanceAccount getBalanceAccount() {
        return BalanceAccount.read(BalanceAccount.class, this.balanceAccountNumber);
    }

    public static interface Status {

        String PENDING = "PENDING";
        String APROVED = "APROVED";
        String REJECT = "REJECT";

    }

    public String toStringForTelegram() {
        return "📋 ID: " + id + "\n"
                + "📋 Referencia: " + getReference() + "\n"
                + "💵 Monto: " + String.format("%.2f", amount) + " MXN\n"
                + "🌐 Concepto: " + concept + "\n"
                + "💼 Cuenta de Balance: " + balanceAccountNumber + "\n"
                + "🔒 Estado: " + status;
    }

    public Receptor getReceptor() {
        Tuser read = Tuser.read(Tuser.class, getMetaData().getTelegramId());
        return read.getReceptor();
    }

    public PaymentMetaData getMetaData() {
        return new Gson().fromJson(this.metadata, PaymentMetaData.class);
    }

    public void setMetaData(PaymentMetaData meta) {
        this.metadata = new Gson().toJson(meta);

    }

    @Data
    public static class PaymentMetaData {

        private String telegramId;

        public PaymentMetaData(String telegramId) {
            this.telegramId = telegramId;
        }

    }

}
