const SIDEBAR_ITEM_CLASSES = 'p-4 hover:bg-green-300';
const SIDEBAR_WIDTH = 'w-1/4';
const MAIN_CONTENT_WIDTH = 'w-3/4';
const SIDEBAR_BG_COLOR = 'bg-black';
const MAIN_CONTENT_BG_COLOR = 'bg-zinc-800';
const TEXT_COLOR = 'text-white';
const FULL_HEIGHT = 'h-screen';
const FLEX_CONTAINER = 'flex';

var mainContent = document.getElementsByClassName('main-content')[0];

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

function renderStart() {
    mainContent.innerHTML = '';

    var button = document.createElement('button');
    button.textContent = '启动游戏';
    button.style.backgroundColor = 'rgb(141,31,26)';
    button.style.color = 'rgb(225,207,147)';
    button.style.position = 'absolute';
    button.style.right = '30px';
    button.style.bottom = '30px';
    button.style.width = '250px';
    button.style.height = '90px';
    button.style.fontSize = '30px';
    button.style.border = 'none';
    button.style.borderRadius = '5px';

    mainContent.appendChild(button);

    button.addEventListener('click', function() {
        const minecraft_path = "E:\\Desktop\\launcher\\.minecraft";
        const version_name = "1.12.2-Forge_14.23.5.2854-OptiFine_G5";
        const uuid = 'fb48efcbb7014a6f883d5f5bdacda3dd';
        const user_name = 'MCI_vbuser';
        const accessToken = 'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX';
        const width = '854';
        const height = '480';
        window.electronAPI.launchMinecraft(version_name, uuid, user_name, width, height, accessToken, minecraft_path);
    })
}

function renderDownload() {
    mainContent.innerHTML = '';

    var labelSetting = document.createElement('label');
    labelSetting.textContent = '下载设置';
    labelSetting.style.color = 'rgb(212,185,175)';
    labelSetting.style.fontSize = '30px';
    labelSetting.style.fontWeight = 'bold';
    mainContent.appendChild(labelSetting);

    var div = document.createElement('div');
    div.style.height = '10px';

    mainContent.appendChild(div);

    renderDownload1();

    var div3 = document.createElement('div');
    div3.style.height = '15px';

    mainContent.appendChild(div3);

    var labelUpdate = document.createElement('label');
    labelUpdate.textContent = '检查更新';
    labelUpdate.style.color = 'rgb(212,185,175)';
    labelUpdate.style.fontSize = '30px';
    labelUpdate.style.fontWeight = 'bold';
    mainContent.appendChild(labelUpdate);

    var div2 = document.createElement('div');
    div2.style.height = '10px';

    mainContent.appendChild(div2);

    renderDownload2();

    var button = document.createElement('button');
    button.textContent = '更新';
    button.style.backgroundColor = 'rgb(141,31,26)';
    button.style.color = 'rgb(225,207,147)';
    button.style.position = 'absolute';
    button.style.right = '30px';
    button.style.bottom = '30px';
    button.style.width = '250px';
    button.style.height = '90px';
    button.style.fontSize = '30px';
    button.style.border = 'none';
    button.style.borderRadius = '5px';

    mainContent.appendChild(button);

    async function necessaryUpdate() {
        var isAllUpadated = await window.electronAPI.checkMinecraft("all");
        if (!isAllUpadated) {
            mainContent.appendChild(updateButton);
        }
    }
    necessaryUpdate();
}

