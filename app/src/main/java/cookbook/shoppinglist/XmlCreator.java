package cookbook.shoppinglist;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlCreator {

  public void createxml(ArrayList<String> ingredients, ArrayList<String> amounts, 
      String path, int weekNumber) {
    try {
      DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
      Document document = documentBuilder.newDocument();

      // Create root element
      Element shoppingListDataElement = document.createElement("shopping-list-data");
      document.appendChild(shoppingListDataElement);

      // Add week element
      Element weekElement = document.createElement("week");
      weekElement.appendChild(document.createTextNode(Integer.toString(weekNumber)));
      shoppingListDataElement.appendChild(weekElement);

      // Add ingredient and amounts
      for (int i = 0; i < ingredients.size(); i++) {
        Element ingredientDataElement = document.createElement("ingredient-data");
        shoppingListDataElement.appendChild(ingredientDataElement);

        Element ingredientElement = document.createElement("ingredient");
        ingredientElement.appendChild(document.createTextNode(ingredients.get(i)));
        ingredientDataElement.appendChild(ingredientElement);

        Element amountElement = document.createElement("amount");
        amountElement.appendChild(document.createTextNode(amounts.get(i)));
        ingredientDataElement.appendChild(amountElement);
      }

      // Write the XML content to file
      javax.xml.transform.TransformerFactory transformerFactory = javax.xml
          .transform.TransformerFactory.newInstance();
      javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
      javax.xml.transform.dom.DOMSource domSource = new javax.xml.transform.dom.DOMSource(document);
      javax.xml.transform.stream.StreamResult streamResult = new javax.xml.transform.stream
          .StreamResult(new java.io.File(path));
      transformer.transform(domSource, streamResult);

      System.out.println("XML file created successfully!");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
