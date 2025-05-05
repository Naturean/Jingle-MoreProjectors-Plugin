package com.naturean.moreprojectors.hotkey;

import xyz.duncanruns.jingle.hotkey.Hotkey;

import java.util.Set;

public class ProjectorHotkey {
    public Hotkey hotkey;
    public Set<Runnable> toggles;
    public boolean alwaysActivate;

    public ProjectorHotkey(Hotkey hotkey, Set<Runnable> toggles, boolean alwaysActivate) {
        this.hotkey = hotkey;
        this.toggles = toggles;
        this.alwaysActivate = alwaysActivate;
    }

    public void toggle() {
        for (Runnable toggle: this.toggles) {
            toggle.run();
        }
    }
}
