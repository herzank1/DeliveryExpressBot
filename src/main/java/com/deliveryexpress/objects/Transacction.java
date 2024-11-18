/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects;

import com.deliveryexpress.de.DataBase;
import com.deliveryexpress.utils.Utils;
import java.util.ArrayList;

/**
 *
 * @author DeliveryExpress
 */
public class Transacction {
    
    String id;
    String from, to;
    float mount;
    String concept,reference;
    String date;

    public Transacction() {
        this.date = Utils.DateUtils.getNowDate();
    }

    public Transacction(String from, String to, float mount, String concept, String reference) {
        this.from = from;
        this.to = to;
        this.mount = mount;
        this.concept = concept;
        this.reference = reference;
        this.date = Utils.DateUtils.getNowDate();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public float getMount() {
        return mount;
    }

    public void setMount(float mount) {
        this.mount = mount;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "📄 " + id + " " + from + " ➡️ " + to + " 🟰 " + mount + " 📄 " + concept + " 📄 " + reference + " 🗓 " + date;
    }

    
    public ArrayList<String> toSqlArrayListValues() {

        ArrayList<String> values = new ArrayList<>();
        values.add("null");
        values.add("'" +this.from+ "'");
        values.add("'" +this.to+ "'");
        values.add("" +String.valueOf(this.mount)+ "");
        values.add("'" +this.concept+ "'");
        values.add("'" +this.reference+ "'");
        values.add("'" +this.date+ "'");

      
        return values;

    }

    boolean execute() {
        return DataBase.BalancesAccounts.transfer(this);

    }
    
    
}
