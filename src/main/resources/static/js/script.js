// Alternar sidebar em telas pequenas
const toggleButton = document.querySelector('.toggle-btn');
const sidebar = document.getElementById('sidebar');

if (toggleButton && sidebar) {
    toggleButton.addEventListener('click', () => sidebar.classList.toggle('active'));
}

// Alternar submenu
function toggleSubmenu(event, element) {
    if (!element || !element.parentElement) return;
    event.preventDefault();
    element.parentElement.classList.toggle('open');
}
