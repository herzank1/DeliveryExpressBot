let timeout; // Variable para evitar solicitudes innecesarias al servidor

const path = "/bussines";

async function fetchSuggestions(userid, inputAddress) {
    if (!inputAddress)
        return;

    const data = await fetchJSON(url + path + "?action=fetchAddreses&query=" + encodeURIComponent(inputAddress) + _getUserIdParam());

    if (data) {
        console.log("Respuestas recibidas:", data);
        populateOptions(data);
    }
}

function populateOptions(options) {

    suggestionsListElement.innerHTML = ""; // Limpiar opciones anteriores
    suggestionsListElement.style.display = options.length ? "block" : "none"; // Mostrar si hay opciones

    options.forEach(option => {
        const li = document.createElement("li");
        li.textContent = option;
        li.style.cursor = "pointer";
        li.onclick = () => selectOption(option);
        suggestionsListElement.appendChild(li);
    });
}

function selectOption(option) {
    addresInputElement.value = option;
    suggestionsListElement.style.display = "none"; // Ocultar la lista
}

async function getCustomer(phoneInput, customerNameElement, addresInputElement, customerNoteElement) {
    if (!phoneInput)
        return;

    const data = await fetchJSON(url + path + "?action=getCustomer&phone=" + encodeURIComponent(phoneInput) + _getUserIdParam());

    if (data && Object.keys(data).length > 0) {
        const customer = new Customer(data);
        customerNameElement.value = customer.name;
        addresInputElement.value = customer.lastAddress;
        customerNoteElement.value = customer.lastNote;
    } else {
        alert("No se encontraron datos para el número ingresado.");
    }
}

function validate() {
    fetch(url + "/validate", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: "initData=" + encodeURIComponent(Telegram.WebApp.initData),
    })
            .then(response => response.json())
            .then(data => console.log(data))
            .catch(err => console.error(err));


}

// Funcionalidad para calcular el costo
/***
 * 
 * @param {type} address
 * @returns {undefined}
 */
async function calculateCost(address, shippingCostElement) {
    const data = await fetchJSON(url + path + "?action=cotizer&address=" + encodeURIComponent(address) + _getUserIdParam());

    if (data?.deliveryCost) {
        alert(JSON.stringify(data));
        shippingCostElement.value = data.deliveryCost;
    } else {
        alert("No se encontraron datos para el número ingresado.");
    }
}


async function sendNewOrder(orderData) {
    const response = await fetchJSON(url + "/bussines", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(orderData),
    });

    if (response) {
        alert("Formulario enviado con éxito, vaya a telegram para su seguimiento.");
        clearForm();
    } else {
        alert("Ocurrió un error al enviar el formulario.");
    }
}


async function getBussinesData() {

    const data = await fetchJSON(url + path + "?action=getBussinesData" + _getUserIdParam());

    if (data) {
        return data;

    } else {

        return null;
    }
}
