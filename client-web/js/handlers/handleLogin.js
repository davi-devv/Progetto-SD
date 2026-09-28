import handleServerRequest from "./handleServerRequest.js";
import resetInput from "../utilities/resetInput.js";

/**
 * Handle login logic, makes a request to the server with the specified CF
 * @param {String} CF 
 * @param {Object} callbackFunctions
 * @param {Object} args inputCF (HTMLElement), location, sessionStorage, message (String)
 */
const handleLogin = (CF, callbackFunctions, args) => {
    /* handleServerRequest Parameters */
    const requestParameters = { "method": "GET", "URI": `login/${CF}` };

    const newCallbackFunctions = { "code200": on200, "code404": on404, "default": onDefault }

    // If the called of this function specified its overrides, use them.
    // OTHERWISE use the overrides of this function (down below)
    // If no other overrides are speciefied, DEFAULT ones are used (found in handleServerRequest).
    Object.keys(callbackFunctions).forEach((key) => {
        newCallbackFunctions[key] = callbackFunctions[key];
    })
    /* --- */

    handleServerRequest(requestParameters, newCallbackFunctions, args);
}

// Code 200 override
/**
 * @param {Response} response 
 * @param {Object} args 
 */
const on200 = async (response, args) => {
    const JSON = await response.json();

    const { name, surname, email } = JSON;
    const { inputCF, location, sessionStorage, message } = args;
    const CF = inputCF.value;

    if (!name || !surname || !email)
        return console.error('Something is wrong with the response');

    // If someone is already logged, logout
    if (sessionStorage.getItem('CF'))
        sessionStorage.clear();

    sessionStorage.setItem("CF", String(CF));
    sessionStorage.setItem("name", name);
    sessionStorage.setItem("surname", surname);
    sessionStorage.setItem("email", email);

    alert(message);
    location.href = '../index.html';
}

// Code 404 override
/**
 * @param {Response} response 
 * @param {Object} args 
 */
const on404 = async (response, args) => {
    alert(`Questo utente non esiste!`);

    // Resets the input values
    resetInput(args.inputCF);
}

// On default override
// Gets executed if no override for other codes is specified (e.g. Server returns code 500 and there's no override for it)
/**
 * @param {Response} response 
 * @param {Object} args 
 */
const onDefault = async (response, args) => {
    alert(`Server error: ${response.status} ${response.statusText}`);

    // Resets the input values
    resetInput(args.inputCF);
}

export default handleLogin;