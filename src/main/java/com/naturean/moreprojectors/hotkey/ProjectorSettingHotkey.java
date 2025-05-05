package com.naturean.moreprojectors.hotkey;

import xyz.duncanruns.jingle.hotkey.Hotkey;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public class ProjectorSettingHotkey {
    public static final ProjectorSettingHotkey ALWAYS_ACTIVATE = new ProjectorSettingHotkey(Collections.singletonList(Integer.MIN_VALUE), true);

    private List<Integer> keys;
    private boolean ignoreModifiers;

    public ProjectorSettingHotkey() {
        this.keys = Collections.emptyList();
        this.ignoreModifiers = true;
    }

    public ProjectorSettingHotkey(List<Integer> keys, boolean ignoreModifiers) {
        this.keys = keys;
        this.ignoreModifiers = ignoreModifiers;
    }

    public void setKeys(List<Integer> keys) {
        this.keys = keys;
    }

    public void setIgnoreModifiers(boolean ignoreModifiers) {
        this.ignoreModifiers = ignoreModifiers;
    }

    public List<Integer> getKeys() {
        return keys;
    }

    public boolean isIgnoreModifiers() {
        return ignoreModifiers;
    }

    public static String formatHotkey(ProjectorSettingHotkey hotkey) {
        return (hotkey.ignoreModifiers ? "* " : "") + Hotkey.formatKeys(hotkey.keys);
    }

    public static String formatHotkeys(LinkedHashSet<ProjectorSettingHotkey> hotkeys) {
        StringBuilder sb = new StringBuilder();
        for (ProjectorSettingHotkey settingHotkey: hotkeys) {
            if (settingHotkey.getKeys().isEmpty()) continue;
            if (sb.length() > 0) sb.append(" | ");
            sb.append(formatHotkey(settingHotkey));
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;

        ProjectorSettingHotkey that = (ProjectorSettingHotkey) o;
        return (
                Objects.equals(this.keys, that.keys)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(keys);
    }
}
