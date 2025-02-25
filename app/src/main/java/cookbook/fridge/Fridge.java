package cookbook.fridge;

import cookbook.Ingredient;
import cookbook.TripleTuple;
import cookbook.Unit;
import java.util.ArrayList;

/**
 * Describes the fridge which contains ingredients already at home.
 */
public class Fridge {

  ArrayList<TripleTuple<Ingredient, Double, Unit>> fridgeItems = new ArrayList<>();

  public Fridge() {

  }

  public ArrayList<TripleTuple<Ingredient, Double, Unit>> getFridgeItems() {
    return fridgeItems;
  }

  //?????
  /**
   * Get the names of all ingredients in the fridge.
   *
   * @return The names of all the ingredients.
   */
  public String[] getIngredientNames() {
    String[] ingredientNames = new String[fridgeItems.size()];
    int i = 0;
    for (TripleTuple<Ingredient, Double, Unit> tuple : fridgeItems) {
      ingredientNames[i] = tuple.first.getName();
    }
    return ingredientNames;
  }

  //?????

  /**
   * Get the names of all units in the fridge.
   *
   * @return The units of all the ingredients.
   */
  public String[] getUnitNames() {
    String[] unitNames = new String[fridgeItems.size()];
    int i = 0;
    for (TripleTuple<Ingredient, Double, Unit> tuple : fridgeItems) {
      unitNames[i] = tuple.third.getName();
    }
    return unitNames;
  }

  /**
   * Get quantities for all items in the fridge.
   *
   * @return The quantities for all the items in the fridge
   */
  public Double[] getQuantitys() {
    Double[] quantities = new Double[fridgeItems.size()];
    int i = 0;
    for (TripleTuple<Ingredient, Double, Unit> tuple : fridgeItems) {
      quantities[i] = tuple.second;
    }
    return quantities;
  }
  
}
