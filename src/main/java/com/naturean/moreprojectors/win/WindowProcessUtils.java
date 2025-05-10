package com.naturean.moreprojectors.win;

import com.sun.jna.platform.win32.WinDef;
import xyz.duncanruns.jingle.win32.User32;

import java.awt.*;

public class WindowProcessUtils {
    public static final Rectangle INVALID_RECTANGLE = new Rectangle(-32000, -32000, 160, 28);

    private WindowProcessUtils() {
    }

    public static void clipHwndRectangle(WinDef.HWND hwnd, WinDef.HRGN hrgn) {
        User32.INSTANCE.SetWindowRgn(hwnd, hrgn, true);
    }

    public static void restoreHwndClipping(WinDef.HWND hwnd) {
        User32.INSTANCE.SetWindowRgn(hwnd, null, true);
    }
}
