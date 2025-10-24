document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const errorCode = urlParams.get('error');

    if (errorCode) {
        let errorMessage = '';
        let toastType = 'danger'; // Predvolený typ pre chyby

        switch (errorCode) {
            case 'locked_account':
                errorMessage = 'Chyba prihlásenia: Váš účet bol zablokovaný z dôvodu príliš mnohých neúspešných pokusov.';
                break;
            case 'disabled_account':
                errorMessage = 'Chyba prihlásenia: Váš účet nie je aktivovaný alebo je deaktivovaný. Skontrolujte email.';
                break;
            case 'true':
            case 'generic':
                errorMessage = 'Prihlásenie zlyhalo. Skontrolujte prosím svoje prihlasovacie údaje.';
                break;
            default:
                errorMessage = 'Nastala neznáma chyba pri prihlasovaní.';
        }

        if (errorMessage) {
            showBootstrapToast(errorMessage, toastType);

            // Voliteľné: Vyčistenie URL po zobrazení správy
            removeUrlParameter('error');
            removeUrlParameter('modal');
        }
    }

    // Voliteľné: Automatické zobrazenie modálneho okna pri ?modal=login
    // Ak už máte implementované, túto časť preskočte
    if (urlParams.get('modal') === 'login') {
        const loginModalElement = document.getElementById('loginModal'); // Predpokladáme ID pre vaše prihlasovacie modal
        if (loginModalElement) {
            const loginModal = new bootstrap.Modal(loginModalElement);
            loginModal.show();
        }
    }
});


/**
 * Funkcia pre dynamické vytvorenie a zobrazenie Bootstrap Toast správy.
 * @param {string} message - Text správy, ktorá sa zobrazí.
 * @param {string} type - Typ správy (napr. 'danger', 'success', 'warning').
 */
function showBootstrapToast(message, type) {
    const toastContainer = document.getElementById('toastContainer');
    if (!toastContainer) {
        console.error("Toast container nenájdený!");
        return;
    }

    // Unikátne ID pre každú správu
    const toastId = `toast-${Date.now()}`;

    // Vytvorenie HTML pre Bootstrap Toast
    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center text-bg-${type} border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

    // Pridanie toastu do kontajnera
    toastContainer.insertAdjacentHTML('beforeend', toastHtml);

    // Inicializácia a zobrazenie toastu pomocou Bootstrap JavaScriptu
    const toastEl = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastEl, {
        delay: 5000 // Zobraziť na 5 sekúnd
    });

    // Po skrytí toastu ho odstránime z DOM
    toastEl.addEventListener('hidden.bs.toast', () => {
        toastEl.remove();
    });

    toast.show();
}

/**
 * Pomocná funkcia pre vyčistenie URL
 */
function removeUrlParameter(key) {
    const url = new URL(window.location);
    if (url.searchParams.has(key)) {
        url.searchParams.delete(key);
        window.history.replaceState({}, '', url);
    }
}