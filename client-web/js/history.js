import init from "./utilities/init.js";
import { buoniTypes } from "./utilities/constants.js";
import handleServerRequest from "./handlers/handleServerRequest.js";
import jsonDateToFormatted from "./utilities/jsonDateToFormatted.js";

window.onload = loadPage();


/**
 * 
 * @param {Object} JSON 
 * @param {Document} document 
 * @returns 
 */
function createTable(JSON) {
    const table = document.getElementById('table');
    let newDiv = document.createElement('div');
    newDiv.id = ('table-list-id');

    // If the response is empty (No buoni)
    if (Object.keys(JSON).length == 0) {
        newDiv.innerHTML += `
            <div class="table-item">
                <div class="item container">Non hai ancora creato un buono!</div>
             </div>            
        `;

        return table.append(newDiv)
    }

    // Otherwise, if the response is not empty, loop through the response and make a div for each buono
    for (const [key, values] of Object.entries(JSON)) {

        newDiv.innerHTML +=
            `
            <div class="table-item">
                <div class="item">${key}</div>
                <div class="item">${buoniTypes[values.type]}</div>
                <div class="item">${Number.parseFloat(values.value).toFixed(2)}€</div>
                <div class="item">${values.consumed ? 'Si' : 'No'}</div>
                <div class="item">${jsonDateToFormatted(values.creation)}</div>
                <div class="item">${jsonDateToFormatted(values.modified)}</div>
             </div>            
        `;
    }

    table.append(newDiv);
}

/**
 * 
 * @param {Document} document 
 */
function handleTableUI() {
    const tableItems = document.getElementsByClassName('table-item');
    const buttons = document.getElementsByClassName('button');

    for (const [key, element] of Object.entries(tableItems)) {

        // Skips the first row
        if (key == "0") continue;

        element.addEventListener(('click'), (e) => {
            const classList = e.currentTarget.classList;

            // Resets buttons visibility, gets re-added later depending on the voucher consumed status
            // (all the buttons get shown if the voucher is not consumed, just one is shown if it is)
            for (const [key, element] of Object.entries(buttons)) {
                element.classList.remove('available');
            }

            // If i re-click on a selected element, deselect it
            if (classList.contains('selected')) {
                return classList.remove('selected');
            }

            // If I click on a not selected element, remove all other selections
            for (const [key, element] of Object.entries(tableItems)) {
                /**
                 * @type {DOMTokenList} classList
                 */
                const classList = element.classList;
                if (classList.contains('selected')) classList.remove('selected');
            }

            // Then add it to the clicked element
            classList.add('selected');

            // In case the user has no vouchers
            if (!e.currentTarget.children[3]) return;
            if (e.currentTarget.children[3].innerHTML == 'Si') return buttons[3].classList.add('available');

            // Makes the buttons visible
            for (const [key, element] of Object.entries(buttons)) {
                element.classList.add('available');
            }
        })
    }
}

/**
 * 
 * @param {Document} document
 * @param {Storage} sessionStorage 
 * @param {Object} JSON 
 */
function handleButtonsUI(json) {
    const detailButton = document.getElementById('detail-button');
    const deleteButton = document.getElementById('delete-button');
    const modifyButton = document.getElementById('mod-button');
    const consumeButton = document.getElementById('consume-button');

    detailButton.addEventListener(('click'), () => {
        const buono = findSelectedBuono(json);

        sessionStorage.setItem("buono", JSON.stringify(buono));

        document.location.href = './detail.html';
    });

    modifyButton.addEventListener(('click'), () => {
        const buono = findSelectedBuono(json);

        // Already consumed buoni can't be modified
        if (buono.consumed) return alert('Il buono è già stato consumato, non può essere modificato!');

        sessionStorage.setItem("buono", JSON.stringify(buono));
        document.location.href = './modify.html'
    });

    consumeButton.addEventListener(('click'), () => {
        if (!confirm("Sei sicuro? Quest'azione è irreversibile.")) return;

        const buono = findSelectedBuono(json);

        /* handleServerRequest Parameters */
        const requestBody = {
            "value": buono.value,
            "consumed": true,
            "type": buono.type
        }

        const requestParameters = { "method": "PUT", "URI": `modify/${buono.id}`, "CF": sessionStorage.getItem('CF'), "body": requestBody };
        const callbackFunctions = { "code200": consumeOn200, "default": consumeOnDefault }
        const args = { /* No args required */ }
        /* --- */

        function consumeOn200(response, args) {
            alert('Azione eseguita con successo!');
            return location.href = '../index.html';
        }

        function consumeOnDefault(response, args) {
            alert('Qualcosa è andato storto! Si prega di riprovare.');
            return location.href = '../index.html';
        }

        handleServerRequest(requestParameters, callbackFunctions, args);
    });

    deleteButton.addEventListener(('click'), () => {
        if (!confirm("Sei sicuro? Quest'azione è irreversibile.")) return;

        const buono = findSelectedBuono(json);

        /* handleServerRequest Parameters */
        const requestBody = {
            "value": 0,
            "consumed": buono.consumed,
            "type": buono.type
        }

        const requestParameters = { "method": "PUT", "URI": `modify/${buono.id}`, "CF": sessionStorage.getItem('CF'), "body": requestBody };
        const callbackFunctions = { "code200": consumeOn200, "default": consumeOnDefault }
        const args = { /* No args required */ }
        /* --- */

        function consumeOn200(response, args) {
            alert('Azione eseguita con successo!');
            return location.href = '../index.html';
        }

        function consumeOnDefault(response, args) {
            alert('Qualcosa è andato storto! Si prega di riprovare.');
            return location.href = '../index.html';
        }

        handleServerRequest(requestParameters, callbackFunctions, args);
    });
}

function findSelectedBuono(buoni) {
    const buonoElement = document.getElementsByClassName('selected');

    // children[0] == ID
    const id = buonoElement[0].children[0].textContent;
    const buono = buoni[id];

    // Adds the ID to the buono object since the server doesn't return it in the buoni list
    buono.id = id;
    return buono;
}

function loadPage() {
    init();

    // Specific page functionality from here
    /* handleServerRequest Parameters */
    const requestParameters = { "method": "GET", "URI": `history`, "CF": sessionStorage.getItem('CF') };
    const callbackFunctions = { "code200": on200, "default": onDefault }
    const args = { /*  */ }
    /* --- */

    handleServerRequest(requestParameters, callbackFunctions, args);
}

// Code 200 override
/**
 * @param {Response} response 
 * @param {Object} args 
 */
async function on200(response, args) {
    const JSON = await response.json();
    const { listaBuoni } = JSON;

    // The functions does not need document as parameter since we're working on the same js file.
    // document as parameter is needed when importing functions from external JS files.
    createTable(listaBuoni);
    handleTableUI();
    handleButtonsUI(listaBuoni);
}

// Default override
/**
 * @param {Response} response 
 * @param {Object} args 
 */
async function onDefault(response, args) {
    alert('Qualcosa è andato storto! Si prega di riprovare.');
    document.location = './user.html';
}