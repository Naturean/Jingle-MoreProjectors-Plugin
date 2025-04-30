package com.naturean.moreprojectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naturean.moreprojectors.projector.Projector;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.util.FileUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MoreProjectorsOptions {
    public static final Path OPTIONS_PATH = MoreProjectors.MORE_PROJECTORS_FOLDER_PATH.resolve("options.json").toAbsolutePath();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public List<Projector> projectors = new ArrayList<>();

    private MoreProjectorsOptions() {
    }

    public synchronized void tick() {
        for (Projector projector: this.projectors) {
            projector.tick();
        }
    }

    public void onExitWorld() {
        for (Projector projector: this.projectors) {
            if (!projector.settings.alwaysActivate && projector.getActivated()) {
                projector.toggle();
            }
        }
    }

    public synchronized void stop() {
        for (Projector projector: this.projectors) {
            projector.close();
        }
    }

    public static MoreProjectorsOptions load() {
        if(Files.exists(OPTIONS_PATH)) {
            try {
                MoreProjectorsOptions options = FileUtil.readJson(OPTIONS_PATH, MoreProjectorsOptions.class);
                MoreProjectors.log(Level.INFO, "Options loaded successfully.");
                return options;
            } catch (Exception e) {
                MoreProjectors.logError("Failed to load options.json:", e);
            }
        }
        return (new MoreProjectorsOptions());
    }

    public void save() {
        try {
            FileUtil.writeString(OPTIONS_PATH, GSON.toJson(this));
        } catch(Exception e) {
            MoreProjectors.logError("Failed to save options.json:", e);
        }
    }
}
