const { app, BrowserWindow } = require('electron')
const path = require('node:path')
const request = require('request');
const fs = require('fs');
const axios = require('axios');

function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 900,
    height: 480,
    frame: false
  });

  mainWindow.loadFile('index.html');

}

app.on('ready', createWindow);

app.on('web-contents-created', (e, contents) => {
  contents.on('will-navigate', (event, navigationUrl) => {
    if (navigationUrl.endsWith('exit.html')) {
      app.quit();
    }
  })
});

app.on('ready', () => {
  
})

function download_environment() {
  if(isChina()){
    console.log("Source:硬 核 马 赛 克");
    download_environment2();
  }else{
    console.log("Source:Github");
    download_environment1();
  }
}

function download_environment1() {
  request('https://proxy-gh.1l1.icu/https://github.com/Douyin-vbuser/Minecraft-Genshin-Mod/releases/download/enviroment/default.minecraft.exe')
    .pipe(fs.createWriteStream('.minecraft.7z'))
    .on('finish', function () {
      console.log('Download completed.');
      extract_environment();
    });
}

function download_environment2() {
  request("http://硬 核 马 赛 克:11451/.minecraft.exe")
    .pipe(fs.createWriteStream('.minecraft.7z'))
    .on('finish', function () {
      console.log('Download completed.');
      extract_environment();
    })
}

async function isChina() {
  try {
    const response = await axios.get('http://ip-api.com/json')
    const country = response.data.country
    if (country === 'China') {
      return true;
    } else {
      return false;
    }
  } catch (error) {
    console.error('Error fetching data:', error)
  }
}

function extract_environment() {
  const { spawn } = require('child_process');

  const selfExtractingArchivePath = './.minecraft.exe';
  const destinationPath = './';

  const sevenZipSpawn = spawn(selfExtractingArchivePath, [
    '-y', 
    `x`, 
    `-o${destinationPath}` 
  ]);

  sevenZipSpawn.stdout.on('data', (data) => {
    console.log(`stdout: ${data}`);
  });

  sevenZipSpawn.stderr.on('data', (data) => {
    console.error(`stderr: ${data}`);
  });

  sevenZipSpawn.on('close', (code) => {
    console.log(`child process exited with code ${code}`);
  });

}