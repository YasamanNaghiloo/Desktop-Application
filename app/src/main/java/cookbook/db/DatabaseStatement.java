package cookbook.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseStatement {

	public void insert(String table, String[] columns, String[] values, boolean ignoreOnDuplicate) {
	  Connection conn = DB.getInstance().getConnection();
    try {
      String method = "";
      if (ignoreOnDuplicate) {
        method = "INSERT IGNORE";
      } else {
        method = "INSERT";
      }
      // Create a Statement
      PreparedStatement statement = conn.prepareStatement(createQuery(method, table, columns, values));
      System.out.println(statement);
      int result = statement.executeUpdate();
      // Execute the INSERT statement
      //int rows = statement.executeUpdate(createQuery(method, table, columns, values));
      System.out.println("Insert statement completed: " + result);
    } catch (SQLException e) {
      System.err.println("Error on insert:" + e);
    }
    
	}

  public ResultSet selectInt(String query) {
	  Connection conn = DB.getInstance().getConnection();
    ResultSet resultSet;
    try {
      System.out.println("Connected to the database");

      // Create a statement
      Statement statement = conn.createStatement();

      // Define your SQL query
      String sqlQuery = query;

      // Execute the query
      resultSet = statement.executeQuery(sqlQuery);
    } catch (SQLException e) {
      resultSet = null;
    }
      return resultSet;
    
	}

  private String createQuery(String type, String table, String[] columns, String[] values) {
    StringBuilder queryBuilder = new StringBuilder();

    // Building the columns part of the query
    queryBuilder.append(type)
                .append(" INTO ")
                .append(table)
                .append(" (")
                .append(String.join(", ", columns))
                .append(") VALUES (");
      
    for (int i = 0; i < values.length; i++) {
      queryBuilder.append("'");
      queryBuilder.append(values[i]);
      queryBuilder.append("'");
      if (i < values.length - 1) {
        queryBuilder.append(", ");
      }
    }
    
    
    queryBuilder.append(")");

    return queryBuilder.toString();
  }
}
