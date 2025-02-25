package cookbook;

import java.util.Objects;

/**
 * Represents a unit.
 */
public class Unit {

  private int id;
  private String name;

  public Unit(int id, String name) {
    setName(name);
    setid(id);
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
    Unit u = (Unit) o;
    return Objects.equals(name, u.name)
        && Objects.equals(id, u.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, id);
  }
}
