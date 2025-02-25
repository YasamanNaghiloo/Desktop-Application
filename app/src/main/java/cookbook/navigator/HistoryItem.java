package cookbook.navigator;

import cookbook.controllers.RouteController;
import javafx.scene.Parent;

/**
 * Wrapper class for a route and its corresponding item.
 */
public class HistoryItem {
  private Parent item;
  private Route route;
  private RouteController controller;
  protected Runnable popCallback;

  /**
   * Constructor for the RouteWrapper class.
   * 
   * @param item  The item.
   * @param route The route.
   */
  public HistoryItem(Parent item, Route route, RouteController controller) {
    this.item = item;
    this.route = route;
    this.controller = controller;
  }

  /**
   * Get the item.
   * 
   * @return The item.
   */
  public Parent getItem() {
    return item;
  }

  /**
   * Get the route.
   * 
   * @return The route.
   */
  public Route getRoute() {
    return route;
  }

  /**
   * Get the controller.
   * 
   * @return The controller.
   */
  public RouteController getController() {
    return controller;
  }
}