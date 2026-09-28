import init from "./utilities/init.js";
import handleLogin from "./handlers/handleLogin.js";

window.onload = loadPage();

function loadPage() {
    init();

    // Specific page functionality from here
    const inputCF = document.getElementById('input-CF');
    const submitButton = document.getElementById('submit-button');
    inputCF.addEventListener(('input'), () => {


        const CF = inputCF.value;
        // Regex to find all non-alfanumeric characters. If there's any alphanumeric character in the string, remove the availability
        if (!CF || CF.match(/[^0-9a-z]/gi)) return submitButton.classList.remove('available');

        if (submitButton.classList.contains('available')) return;
        submitButton.classList.add('available');
    });

    submitButton.addEventListener(('click'), async () => {
        const CF = inputCF.value;

        // If the value inside the input is empty or non valid
        if (!submitButton.classList.contains('available')) return alert('Il Codice Fiscale contiene caratteri non ammessi!');

        /* handleLogin Parameters */
        const callbackFunctions = { /* No overrides needed from behaviour specified in handleLogin.js */ };
        const args = {
            "inputCF": inputCF, "location": location, "sessionStorage": sessionStorage,
            "message": "Login avvenuto con successo!"
        };
        /* --- */

        handleLogin(CF, callbackFunctions, args);
    });
}