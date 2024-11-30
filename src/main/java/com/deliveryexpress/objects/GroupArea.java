/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects;

import com.j256.ormlite.field.DatabaseField;
import com.monge.xsqlite.xsqlite.BaseDao;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author DeliveryExpress representa un area de operaciones
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GroupArea extends BaseDao {

    @DatabaseField(id = true)
    String id; //example DE_AREA_SANMARCOS
    @DatabaseField
    String name; //Nombre del grupo
    @DatabaseField
    String description; //descripcion
    @DatabaseField
    String adminTelegramId; //id del administrador del grupo
    @DatabaseField
    String areaModsGroupId; //id de grupo de moderadores de esta area
    @DatabaseField
    String ordersThreadModsGroupId;// hilo de conversacion o ingreso de ordenes del grupo de moderadores de esta area
    @DatabaseField
    String mainDeliveriesGroupId;//id telegram grupo principal para repartidores, moderadores, internos y de bases
    @DatabaseField
    String deliveriesGroupId; //id telegram grupo secundario de repartidores

    /*new params*/
    @DatabaseField
    String status; //estado del grupo
    @DatabaseField
    String tag; //etiquetas
    @DatabaseField
    String bussinesPaymentMessage;//informacion bancaria para pagos de los restaurantes

    public GroupArea() {
    }

}
