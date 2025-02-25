package cookbook.shoppinglist;

import cookbook.Ingredient;
import cookbook.RecipeInterface;
import cookbook.TripleTuple;
import cookbook.Unit;
import cookbook.db.IngredientDao;
import cookbook.db.ShoppingListDao;
import cookbook.db.UnitDao;
import cookbook.searcher.SearchInFile;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Describes the functionality of the shopping list.
 */
public class IngredientsShoppingList {

  private ShoppingListDao shoppingListDao;

  public IngredientsShoppingList() {
    shoppingListDao = new ShoppingListDao();
  }

  /**
   * Creates a shopping list pdf for the shopping list.
   */
  public void createShoppinglist() {

    ArrayList<TripleTuple<Ingredient, Double, Unit>> ingredTripleTuples = shoppingListDao.getShoppingListItems();
    System.out.println();
    String cwd = System.getProperty("user.dir");
    PdfCreator pdfCreator = new PdfCreator();

    ArrayList<String> amounts = new ArrayList<>();
    for (int i = 0; i < ingredTripleTuples.size(); i++) {
      String s = ingredTripleTuples.get(i).second + " " + ingredTripleTuples.get(i).third.getName();
      amounts.add(s);
    }

    ArrayList<String> ingredienNames = new ArrayList<>();
    for (int i = 0; i < ingredTripleTuples.size(); i++) {
      String s = ingredTripleTuples.get(i).first.getName();
      ingredienNames.add(s);
    }
    LocalDateTime dateTime = LocalDateTime.now();
    // Define a custom date and time formatter without milliseconds
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    // Format the current date and time using the formatter
    String formattedDateTime = dateTime.format(formatter);

    WeekFields weekFields = WeekFields.of(Locale.getDefault());
    int week = dateTime.get(weekFields.weekOfWeekBasedYear());

    // pdfCreator.createShoppingList(cwd + "/ShoppingList.pdf",test,test, false);
    pdfCreator.createPdfFromXml(ingredienNames, amounts, cwd
        + "/src/main/resources/ShoppingList.xml",
        cwd + "/src/main/resources/styleShoppingList.xsl", cwd + "/ShoppingList/shoppingListWeek"
            + formattedDateTime + ".pdf",
        week);

  }

  /**
   * Create pdf from one weekly dinner list only, with no respect to the the
   * current shopping list.
   *
   * @param recipeList The list of recipes.
   * @param week       The week number.
   * @param year       The year.
   */
  public void createShoppinglistFromWeeklyDinnerList(ArrayList<RecipeInterface> recipeList,
      int week, int year) {
    ArrayList<String> totingredientNames = new ArrayList<>();
    ArrayList<Double> totquantitys = new ArrayList<>();
    ArrayList<Unit> totUnits = new ArrayList<>();
    IngredientDao ingredientDao = new IngredientDao();
    // Go through every recipe
    for (RecipeInterface recipe : recipeList) {
      ResultSet res = ingredientDao.getIngredientsFromDb(recipe.getId());
      ArrayList<String> ingredientNames = new ArrayList<>();
      ArrayList<Double> quantitys = new ArrayList<>();
      ArrayList<Unit> units = new ArrayList<>();
      try {
        // Go through each entry and combine if possible
        while (res.next()) {
          String ingredientName = res.getString("ingredientName");
          Unit unit = new Unit(res.getInt("unitId"), res.getString("unitName"));
          Double quantity = res.getDouble("quantity");
          List<Integer> indexes = findIndexesInArrayList(totingredientNames, ingredientName);
          if (indexes.size() >= 1) {
            for (int i : indexes) {
              SearchInFile searchInFile = new SearchInFile();
              if (totUnits.get(i).equals(unit)) {
                Double q = totquantitys.get(i);
                totquantitys.set(i, q + quantity);
              } else {
                // try to convert
                Double convertedQunaitty;
                try {
                  convertedQunaitty = searchInFile.convertToUnit(
                      ingredientName, quantity, unit, totUnits.get(i),
                      ingredientDao.getDensity(ingredientDao
                          .getIngredientId(ingredientName)));
                } catch (IllegalArgumentException e) {
                  convertedQunaitty = null;
                }

                if (convertedQunaitty != null) {
                  totquantitys.set(i, convertedQunaitty + totquantitys.get(i));
                } else {
                  // Make sure that it is the last chance to merge to avoid duplicates
                  if (indexes.indexOf(i) + 1 >= indexes.size()) {
                    units.add(unit);
                    ingredientNames.add(ingredientName);
                    quantitys.add(quantity);
                    totUnits.add(unit);
                    totingredientNames.add(ingredientName);
                    totquantitys.add(quantity);
                  }
                }
              }

            }
          } else {
            units.add(unit);
            ingredientNames.add(ingredientName);
            quantitys.add(quantity);
            totUnits.add(unit);
            totingredientNames.add(ingredientName);
            totquantitys.add(quantity);
          }
        }
      } catch (SQLException e) {
        System.err.println(e);
      }
    }
    String cwd = System.getProperty("user.dir");
    PdfCreator pdfCreator = new PdfCreator();

    ArrayList<String> amounts = new ArrayList<>();
    for (int i = 0; i < totquantitys.size(); i++) {
      String s = totquantitys.get(i) + " " + totUnits.get(i);
      amounts.add(s);
    }
    // Create a pdf from the ingredients from the recipes.
    pdfCreator.createPdfFromXml(totingredientNames, amounts, cwd
        + "/src/main/resources/ShoppingList.xml",
        cwd + "/src/main/resources/styleShoppingList.xsl", cwd
            + "/ShoppingList/shoppingListWeek"
            + week + "Year" + year + ".pdf",
        week);
  }

  public List<Integer> findIndexesInArrayList(List<String> list, String target) {
    List<Integer> indexes = new ArrayList<>();
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).equals(target)) {
        indexes.add(i);
      }
    }
    return indexes;
  }

}
