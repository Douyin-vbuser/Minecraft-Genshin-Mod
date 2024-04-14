const { app, BrowserWindow } = require('electron')
const path = require('node:path')
const axios = require('axios');

function createWindow () {
  const mainWindow = new BrowserWindow({
    width: 900,
    height: 480,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js')
    },
    frame:false
  });

  mainWindow.loadFile('index.html');
  
}

app.on('ready', createWindow);

app.on('web-contents-created', (e, contents) => {
  contents.on('will-navigate', (event, navigationUrl) => {
    if (navigationUrl.endsWith('exit.html')) {
      app.quit();
  }
})});