class CustomDialog extends HTMLElement {
    connectedCallback() {
        this.render();
        this.addEventListener('click', this.handleButtonClick);
    }

    handleButtonClick(event) {
        if (event.target.classList.contains('confirm')) {
            window.location.href="exit.html"
        } else if (event.target.classList.contains('cancel')) {
            this.remove();
            window.location.href="index.html"
        }
    }

    render() {
        this.innerHTML = `
        <div class="dialog">
            <p class="text-lg">你确定要退出吗?</p>
            <div class="flex justify-end space-x-2">
                <button class="button confirm">确认</button>
                <button class="button cancel">取消</button>
            </div>
        </div>
        `;
    }
}

customElements.define('custom-dialog', CustomDialog);