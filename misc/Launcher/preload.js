const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld(
  'electronAPI', {
    downloadEnvironment: () => ipcRenderer.invoke('download_environment'),
    launchMinecraft: (version_name, uuid, user_name, width, height, accessToken, minecraft_path) =>
      ipcRenderer.invoke('launchMinecraft', version_name, uuid, user_name, width, height, accessToken, minecraft_path),
    writeSetting: (key, value) => ipcRenderer.invoke('writeSetting', key, value),
    readSetting: key => ipcRenderer.invoke('readSetting', key),
    checkSetting: key => ipcRenderer.invoke('checkSetting', key),
    checkMinecraft: (item) => ipcRenderer.invoke('checkMinecraft',item)
  }
);