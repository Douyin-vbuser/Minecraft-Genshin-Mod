const SIDEBAR_ITEM_CLASSES = 'p-4 hover:bg-green-300';
const SIDEBAR_WIDTH = 'w-1/4';
const MAIN_CONTENT_WIDTH = 'w-3/4';
const SIDEBAR_BG_COLOR = 'bg-black';
const MAIN_CONTENT_BG_COLOR = 'bg-zinc-800';
const TEXT_COLOR = 'text-white';
const FULL_HEIGHT = 'h-screen';
const FLEX_CONTAINER = 'flex';

var download_left = 0;
var mainContent = document.getElementsByClassName('main-content')[0];
var listener2;

class CustomSidebar extends HTMLElement {
    
}

//Exit app.
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

//Renderer Index page.
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

    var minecraft_path,version_name,uuid,user_name,accessToken,width,height;

    async function getSetting(){
        minecraft_path = await window.electronAPI.readSetting('minecraft_path');
        version_name = await window.electronAPI.readSetting('version_name');
        uuid = await window.electronAPI.readSetting('uuid');
        user_name = await window.electronAPI.readSetting('user_name');
        accessToken = await window.electronAPI.readSetting('accessToken');
        width = await window.electronAPI.readSetting('width');
        height = await window.electronAPI.readSetting('height');
    }
    getSetting();

    button.addEventListener('click', function() {
        window.electronAPI.launchMinecraft(version_name, uuid, user_name, width, height, accessToken, minecraft_path);
    })
}

//Renderer Download page.
function renderDownload() {
    setInterval.disabled = true;
    mainContent.innerHTML = '';

    //Render:
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

    //Event:
    async function necessaryUpdate() {
        var isAllUpdated = await window.electronAPI.checkMinecraft("all");
        if (!isAllUpdated) {
            mainContent.appendChild(button);
            count();
        } else {
            button.style.display = 'none';
            intervalId.disabled = true;
        }
    }
    necessaryUpdate();

    button.addEventListener('click', function () {
        download_left=114514;
        window.electronAPI.updateMCI();
        button.disabled = true;
        button.textContent = '更新中...';
        intervalId.disabled = false;
        count();
    });

    async function count() {
        download_left = await window.electronAPI.checkMinecraft('count');
        if (download_left == 0) {
            clearInterval(intervalId);
            renderDownload();
        }
    }

    var intervalId = setInterval(function () {
        if (download_left != 0) {
            if (window.electronAPI.checkFinish('minecraft')) {
                download_left--;
                if(download_left==0){
                    count();
                    clearInterval(intervalId);
                }
            }
            if(button.disabled){
                button.textContent = '更新中...';
                button.disabled = true;
            }
        }
    }, 1000);
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
        renderDownload();
    });
    target.addEventListener('blur', function() {
        window.electronAPI.writeSetting('minecraft_path', target.value);
        renderDownload();
    });
    versionName.addEventListener('blur', function() {
        window.electronAPI.writeSetting('version_name', versionName.value);
        renderDownload();
    })

    mainContent.appendChild(downloadDiv);
    mainContent.appendChild(targetDiv);
    mainContent.appendChild(versionDiv);
}

