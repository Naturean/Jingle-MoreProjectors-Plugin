package com.naturean.moreprojectors.projector;

import xyz.duncanruns.jingle.instance.InstanceState;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ProjectorSettings {
    public boolean autoOpen = true;
    public boolean alwaysActivate = false;
    public boolean ignoreModifiers = true;
    public boolean shouldBorderless = true;
    public boolean topWhenActive = true;
    public boolean minimizeWhenInactive = false;
    public boolean inactivateWhenOther = true;

    // [x,y,w,h]
    public int[] geometry = new int[]{0, 0, 0, 0};

    public List<Integer> hotkeys = Collections.emptyList();

    public Set<InstanceState> allowedInstanceStates = new HashSet<>();
    public Set<InstanceState.InWorldState> allowedInWorldStates = new HashSet<>();

    public ProjectorSettings() {
        this.allowedInstanceStates.add(InstanceState.INWORLD);
        this.allowedInWorldStates.add(InstanceState.InWorldState.UNPAUSED);
        this.allowedInWorldStates.add(InstanceState.InWorldState.PAUSED);
    }

    public ProjectorSettings(boolean autoOpen, boolean alwaysActivate, boolean ignoreModifiers, boolean shouldBorderless, boolean topWhenActive,
                             boolean minimizeWhenInactive, boolean inactivateWhenOther, int[] geometry, List<Integer> hotkeys,
                             Set<InstanceState> allowedInstanceStates, Set<InstanceState.InWorldState> allowedInWorldStates)
    {
        this.autoOpen = autoOpen;
        this.alwaysActivate = alwaysActivate;
        this.ignoreModifiers = ignoreModifiers;
        this.shouldBorderless = shouldBorderless;
        this.topWhenActive = topWhenActive;
        this.minimizeWhenInactive = minimizeWhenInactive;
        this.inactivateWhenOther = inactivateWhenOther;
        this.geometry = geometry;
        this.hotkeys = hotkeys;
        this.allowedInstanceStates = allowedInstanceStates;
        this.allowedInWorldStates = allowedInWorldStates;
    }

    public boolean isGeometryLegal() {
        return (this.geometry[0] >= 0 && this.geometry[1] >= 0 && this.geometry[2] > 0 && this.geometry[3] > 0);
    }
}
