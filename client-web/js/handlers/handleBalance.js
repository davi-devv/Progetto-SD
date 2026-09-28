import handleServerRequest from "./handleServerRequest.js";

/**
 * Handle balance logic, makes a request to the server with the specified CF
 * @param {String} CF 
 * @param {Object} callbackFunctions
 * @param {Object} args document, sessionStorage
 */
const handleBalance = (CF, callbackFunctions, args) => {
    /* handleServerRequest Parameters */
    const requestParameters = { "method": "GET", "URI": `balance`, "CF": CF };

    const newCallbackFunctions = { "code200": on200, "default": onDefault }

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

    const { freeBalance, usedNotConsumed } = JSON;
    const usedAndConsumed = 500 - freeBalance - usedNotConsumed;

    sessionStorage.setItem('freeBalance', freeBalance);
    sessionStorage.setItem('usedNotConsumed', usedNotConsumed);
    sessionStorage.setItem('usedAndConsumed', usedAndConsumed);
}

// On default override
// Gets executed if no override for other codes is specified (e.g. Server returns code 500 and there's no override for it)
/**
 * @param {Response} response 
 * @param {Object} args 
 */
const onDefault = async (response, args) => {
    alert("Qualcosa è andato storto con il bilancio! Riprovare!");
    const { document } = args;
    document.location.href = './user.html';
}

export default handleBalance;