import init from "./utilities/init.js";
import handleServerRequest from "./handlers/handleServerRequest.js";

window.onload = loadPage();

function loadPage() {    
    init();

    // Specific page functionality from here

    /* handleServerRequest Parameters */
    const requestParameters = { "method": "GET", "URI": `stats` };
    const callbackFunctions = { "code200": on200, "default": onDefault }
    const args = { "document": document }
    /* --- */

    handleServerRequest(requestParameters, callbackFunctions, args);
}

async function on200(response, args) {
    const JSON = await response.json();

    const { totalUsers, contributiDisponibili, nonAncoraSpesi, spesi, buoniGeneratiConsumati, buoniGeneratiNonConsumati } = JSON;
    const { document } = args;

    document.getElementById('total-users').innerHTML = totalUsers;
    document.getElementById('contributi-disponibili').innerHTML = `${Number.parseFloat(contributiDisponibili).toFixed(2)}€`;
    document.getElementById('non-ancora-spesi').innerHTML = `${Number.parseFloat(nonAncoraSpesi).toFixed(2)}€`;
    document.getElementById('spesi').innerHTML = `${Number.parseFloat(spesi).toFixed(2)}€`;
    document.getElementById('buoni-generati-consumati').innerHTML = buoniGeneratiConsumati;
    document.getElementById('buoni-generati-non-consumati').innerHTML = buoniGeneratiNonConsumati;
}

async function onDefault(response, args) {
    const { document } = args;

    alert('Qualcosa è andato storto con le statistiche! Si prega di riprovare.');
    document.location = '../index.html'
}