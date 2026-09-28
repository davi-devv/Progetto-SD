import init from "./utilities/init.js";
import handleServerRequest from "./handlers/handleServerRequest.js";

window.onload = loadPage();

function loadPage() {
    init();

    // Specific page functionality from here
    handleUI(document, sessionStorage);
}

function handleUI(document, sessionStorage) {
    const homeButton = document.getElementById('home-button');
    homeButton.innerText = 'Torna alla cronologia';
    homeButton.addEventListener(('click'), () => {
        location.href = './history.html';
    });

    const valueInput = document.getElementById('value-input');
    const modifyButton = document.getElementById('modify-button');
    const freeBalance = sessionStorage.getItem('freeBalance');

    const maxAllowedValue = parseInt(freeBalance);
    valueInput.addEventListener('input', (e) => {
        if (valueInput.value > maxAllowedValue || Number.parseFloat(valueInput.value) < 0.01) return modifyButton.classList.remove('available');

        if (modifyButton.classList.contains('available')) return;
        modifyButton.classList.add('available');
    });

    // Makes the button instantly available
    valueInput.dispatchEvent(new Event('input'));

    modifyButton.addEventListener('click', (e) => {
        if (!modifyButton.classList.contains('available')) return alert('Valore non accettato!');

        const type = document.getElementById('type').value;
        const value = valueInput.value;

        /* handleServerRequest Parameters */
        const requestBody = {
            "type": type,
            "value": value,
        }

        const requestParameters = { "method": "POST", "URI": `create`, "CF": sessionStorage.getItem('CF'), "body": requestBody };
        const callbackFunctions = { "code200": on200, "default": onDefault };
        const args = { /* No args required */ };
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