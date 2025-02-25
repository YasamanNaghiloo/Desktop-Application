package cookbook.savelocal;

import java.util.ArrayList;
import java.io.*;

public class ReadObjectsFromFile<T> {

  String path;

  public ReadObjectsFromFile(String path) {
    setPath(path);
  }

  private void setPath(String path) {
    this.path = path;
  }

  public ArrayList<T> readObjectsfromFile() throws IOException {

    ObjectInputStream objectinputstream = null;
    ArrayList<T> record = new ArrayList<>();
    try {
      FileInputStream streamIn = new FileInputStream(path);
      objectinputstream = new ObjectInputStream(streamIn);
      ArrayList<T> readCase = (ArrayList<T>) objectinputstream.readObject();
      record.addAll(readCase);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (objectinputstream != null){
        objectinputstream.close();
      } 
    }
    return record;
  }
}