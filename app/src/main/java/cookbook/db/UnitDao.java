package cookbook.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import cookbook.Unit;

/**
 * Data Access object for unit.
 */
public class UnitDao {
  public ArrayList<Unit> getUnitsofType(String type) {
    ArrayList<Unit> units = new ArrayList<>();
    try {
      String query = "SELECT * from unit where type = ?";
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          query);
      statement.setString(1, type);
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        units.add(new Unit(resultSet.getInt("unitId"), resultSet.getString("unitName")));
      }

    } catch (SQLException e) {
      System.err.println(e);
    }
    return units;
  }

  // TODO:MOVE TO MORE RELEVANT CLASS static??
  public ArrayList<Unit> getUnits() {
    ArrayList<Unit> units = new ArrayList<>();

    try {
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          "SELECT unitName, unitId from unit");
      ResultSet res = statement.executeQuery();

      while (res.next()) {
        Unit unit = new Unit(res.getInt("unitId"), res.getString("unitName"));
        units.add(unit);
      }

    } catch (SQLException e) {
      System.err.println(e);
    }
    return units;
  }

  // TODO: create classes for ingredient and units
  public Integer getUnitId(String unitName) {
    int id = -1;
    String query = "SELECT unitId from unit where unitName = ?";
    try {
      PreparedStatement statement = DB.getInstance().getConnection().prepareStatement(
          query);
      statement.setString(1, unitName);
      ResultSet res = statement.executeQuery();

      while (res.next()) {
        id = res.getInt("unitId");
      }

    } catch (SQLException e) {
      System.err.println(e);
    }
    if (id == -1) {
      return null;
    }
    return id;
  }
}
