package cookbook.controllers;

import cookbook.App;
import cookbook.navigator.Navigator;

/**
 * Wrapper class for dependency injection
 */
public abstract class RouteController {
  private Navigator navigator;

  public Navigator getNavigator() {
    if (navigator == null) {
      throw new IllegalStateException(
          "Navigator has not been injected into the controller" + this.getClass().getName());
    }
    return navigator;
  }

  /**
   * Inject the navigator into the controller
   * 
   * @param navigator The navigator to inject
   */
  public void injectNavigator(Navigator navigator) {
    this.navigator = navigator;
  }

  public void showError(Exception e) {
    getNavigator().showDialog("Error", e.getMessage());
    if (App.DEBUG) {
      e.printStackTrace();
    }
  }

  /**
   * Called when the route is loaded
   */
  public void onRouteLoaded() {}
}
