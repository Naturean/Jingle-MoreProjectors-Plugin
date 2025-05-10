package com.naturean.moreprojectors.projector;

import com.naturean.moreprojectors.hotkey.ProjectorSettingHotkey;
import xyz.duncanruns.jingle.instance.InstanceState;

import java.util.LinkedHashSet;

public final class ProjectorSettings {
    public boolean autoOpen = true;
    public boolean alwaysActivate = false;
    public boolean shouldBorderless = true;
    public boolean topWhenActive = true;
    public boolean minimizeWhenInactive = false;
    public boolean inactivateWhenOther = true;

    // [x,y,w,h]
    public int[] geometry = new int[]{0, 0, 0, 0};
    // [t,b,l,r]
    public int[] clipping = new int[]{0, 0, 0, 0};

    public LinkedHashSet<ProjectorSettingHotkey> hotkeys = new LinkedHashSet<>();

    public LinkedHashSet<InstanceState> allowedInstanceStates = new LinkedHashSet<>();
    public LinkedHashSet<InstanceState.InWorldState> allowedInWorldStates = new LinkedHashSet<>();

    public ProjectorSettings() {
        this.allowedInstanceStates.add(InstanceState.INWORLD);
        this.allowedInWorldStates.add(InstanceState.InWorldState.UNPAUSED);
        this.allowedInWorldStates.add(InstanceState.InWorldState.PAUSED);
    }

    public ProjectorSettings(boolean autoOpen, boolean alwaysActivate, boolean shouldBorderless, boolean topWhenActive,
                             boolean minimizeWhenInactive, boolean inactivateWhenOther, int[] geometry, int[] clipping, LinkedHashSet<ProjectorSettingHotkey> hotkeys,
                             LinkedHashSet<InstanceState> allowedInstanceStates, LinkedHashSet<InstanceState.InWorldState> allowedInWorldStates)
    {
        this.autoOpen = autoOpen;
        this.alwaysActivate = alwaysActivate;
        this.shouldBorderless = shouldBorderless;
        this.topWhenActive = topWhenActive;
        this.minimizeWhenInactive = minimizeWhenInactive;
        this.inactivateWhenOther = inactivateWhenOther;
        this.geometry = geometry;
        this.clipping = clipping;
        this.hotkeys = hotkeys;
        this.allowedInstanceStates = allowedInstanceStates;
        this.allowedInWorldStates = allowedInWorldStates;
    }

    public boolean isGeometryLegal() {
        return (this.geometry[0] >= 0 && this.geometry[1] >= 0 && this.geometry[2] > 0 && this.geometry[3] > 0);
    }

    public boolean isClippingLegal() {
        return (this.clipping[0] >= 0 && this.clipping[1] >= 0 && this.clipping[2] >= 0 && this.clipping[3] >= 0);
    }
}
