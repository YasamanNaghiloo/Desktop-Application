package cookbook.savelocal;
import java.io.*;
import java.util.ArrayList;

public class SaveToFile {

  String path;

  public SaveToFile(String path) {
    setPath(path);
  }

  private void setPath(String path) {
    this.path = path;
  }

  public void writeObjectToFile(ArrayList<Object> objects) {

    File file = new File(path);
    // Create directories if they do not exist
    file.getParentFile().mkdirs();

    try (FileOutputStream fos = new FileOutputStream(path, true);
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {

      // write object to file
      oos.writeObject(objects);

    } catch (IOException ex) {
      //TODO: Printclass?????
      ex.printStackTrace();
    }
  }
  
}