class OrderRequest {
    constructor(businessId, name, phone, address, note, orderCost, deliveryCost) {
        this.businessId = businessId; // ID del negocio
        this.name = name;             // Nombre del cliente
        this.phone = phone;           // Teléfono del cliente
        this.address = address;       // Dirección del cliente
        this.note = note;             // Nota adicional
        this.orderCost = orderCost;   // Costo del pedido
        this.deliveryCost = deliveryCost; // Costo de entrega
    }
}