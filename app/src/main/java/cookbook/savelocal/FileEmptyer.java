package cookbook.savelocal;

import java.io.*;

public class FileEmptyer {

  String path;

  public FileEmptyer(String path) {
    setPath(path);
  }

  private void setPath(String path) {
    this.path = path;
  }

  public void emptyFile() throws FileNotFoundException, IOException {
    try {
      new FileOutputStream(path).close();
    } catch (Exception e) {
      System.err.println(e);
    }
    
  }
}
