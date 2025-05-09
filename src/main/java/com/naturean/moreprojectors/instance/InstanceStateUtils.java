package com.naturean.moreprojectors.instance;

import xyz.duncanruns.jingle.instance.InstanceState;

import java.util.LinkedHashSet;

public class InstanceStateUtils {
    private InstanceStateUtils() {
    }

    public static String formatInstanceState(InstanceState instanceState) {
        switch (instanceState) {
            case WAITING:
                return "等待";
            case TITLE:
                return "标题";
            case INWORLD:
                return "世界内";
            case WALL:
                return "墙";
            case GENERATING:
                return "生成中";
            case PREVIEWING:
                return "预览中";
        }
        return "(错误)";
    }

    public static String formatInWorldState(InstanceState.InWorldState inWorldState) {
        switch (inWorldState) {
            case UNPAUSED:
                return "未暂停";
            case PAUSED:
                return "暂停";
            case GAMESCREENOPEN:
                return "游戏界面打开";
        }
        return "(错误)";
    }

    public static String formatInstanceStates(LinkedHashSet<InstanceState> instanceStates) {
        StringBuilder sb = new StringBuilder();
        for (InstanceState instanceState : instanceStates) {
            if (sb.length() > 0) {
                sb.append("、");
            }
            sb.append(formatInstanceState(instanceState));
        }
        return sb.toString();
    }

    public static String formatInWorldStates(LinkedHashSet<InstanceState.InWorldState> inWorldStates) {
        StringBuilder sb = new StringBuilder();
        for (InstanceState.InWorldState inWorldState : inWorldStates) {
            if (sb.length() > 0) {
                sb.append("、");
            }
            sb.append(formatInWorldState(inWorldState));
        }
        return sb.toString();
    }
}
