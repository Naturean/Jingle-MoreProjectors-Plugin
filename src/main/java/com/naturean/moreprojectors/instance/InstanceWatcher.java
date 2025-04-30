package com.naturean.moreprojectors.instance;

import com.naturean.moreprojectors.MoreProjectors;
import com.sun.jna.platform.win32.WinDef;
import xyz.duncanruns.jingle.instance.InstanceChecker;
import xyz.duncanruns.jingle.instance.InstanceState;
import xyz.duncanruns.jingle.instance.OpenedInstanceInfo;
import xyz.duncanruns.jingle.util.FileUtil;
import xyz.duncanruns.jingle.win32.User32;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

public class InstanceWatcher {
    public OpenedInstanceInfo mainInstanceInfo = null;
    private InstanceState instanceState = null;
    private InstanceState.InWorldState inWorldState = null;

    public InstanceWatcher(){
    }

    private void reset() {
        this.mainInstanceInfo = null;
        this.instanceState = null;
        this.inWorldState = null;
    }

    private void updateMainInstanceInfo() {
        this.reset();

        Set<OpenedInstanceInfo> currentOpenInstances = InstanceChecker.getAllOpenedInstances();
        WinDef.HWND mainInstanceHwnd = User32.INSTANCE.GetForegroundWindow();;

        for (OpenedInstanceInfo openInstanceInfo: currentOpenInstances) {
            if (Objects.equals(openInstanceInfo.hwnd, mainInstanceHwnd)) {
                this.mainInstanceInfo = openInstanceInfo;
                return;
            }
        }
    }

    private void getMainInstanceState() {
        if (this.mainInstanceInfo == null) {
            return;
        }

        Path mainInstancePath = this.mainInstanceInfo.instancePath.toAbsolutePath();
        Path stateFilePath = mainInstancePath.resolve("wpstateout.txt");

        try {
            String stateString = FileUtil.readString(stateFilePath);

            if (stateString.isEmpty()) {
                return;
            }

            switch (stateString) {
                case "waiting":
                    this.instanceState = InstanceState.WAITING;
                    return;
                case "title":
                    this.instanceState = InstanceState.TITLE;
                    return;
                case "inworld,paused":
                    this.instanceState = InstanceState.INWORLD;
                    this.inWorldState = InstanceState.InWorldState.PAUSED;
                    return;
                case "inworld,unpaused":
                    this.instanceState = InstanceState.INWORLD;
                    this.inWorldState = InstanceState.InWorldState.UNPAUSED;
                    return;
                case "inworld,gamescreenopen":
                    this.instanceState = InstanceState.INWORLD;
                    this.inWorldState = InstanceState.InWorldState.GAMESCREENOPEN;
                    return;
                case "wall":
                    this.instanceState = InstanceState.WALL;
            }
        } catch (IOException e) {
            MoreProjectors.logError("Reading wpstateout.txt failed:\n", e);
        }
    }

    public void tick() {
        this.updateMainInstanceInfo();
        this.getMainInstanceState();
    }

    public InstanceState getCurrentState() {
        return this.instanceState;
    }

    public InstanceState.InWorldState getCurrentInWorldState() {
        return this.inWorldState;
    }
}
