package cookbook.dishlist;

import cookbook.RecipeInterface;
import java.io.Serializable;

/**
 * A dish, which contains a recipe and time of day to eat.
 */
public class Dish implements Comparable<Dish>, Serializable {

  private RecipeInterface recipe;
  private MealTime timeOfDay;

  protected Dish(RecipeInterface recipe, MealTime timeOfDay) {
    setRecipe(recipe);
    setMealTime(timeOfDay);
  }

  private void setRecipe(RecipeInterface recipe) {
    if (recipe == null) {
      throw new IllegalArgumentException(" Recipe can not be null");
    }
    this.recipe = recipe;
  }

  private void setMealTime(MealTime timeOfDay) {
    this.timeOfDay = timeOfDay;
  }

  @Override
  public int compareTo(Dish o) {
    // Compare by the time of day
    return this.timeOfDay.compareTo(o.timeOfDay);
  }

  @Override
  public String toString() {
    return recipe.getName();
  }

  protected MealTime getTimeOfDay() {
    return timeOfDay;
  }

  protected RecipeInterface getRecipe() {
    return recipe;
  }
  
}
