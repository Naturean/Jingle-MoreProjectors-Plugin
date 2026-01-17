package com.naturean.moreprojectors;

import com.github.tuupertunut.powershelllibjava.PowerShellExecutionException;
import com.google.gson.JsonObject;
import com.naturean.moreprojectors.gui.DownloadProgressFrame;
import com.naturean.moreprojectors.util.I18n;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.JingleAppLaunch;
import xyz.duncanruns.jingle.gui.JingleGUI;
import xyz.duncanruns.jingle.util.GrabUtil;
import xyz.duncanruns.jingle.util.PowerShellUtil;
import xyz.duncanruns.jingle.util.VersionUtil;

import javax.swing.*;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Mostly references <a href="https://github.com/DuncanRuns/Julti/blob/main/src/main/java/xyz/duncanruns/julti/JingleUpdater.java">Julti</a>
 */
public class MoreProjectorsUpdater {
    private static String currentVersion;
    private static String latestVersion;
    private static String latestDownloadLink;

    // Version compare
    private static final int NEWER = 1;
    private static final int OLDER = -1;
    // private static final int EQUAL = 0;

    public static void run() {
        new Thread(MoreProjectorsUpdater::runAsync, "more-projectors-updater").start();
    }

    private static void runAsync() {
        MoreProjectors.log(Level.DEBUG, I18n.get("updater.checking"));
        try {
            if (tryCheckForUpdates()) {
                int ans = JOptionPane.showConfirmDialog(
                        JingleGUI.get(),
                        I18n.format("updater.dialog.message", currentVersion, latestVersion),
                        I18n.get("updater.dialog.title"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
                );
                if(ans == JOptionPane.YES_OPTION) {
                    tryUpdate();
                }
            }
        } catch (Exception e) {
            MoreProjectors.logError(I18n.get("updater.unknown.error") + ":\n", e);
        }
    }

    private static boolean tryCheckForUpdates() {
        try {
            return checkForUpdates();
        } catch (Exception e) {
            MoreProjectors.logError(I18n.get("updater.check.failed") + ":\n", e);
        }
        return false;
    }

    private synchronized static boolean checkForUpdates() {
        JsonObject meta;

        synchronized (MoreProjectors.class) {
            currentVersion = MoreProjectors.CURRENT_VERSION;
        }

        if (MoreProjectors.IS_DEV) return false;

        try {
            meta = GrabUtil.grabJson("https://raw.githubusercontent.com/Naturean/Jingle-MoreProjectors-Plugin/main/meta.json");
        } catch (Exception e) {
            MoreProjectors.logError(I18n.get("updater.fetch.failed") + ":\n", e);
            return false;
        }

        if (!meta.has("latest_version")) {
            MoreProjectors.log(Level.ERROR, I18n.get("updater.meta.invalid"));
            return false;
        }


        String currentLanguage = I18n.getCurrentLanguage();
        // non-english language package ends with a "+ab_CD" classifier.
        String classifier = Objects.equals(currentLanguage, I18n.DEFAULT_LANGUAGE) ? "" : "+" + currentLanguage;
        // Concat download link depends on the current language
        String downloadLinkTemplate = "https://github.com/Naturean/Jingle-MoreProjectors-Plugin/releases/download/v%s/more-projectors-plugin-%s%s.jar";

        latestVersion = meta.get("latest_version").getAsString();
        latestDownloadLink = String.format(downloadLinkTemplate, latestVersion, latestVersion, classifier);

        return shouldUpdate(currentVersion, latestVersion);
    }

    private static void tryUpdate() {
        try {
            update();
        } catch (Exception e) {
            MoreProjectors.logError(I18n.get("updater.download.failed") + ":\n", e);
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
        MoreProjectors.log(Level.INFO, I18n.get("updater.powershell.command") + ": " + powerCommand);

        PowerShellUtil.execute(powerCommand);
        System.exit(0);
    }

    private static void downloadWithProgress(String download, Path newJarPath) throws IOException {
        JProgressBar bar = new DownloadProgressFrame(JingleGUI.get()).getBar();
        bar.setMaximum((int) GrabUtil.getFileSize(download));
        GrabUtil.download(download, newJarPath, bar::setValue, 128);
    }

    private static boolean shouldUpdate(String currentVersion, String latestVersion) {
        if (currentVersion.equals(latestVersion)) {
            MoreProjectors.log(Level.INFO, I18n.get("updater.running.latest"));
            return false;
        }

        int compare = VersionUtil.tryCompare(
                VersionUtil.extractVersion(currentVersion),
                VersionUtil.extractVersion(latestVersion),
                OLDER
        );

        if (compare == NEWER) {
            MoreProjectors.log(Level.INFO, I18n.get("updater.current.newer"));
            return false;
        }

        // if (compare == EQUAL)
        // Already checked.

        // if (compare == OLDER)
        return true;
    }
}
