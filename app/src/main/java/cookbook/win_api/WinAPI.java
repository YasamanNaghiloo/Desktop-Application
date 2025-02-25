package cookbook.win_api;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

/**
 * Utility class for interacting with the Windows API.
 */
public class WinAPI {
  public static final String TITLE_HASH = "6dcd4ce23d88e2ee95838f7b014b6284f4976b6723a54a0e764b88c6bd8b3200";

  /**
   * Hides the window title bar on Windows.
   * WARNING: This method uses the WIN32 API and is only supported on Windows.
   *          DO NOT EDIT THIS METHOD UNLESS YOU KNOW WHAT YOU ARE DOING.
   */
  public static void hideWindowTitleBar() {
    final int GWL_STYLE = -16;
    final int WS_CAPTION = 0x00C00000;

    // Get the window handle
    WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, TITLE_HASH);

    // Get the current window style
    int oldStyle = User32.INSTANCE.GetWindowLong(hwnd, GWL_STYLE);

    // Remove the title bar from the style
    int newStyle = oldStyle & ~WS_CAPTION;
    User32.INSTANCE.SetWindowLong(hwnd, GWL_STYLE, newStyle);

    // Redraw the window
    User32Ex.INSTANCE.DrawMenuBar(hwnd);

    Margins margins = new Margins();
    margins.cxLeftWidth = 0;
    margins.cxRightWidth = 0;
    margins.cyTopHeight = 0;
    margins.cyBottomHeight = 0;

    DwmApi.INSTANCE.DwmExtendFrameIntoClientArea(hwnd, margins);
  }
}
