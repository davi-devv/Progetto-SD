import addHeaderButtons from "../UI/addHeaderButtons.js";
import handleHeaderName from "../UI/handleLoginUI.js";

/**
 * Calls the main functions, is fired by all pages as soon as window.onload is fired
 */
const init = () => {
    // Re-enables transitions
    document.body.classList.remove("preload");
    addHeaderButtons();
    handleHeaderName();
}

export default init;