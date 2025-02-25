package cookbook.win_api;

import java.util.Arrays;
import java.util.List;
import com.sun.jna.Structure;

/**
 * Only used for WinAPI; This class is a C Structure, that's
 * why it doesn't use encapsulation.
 */
public class Margins extends Structure {
  public int cxLeftWidth;
  public int cxRightWidth;
  public int cyTopHeight;
  public int cyBottomHeight;

  @Override
  protected List<String> getFieldOrder() {
    return Arrays.asList("cxLeftWidth", "cxRightWidth", "cyTopHeight", "cyBottomHeight");
  }
}