/**
 * Makes the login button responsive, adding the name of the user who's currently logged
 */
const handleHeaderName = () => {

    const loginContent = document.getElementById('login-content');

    // If no user is logged
    if (!sessionStorage.getItem('CF')) {
        return loginContent.innerHTML = 'Login';
    }

    // Overrides the redirects made by addHeaderButton.js
    document.getElementById('login-button').addEventListener('click', () => { location.href = './user.html' });
    document.getElementById('new-button').addEventListener('click', () => { location.href = './new.html' });
    document.getElementById('history-button').addEventListener('click', () => { location.href = './history.html' });

    const name = sessionStorage.getItem("name");
    const surname = sessionStorage.getItem("surname");
    const freeBalance = sessionStorage.getItem('freeBalance');

    loginContent.innerHTML = `
        <div>Ciao,</div>
        <span>${name} ${surname}</span>
        <div>Saldo: <span>${Number.parseFloat(freeBalance).toFixed(2)}€</span></div>`
    return;
}

export default handleHeaderName;