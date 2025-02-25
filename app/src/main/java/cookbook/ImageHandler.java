package cookbook;

import cookbook.auth.Auth;
import cookbook.db.DB;
import cookbook.db.User;
import cookbook.server.Server;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.InputStream;

/** Is the class in order to manange each Image.
 * 
 */
public class ImageHandler {

  User user;
  private boolean rowsAffected;
  private Image image;
  private ImageView imageVieww;
  private String filep;
  private int userId;
  private CustomAlertMessage ee;


  public ImageHandler() {
    this.userId = Auth.currentUser() != null ? Auth.currentUser().getId() : -1;
    this.ee = new CustomAlertMessage(); // Initialize ee
  }

  /** Is a method inorder to add an image.

   * @param table is the intended image table.
   * @param imagePath is the path of the image.
   * @throws SQLException is realted to SQL.
   * @throws IOException is related to file manager.
   */
  public void addImage(String table, String imagePath, String name,
      int recipeId, int userIdd) throws SQLException, IOException {
    File imageFile = new File(imagePath);
    System.out.println("Image path: " + imagePath);
    try {
      String upLoadedFilePath = Server.uploadFile(imageFile);
      String query = "INSERT INTO cookbook." + table + " (imageName, imagePath, recipeId, userId) ";
      query += " VALUES (?, ?, ?, ?)";
      try (PreparedStatement st = DB.getInstance().getConnection().prepareStatement(query)) {
        DB.getInstance().getConnection().setAutoCommit(false);
        st.setString(1, name);
        st.setString(2, upLoadedFilePath);
        st.setInt(3, recipeId);
        st.setInt(4, userIdd);
        int row = st.executeUpdate();
        this.rowsAffected = (row > 0) ? true : false;
        DB.getInstance().getConnection().commit();
      } catch (SQLException ee) {
        System.err.println(ee);
      }
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  /** Is the method in order to show the image.

   * @param table is the intended image table.
   * @param imageId is the given parameter for diplaying.
   * @return returns an Image.
   * @throws SQLException is the exception.
   */
  public Image displayImage(String table, int imageId) throws SQLException {
    String query = "SELECT imagePath FROM cookbook." + table + " WHERE imageId = ?";
    Image image = null;
    try (PreparedStatement ps = DB.getInstance().getConnection().prepareStatement(query)) {
      ps.setInt(1, imageId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          String halfUrl = rs.getString(1);
          String path = "http://localhost:4000" + halfUrl;
          try {
            URI uri = new URI(path);
            URL url = uri.toURL();
            try (InputStream inputStream = url.openStream()) {
              image = new Image(inputStream);
            } catch (IOException e) {
              System.err.println("Error loading image from URL: " + e.getMessage());
            }
          } catch (MalformedURLException | URISyntaxException e) {
            System.err.println("Error creating URL from path: " + e.getMessage());
          }
        } else {
          throw new SQLException("Couldn't retrieve the Image !!!");
        }
      }
    } catch (SQLException se) {
      System.err.println(se);
      throw se; // Rethrow the exception for the caller to handle
    }
    System.out.println("Image was retrieved");
    return image;
  }

  /** Is the method in order to delet an Image.

   * @param imageId is the imageId.
   */
  public void deletImage(int imageId, String table) {
    String q = "DELETE FROM cookbook." + table + " WHERE imageId = ?";
    try (PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement(q)) {
      stmt.setInt(1, imageId);
      int row = stmt.executeUpdate();
      this.rowsAffected = (row > 0) ? true : false;

    } catch (SQLException e) {
      System.err.println(e);
    }
  }
                        // !!!! WARNING !!!! //
  /* It is here just for test
  @Override
  public void start(Stage primaryStage) {
    // Create a rectangle
    ImageView imageView = new ImageView(); // Initialize imageView here

    Rectangle rectangle = new Rectangle(500, 100);
    rectangle.setFill(Color.TRANSPARENT);
    rectangle.setStroke(Color.BLACK);
    Button button = new Button("Display the image.");
    button.setPrefSize(80, 80);
    button.setOnAction(event -> {
      try {
        Image image = displayImage(2); // Assuming imageId is 1
        imageView.setImage(image);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    // Enable drag-and-drop for the rectangle
    rectangle.setOnDragOver(event -> {
      if (event.getGestureSource() != rectangle && event.getDragboard().hasFiles()) {
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
      }
      event.consume();
    });

    rectangle.setOnDragDropped(event -> {
      Dragboard db = event.getDragboard();
      boolean success = false;
      if (db.hasFiles()) {
        success = true;
        // Handle the dropped files here
        for (File file : db.getFiles()) {
          // Load the image and display it in an ImageView
          Image image = new Image(file.toURI().toString());
          imageVieww = new ImageView(image);
          StackPane.setAlignment(imageView, javafx.geometry.Pos.CENTER);
          ((StackPane) rectangle.getParent()).getChildren().add(imageView);
          String filePath = file.getAbsolutePath();
          this.filep = filePath;
          try {
            addImage(filePath);
            System.out.println("I was added successfuly");
          } catch (SQLException | IOException e) {
            System.out.println("I was not added succkesfully");
            e.printStackTrace();
          }
        }
      }
      event.setDropCompleted(success);
      event.consume();
    });

    // Create a stack pane and add the rectangle to it
    StackPane stackPane = new StackPane();
    // stackPane.getChildren().add(rectangle);
    stackPane.getChildren().addAll(button, imageView);

    // Create a scene and set the stack pane as its root
    Scene scene = new Scene(stackPane, 600, 800);

    // Set the scene to the stage and show the stage
    primaryStage.setScene(scene);
    primaryStage.setTitle("JavaFX Application");
    primaryStage.show();
  }
  */
}


