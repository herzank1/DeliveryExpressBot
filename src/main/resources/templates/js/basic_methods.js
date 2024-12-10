/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */


async function fetchJSON(url, options = {}) {
    try {
        const response = await fetch(url, options);  // Usa 'options' directamente
        if (!response.ok) {
            return null; // Devuelve null si la respuesta no es OK
        }
        const jsonObj = await response.json();
        return jsonObj;
    } catch (error) {
        console.error("Error en fetchJSON:", error);
        return null; // Devuelve null si ocurre un error
    }
}

