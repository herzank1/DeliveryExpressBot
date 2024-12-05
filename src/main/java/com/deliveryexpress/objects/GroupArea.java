/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.objects;

import com.deliveryexpress.objects.users.Tuser;
import com.j256.ormlite.field.DatabaseField;
import com.monge.tbotboot.messenger.Methods;
import com.monge.xsqlite.xsqlite.BaseDao;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.telegram.telegrambots.meta.api.objects.ChatInviteLink;

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
        this.id = UUID.randomUUID().toString();
    }

    public Tuser getDeliveriesGroup() {
        return Tuser.read(Tuser.class, this.deliveriesGroupId);
    }
    
     public String getDeliveriesGroupInviteLink() {
        Tuser read = Tuser.read(Tuser.class, this.deliveriesGroupId);
        ChatInviteLink createChatInviteLink = Methods.createChatInviteLink(read.getReceptor(), read.getReceptor().getBot(), 1);
        return createChatInviteLink.getInviteLink();
    }
     
public String toStringForTelegram() {
    return String.format(
        "📋 Detalles del Grupo de Área\n" +
        "📌 ID: %s\n" +
        "🏷️ Nombre: %s\n" +
        "📝 Descripción: %s\n" +
        "👤 Administrador: %s\n" +
        "👥 Grupo de Moderadores: %s\n" +
        "📑 Hilo de Órdenes: %s\n" +
        "🚛 Grupo Principal de Repartidores: %s\n" +
        "📦 Grupo Secundario de Repartidores: %s",
        id != null ? id : "null",
        name != null ? name : "null",
        description != null ? description : "null",
        adminTelegramId != null ? adminTelegramId : "null",
        areaModsGroupId != null ? areaModsGroupId : "null",
        ordersThreadModsGroupId != null ? ordersThreadModsGroupId : "null",
        mainDeliveriesGroupId != null ? mainDeliveriesGroupId : "null",
        deliveriesGroupId != null ? deliveriesGroupId : "null"
    );
}



}
