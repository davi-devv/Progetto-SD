import init from "./utilities/init.js";
import handleServerRequest from "./handlers/handleServerRequest.js";

window.onload = loadPage();

function loadPage() {
    init();

    // Specific page functionality from here

    // If no buono is detected in session storage, get back to './history.html'
    if (!sessionStorage.getItem('buono')) return location.href = './history.html';

    handleUI(document, sessionStorage);
}

function handleUI(document, sessionStorage) {
    const homeButton = document.getElementById('home-button');
    homeButton.innerText = 'Torna alla cronologia';
    homeButton.addEventListener(('click'), () => {
        location.href = './history.html';
    });

    const buono = JSON.parse(sessionStorage.getItem('buono'));
    // sessionStorage.removeItem('buono');

    const idText = document.getElementById('id-text');
    idText.innerHTML = buono.id;

    const consumedButton = document.getElementById('consumed-button');
    consumedButton.addEventListener(('click'), () => {
        if (consumedButton.innerHTML == 'Consumato') {
            consumedButton.innerHTML = 'Non Consumato';
            return consumedButton.classList.add('green');
        }

        consumedButton.innerHTML = 'Consumato';
        consumedButton.classList.remove('green');
    });

    const valueInput = document.getElementById('value-input');
    const modifyButton = document.getElementById('modify-button');
    const freeBalance = sessionStorage.getItem('freeBalance');

    const maxAllowedValue = parseInt(buono.value) + parseInt(freeBalance);

    valueInput.value = buono.value;

    valueInput.addEventListener('input', (e) => {
        if (valueInput.value > maxAllowedValue || Number.parseFloat(valueInput.value) < 0.01) return modifyButton.classList.remove('available');

        if (modifyButton.classList.contains('available')) return;
        modifyButton.classList.add('available');
    });

    // Makes the button instantly available
    valueInput.dispatchEvent(new Event('input'));

    modifyButton.addEventListener('click', (e) => {
        if (!modifyButton.classList.contains('available')) return alert('Valore non accettato!');

        // If innerHTML of consumed-button is 'Consumato', returns true. Returns false if not
        const newConsumed = (document.getElementById('consumed-button').innerHTML) == 'Consumato' ? true : false;
        const newType = document.getElementById('type').value;
        const newValue = valueInput.value;

        /* handleServerRequest Parameters */
        const requestBody = {
            "type": newType,
            "value": newValue,
            "consumed": newConsumed
        }

        const requestParameters = { "method": "PUT", "URI": `modify/${buono.id}`, "CF": sessionStorage.getItem('CF'), "body": requestBody };
        const callbackFunctions = { "code200": on200, "default": onDefault }
        const args = { /* No args required */ }
        /* --- */
        
        handleServerRequest(requestParameters, callbackFunctions, args);
    });
}

async function on200(response, args) {
    alert('Operazione avvenuta con successo!');
    location.href = '../index.html';
}

async function onDefault(response, args) {
    alert('Qualcosa è andato storto! Si prega di riprovare.');
}