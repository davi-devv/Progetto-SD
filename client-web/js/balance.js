import init from "./utilities/init.js";
import handleBalance from "./handlers/handleBalance.js";
import handleServerRequest from "./handlers/handleServerRequest.js";

window.onload = loadPage();

async function loadPage() {
    // init gets called LATER in the code, once we get the free balance of the user, so the header balance value can update accordingly
    // init()

    // Specific page functionality from here

    const CF = sessionStorage.getItem("CF");
    if (!CF) {
        alert('Qualcosa è andato storto! Si prega di riprovare');
        location.href = '../index.html';
        return;
    }

    /* handleServerRequest Parameters */

    // Override needed in order to get the progress bar working.
    const callbackFunctions = { "code200": on200 };
    const args = { "document": document, "sessionStorage": sessionStorage, "init": init };
    /* --- */

    handleBalance(CF, callbackFunctions, args);
}

async function on200(response, args) {
    const JSON = await response.json();

    const { freeBalance, usedNotConsumed } = JSON;
    const { document, sessionStorage, init } = args;

    const usedAndConsumed = Number((500 - freeBalance - usedNotConsumed).toFixed(2));

    sessionStorage.setItem('freeBalance', freeBalance);
    sessionStorage.setItem('usedNotConsumed', usedNotConsumed);
    sessionStorage.setItem('usedAndConsumed', usedAndConsumed);

    // Initializes the page, it needs to be here so the header gets updated with the correct balance value
    init();

    const freeBalanceElement = document.getElementById('balance');
    const usedNotConsumedElement = document.getElementById('not-spent');
    const usedAndConsumedElement = document.getElementById('spent');

    freeBalanceElement.innerHTML = `${Number.parseFloat(freeBalance).toFixed(2)}€`;
    usedNotConsumedElement.innerHTML = `${Number.parseFloat(usedNotConsumed).toFixed(2)}€`;
    usedAndConsumedElement.innerHTML = `${Number.parseFloat(usedAndConsumed).toFixed(2)}€`;

    const balanceProgess = document.getElementById('balance-bar');
    const notSpentBar = document.getElementById('not-spent-bar');

    balanceProgess.style.width = `${freeBalance / 500 * 100}%`;
    notSpentBar.style.width = `${usedNotConsumed / 500 * 100}%`;
}