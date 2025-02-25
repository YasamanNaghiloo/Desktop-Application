package cookbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {

  /**
   * Gets a property from a properties file.
   *
   * @param path The path of the property file.
   * @param propertyName The name of the propery to look for.
   *
   * @return The property which was requested.
   */
  public String getProperty(String path, String propertyName) {
    String s = "";
    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream(path));
      s = properties.getProperty(propertyName);
    } catch (IOException e) {
      System.out.println(e);
      //TODO:USE PRINTCLASS
    }
    return s;
  }
    
}
