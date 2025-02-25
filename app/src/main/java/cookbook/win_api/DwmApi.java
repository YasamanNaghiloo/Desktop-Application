package cookbook.win_api;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Extension interface for DwmApi.dll to expose additional methods.
 */
public interface DwmApi extends StdCallLibrary {
  DwmApi INSTANCE = (DwmApi) Native.load("dwmapi", DwmApi.class);

  int DwmExtendFrameIntoClientArea(WinDef.HWND hWnd, Margins pMarInset);
}
