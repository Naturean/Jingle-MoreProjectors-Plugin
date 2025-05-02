package com.naturean.moreprojectors;

import com.github.tuupertunut.powershelllibjava.PowerShellExecutionException;
import com.google.gson.JsonObject;
import com.naturean.moreprojectors.gui.DownloadProgressFrame;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.JingleAppLaunch;
import xyz.duncanruns.jingle.gui.JingleGUI;
import xyz.duncanruns.jingle.util.GrabUtil;
import xyz.duncanruns.jingle.util.PowerShellUtil;

import javax.swing.*;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Mostly references <a href="https://github.com/DuncanRuns/Julti/blob/main/src/main/java/xyz/duncanruns/julti/JingleUpdater.java">Julti</a>
 */
public class MoreProjectorsUpdater {
    private static String currentVersion;
    private static String latestVersion;
    private static String latestDownloadLink;

    public static void run() {
        try {
            if (tryCheckForUpdates()) {
                int ans = JOptionPane.showConfirmDialog(
                        JingleGUI.get(),
                        String.format("A newer version of MoreProjectors Plugin is found!\nUpdate now? (v%s -> v%s)", currentVersion, latestVersion),
                        "MoreProjectors: New Version",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
                );
                if(ans == JOptionPane.YES_OPTION) {
                    tryUpdate();
                }
            }
        } catch (Exception e) {
            MoreProjectors.logError("Unknown error when running updater:\n", e);
        }
    }

    private static boolean tryCheckForUpdates() {
        try {
            return checkForUpdates();
        } catch (Exception e) {
            MoreProjectors.logError("Could not check for updates:\n", e);
        }
        return false;
    }

    private synchronized static boolean checkForUpdates() {
        JsonObject meta;

        synchronized (MoreProjectors.class) {
            currentVersion = MoreProjectors.CURRENT_VERSION;
        }

        if (currentVersion.equals("DEV")) return false;

        try {
            meta = GrabUtil.grabJson("https://raw.githubusercontent.com/Naturean/Jingle-MoreProjectors-Plugin/main/meta.json");
        } catch (Exception e) {
            MoreProjectors.logError("Failed to grab update meta:", e);
            return false;
        }

        if (!meta.has("latest_version") || !meta.has("latest_download")) {
            MoreProjectors.log(Level.ERROR, "Update meta is invalid, please check meta.json!");
            return false;
        }

        latestVersion = meta.get("latest_version").getAsString();
        latestDownloadLink = meta.get("latest_download").getAsString();

        return !latestVersion.equals(currentVersion);
    }

    private static void tryUpdate() {
        try {
            update();
        } catch (Exception e) {
            MoreProjectors.logError("Could not update:\n", e);
        }
    }

    private static void update() throws IOException, PowerShellExecutionException {
        Path newJarPath = MoreProjectors.PLUGINS_FOLDER_PATH.resolve(URLDecoder.decode(FilenameUtils.getName(latestDownloadLink), StandardCharsets.UTF_8.name()));

        if (!Files.exists(newJarPath)) {
            downloadWithProgress(latestDownloadLink, newJarPath);
        }

        // Release LOCK so updating can go smoothly
        JingleAppLaunch.releaseLock();

        // Use powershell's start-process to start it detached
        Path javaExe = Paths.get(System.getProperty("java.home")).resolve("bin").resolve("javaw.exe");

        // -deleteOldJar is one of the args of Jingle for deleting specific file
        String powerCommand = String.format("start-process '%s' '-jar \"%s\" -deleteOldJar \"%s\"'", javaExe, Jingle.getSourcePath(), MoreProjectors.getSourcePath());
        MoreProjectors.log(Level.INFO, "Exiting and running powershell command: " + powerCommand);

        PowerShellUtil.execute(powerCommand);
        System.exit(0);
    }

    private static void downloadWithProgress(String download, Path newJarPath) throws IOException {
        JProgressBar bar = new DownloadProgressFrame(JingleGUI.get()).getBar();
        bar.setMaximum((int) GrabUtil.getFileSize(download));
        GrabUtil.download(download, newJarPath, bar::setValue, 128);
    }
}
