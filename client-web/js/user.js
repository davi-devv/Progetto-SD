import init from "./utilities/init.js";

window.onload = loadPage();

function loadPage() {    
    init();

    // Specific page functionality from here
    document.getElementById('exit-button').addEventListener(('click'), () => {
        sessionStorage.clear();
        location.href = '../index.html';
    });
}