async function renderDownload2() {
    var environmentDiv = document.createElement('div');
    var label = document.createElement('label');
    label.textContent = '.minecraft文件夹:';
    label.style.color = 'rgb(223,200,170)';
    var enviroment = document.createElement('label');

    var isMinecraftExist = await window.electronAPI.checkMinecraft('minecraft');
    if (isMinecraftExist) {
        enviroment.style.color = 'rgb(168,239,221)';
        enviroment.textContent = '已存在';
    } else {
        enviroment.style.color = 'rgb(179,40,30)';
        enviroment.textContent = '不存在';
    }

    environmentDiv.appendChild(label);
    environmentDiv.appendChild(enviroment);

    var modDiv = document.createElement('div');
    var label1 = document.createElement('label');
    label1.textContent = 'mod:';
    label1.style.color = 'rgb(223,200,170)';
    var mod = document.createElement('label');

    var isModLatest = await window.electronAPI.checkMinecraft('mod');
    if (isModLatest) {
        mod.style.color = 'rgb(168,239,221)';
        mod.textContent = '已是最新版';
    } else {
        mod.style.color = 'rgb(179,40,30)';
        mod.textContent = '需要重新下载';
    }

    modDiv.appendChild(label1);
    modDiv.appendChild(mod);

    var mapDiv = document.createElement('div');
    var label2 = document.createElement('label');
    label2.textContent = '地图:';
    label2.style.color = 'rgb(223,200,170)';
    var map = document.createElement('label');

    var isMapLatest = await window.electronAPI.checkMinecraft('map');
    if (isMapLatest) {
        map.style.color = 'rgb(168,239,221)';
        map.textContent = '已是最新版';
    } else {
        map.style.color = 'rgb(179,40,30)';
        map.textContent = '需要下载或更新';
    }

    mapDiv.appendChild(label2);
    mapDiv.appendChild(map);

    var resourceDiv = document.createElement('div');
    var label3 = document.createElement('label');
    label3.textContent = '资源包:';
    label3.style.color = 'rgb(223,200,170)';
    var resource = document.createElement('label');

    var isResourceLatest = await window.electronAPI.checkMinecraft('resource');
    if (isResourceLatest) {
        resource.style.color = 'rgb(168,239,221)';
        resource.textContent = '已是最新版';
    } else {
        resource.style.color = 'rgb(179,40,30)';
        resource.textContent = '需要下载或更新';
    }

    resourceDiv.appendChild(label3);
    resourceDiv.appendChild(resource);

    mainContent.appendChild(environmentDiv);
    mainContent.appendChild(modDiv);
    mainContent.appendChild(mapDiv);
    mainContent.appendChild(resourceDiv);
}

function renderDownload1(){
    var downloadDiv = document.createElement('div');
    var label = document.createElement('label');
    label.textContent = '下载源:';
    label.style.color = 'rgb(223,200,170)';
    label.style.fontSize = '15px';
    var downloadSource = document.createElement('input');
    downloadSource.style.color = 'rgb(168,239,221)';
    downloadSource.style.backgroundColor = 'rgb(10,0,0)';
    downloadSource.style.height = '20px';
    downloadSource.style.fontSize = '15px';
    downloadSource.style.width = '100%';
    downloadSource.style.border = 'none';
    downloadSource.type = 'text';
    downloadDiv.appendChild(label);
    downloadDiv.appendChild(downloadSource);

    var targetDiv = document.createElement('div');
    var label1 = document.createElement('label');
    label1.textContent = '.minecraft文件夹:';
    label1.style.color = 'rgb(223,200,170)';
    label1.style.fontSize = '15px';
    var target = document.createElement('input');
    target.style.color = 'rgb(168,239,221)';
    target.style.backgroundColor = 'rgb(10,0,0)';
    target.style.height = '20px';
    target.style.fontSize = '15px';
    target.style.width = '100%';
    target.style.border = 'none';
    target.type = 'text';
    targetDiv.appendChild(label1);
    targetDiv.appendChild(target);

    var versionDiv = document.createElement('div');
    var label2 = document.createElement('label');
    label2.textContent = '版本名称:';
    label2.style.color = 'rgb(223,200,170)';
    label2.style.fontSize = '15px';
    var versionName = document.createElement('input');
    versionName.style.color = 'rgb(168,239,221)';
    versionName.style.backgroundColor = 'rgb(10,0,0)';
    versionName.style.height = '20px';
    versionName.style.fontSize = '15px';
    versionName.style.width = '100%';
    versionName.style.border = 'none';
    versionName.type = 'text';
    versionDiv.appendChild(label2);
    versionDiv.appendChild(versionName);

    async function getSetting() {
        try {
            downloadSource.value = await window.electronAPI.readSetting('source_server');
            target.value = await window.electronAPI.readSetting('minecraft_path');
            versionName.value = await window.electronAPI.readSetting('version_name');
        } catch (error) {
            console.error('读取设置时发生错误:', error);
        }
    }
    getSetting();

    downloadSource.addEventListener('blur', function() {
        window.electronAPI.writeSetting('source_server', downloadSource.value);
    });
    target.addEventListener('blur', function() {
        window.electronAPI.writeSetting('minecraft_path', target.value);
    });
    versionName.addEventListener('blur', function() {
        window.electronAPI.writeSetting('version_name', versionName.value);
    })

    mainContent.appendChild(downloadDiv);
    mainContent.appendChild(targetDiv);
    mainContent.appendChild(versionDiv);
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
                    renderStart();
                    break;
                case 'download':
                    renderDownload();
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

window.addEventListener('load', renderStart);

customElements.define('custom-sidebar', CustomSidebar);