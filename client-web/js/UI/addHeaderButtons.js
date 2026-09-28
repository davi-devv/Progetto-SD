/**
 * Adds functionality to header buttons
 */
const addHeaderButtons = () => {;
    document.getElementById('home-button').addEventListener('click', () => { location.href = '../index.html' });
    document.getElementById('new-button').addEventListener('click', () => { location.href = './login.html' });      // When the user is not logged, redirects him to login
    document.getElementById('history-button').addEventListener('click', () => { location.href = './login.html' });  // When the user is not logged, redirects him to login
    document.getElementById('stats-button').addEventListener('click', () => { location.href = './stats.html' });
    document.getElementById('register-button').addEventListener('click', () => { location.href = './register.html' });
    document.getElementById('login-button').addEventListener('click', () => { location.href = './login.html' });
}

export default addHeaderButtons;