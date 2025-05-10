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
import xyz.duncanruns.jingle.util.VersionUtil;

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

    // Version compare
    private static final int NEWER = 1;
    private static final int OLDER = -1;
    // private static final int EQUAL = 0;

    public static void run() {
        new Thread(MoreProjectorsUpdater::runAsync, "more-projectors-updater").start();
    }

    private static void runAsync() {
        MoreProjectors.log(Level.DEBUG, "正在异步运行更新器！");
        try {
            if (tryCheckForUpdates()) {
                int ans = JOptionPane.showConfirmDialog(
                        JingleGUI.get(),
                        String.format("发现MoreProjectors插件新版本！\n现在更新？（v%s -> v%s）", currentVersion, latestVersion),
                        "MoreProjectors: 新版本",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
                );
                if(ans == JOptionPane.YES_OPTION) {
                    tryUpdate();
                }
            }
        } catch (Exception e) {
            MoreProjectors.logError("运行更新器时出现未知错误：\n", e);
        }
    }

    private static boolean tryCheckForUpdates() {
        try {
            return checkForUpdates();
        } catch (Exception e) {
            MoreProjectors.logError("无法检查更新：\n", e);
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
            meta = GrabUtil.grabJson("https://raw.githubusercontent.com/Naturean/Jingle-MoreProjectors-Plugin/zh_cn/meta.json");
        } catch (Exception e) {
            MoreProjectors.logError("抓取更新meta文件失败：", e);
            return false;
        }

        if (!meta.has("latest_version") || !meta.has("latest_download")) {
            MoreProjectors.log(Level.ERROR, "非法更新meta文件，请检查meta.json！");
            return false;
        }

        latestVersion = meta.get("latest_version").getAsString();
        latestDownloadLink = meta.get("latest_download").getAsString();

        return shouldUpdate(currentVersion, latestVersion);
    }

    private static void tryUpdate() {
        try {
            update();
        } catch (Exception e) {
            MoreProjectors.logError("无法更新：\n", e);
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
        MoreProjectors.log(Level.INFO, "退出并运行Powershell指令：" + powerCommand);

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
            MoreProjectors.log(Level.INFO, "正运行于最新版本。");
            return false;
        }

        int compare = VersionUtil.tryCompare(
                VersionUtil.extractVersion(currentVersion),
                VersionUtil.extractVersion(latestVersion),
                OLDER
        );

        if (compare == NEWER) {
            MoreProjectors.log(Level.INFO, "当前版本比最新版本更新！？");
            return false;
        }

        // if (compare == EQUAL)
        // Already checked.

        // if (compare == OLDER)
        return true;
    }
}
