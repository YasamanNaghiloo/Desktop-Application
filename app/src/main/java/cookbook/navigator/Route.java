package cookbook.navigator;

public enum Route {
    SPLASH("/myAnimation.fxml", "Splash"),
    LOGIN("/Login.fxml", "Login"),
    OLD_HOME("/OldHome.fxml", "Home"),
    HOME("/home/Home.fxml", "Home"),
    PROFILE("/home/Profile.fxml", "Profile"),
    ADMIN("/administration1.fxml", "Admin"),
    ADMIN_ADD_USER("/addUser.fxml", "Add User"),
    ADMIN_EDIT_USER("/modifyUser.fxml", "Edit User"),
    WEEKLY_DINNER("/weeklydinnerUpdate.fxml", "Weekly Dinner"),
    ADD_TO_WEEKLY_DINNER("/addWeeklyDinner.fxml", "Add to Weekly Dinner List"),
    RECIPE_VIEW("/home/RecipeView.fxml", "Recipe View"),
    FRIDGE("/Fridge.fxml", "Fridge"),
    SHOPPING_LIST("/shoppingListView.fxml", "Shopping List"),
    ADD_TO_FRIDGE("/AddToFridge.fxml", "Add to Fridge"),
    ADD_TO_SHOPPING_LIST("/AddToShoppingList.fxml", "Add to shopping list"),
    ADD_RECIPE("/AddRecipe.fxml", "Add Recipe"),
    SHARE_RECIPE("/SendRecipe.fxml", "Share Recipe"),
    SHOW_MESSAGES("/ShowMessages.fxml", "Messages Menu"),
    TUTORIAL("/TutorialView.fxml", "Tutorial"),
    //ADD_DENSITY("/addDensity.fxml", "Add Density"),
    HELP_FUNCTION("/SearchFunction.fxml", "Help Menu"),
    //TUTORIAL("/TutorialView.fxml", "Tutorial"),
    ADD_DENSITY("/addDensity.fxml", "Density"),
    //TUTORIAL("/TutorialView.fxml", "Tutorial"),
    SEARCH_FUNCTION("/SearchFunction.fxml", "Search Menu"); 

    public final String path;
    public final String title;

    Route(String path, String title) {
        this.path = path;
        this.title = title;
    }
}
