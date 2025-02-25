package cookbook.win_api;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Extension interface for User32.dll to expose additional methods.
 */
public interface User32Ex extends StdCallLibrary {
  User32Ex INSTANCE = (User32Ex) Native.load("user32", User32Ex.class);

  boolean DrawMenuBar(WinDef.HWND hWnd);
}