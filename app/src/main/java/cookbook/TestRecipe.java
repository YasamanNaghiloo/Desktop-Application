package cookbook;

import java.io.Serializable;

public class TestRecipe implements RecipeInterface, Serializable {
  String name;
  int id;

  public TestRecipe(String name, int id) {
    this.name = name;
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }
}
