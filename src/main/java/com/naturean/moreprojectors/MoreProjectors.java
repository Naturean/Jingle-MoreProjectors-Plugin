package com.naturean.moreprojectors;

import com.google.common.io.Resources;
import com.naturean.moreprojectors.gui.MoreProjectorsGUI;
import com.naturean.moreprojectors.hotkey.ProjectorHotkeyManager;
import com.naturean.moreprojectors.instance.InstanceWatcher;
import com.naturean.moreprojectors.obs.OBSLink;
import com.naturean.moreprojectors.util.I18n;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.JingleAppLaunch;
import xyz.duncanruns.jingle.gui.JingleGUI;
import xyz.duncanruns.jingle.plugin.PluginEvents;
import xyz.duncanruns.jingle.plugin.PluginManager;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

public class MoreProjectors {
    public static final Path MORE_PROJECTORS_FOLDER_PATH = Jingle.FOLDER.resolve("more-projectors-plugin");
    public static final Path PLUGINS_FOLDER_PATH = Jingle.FOLDER.resolve("plugins");
    public static final Path OBS_LINK_STATE_PATH = MORE_PROJECTORS_FOLDER_PATH.resolve("obs-link-state");
    public static final Path OBS_SCRIPT_PATH = MORE_PROJECTORS_FOLDER_PATH.resolve("more-projector-obs-link.lua");

    public static final String CURRENT_VERSION = Optional.ofNullable(MoreProjectors.class.getPackage().getImplementationVersion()).orElse("DEV");
    /** Current is development environment */
    public static final boolean IS_DEV = CURRENT_VERSION.equals("DEV");

    private static boolean running = false;

    public static MoreProjectorsOptions options = null;

    public static InstanceWatcher instanceWatcher = new InstanceWatcher();

    public static void main(String[] args) throws IOException {
        JingleAppLaunch.launchWithDevPlugin(args, PluginManager.JinglePluginData.fromString(
                Resources.toString(Resources.getResource(MoreProjectors.class, "/jingle.plugin.json"), Charset.defaultCharset())
        ), MoreProjectors::initialize);
    }

    public static void log(Level level, String message) {
        Jingle.log(level, "(MoreProjectors) " + message);
    }
    public static void logError(String failMessage, Throwable t) {
        Jingle.logError("(MoreProjectors) " + failMessage, t);
    }

    public static void initialize() {
        MoreProjectors.log(Level.INFO, I18n.format("plugin.running", CURRENT_VERSION));

        running = true;

        createMoreProjectorsFolder();
        createObsLinkStateFile();
        createObsScriptFile();

        options = MoreProjectorsOptions.load();
        ProjectorHotkeyManager.reload();
        ProjectorHotkeyManager.start();

        registerTick();
        registerExitWorld();
        registerStop();

        MoreProjectorsUpdater.run();

        JPanel configGUI = new MoreProjectorsGUI().mainPanel;
        JingleGUI.addPluginTab(I18n.get("gui.title.main"), configGUI);
    }

    private static void registerTick() {
        PluginEvents.END_TICK.register(MoreProjectors.options::tick);
        PluginEvents.END_TICK.register(OBSLink::tick);
    }

    private static void registerExitWorld() {
        PluginEvents.EXIT_WORLD.register(MoreProjectors.options::onExitWorld);
    }

    private static void registerStop() {
        PluginEvents.STOP.register(() -> running = false);
        PluginEvents.STOP.register(MoreProjectors.options::stop);
        PluginEvents.STOP.register(MoreProjectors.options::save);
    }

    public static boolean isRunning() {
        return running;
    }

    private static void createMoreProjectorsFolder() {
        if(MORE_PROJECTORS_FOLDER_PATH.toFile().mkdirs()) {
            MoreProjectors.log(Level.INFO, I18n.get("plugin.folder.created") + ": " + MORE_PROJECTORS_FOLDER_PATH);
        }
    }

    private static void createObsLinkStateFile() {
        try {
            if (OBS_LINK_STATE_PATH.toFile().createNewFile()) {
                MoreProjectors.log(Level.INFO, I18n.get("plugin.obs.link.state.created") + ": " + OBS_LINK_STATE_PATH);
            }
        } catch (Exception e) {
            MoreProjectors.logError(I18n.get("plugin.obs.link.state.create.failed") + ":\n", e);
        }
    }

    private static void createObsScriptFile() {
        try {
            String logMessage = Files.exists(OBS_SCRIPT_PATH) ? I18n.get("plugin.script.regenerated") : I18n.get("plugin.script.generated");
            Files.copy(Objects.requireNonNull(MoreProjectors.class.getResourceAsStream("/more-projector-obs-link.lua")), OBS_SCRIPT_PATH, StandardCopyOption.REPLACE_EXISTING);
            MoreProjectors.log(Level.INFO, logMessage);
        } catch (IOException e) {
            MoreProjectors.logError(I18n.get("plugin.obs.script.write.failed") + ":\n", e);
        }
    }

    /**
     * Get the path of jar file when running as a jar
     * <p>
     * Mostly references from <a href="https://github.com/DuncanRuns/Julti/blob/main/src/main/java/xyz/duncanruns/julti/Jingle.java">Julti</a>
     */
    public static Path getSourcePath() {
        try {
            URI uri = MoreProjectors.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path sourcePath = null;
            if (uri.getScheme().equals("file")) {
                String jarPath = uri.toString().replace("file:", "");
                sourcePath = Paths.get(jarPath);
            }
            return sourcePath;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}