package cookbook.dishlist;

import cookbook.RecipeInterface;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The menu with all dishes for one day.
 */
public class DayMenu implements Serializable, Comparable<DayMenu> {

  private ArrayList<Dish> dishes;
  private Weekday weekday;

  protected DayMenu(Weekday weekday) {
    dishes = new ArrayList<>();
    setWeekday(weekday);
  }

  protected DayMenu(DayMenu other) {
    this.weekday = other.getWeekDay();
    this.dishes = new ArrayList<>();
        
    // Copy dishes
    for (Dish originalDish : other.dishes) {
      this.dishes.add(new Dish(originalDish.getRecipe(), originalDish.getTimeOfDay()));
    }
  }

  protected DayMenu(Weekday weekday, ArrayList<Dish> dishs) {
    dishes = dishs;
    setWeekday(weekday);
  }

  protected void addDish(Dish dish) {
    dishes.add(dish);
  }

  private void setWeekday(Weekday weekday) {
    this.weekday = weekday;
  }

  protected Weekday getWeekDay() {
    return weekday;
  }
  
  protected ArrayList<ArrayList<String>> getStringRepresentation() {
    ArrayList<ArrayList<String>> dayDishStrings = new ArrayList<>();
    ArrayList<Dish> dishes = getDishes();
    MealTime[] mealTimes = MealTime.values();
    
    for (int i = 0; i < mealTimes.length; i++) {
      ArrayList<String> s = new ArrayList<>(); 
      for (Dish dish : dishes) {
      
        if (dish.getTimeOfDay() == mealTimes[i]) {
          s.add(dish.toString());
        }
      }
      dayDishStrings.add(s);
    }
    return dayDishStrings;
  }

  protected ArrayList<ArrayList<RecipeInterface>> getRecipeRepresentation() {
    ArrayList<ArrayList<RecipeInterface>> dayDishRecipe = new ArrayList<>();
    ArrayList<Dish> dishes = getDishes();
    MealTime[] mealTimes = MealTime.values();
    
    for (int i = 0; i < mealTimes.length; i++) {
      ArrayList<RecipeInterface> s = new ArrayList<>(); 
      for (Dish dish : dishes) {
      
        if (dish.getTimeOfDay() == mealTimes[i]) {
          s.add(dish.getRecipe());
        }
      }
      dayDishRecipe.add(s);
    }
    return dayDishRecipe;
  }

  protected ArrayList<Dish> getDishes() {
    ArrayList<Dish> dishs = new ArrayList<>();
    for (int i = 0; i < dishes.size(); i++) {
      Dish d = dishes.get(i);
      dishs.add(new Dish(d.getRecipe(), d.getTimeOfDay()));
    }
    return dishs;
  }

  @Override
  public int compareTo(DayMenu o) {
    // Compare by the time of day
    return this.weekday.compareTo(o.weekday);
  }

  //TODO: Implement remove

}
