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

document.addEventListener('DOMContentLoaded', () => {
    const customSidebar = document.querySelector('custom-sidebar');

    if (customSidebar) {
        customSidebar.addEventListener('click', (event) => {
            const clickedItem = event.target;
            const action = clickedItem.dataset.action;
            clickedItem.classList.add('selected');
            document.querySelectorAll('.sidebar li').forEach((item) => {
                if (item !== clickedItem) {
                    item.classList.remove('selected');
                }
            })
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
                    window.location.href = 'exit.html';
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