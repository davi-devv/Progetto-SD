import init from "./utilities/init.js";
import handleServerRequest from "./handlers/handleServerRequest.js";
import handleLogin from "./handlers/handleLogin.js";

window.onload = loadPage();

function loadPage() {
    init();

    // Specific page functionality from here
    const inputNome = document.getElementById('input-nome');
    const inputCognome = document.getElementById('input-cognome');
    const inputEmail = document.getElementById('input-email');
    const inputCF = document.getElementById('input-CF');
    const inputs = [inputNome, inputCognome, inputEmail, inputCF];

    const submitButton = document.getElementById('submit-button');

    inputs.forEach(input => {
        input.addEventListener(('input'), () => {
            let isAnyNotValid = false;

            // Checks if ANY of the inputs is empty
            inputs.forEach(input => {
                if (!input.value || input.value.length > 20) return isAnyNotValid = true;
            });

            if (inputNome.value.match(/[^0-9a-z ]/gi)
                || inputCF.value.match(/[^0-9a-z]/gi)
                || inputCognome.value.match(/[^0-9a-z ]/gi)
                || inputEmail.value.match(/[^0-9a-z\.\@\_\-]/gi)) isAnyNotValid = true;

            // If ANY is, removes the availability to the button
            if (isAnyNotValid) return submitButton.classList.remove('available');

            // Otherwise add it back
            if (submitButton.classList.contains('available')) return;
            submitButton.classList.add('available');
        });
    });

    submitButton.addEventListener(('click'), async () => {

        // If the button is not available, do nothing
        if (!submitButton.classList.contains('available')) return alert('Uno degli input contiene caratteri non validi!');

        /* handleServerRequest Parameters */
        const requestBody = {
            "CF": inputCF.value,
            "nome": inputNome.value,
            "cognome": inputCognome.value,
            "email": inputEmail.value
        }
        const requestParameters = { "method": "POST", "URI": "register", "body": requestBody };
        const callbackFunctions = { "code200": on200, "code409": on409, "default": onDefault };
        const args = {
            "inputs": inputs, "inputCF": inputCF, "location": location, "sessionStorage": sessionStorage,
            "message": "Registrazione avvenuta con successo!"
        };
        /* --- */

        handleServerRequest(requestParameters, callbackFunctions, args);
    });
}

// Code 200 override
/**
 * @param {Response} response 
 * @param {Array} args 
 */
async function on200(response, args) {
    // Logs with the new user credentials
    const CF = args.inputCF.value;

    /* handleLogin Parameters */
    const callbackFunctions = { /* No overrides needed from behaviour specified in login.js */ };
    /* --- */

    handleLogin(CF, callbackFunctions, args);
}

// Code 409 override
/**
 * @param {Response} response 
 * @param {Array} args 
 */
async function on409(response, args) {
    alert(`Un utente con questo Codice Fiscale già esiste! Riprovare!`);
}

async function onDefault(response, args) {
    alert(`Server error: ${response.status} ${response.statusText}`);

    // Resets all inputs to their original values
    args.inputs.forEach((input) => {
        input.value = '';
    });

    args.inputEmail.value = '';

    // Dispatches an event to reset button functionality
    args.inputs[0].dispatchEvent(new Event('input'));
}
