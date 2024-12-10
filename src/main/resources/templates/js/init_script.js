

var userId = null;

const currentDateElement = document.getElementById("currentDate");
const currentBussinesIdElement = document.getElementById("bussinesId");
const deliveriesCountElement = document.getElementById("deliveriesCount");
/*customer fields elements*/
const customerNameElement = document.getElementById("name");
const customerPhoneElement = document.getElementById("phone");
/*Auto fill address elements*/
const addresInputElement = document.getElementById("addressInput");
const suggestionsListElement = document.getElementById("suggestionsList");
const customerNoteElement = document.getElementById("note");

/*top order elements*/
const orderCostElement = document.getElementById("orderCost");
const shippingCostElement = document.getElementById("shippingCost");

var bussinesData;

document.addEventListener("DOMContentLoaded", function () {
    // Configurar fecha actual

    const currentDate = new Date();
    currentDateElement.innerText = `Fecha: ${currentDate.toLocaleDateString()}`;

    // Obtén los datos iniciales
    const initDataUnsafe = Telegram.WebApp.initDataUnsafe;
    console.log("initData (Decodificado):", initDataUnsafe);
    // Establecer el ID del usuario en el párrafo
    userId = initDataUnsafe.user.id;
    console.log(`Id del usuario es: ${userId}`);
    shippingCostElement.value = 40;

    _loadBussinesData();

    initLiveData();


});

async function initLiveData() {
    // Ejecuta una acción cada 1 segundo

    setInterval(() => {
        _loadBussinesData();
    }, 1000 * 10); // Intervalo en milisegundos (1000 ms = 1 segundo)


}

async function submitForm() {
    _sendNewOrder();
}

// Funcionalidad para limpiar el formulario
function clearForm() {
    customerNameElement.value = "";
    customerPhoneElement.value = "";
    addresInputElement.value = "";
    customerNoteElement.value = "";
    orderCostElement.value = "";
    shippingCostElement.value = "";

}

async function _fetchSuggestions() {
    fetchSuggestions(userId, addresInputElement.value);
}

async function _getCustomer() {

    const phone = customerPhoneElement.value;
    getCustomer(phone, customerNameElement, addresInputElement, customerNoteElement)

}
function _getUserIdParam() {
    return  `&userid=${userId}`;
}

async function _calculateCost() {
    const address = addresInputElement.value;
    calculateCost(address, shippingCostElement);

}

async function _sendNewOrder() {

    const orderData = {
        action: "newOrder",
        userid: userId,
        name: customerNameElement.value,
        phone: customerPhoneElement.value,
        address: addresInputElement.value,
        note: customerNoteElement.value,
        orderCost: orderCostElement.value || 0,
        deliveryCost: shippingCostElement.value || 0
    };

    sendNewOrder(orderData);
}

async function _loadBussinesData() {
    const data = await getBussinesData(userId);

    if (data) {
        bussinesData = data;
        console.log(data);

        const bussinesName = bussinesData.bussines.name;
        currentBussinesIdElement.innerText = bussinesName;

        const liveData = bussinesData.liveData.connectedDeliveries;
        deliveriesCountElement.innerText = "Conectados: " + liveData;
    }

}