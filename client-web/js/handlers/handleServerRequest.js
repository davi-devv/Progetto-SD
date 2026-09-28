import { API_URI } from "../utilities/constants.js";

/**
 * @typedef {Object} requestParameters
 * @property {String} method GET, POST, PUT
 * @property {String} URI URI parameters for the request, something like login/{CF}
 * @property {String} CF Codice Fiscale in header
 * @property {Object} body
 */

/**
 * @typedef {Object} callbackFunctions an object containing functions to call for each response code.
 * @property {Function} code200 OK
 * @property {Function} code401 Unauthorized
 * @property {Function} code403 Forbidden
 * @property {Function} code404 Not Found
 * @property {Function} code500 Server Error
 * @property {Function} default Default behaviour
 */

/**
 * Handles every request to the server. 
 * It works by requiring 2 objects
 * @param {requestParameters} requestParameters Represents every detail of the request to the server.
 * @param {callbackFunctions} callbackFunctions An object which holds the functions that will be called 
 * based on the server response. The functions in this object represents an override from the default behaviour. 
 * @param {Object} args Used for callback functions
 * @returns 
 */
const handleServerRequest = async (requestParameters, callbackFunctions, args) => {

    let headers = {
        "Content-Type": 'application/json'
    };
    let body = {};
    if (requestParameters.CF) headers["Authentication-ID"] = requestParameters.CF
    if (requestParameters.body) body = requestParameters.body;

    const response = await fetch(`${API_URI}/${requestParameters.URI}`, {
        method: requestParameters.method,
        headers: headers,
        body: JSON.stringify(requestParameters.body)
    }).catch(err => {
        // Catches Network or CORS errors
        // If the fetch errored and we have no response or the response is a CORS, end the execution here.
        if (callbackFunctions.hasOwnProperty(`default`))
            return callbackFunctions[`default`](null, args);

        return defaultCallbackFunctions.default(null, args);
    });

    // Searches if the caller has set an override from the default code behaviour.
    if (callbackFunctions.hasOwnProperty(`code${response.status}`))
        return callbackFunctions[`code${response.status}`](response, args);

    // Searches if the caller has set an override to the default function.
    if (callbackFunctions.hasOwnProperty("default"))
        return callbackFunctions.default(response, args);

    // If not, the default behaviour of the specific code gets called.
    if (defaultCallbackFunctions.hasOwnProperty(`code${response.status}`))
        return defaultCallbackFunctions[`code${response.status}`](response, args);

    // If the default behaviour of that code is not specified, the default function gets called instead.
    return defaultCallbackFunctions.default(response, args);
}

const defaultCallbackFunctions = {
    "code200": on200,
    "code401": on401,
    "code403": on403,
    "code404": on404,
    "code500": on500,
    "default": onDefault
}

// Default behaviour for code 200 OK
function on200(response, args) {
    console.log("Request status: OK");
}

// Default behaviour for code 401 Unauthorized
function on401(response, args) {
    console.log("Request status: Unauthorized");
}

// Default behaviour for code 403 Forbidden
function on403(response, args) {
    console.log("Request status: Forbidden");
}

// Default behaviour for code 404 Not Found
function on404(response, args) {
    console.log("Request status: Not Found");
}

// Default behaviour for code 500 Server Error
function on500(response, args) {
    console.log("Request status: Error");
}

function onDefault(response, args) {
    alert(`Request status: ${response.statusText}`);
}

export default handleServerRequest;
