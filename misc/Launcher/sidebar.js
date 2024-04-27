const SIDEBAR_ITEM_CLASSES = 'p-4 hover:bg-green-300';
const SIDEBAR_WIDTH = 'w-1/4';
const MAIN_CONTENT_WIDTH = 'w-3/4';
const SIDEBAR_BG_COLOR = 'bg-black';
const MAIN_CONTENT_BG_COLOR = 'bg-zinc-800';
const TEXT_COLOR = 'text-white';
const FULL_HEIGHT = 'h-screen';
const FLEX_CONTAINER = 'flex';

class CustomSidebar extends HTMLElement {
    
}

function exit(){
    if (!document.querySelector('.custom-dialog')) {
        const overlay = document.createElement('div');
        overlay.id= "overlayQuit";
        overlay.classList.add('custom-dialog');
        overlay.style.position = 'fixed';
        overlay.style.top = '0';
        overlay.style.left = '0';
        overlay.style.width = '100%';
        overlay.style.height = '100%';
        overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.5)';
        overlay.style.display = 'flex';
        overlay.style.justifyContent = 'center';
        overlay.style.alignItems = 'center';

        const customDialog = document.createElement('custom-dialog');
        overlay.appendChild(customDialog);

        document.body.appendChild(overlay);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const customSidebar = document.querySelector('custom-sidebar');

    if (customSidebar) {
        const firstItem = customSidebar.querySelector('li');
        if (firstItem) {
            firstItem.classList.add('selected');
        }

        customSidebar.addEventListener('click', (event) => {
            const clickedItem = event.target;
            if (clickedItem.tagName !== 'LI') {
                return;
            }
            const action = clickedItem.dataset.action;

            document.querySelectorAll('.sidebar li').forEach((item) => {
                if (item !== clickedItem) {
                    item.classList.remove('selected');
                }
            });

            clickedItem.classList.add('selected');

            switch (action) {
                case 'start':
                    break;
                case 'download':
                    break;
                case 'login':
                    break;
                case 'settings':
                    break;
                case 'exit':
                    exit();
                    break;
                default:
                    break;
            }
        });
    } else {
        console.error('error');
    }
});

customElements.define('custom-sidebar', CustomSidebar);