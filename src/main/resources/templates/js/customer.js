class Customer {
	
	 constructor(data = {}) {
    this.phone = data.phone || "";
    this.name = data.name || "";
    this.lastAddress = data.lastAddress || "";
    this.lastLocation = data.lastLocation || "";
    this.lastNote = data.lastNote || "";
  }
	


  // Método para comparar instancias (equivalente a @EqualsAndHashCode en Java)
  equals(otherCustomer) {
    if (!(otherCustomer instanceof Customer)) {
      return false;
    }
    return this.phone === otherCustomer.phone;
  }

  // Método para obtener una representación JSON de la instancia
  toJSON() {
    return {
      phone: this.phone,
      name: this.name,
      lastAddress: this.lastAddress,
      lastLocation: this.lastLocation,
      lastNote: this.lastNote,
    };
  }
}
