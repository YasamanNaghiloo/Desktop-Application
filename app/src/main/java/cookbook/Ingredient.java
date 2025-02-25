package cookbook;

import java.util.Objects;

/**
 * Represents ingredients.
 */
public class Ingredient {

  private int id;
  private String name;
  private Double density;

  /**
   * Constructor for ingredient.
   *
   * @param id The id of the ingredient.
   * @param name The name of the ingredient.
   * @param density The density of th ingredient.
   */
  public Ingredient(int id, String name, Double density) {
    setName(name);
    setid(id);
    setDensity(density);
  }
  
  private void setName(String name) {
    this.name = name;
  }

  private void setid(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Double getDensity() {
    return density;
  }

  private void setDensity(Double density) {
    this.density = density;
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Ingredient i = (Ingredient) o;
    return Objects.equals(name, i.name)
      && Objects.equals(id, i.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, id);
  }
}
