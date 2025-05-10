package com.naturean.moreprojectors.projector;

import com.naturean.moreprojectors.MoreProjectors;
import com.naturean.moreprojectors.win.WindowProcessUtils;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.instance.InstanceState;
import xyz.duncanruns.jingle.util.PidUtil;
import xyz.duncanruns.jingle.util.WindowStateUtil;
import xyz.duncanruns.jingle.util.WindowTitleUtil;
import xyz.duncanruns.jingle.win32.User32;
import xyz.duncanruns.jingle.win32.Win32Con;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Projector {
    public String name;
    public boolean enable;
    public ProjectorSettings settings;

    // transient for not saving into options.json
    @Nullable
    private transient WinDef.HWND hwnd = null;

    private transient boolean activated = false;

    private transient long lastCheck = 0;
    private transient long request = -1L;
    private transient int requestDecelerator = 0;

    public Projector(String name, boolean enable, ProjectorSettings settings) {
        this.name = name;
        this.enable = enable;
        this.settings = settings;
    }

    public long getRequestTime() {
        return this.request;
    }

    public boolean getActivated() {
        return this.activated;
    }

    public Rectangle getGeometry() {
        if(!this.settings.isGeometryLegal()) {
            Rectangle rect = new Rectangle(WindowStateUtil.getHwndRectangle(this.hwnd));

            this.settings.geometry[0] = rect.x;
            this.settings.geometry[1] = rect.y;
            this.settings.geometry[2] = rect.width;
            this.settings.geometry[3] = rect.height;

            if (Objects.equals(rect, WindowProcessUtils.INVALID_RECTANGLE)) {
                MoreProjectors.log(Level.WARN, "Got invalid rectangle for projector" + this.name + "!");
                this.settings.geometry[0] = 0;
                this.settings.geometry[1] = 0;
            }

            MoreProjectors.options.save();

            return rect;
        }
        return new Rectangle(
                this.settings.geometry[0],
                this.settings.geometry[1],
                this.settings.geometry[2],
                this.settings.geometry[3]
        );
    }

    public WinDef.HRGN getClipping() {
        if (!this.settings.isClippingLegal()) {
            this.settings.clipping[0] = Math.max(this.settings.clipping[0], 0);
            this.settings.clipping[1] = Math.max(this.settings.clipping[1], 0);
            this.settings.clipping[2] = Math.max(this.settings.clipping[2], 0);
            this.settings.clipping[3] = Math.max(this.settings.clipping[3], 0);
        }

        int windowWidth = this.settings.geometry[2];
        int windowHeight = this.settings.geometry[3];

        int newTop = this.settings.clipping[0];
        int newBottom = windowHeight - this.settings.clipping[1];
        int newLeft = this.settings.clipping[2];
        int newRight = windowWidth - this.settings.clipping[3];

        if (newRight <= newLeft || newBottom <= newTop) {
            MoreProjectors.log(Level.WARN, "Invalid clipping area for projector" + this.name + "!");

            newTop = 0;
            newBottom = windowHeight;
            newLeft = 0;
            newRight = windowWidth;

            this.settings.clipping[0] = 0;
            this.settings.clipping[1] = 0;
            this.settings.clipping[2] = 0;
            this.settings.clipping[3] = 0;
        }

        MoreProjectors.options.save();

        return GDI32.INSTANCE.CreateRectRgn(newLeft, newTop, newRight, newBottom);
    }

    public void applyTransform() {
        // Clipping cause getWindowRect() to return incorrect value.
        WindowProcessUtils.restoreHwndClipping(this.hwnd);
        if(this.settings.shouldBorderless) {
            WindowStateUtil.setHwndBorderless(this.hwnd);
        }
        WindowStateUtil.setHwndRectangle(this.hwnd, this.getGeometry());
        WindowProcessUtils.clipHwndRectangle(this.hwnd, this.getClipping());
    }

    public synchronized void tick() {
        long currentTime = System.currentTimeMillis();
        if (!this.enable) {
            this.request = -1L;
            this.requestDecelerator = 0;
            this.hwnd = null;
            return;
        }
        if(Math.abs(currentTime - this.lastCheck) > 500) {
            if (this.hwnd != null && !User32.INSTANCE.IsWindow(this.hwnd)) {
                this.hwnd = null;
            }
            if (this.hwnd == null) {
                this.lastCheck = currentTime;
                // EnumWindows() ends when it gets false callback
                User32.INSTANCE.EnumWindows((Hwnd, data) -> {
                    if (this.matchesWindow(Hwnd)) {
                        this.hwnd = Hwnd;
                        this.onProjectorFound();
                        return false;
                    }
                    return true;
                }, null);
                if (this.hwnd != null) {
                    this.closeExtra();
                }
            }
            if (this.hwnd == null) {
                // lower request speed
                if (isPowerOfTwo(this.requestDecelerator++)) {
                    this.request = currentTime;
                }
                if (this.requestDecelerator == 33) this.requestDecelerator -= 16;
            }
        }
    }

    private void onProjectorFound() {
        // unminimize first, else transform cannot be applied correctly.
        this.unminimize();
        this.applyTransform();
        this.setZOrder(1);
        this.request = -1L;
        this.requestDecelerator = 0;
        if (this.settings.minimizeWhenInactive) {
            this.minimize();
        }
    }

    private boolean isCorrectState() {
        if (this.settings.allowedInstanceStates.isEmpty()) return false;

        MoreProjectors.instanceWatcher.tick();

        InstanceState currentState = MoreProjectors.instanceWatcher.getCurrentState();
        InstanceState.InWorldState currentInWorldState = MoreProjectors.instanceWatcher.getCurrentInWorldState();

        // currentState equals null means that no instance detected, so it's a waiting state.
        boolean isAllowedInstanceState = (currentState == null) ? this.settings.allowedInstanceStates.contains(InstanceState.WAITING) : this.settings.allowedInstanceStates.contains(currentState);
        boolean needsInWorldCheck = (currentState == InstanceState.INWORLD) && this.settings.allowedInstanceStates.contains(InstanceState.INWORLD);
        boolean isAllowedInWorldState = this.settings.allowedInWorldStates.contains(currentInWorldState);

        return isAllowedInstanceState && (!needsInWorldCheck || isAllowedInWorldState);
    }

    public synchronized void toggle() {
        if (this.hwnd == null) return;

        if (this.isCorrectState() && (this.settings.alwaysActivate || !this.activated)) {
            this.activate();
        }
        else {
            this.inactivate();
        }
    }

    public synchronized void activate() {
        if (this.hwnd == null) return;

        this.unminimize();
        if (this.settings.topWhenActive) {
            // HWND_TOP, bring the window to the top
            this.setZOrder(0);

            // HWND_TOPMOST, keep top even when inactive
            this.setZOrder(-1);
        }
        this.activated = true;
    }

    public synchronized void inactivate() {
        if (this.hwnd == null) return;

        if (this.settings.minimizeWhenInactive) {
            this.minimize();
        }
        // HWND_BOTTOM, bring the window to the bottom
        this.setZOrder(1);
        this.activated = false;
    }

    private void setZOrder(int hwndInsertAfter) {
        User32.INSTANCE.SetWindowPos(this.hwnd,
                new WinDef.HWND(new Pointer(hwndInsertAfter)),
                0, 0, 0, 0, // Does not matter due to flags
                User32.SWP_NOSIZE | User32.SWP_NOMOVE | User32.SWP_NOACTIVATE
        );
    }

    private void minimize() {
        if (this.hwnd != null) User32.INSTANCE.ShowWindow(this.hwnd, User32.SW_MINIMIZE);
    }

    private void unminimize() {
        if (this.hwnd == null) return;

        WinUser.WINDOWPLACEMENT windowplacement = new WinUser.WINDOWPLACEMENT();
        windowplacement.length = windowplacement.size();

        if (User32.INSTANCE.GetWindowPlacement(this.hwnd, windowplacement).booleanValue()) {
            if (windowplacement.showCmd == WinUser.SW_SHOWMINIMIZED) {
                windowplacement.showCmd = WinUser.SW_SHOWNOACTIVATE;
                User32.INSTANCE.SetWindowPlacement(this.hwnd, windowplacement);
            }
        }
    }

    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;

        Projector that = (Projector) o;
        return (
                // only check name, because some properties may be changed, and the name will not be repeated
                Objects.equals(this.name, that.name)
        );
    }

    private boolean matchesWindow(WinDef.HWND Hwnd) {
        final Pattern OBS_EXECUTABLE_PATTERN = Pattern.compile("^.+[/\\\\]obs\\d\\d.exe$");
        return this.matchesTitle(WindowTitleUtil.getHwndTitle(Hwnd)) && OBS_EXECUTABLE_PATTERN.matcher(PidUtil.getProcessExecutable(PidUtil.getPidFromHwnd(hwnd))).matches();
    }

    private boolean matchesTitle(String title) {
        String regex = "^.* - " + this.name.replaceAll("([^a-zA-Z0-9 ])", "\\\\$1") + '$';
        return Pattern.compile(regex).matcher(title).matches();
    }

    private static boolean isPowerOfTwo(int x) {
        return (x & (x - 1)) == 0;
    }

    public synchronized void close() {
        this.hwnd = null;
        this.closeExtra();
    }

    private synchronized void closeExtra() {
        User32.INSTANCE.EnumWindows((Hwnd, data) -> {
            if (this.matchesWindow(Hwnd) && !Hwnd.getPointer().equals(Pointer.NULL) && !Objects.equals(Hwnd, this.hwnd)) {
                User32.INSTANCE.SendNotifyMessageA(Hwnd, new WinDef.UINT(User32.WM_SYSCOMMAND), new WinDef.WPARAM(Win32Con.SC_CLOSE), new WinDef.LPARAM(0));
            }
            return true;
        }, null);
    }
}
