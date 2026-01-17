package com.naturean.moreprojectors.instance;

import com.naturean.moreprojectors.util.I18n;
import xyz.duncanruns.jingle.instance.InstanceState;

import java.util.LinkedHashSet;

public class InstanceStateUtils {
    private InstanceStateUtils() {
    }

    public static String formatInstanceState(InstanceState instanceState) {
        switch (instanceState) {
            case WAITING:
                return I18n.get("format.instance.state.waiting");
            case TITLE:
                return I18n.get("format.instance.state.title");
            case INWORLD:
                return I18n.get("format.instance.state.inworld");
            case WALL:
                return I18n.get("format.instance.state.wall");
            case GENERATING:
                return I18n.get("format.instance.state.generating");
            case PREVIEWING:
                return I18n.get("format.instance.state.previewing");
        }
        return I18n.get("format.error");
    }

    public static String formatInWorldState(InstanceState.InWorldState inWorldState) {
        switch (inWorldState) {
            case UNPAUSED:
                return I18n.get("format.inworld.state.unpaused");
            case PAUSED:
                return I18n.get("format.inworld.state.paused");
            case GAMESCREENOPEN:
                return I18n.get("format.inworld.state.gamescreenopen");
        }
        return I18n.get("format.error");
    }

    public static String formatInstanceStates(LinkedHashSet<InstanceState> instanceStates) {
        StringBuilder sb = new StringBuilder();
        for (InstanceState instanceState : instanceStates) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(formatInstanceState(instanceState));
        }
        return sb.toString();
    }

    public static String formatInWorldStates(LinkedHashSet<InstanceState.InWorldState> inWorldStates) {
        StringBuilder sb = new StringBuilder();
        for (InstanceState.InWorldState inWorldState : inWorldStates) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(formatInWorldState(inWorldState));
        }
        return sb.toString();
    }
}
