package com.naturean.moreprojectors.hotkey;

import xyz.duncanruns.jingle.hotkey.Hotkey;

public class ProjectorHotkey {
    public Hotkey hotkey;
    public Runnable toggle;
    public boolean alwaysActivate;

    public ProjectorHotkey(Hotkey hotkey, Runnable toggle, boolean alwaysActivate) {
        this.hotkey = hotkey;
        this.toggle = toggle;
        this.alwaysActivate = alwaysActivate;
    }
}
