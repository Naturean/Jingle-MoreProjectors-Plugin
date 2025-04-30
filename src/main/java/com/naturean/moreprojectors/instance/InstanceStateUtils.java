package com.naturean.moreprojectors.instance;

import xyz.duncanruns.jingle.instance.InstanceState;

import java.util.Set;

public class InstanceStateUtils {
    private InstanceStateUtils() {
    }

    public static String formatInstanceState(InstanceState instanceState) {
        switch (instanceState) {
            case WAITING:
                return "waiting";
            case TITLE:
                return "title";
            case INWORLD:
                return "inworld";
            case WALL:
                return "wall";
            case GENERATING:
                return "generating";
            case PREVIEWING:
                return "previewing";
        }
        return "(error)";
    }

    public static String formatInWorldState(InstanceState.InWorldState inWorldState) {
        switch (inWorldState) {
            case UNPAUSED:
                return "unpaused";
            case PAUSED:
                return "paused";
            case GAMESCREENOPEN:
                return "gamescreenopen";
        }
        return "(error)";
    }

    public static String formatInstanceStates(Set<InstanceState> instanceStates) {
        StringBuilder sb = new StringBuilder();
        for (InstanceState instanceState : instanceStates) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(formatInstanceState(instanceState));
        }
        return sb.toString();
    }

    public static String formatInWorldStates(Set<InstanceState.InWorldState> inWorldStates) {
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
