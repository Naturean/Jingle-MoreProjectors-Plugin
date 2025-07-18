package com.naturean.moreprojectors.win;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;

public class WindowUtils {
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);

        int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
        int GetWindowTextLengthW(HWND hWnd);
    }

    public static String getHwndTitle(HWND hwnd) {
        int length = User32.INSTANCE.GetWindowTextLengthW(hwnd);
        if (length <= 0) {
            return "";
        }

        char[] buffer = new char[length + 1];

        int resultLen = User32.INSTANCE.GetWindowTextW(hwnd, buffer, buffer.length);
        if (resultLen <= 0) {
            return "";
        }

        return new String(buffer, 0, resultLen);
    }
}