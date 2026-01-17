package com.naturean.moreprojectors.hotkey;

import com.naturean.moreprojectors.MoreProjectors;
import com.naturean.moreprojectors.projector.Projector;
import com.naturean.moreprojectors.util.I18n;
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
    private static List<Integer> last_activated = Collections.emptyList();

    private ProjectorHotkeyManager() {
    }

    public static void start() {
        new Thread(ProjectorHotkeyManager::run, "more-projectors-hotkey-checker").start();
    }

    public static void reload() {
        HOTKEYS.clear();

        Map<ProjectorSettingHotkey, Set<Runnable>> hotkeys = new HashMap<>();
        for(Projector projector: MoreProjectors.options.getProjectors()) {
            if (!projector.enable) continue;
            if (projector.settings.alwaysActivate) {
                hotkeys.computeIfAbsent(ProjectorSettingHotkey.ALWAYS_ACTIVATE, k -> new HashSet<>()).add(projector::toggle);
                continue;
            }

            for (ProjectorSettingHotkey hotkey : projector.settings.hotkeys) {
                if (hotkey.getKeys().isEmpty()) continue;
                hotkeys.computeIfAbsent(hotkey, k -> new HashSet<>()).add(projector::toggle);
            }
        }

        hotkeys.forEach((k, r) -> HOTKEYS.add(new ProjectorHotkey(
                Hotkey.of(k.getKeys(), k.isIgnoreModifiers()),
                r,
                k == ProjectorSettingHotkey.ALWAYS_ACTIVATE
        )));
    }

    private static void run() {
        while(MoreProjectors.isRunning()) {
            sleep(1);
            boolean f3IsPressed = KeyboardUtil.isPressed(Win32VK.VK_F3.code);
            for(ProjectorHotkey projectorHotkey : HOTKEYS) {
                if (projectorHotkey.alwaysActivate) {
                    projectorHotkey.toggle();
                }
                else if (projectorHotkey.hotkey.wasPressed()) {
                    if (f3IsPressed && F3_INCOMPATIBLES.contains(projectorHotkey.hotkey.getMainKey())) continue;
                    try {
                        if (!Objects.equals(last_activated, projectorHotkey.hotkey.getKeys())) {
                            // to hold on a multi-hotkey projector when switching resizes
                            inactivateActivatedWithSameKeys(projectorHotkey.hotkey);
                        }
                        projectorHotkey.toggle();
                        inactivateOtherProjectors(projectorHotkey.hotkey);
                        last_activated = projectorHotkey.hotkey.getKeys();
                    } catch (Throwable t) {
                        MoreProjectors.logError(I18n.get("hotkey.manager.run.error"), t);
                    }
                }
            }
        }
    }

    private static void inactivateActivatedWithSameKeys(Hotkey hotkey) {
        for (Projector projector: MoreProjectors.options.getProjectors()) {
            if (projector.getActivated() && !projector.settings.alwaysActivate) {
                boolean hasSameHotkey = projector.settings.hotkeys.stream()
                        .anyMatch(h -> Hotkey.of(h.getKeys(), h.isIgnoreModifiers()).equals(hotkey));

                if (hasSameHotkey) {
                    projector.inactivate();
                }
            }
        }
    }

    public static void inactivateOtherProjectors(Hotkey hotkey) {
        for (Projector projector: MoreProjectors.options.getProjectors()) {
            if (projector.settings.inactivateWhenOther && !projector.settings.alwaysActivate) {
                boolean hasSameHotkey = projector.settings.hotkeys.stream()
                        .anyMatch(h -> Hotkey.of(h.getKeys(), h.isIgnoreModifiers()).equals(hotkey));

                if (!hasSameHotkey) {
                    projector.inactivate();
                }
            }
        }
    }
}
