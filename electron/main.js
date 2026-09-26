const { app, BrowserWindow, dialog, ipcMain } = require("electron");
const { spawn } = require("child_process");
const path = require("path");
const fs = require("fs");
const net = require("net");

let backendProcess = null;
let mainWindow = null;
let backendPort = null;

ipcMain.handle('select-directory', async () => {
  const result = await dialog.showOpenDialog({
    properties: ['openDirectory'],
  });

  if (result.canceled) {
    return null;
  }

  return result.filePaths[0];
});

function getBackendJar() {
    if (app.isPackaged) {
        return path.join(
            process.resourcesPath,
            "backend",
            "gitissues.jar"
        );
    }

    return path.join(
        __dirname,
        "..",
        "server",
        "build",
        "libs",
        "gitissues.jar"
    );
}

/**
 * Ask the operating system for an available TCP port.
 */
function findFreePort() {
    return new Promise((resolve, reject) => {
        const server = net.createServer();

        server.unref();

        server.on("error", reject);

        server.listen(0, "127.0.0.1", () => {
            const address = server.address();

            if (!address || typeof address === "string") {
                server.close();
                reject(new Error("Unable to determine free port."));
                return;
            }

            const port = address.port;

            server.close(() => {
                resolve(port);
            });
        });
    });
}

function startBackend(port) {
    const jarPath = getBackendJar();

    if (!fs.existsSync(jarPath)) {
        throw new Error(`Backend JAR not found: ${jarPath}`);
    }

    const dataDirectory = app.getPath("userData");

    fs.mkdirSync(dataDirectory, {
        recursive: true
    });

    const databasePath = path.join(
        dataDirectory,
        "gitissues.db"
    );

    console.log("Starting GitIssues backend");
    console.log(`JAR: ${jarPath}`);
    console.log(`Port: ${port}`);
    console.log(`Database: ${databasePath}`);

    backendProcess = spawn(
        "java",
        [
            "-jar",
            jarPath,

            `--server.address=127.0.0.1`,

            // Dynamic localhost port
            `--server.port=${port}`,

            // User-specific SQLite database
            `--spring.datasource.url=jdbc:sqlite:${databasePath}`
        ],
        {
            stdio: ["ignore", "pipe", "pipe"]
        }
    );

    backendProcess.stdout.on("data", (data) => {
        console.log(`[Spring Boot] ${data.toString().trim()}`);
    });

    backendProcess.stderr.on("data", (data) => {
        console.error(`[Spring Boot] ${data.toString().trim()}`);
    });

    backendProcess.on("error", (error) => {
        console.error("Failed to start Spring Boot:", error);
    });

    backendProcess.on("close", (code) => {
        console.log(`Spring Boot exited with code ${code}`);

        backendProcess = null;
    });
}

async function waitForBackend(port) {
    const url = `http://127.0.0.1:${port}`;

    const maxAttempts = 60;

    for (let attempt = 1; attempt <= maxAttempts; attempt++) {
        try {
            const response = await fetch(url);

            if (response.ok) {
                console.log(`Spring Boot is ready at ${url}`);
                return;
            }
        } catch (error) {
            // Backend is not ready yet.
        }

        console.log(
            `Waiting for Spring Boot... ${attempt}/${maxAttempts}`
        );

        await new Promise((resolve) => {
            setTimeout(resolve, 500);
        });
    }

    throw new Error(
        `Spring Boot did not start within ${maxAttempts * 500}ms`
    );
}

async function createWindow() {
    mainWindow = new BrowserWindow({
        width: 1400,
        height: 900,

        webPreferences: {
            preload: path.join(__dirname, "preload.js"),

            // Keep the renderer isolated from Node.js.
            contextIsolation: true,
            nodeIntegration: false
        }
    });

    await waitForBackend(backendPort);

    const url = `http://127.0.0.1:${backendPort}`;

    console.log(`Loading application: ${url}`);

    await mainWindow.loadURL(url);
}

async function startApplication() {
    try {
        // Find a free port instead of assuming 8080.
        backendPort = await findFreePort();

        console.log(`Selected free port: ${backendPort}`);

        startBackend(backendPort);

        await createWindow();
    } catch (error) {
        console.error("Failed to start GitIssues:", error);

        app.quit();
    }
}

app.whenReady().then(startApplication);

app.on("before-quit", () => {
    if (backendProcess) {
        console.log("Stopping Spring Boot...");

        backendProcess.kill();

        backendProcess = null;
    }
});

app.on("window-all-closed", () => {
    if (process.platform !== "darwin") {
        app.quit();
    }
});
