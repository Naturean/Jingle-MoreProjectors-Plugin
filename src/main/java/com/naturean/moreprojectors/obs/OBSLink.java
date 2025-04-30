package com.naturean.moreprojectors.obs;

import com.naturean.moreprojectors.MoreProjectors;
import com.naturean.moreprojectors.projector.Projector;
import xyz.duncanruns.jingle.util.FileUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public final class OBSLink {
    private static final Path OUT = MoreProjectors.MORE_PROJECTORS_FOLDER_PATH.resolve("obs-link-state");
    private static long lastUpdate = 0;
    private static String lastOutput = "";

    private OBSLink() {
    }

    public static void tick() {
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - lastUpdate) > 10) {
            lastUpdate = currentTime;
            String output = createOutput();
            if (Objects.equals(output, lastOutput)) return;
            lastOutput = output;
            try {
                FileUtil.writeString(OUT, output);
            } catch (IOException e) {
                MoreProjectors.logError("Failed to write obs-link-state:", e);
            }
        }
    }

    private static String createOutput() {
        String output = "";
        for(Projector projector: MoreProjectors.options.projectors) {
            long requestProjectorTime = projector.getRequestTime();
            output = String.format("%s%s\n", output, String.join("\t",
                    // Y    Proj Name  Y1234567890
                    (projector.enable && projector.settings.autoOpen) ? "Y" : "N",
                    projector.name,
                    requestProjectorTime == -1L ? "N" : "Y" + requestProjectorTime
            ));
        }
        return output;
    }
}
