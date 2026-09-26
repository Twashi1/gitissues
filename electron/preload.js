const { contextBridge, ipcRenderer } = require('electron');

window.addEventListener("DOMContentLoaded", () => {
    console.log("GitIssues Electron renderer loaded");
});

contextBridge.exposeInMainWorld('electronAPI', {
  selectDirectory: () => ipcRenderer.invoke('select-directory'),
});