//Renderer Setting page.
function renderSettings() {
    //Render:
    mainContent.innerHTML = '';

    const labelSetting = document.createElement('label');
    labelSetting.textContent = '游戏设置';
    labelSetting.style.color = 'rgb(212,185,175)';
    labelSetting.style.fontSize = '30px';
    labelSetting.style.fontWeight = 'bold';
    mainContent.appendChild(labelSetting);

    const div = document.createElement('div');
    div.style.height = '10px';
    mainContent.appendChild(div);

    const javaDiv = document.createElement('div');
    const javaLable = document.createElement('label');
    javaLable.textContent = 'Java路径:';
    javaLable.style.color = 'rgb(223,200,170)';
    javaLable.style.fontSize = '15px';
    const javaPath = document.createElement('input');
    javaPath.style.color = 'rgb(168,239,221)';
    javaPath.style.backgroundColor = 'rgb(10,0,0)';
    javaPath.style.height = '20px';
    javaPath.style.fontSize = '15px';
    javaPath.style.width = '100%';
    javaPath.style.border = 'none';
    javaPath.type = 'text';
    javaDiv.appendChild(javaLable);
    javaDiv.appendChild(javaPath);

    const MCPathDiv = document.createElement('div');
    const MCPathLable = document.createElement('label');
    MCPathLable.textContent = '.minecraft路径:';
    MCPathLable.style.color = 'rgb(223,200,170)';
    MCPathLable.style.fontSize = '15px';
    const MCPath = document.createElement('input');
    MCPath.style.color = 'rgb(168,239,221)';
    MCPath.style.backgroundColor = 'rgb(10,0,0)';
    MCPath.style.height = '20px';
    MCPath.style.fontSize = '15px';
    MCPath.style.width = '100%';
    MCPath.style.border = 'none';
    MCPath.type = 'text';
    MCPathDiv.appendChild(MCPathLable);
    MCPathDiv.appendChild(MCPath);

    const versionDiv = document.createElement('div');
    const versionLable = document.createElement('label');
    versionLable.textContent = '版本名称:';
    versionLable.style.color = 'rgb(223,200,170)';
    versionLable.style.fontSize = '15px';
    const versionName = document.createElement('input');
    versionName.style.color = 'rgb(168,239,221)';
    versionName.style.backgroundColor = 'rgb(10,0,0)';
    versionName.style.height = '20px';
    versionName.style.fontSize = '15px';
    versionName.style.width = '100%';
    versionName.style.border = 'none';
    versionName.type = 'text';
    versionDiv.appendChild(versionLable);
    versionDiv.appendChild(versionName);

    const widthDiv = document.createElement('div');
    const widthLable = document.createElement('label');
    widthLable.textContent = '窗口宽度:';
    widthLable.style.color = 'rgb(223,200,170)';
    widthLable.style.fontSize = '15px';
    const width = document.createElement('input');
    width.style.color = 'rgb(168,239,221)';
    width.style.backgroundColor = 'rgb(10,0,0)';
    width.style.height = '20px';
    width.style.fontSize = '15px';
    width.style.width = '100%';
    width.style.border = 'none';
    width.type = 'text';
    widthDiv.appendChild(widthLable);
    widthDiv.appendChild(width);

    const heightDiv = document.createElement('div');
    const heightLable = document.createElement('label');
    heightLable.textContent = '窗口高度:';
    heightLable.style.color = 'rgb(223,200,170)';
    heightLable.style.fontSize = '15px';
    const height = document.createElement('input');
    height.style.color = 'rgb(168,239,221)';
    height.style.backgroundColor = 'rgb(10,0,0)';
    height.style.height = '20px';
    height.style.fontSize = '15px';
    height.style.width = '100%';
    height.style.border = 'none';
    height.type = 'text';
    heightDiv.appendChild(heightLable);
    heightDiv.appendChild(height);

    mainContent.appendChild(javaDiv);
    mainContent.appendChild(MCPathDiv);
    mainContent.appendChild(versionDiv);
    mainContent.appendChild(widthDiv);
    mainContent.appendChild(heightDiv);

    const div1 = document.createElement('div');
    div1.style.height = '10px';
    mainContent.appendChild(div1);

    const labelSetting2 = document.createElement('label');
    labelSetting2.textContent = '玩家设置';
    labelSetting2.style.color = 'rgb(212,185,175)';
    labelSetting2.style.fontSize = '30px';
    labelSetting2.style.fontWeight = 'bold';
    mainContent.appendChild(labelSetting2);

    const div2 = document.createElement('div');
    div2.style.height = '10px';
    mainContent.appendChild(div2);
    
    const playerNameDiv = document.createElement('div');
    const playerNameLable = document.createElement('label');
    playerNameLable.textContent = '玩家名称:';
    playerNameLable.style.color = 'rgb(223,200,170)';
    playerNameLable.style.fontSize = '15px';
    const playerName = document.createElement('input');
    playerName.style.color = 'rgb(168,239,221)';
    playerName.style.backgroundColor = 'rgb(10,0,0)';
    playerName.style.height = '20px';
    playerName.style.fontSize = '15px';
    playerName.style.border = 'none';
    playerName.type = 'text';
    playerNameDiv.appendChild(playerNameLable);
    playerNameDiv.appendChild(playerName);

    const playerOnlineDiv = document.createElement('div');
    const playerOnlineLable = document.createElement('label');
    playerOnlineLable.textContent = '账号状态:';
    playerOnlineLable.style.color = 'rgb(223,200,170)';
    playerOnlineLable.style.fontSize = '15px';
    const playerOnline = document.createElement('label');
    playerOnline.style.color = 'rgb(168,239,221)';
    playerOnline.style.fontSize = '15px';
    playerOnlineDiv.appendChild(playerOnlineLable);
    playerOnlineDiv.appendChild(playerOnline);

    mainContent.appendChild(playerNameDiv);
    mainContent.appendChild(playerOnlineDiv);

    const div3 = document.createElement('div');
    div3.style.height = '10px';
    mainContent.appendChild(div3);
    
    const labelSetting3 = document.createElement('label');
    labelSetting3.textContent = 'MCI信息';
    labelSetting3.style.color = 'rgb(212,185,175)';
    labelSetting3.style.fontSize = '30px';
    labelSetting3.style.fontWeight = 'bold';
    mainContent.appendChild(labelSetting3);

    const div4 = document.createElement('div');
    div4.style.height = '10px';
    mainContent.appendChild(div4);

    const MCIversionDiv = document.createElement('div');
    const MCIversionLable = document.createElement('label');
    MCIversionLable.textContent = 'MCI版本:';
    MCIversionLable.style.color = 'rgb(223,200,170)';
    MCIversionLable.style.fontSize = '15px';
    const MCIversion = document.createElement('label');
    MCIversion.style.color = 'rgb(168,239,221)';
    MCIversion.style.fontSize = '15px';
    MCIversion.textContent = 'beta 0.0.1';
    MCIversionDiv.appendChild(MCIversionLable);
    MCIversionDiv.appendChild(MCIversion);

    const CoreDiv = document.createElement('div');
    const CoreLable = document.createElement('label');
    CoreLable.textContent = '启动器核心:';
    CoreLable.style.color = 'rgb(223,200,170)';
    CoreLable.style.fontSize = '15px';
    const Core = document.createElement('label');
    Core.style.color = 'rgb(168,239,221)';
    Core.style.fontSize = '15px';
    Core.textContent = 'Maple_snapshot_20231029';
    CoreDiv.appendChild(CoreLable);
    CoreDiv.appendChild(Core);

    mainContent.appendChild(MCIversionDiv);
    mainContent.appendChild(CoreDiv);

    //Event:
    async function getSetting(){
        try{
            javaPath.value = await window.electronAPI.readSetting('java');
            MCPath.value = await window.electronAPI.readSetting('minecraft_path');
            versionName.value = await window.electronAPI.readSetting('version_name');
            width.value = await window.electronAPI.readSetting('width');
            height.value = await window.electronAPI.readSetting('height');
    
            playerName.value = await window.electronAPI.readSetting('user_name');
            var temp = await window.electronAPI.readSetting('accessToken');
            if(temp=='XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX'){
                playerOnline.textContent = '离线账户';
                playerName.disabled = false;
            }else{
                playerOnline.textContent = '正版账户';
                playerName.disabled = true;
            }
        }catch(e){
            console.log(e);
        }
    }
    getSetting();

    javaPath.addEventListener('blur',()=>{
        window.electronAPI.writeSetting('java',javaPath.value);
    });
    MCPath.addEventListener('blur',()=>{
        window.electronAPI.writeSetting('minecraft_path',MCPath.value);
    });
    versionName.addEventListener('blur',()=>{
        window.electronAPI.writeSetting('version_name',versionName.value);
    });
    width.addEventListener('blur',()=>{
        window.electronAPI.writeSetting('width',width.value);
    });
    height.addEventListener('blur',()=>{
        window.electronAPI.writeSetting('height',height.value);
    });
    playerName.addEventListener('blur',()=>{
        window.electronAPI.writeSetting('user_name',playerName.value);
    })
}

//Renderer Login page.
function renderLogin(){
    window.location.href = 'https://login.live.com/oauth20_authorize.srf?client_id=00000000402b5328&scope=service%3a%3auser.auth.xboxlive.com%3a%3aMBI_SSL&redirect_uri=https%3a%2f%2flogin.live.com%2foauth20_desktop.srf&response_type=code&prompt=login&uaid=ef1deb211f974fa9bd24a6328a4e6e7f&msproxy=1&issuer=mso&tenant=consumers&ui_locales=zh-CN&epct=PAQABAAEAAAAmoFfGtYxvRrNriQdPKIZ-uq5Ng0fwwx8En7ELDUqg1NjpoxMbvJpz__xRdRURSue8vesxWs57Dt_S77ukq0YFmNlN7Kn2-fZsxKdUK1HxBpsMyYqISVHaKM0O4MvUCpDVkksp8Rs4Lo0xdFSNiWVadjrvXNpwOnkzALbiGBwZI1g6OxoTl6nVBzspeO2GLwL44mSdxzImBo7LNa_yr8dZ17ctzy_YxZ6GsSklNHgg4SAA&jshs=0#'
}

//Implement sidebar.
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
                    renderLogin();
                    break;
                case 'settings':
                    renderSettings();
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