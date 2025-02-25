package cookbook.shoppinglist;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.fop.apps.*;

/**
 * Creates shopping list pdfs.
 */
public class PdfCreator {

  /**
   * Creates a pdf.
   *
   * @param ingredients The ingredient names of the shopping list.
   * @param amount      The amount of each ingredient.
   * @param xmlFilePath The filepath to the xml.
   * @param xslFilePath The filepath to the xsl.
   * @param pdfFilePath The filepath to save the pdf to.
   * @param weekNumber  The weeknumber of the shoppinglist.
   */
  public void createPdfFromXml(ArrayList<String> ingredients, ArrayList<String> amount,
      String xmlFilePath, String xslFilePath, String pdfFilePath, int weekNumber) {
    try {
      XmlCreator xmlcCreator = new XmlCreator();
      xmlcCreator.createxml(ingredients, amount, xmlFilePath, weekNumber);
      // Setup input and output files
      File xmlFile = new File(xmlFilePath);
      File xslFile = new File(xslFilePath);

      String directoryPath = System.getProperty("user.dir") + "/ShoppingList";

      // Convert the string path to a Path object
      Path path = Paths.get(directoryPath);

      // Check if the directory exists
      if (Files.exists(path)) {
        System.out.println("Directory already exists: " + path);
      } else {
        try {
          // Create the directory
          Files.createDirectories(path);
          System.out.println("Directory created: " + path);
        } catch (IOException e) {
          e.printStackTrace();
          System.err.println("Failed to create directory: " + path);
        }
      }
      File pdfFile = new File(pdfFilePath);

      // Configure FOP
      FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());

      // Setup output stream
      OutputStream out = new FileOutputStream(pdfFile);
      Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

      // Setup Transformer
      TransformerFactory factory = TransformerFactory.newInstance();
      Transformer transformer = factory.newTransformer(new StreamSource(xslFile));

      // Perform transformation
      Source src = new StreamSource(xmlFile);
      Result res = new SAXResult(fop.getDefaultHandler());
      transformer.transform(src, res);

      // Cleanup
      out.close();
    } catch (Exception e) {
      System.err.println(e);
    }

  }
}
