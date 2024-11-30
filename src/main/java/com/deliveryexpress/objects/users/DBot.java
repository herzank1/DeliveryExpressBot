/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects.users;

import com.j256.ormlite.field.DatabaseField;
import com.monge.tbotboot.messenger.Bot;
import com.monge.xsqlite.xsqlite.BaseDao;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * Esta clase contine la informacion de un Bot
 * espara almacenar con DAO
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class DBot extends BaseDao{
    @DatabaseField(id = true)
    String userName;
    @DatabaseField
    String apiKey;

    public DBot() {
    }
    
    
    /***
     * 
     * @return 
     */
    public Bot getBot() {
        return new Bot(this.userName, this.apiKey);
    }
    
}
