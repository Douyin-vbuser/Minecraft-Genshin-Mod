const { app, BrowserWindow } = require('electron')
const path = require('node:path')
const request = require('request');
const fs = require('fs');
const axios = require('axios');
const { spawn } = require('child_process');
const os = require('os');
const { memoryUsage } = require('node:process');

function createWindow() {
  const mainWindow = new BrowserWindow({
    width: 1068,
    height: 519,
    frame: false,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js')
    }
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
  launcher_folder();
});

//Expose method through ipc to renderer thread.
app.on('ready', () => {
  const { ipcMain } = require('electron');
  ipcMain.handle('download_environment', () => {
    download_environment();
  });
  ipcMain.handle('launchMinecraft', (event, version_name,uuid,user_name,width,height,accessToken,minecraft_path) => {
    launchMinecraft(version_name,uuid,user_name,width,height,accessToken,minecraft_path);
  });
  ipcMain.handle('writeSetting',(event,key,value)=>{
    writeSetting(key,value);
  });
  ipcMain.handle('readSetting',(event,key)=>{
    return readSetting(key);
  });
  ipcMain.handle('checkSetting',(event,key)=>{
    return checkSetting(key);
  });
  ipcMain.handle('checkMinecraft',(event,item)=>{
    return checkMinecraft(item);
  })
})

//Check setting in config profile.
function launcher_folder(){
  const mapleFolderExists = fs.existsSync(path.join(__dirname, 'Maple'));
  if (!mapleFolderExists) {
    fs.mkdirSync(path.join(__dirname, 'Maple'));
    fs.writeFileSync(path.join(__dirname, 'Maple', 'setting.json'), '{}');
  }
  if(!checkSetting('version')){
    writeSetting('version', 'beta_0.0.1');
  }
  if(!checkSetting('source_server')){
    writeSetting('source_server','https://proxy-gh.1l1.icu/https://github.com/Douyin-vbuser/Minecraft-Genshin-Mod/releases/download');
  }
  if(!checkSetting('java')){
    getJavaPath().then((paths) => {
      const javaPath = paths.split(';')[0];
      writeSetting("java",javaPath);
    }).catch((error) => {
      console.error(error);
    });
  }
  if(!checkSetting('minecraft_path')){
    writeSetting("minecraft_path",path.join(__dirname,'.minecraft'));
  }
  if(!checkSetting('version_name')){
    writeSetting("version_name",'1.12.2-Forge_14.23.5.2854-OptiFine_G5');
  }
}

//Read and Write information to setting.json.
function writeSetting(key, value) {
  const filePath = path.join(__dirname, 'Maple', 'setting.json');

  let data = {};
  try {
      data = JSON.parse(fs.readFileSync(filePath, 'utf8'));
  } catch (err) {
      console.error('Error reading setting.json:', err);
  }

  data[key] = value;

  try {
      fs.writeFileSync(filePath, JSON.stringify(data, null, 2));
      console.log('Setting updated successfully.');
  } catch (err) {
      console.error('Error writing setting.json:', err);
  }
}

function readSetting(key) {
  const data = JSON.parse(fs.readFileSync(path.join(__dirname, 'Maple', 'setting.json')));
  return data[key];
}

function checkSetting(key) {
  const value = readSetting(key);
  return value !== undefined && value !== null && value !== '';
}

//Download .minecraft.
function download_environment() {
  request(readSetting('source_server')+'/enviroment/default.minecraft.exe')
    .pipe(fs.createWriteStream('.minecraft.exe'))
    .on('finish', function () {
      console.log('Download completed.');
      extract_environment();
    });
}

//Extract .minecraft.7z.
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

//Get java path.
function getJavaPath() {
  return new Promise((resolve, reject) => {
    let cmd;
    let firstPath = true;
    if (os.platform() === 'win32') {
      cmd = 'where';
    } else {
      cmd = 'which';
    }

    const child = spawn(cmd, ['java']);

    let javaPaths = '';

    child.stdout.on('data', (data) => {
      const path = data.toString().trim();
      if (firstPath) {
        firstPath = false;
      } else {
        javaPaths += ';';
      }
      javaPaths += path;
    });

    child.stderr.on('data', (error) => {
      reject(error);
    });

    child.on('close', (code) => {
      if (code === 0) {
        resolve(javaPaths);
      } else {
        reject(`Failed to get Java path, exit code: ${code}`);
      }
    });
  });
}

