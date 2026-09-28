import init from "./utilities/init.js";
import { buoniTypes } from "./utilities/constants.js";
import jsonDateToFormatted from "./utilities/jsonDateToFormatted.js";

window.onload = loadPage();

function loadPage() {
    init();

    // Specific page functionality from here

    // If no buono is detected in session storage, get back to './history.html'
    if (!sessionStorage.getItem('buono')) return location.href = './history.html';

    const homeButton = document.getElementById('home-button');
    homeButton.innerText = 'Torna alla cronologia';
    homeButton.addEventListener(('click'), () => {
        location.href = './history.html';
    });

    const buono = JSON.parse(sessionStorage.getItem('buono'));
    sessionStorage.removeItem('buono');

    const idText = document.getElementById('id');
    idText.innerHTML = buono.id;

    const valueText = document.getElementById('value');
    valueText.innerHTML = `${Number.parseFloat(buono.value).toFixed(2)}€`;

    const typeText = document.getElementById('type');
    typeText.innerHTML = buoniTypes[buono.type];

    const consumedText = document.getElementById('status');
    consumedText.innerHTML = buono.consumed ? 'Consumato' : 'Non consumato';

    const creationDate = document.getElementById('creation-date');
    creationDate.innerHTML = jsonDateToFormatted(buono.creation);

    const modifiedDate = document.getElementById('modified-date');
    console.log(buono.modified)
    modifiedDate.innerHTML = jsonDateToFormatted(buono.modified);
}