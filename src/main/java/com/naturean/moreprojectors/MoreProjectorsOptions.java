package com.naturean.moreprojectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naturean.moreprojectors.projector.Projector;
import com.naturean.moreprojectors.projector.ProjectorSettings;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.util.FileUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MoreProjectorsOptions {
    public static final Path OPTIONS_PATH = MoreProjectors.MORE_PROJECTORS_FOLDER_PATH.resolve("options.json").toAbsolutePath();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private CopyOnWriteArrayList<Projector> projectors = new CopyOnWriteArrayList<>();

    private MoreProjectorsOptions() {
    }

    public synchronized void tick() {
        for (Projector p: this.projectors) {
            p.tick();
        }
    }

    public void onExitWorld() {
        for (Projector p: this.projectors) {
            if (p.getActivated()) {
                p.toggle();
            }
        }
    }

    public synchronized void stop() {
        for (Projector p: this.projectors) {
            p.close();
        }
    }

    public void addProjector(Projector projector) {
        projectors.add(projector);
    }

    public void removeProjector(Projector projector) {
        for (Projector p: this.projectors) {
            if (p.equals(projector)) {
                projectors.remove(p);
                return;
            }
        }
    }

    public List<Projector> getProjectors() {
        return new CopyOnWriteArrayList<>(projectors);
    }

    public synchronized void editProjector(Projector target, String name, ProjectorSettings settings) {
        CopyOnWriteArrayList<Projector> newList = new CopyOnWriteArrayList<>();
        for (Projector p : projectors) {
            newList.add(p.equals(target) ? new Projector(name, p.enable, settings) : p);
        }
        this.projectors = newList;
    }

    public void setProjectorEnable(Projector target, boolean enable) {
        for (Projector p : projectors) {
            if (p.equals(target)) {
                p.enable = enable;
                break;
            }
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