//Launch Minecraft.
function launchMinecraft(version_name,uuid,user_name,width,height,accessToken,minecraft_path) {
  const win = BrowserWindow.getAllWindows();
  win.forEach(function (window) {
    window.minimize();
  })

  const freeMemory = os.freemem();
  const freeMemoryMB = Math.floor(freeMemory / 1024 / 1024);
  const java_path = readSetting('java');
  const minecraftArgs = " "+
  '-XX:+UseG1GC -XX:-UseAdaptiveSizePolicy -XX:-OmitStackTraceInFastThrow -Dfml.ignoreInvalidMinecraftCertificates=True -Dfml.ignorePatchDiscrepancies=True -Dlog4j2.formatMsgNoLookups=true -XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump -Xmn256m -Xmx'+freeMemoryMB+'m '+
  '\"-Djava.library.path='+minecraft_path+'\\versions\\'+version_name+'\\'+version_name+'-natives\" -cp \"'+minecraft_path+'\\libraries\\com\\mojang\\patchy\\1.3.9\\patchy-1.3.9.jar;'+minecraft_path+'\\libraries\\oshi-project\\oshi-core\\1.1\\oshi-core-1.1.jar;'+minecraft_path+'\\libraries\\net\\java\\dev\\jna\\jna\\4.4.0\\jna-4.4.0.jar;'+minecraft_path+'\\libraries\\net\\java\\dev\\jna\\platform\\3.4.0\\platform-3.4.0.jar;'+minecraft_path+'\\libraries\\com\\ibm\\icu\\icu4j-core-mojang\\51.2\\icu4j-core-mojang-51.2.jar;'+minecraft_path+'\\libraries\\net\\sf\\jopt-simple\\jopt-simple\\5.0.3\\jopt-simple-5.0.3.jar;'+minecraft_path+'\\libraries\\com\\paulscode\\codecjorbis\\20101023\\codecjorbis-20101023.jar;'+minecraft_path+'\\libraries\\com\\paulscode\\codecwav\\20101023\\codecwav-20101023.jar;'+minecraft_path+'\\libraries\\com\\'+
  'paulscode\\libraryjavasound\\20101123\\libraryjavasound-20101123.jar;'+minecraft_path+'\\libraries\\com\\paulscode\\librarylwjglopenal\\20100824\\librarylwjglopenal-20100824.jar;'+minecraft_path+'\\libraries\\com\\paulscode\\soundsystem\\20120107\\soundsystem-20120107.jar;'+minecraft_path+'\\libraries\\io\\netty\\netty-all\\4.1.9.Final\\netty-all-4.1.9.Final.jar;'+minecraft_path+'\\libraries\\com\\google\\guava\\guava\\21.0\\guava-21.0.jar;'+minecraft_path+'\\libraries\\org\\apache\\commons\\commons-lang3\\3.5\\commons-lang3-3.5.jar;'+minecraft_path+'\\libraries\\commons-io\\commons-io\\2.5\\commons-io-2.5.jar;'+minecraft_path+'\\libraries\\commons-codec\\commons-codec\\1.10\\commons-codec-1.10.jar;'+minecraft_path+'\\libraries\\net\\java\\jinput\\jinput\\2.0.5\\jinput-2.0.5.jar;'+minecraft_path+'\\libraries\\net\\java\\jutils\\jutils\\1.0.0\\jutils-1.0.0.jar;'
  +minecraft_path+'\\libraries\\com\\google\\code\\gson\\gson\\2.8.0\\gson-2.8.0.jar;'+minecraft_path+'\\libraries\\com\\mojang\\authlib\\1.5.25\\authlib-1.5.25.jar;'+minecraft_path+'\\libraries\\com\\mojang\\realms\\1.10.22\\realms-1.10.22.jar;'+minecraft_path+'\\libraries\\org\\apache\\commons\\commons-compress\\1.8.1\\commons-compress-1.8.1.jar;'+minecraft_path+'\\libraries\\org\\apache\\httpcomponents\\httpclient\\4.3.3\\httpclient-4.3.3.jar;'+minecraft_path+'\\libraries\\commons-logging\\commons-logging\\1.1.3\\commons-logging-1.1.3.jar;'+minecraft_path+'\\libraries\\org\\apache\\httpcomponents\\httpcore\\4.3.2\\httpcore-4.3.2.jar;'+minecraft_path+'\\libraries\\it\\unimi\\dsi\\fastutil\\7.1.0\\fastutil-7.1.0.jar;'+minecraft_path+'\\libraries\\org\\apache\\logging\\log4j\\log4j-api\\2.8.1\\log4j-api-2.8.1.jar;'+minecraft_path+'\\libraries\\org\\apache\\logging\\log4j\\log4j-core\\2.8.1\\log4j-core-2.8.1.jar;'+minecraft_path+
  '\\libraries\\org\\lwjgl\\lwjgl\\lwjgl\\2.9.4-nightly-20150209\\lwjgl-2.9.4-nightly-20150209.jar;'+minecraft_path+'\\libraries\\org\\lwjgl\\lwjgl\\lwjgl_util\\2.9.4-nightly-20150209\\lwjgl_util-2.9.4-nightly-20150209.jar;'+minecraft_path+'\\libraries\\com\\mojang\\text2speech\\1.10.3\\text2speech-1.10.3.jar;'+minecraft_path+'\\libraries\\net\\minecraft\\launchwrapper\\1.12\\launchwrapper-1.12.jar;'+minecraft_path+'\\libraries\\net\\minecraftforge\\forge\\1.12.2-14.23.5.2854\\forge-1.12.2-14.23.5.2854.jar;'+minecraft_path+'\\libraries\\org\\ow2\\asm\\asm-debug-all\\5.2\\asm-debug-all-5.2.jar;'+minecraft_path+'\\libraries\\org\\jline\\jline\\3.5.1\\jline-3.5.1.jar;'+minecraft_path+'\\libraries\\com\\typesafe\\akka\\akka-actor_2.11\\2.3.3\\akka-actor_2.11-2.3.3.jar;'+minecraft_path+'\\libraries\\com\\typesafe\\config\\1.2.1\\config-1.2.1.jar;'+minecraft_path+'\\libraries\\org\\scala-lang\\scala-actors-migration_2.11\\1.1.0\\scala-actors-migration_2.11-1.1.0.jar;'
  +minecraft_path+'\\libraries\\org\\scala-lang\\scala-compiler\\2.11.1\\scala-compiler-2.11.1.jar;'+minecraft_path+'\\libraries\\org\\scala-lang\\plugins\\scala-continuations-library_2.11\\1.0.2_mc\\scala-continuations-library_2.11-1.0.2_mc.jar;'+minecraft_path+'\\libraries\\org\\scala-lang\\plugins\\scala-continuations-plugin_2.11.1\\1.0.2_mc\\scala-continuations-plugin_2.11.1-1.0.2_mc.jar;'+minecraft_path+'\\libraries\\org\\scala-lang\\scala-library\\2.11.1\\scala-library-2.11.1.jar;'+minecraft_path+'\\libraries\\org\\scala-lang\\scala-parser-combinators_2.11\\1.0.1\\scala-parser-combinators_2.11-1.0.1.jar;'+minecraft_path+'\\libraries\\org\\scala-lang\\scala-reflect\\2.11.1\\scala-reflect-2.11.1.jar;'+minecraft_path+'\\libraries\\org\\scala-lang\\scala-swing_2.11\\1.0.1\\scala-swing_2.11-1.0.1.jar;'+minecraft_path+'\\libraries\\org\\scala-lang\\scala-xml_2.11\\1.0.2\\scala-xml_2.11-1.0.2.jar;'+minecraft_path+'\\libraries\\lzma\\lzma\\0.0.1\\lzma-0.0.1.jar;'+minecraft_path+
  '\\libraries\\java3d\\vecmath\\1.5.2\\vecmath-1.5.2.jar;'+minecraft_path+'\\libraries\\net\\sf\\trove4j\\trove4j\\3.0.3\\trove4j-3.0.3.jar;'+minecraft_path+'\\libraries\\optifine\\OptiFine\\1.12.2_HD_U_G5\\OptiFine-1.12.2_HD_U_G5.jar;'+minecraft_path+'\\libraries\\org\\apache\\maven\\maven-artifact\\3.5.3\\maven-artifact-3.5.3.jar;'+minecraft_path+'\\versions\\'+version_name+'\\'+version_name+'.jar\" net.minecraft.launchwrapper.Launch --username '+user_name+' --version '+version_name+' --gameDir \"'+minecraft_path+'\\versions\" --assetsDir \"'+minecraft_path+'\\assets\" --assetIndex 1.12 '
  +'--uuid '+uuid+' --accessToken '+accessToken+' --userType msa --tweakClass net.minecraftforge.fml.common.launcher.FMLTweaker --versionType Forge --height '+height+' --width '+width+' --tweakClass optifine.OptiFineForgeTweaker'

  const command = '\"'+java_path+'\"'+ minecraftArgs;

  if (!fs.existsSync('logs')) {
    fs.mkdirSync('logs');
  }
  const exec = require('child_process').exec;
  
  const folderPath = 'logs';
  exec(process.platform === 'win32' ? `attrib +h "${folderPath}"` : `chflags hidden "${folderPath}"`);

  exec(command, (error, stdout, stderr) => {
    win.forEach(function (window) {
      window.restore();
    })
  })
}

//Check if config is set.
function checkMinecraft(item){
  var a = false;
  if(item=="minecraft"){
    const minecraft_path = readSetting('minecraft_path');
    const version_name = readSetting('version_name');
    a = (fs.existsSync(minecraft_path+'\\versions\\'+version_name+'\\'+version_name+'.jar'));
  }
  if(item=="mod"){
    a=true;
  }
  if(item=="map"){
    a=true;
  }
  if(item=="resource"){
    a=false;
  }
  if(item=="all"){
    a=checkMinecraft("minecraft")&&checkMinecraft("mod")&&checkMinecraft("map")&&checkMinecraft("resource");
  }
  return a;
}