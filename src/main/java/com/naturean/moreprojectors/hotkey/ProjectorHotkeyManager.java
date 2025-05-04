package com.naturean.moreprojectors.hotkey;

import com.naturean.moreprojectors.MoreProjectors;
import com.naturean.moreprojectors.projector.Projector;
import com.sun.jna.platform.win32.Win32VK;
import xyz.duncanruns.jingle.hotkey.Hotkey;
import xyz.duncanruns.jingle.util.KeyboardUtil;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static xyz.duncanruns.jingle.util.SleepUtil.sleep;

public final class ProjectorHotkeyManager {
    public static final CopyOnWriteArrayList<ProjectorHotkey> HOTKEYS = new CopyOnWriteArrayList<>();
    private static final Set<Integer> F3_INCOMPATIBLES = new HashSet<>(Arrays.asList(
            Win32VK.VK_F4.code, // F4
            65, 66, 67, 68, 70, 71, 72, 73, 76, 78, 80, 81, 83, 84, // A, B, C, D, F, G, H, I, L, N, P, Q, S, T
            49, 50, 51 // 1, 2, 3
    ));

    private ProjectorHotkeyManager() {
    }

    public static void start() {
        new Thread(ProjectorHotkeyManager::run, "more-projectors-hotkey-checker").start();
    }

    public static void reload() {
        HOTKEYS.clear();

        for(Projector projector: MoreProjectors.options.getProjectors()) {
            addHotkey(projector);
        }
    }

    public static void addHotkey(Projector projector) {
        if(!projector.enable) {
            return;
        }
        HOTKEYS.add(new ProjectorHotkey(
                Hotkey.of(projector.settings.hotkeys, projector.settings.ignoreModifiers),
                projector::toggle,
                projector.settings.alwaysActivate
        ));
    }

    private static void run() {
        while(MoreProjectors.isRunning()) {
            sleep(1);
            boolean f3IsPressed = KeyboardUtil.isPressed(Win32VK.VK_F3.code);
            for(ProjectorHotkey projectorHotkey : HOTKEYS) {
                if (projectorHotkey.alwaysActivate) {
                    projectorHotkey.toggle.run();
                    continue;
                }
                if (projectorHotkey.hotkey.wasPressed()) {
                    if (f3IsPressed && F3_INCOMPATIBLES.contains(projectorHotkey.hotkey.getMainKey())) continue;
                    try {
                        projectorHotkey.toggle.run();
                    } catch (Throwable t) {
                        MoreProjectors.logError("Error while running hotkey!", t);
                    }
                }
            }
        }
    }

    public static void inactivateOtherProjectors(List<Integer> hotkeys) {
        for (Projector projector: MoreProjectors.options.getProjectors()) {
            if (projector.settings.inactivateWhenOther && !Objects.equals(projector.settings.hotkeys, hotkeys)) {
                projector.toggle(false);
            }
        }
    }
}
