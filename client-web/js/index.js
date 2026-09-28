window.onload = loadPage();

function loadPage() {    
    // Specific page functionality from here

    // If the user is logged, home returns to 'balance.html'
    if(!sessionStorage.getItem('CF')) return location.href = './pages/login.html';
    
    return location.href = './pages/balance.html';
}