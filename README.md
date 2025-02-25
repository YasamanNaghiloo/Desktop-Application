# Desktop-Application CookBook

## Licence
Any commercial use of this project will be met with a lawsuit.
## Potential issues
Your credentials.properties should look something like this:
url = jdbc:mysql://localhost/cookbook?user=yourUsername&password=yourPassword&useSSL=false

## Description
This is a cookbook that allows the user to:


- Add recipes
Add custom recipes which other users may se, for the recipes, you can enter the portion size, add a description, and instructions on how it should be made.

- Add density
Add density is made so you can add density for an ingredient, so that a recipe may be scaled better.

- Edit recipeso
If you are disatisfied with how a recipe looks, you can edit a recipe to include something it's missing, whether it's an ingredient or a specific part of the instructions. 

- View a weekly dinner list of recipes
You can view the weekly dinner list, in which you may add your own recipes if you wish. You can add a recipe to the weekly dinner list months in advance. If you double click a recipe, you get sent to the recipe view.

- Fridge
The fridge is made so you can view the ingredients you already have, this will be useful for when you want to add ingredients to your shoppinglist, and want to sync it with the existing ingredients you already have. If you change the unit of an ingredient, a unit conversion is possible, where it automatically converts for example 1kg of milk powder to 1000g of milk powder. If there are a lot of ingredients where the units can be converted to each other, the units will be converted to the same and the quantities will be combined, this applies for shoppinglist as well.

- a shopping list
The shoppinglist will create a PDF with ingredients you need for your recipes which you wish to make.

- a sync button
The sync button will sync your shoppinglist with your fridge, in other words, if you have ingredients in your fridge, let's say a liter of milk, and you add a cake into your recipe, which requires 2 liters of cake, the created shoppinglist will only list 1 liter of milk needed instead of two because your fridge already has a liter of milk.

- search for recipes
If you wish to search for a specific recipe, there's a filter button in which you may search for your desired recipe.

- share recipes to other users
If you wish to send a recipe to a user, there's a share recipe button so you can share your a recipe to a user in the cookbook.

- Administration
There's an administration portion, where you may add other admins.

- Tutorial
There's a tutorial section if there was any ambiguity with the explaination in the readme, where you may search for specific parts of the app as well.

## Installation

# Requirements:

- Clone the project to run
- windows or macos operating system
- the admin user credentials are : username == test, password == test123

## Guide

Clone the project contents into your desired folder, and then open the project through your desired IDE, once that is done, enter the cookbook database in resources directory and use the sql file of the schema and import it in your Mysql workbench. If you wish to load the images from the database, first go to server directory and read the README there and follow instructions. After installing the "go 1.22.2", in your terminal, type "cd server" and once you entered the server directory, type "go run ." into the terminal before you launch the application and click "Allow" to run "go". Open another terminal while keeping the last one open and type "./gradlew run" in the terminal for the application to start.   

After the program has opened, you will be presented with the log in screen, and after logging in, you can start using the app with test and test123 as admin's username and password respectively. Now you have a few options, if you click on "add recipe", you can add your desired recipe, complete with adding descriptions, portions, image adding and even instructions. 

Once your recipe is added, you will be sent back to the homescreen, where you can choose to view your own recipes that have been posted, all recipes, favourite recipes or the weekly dishlist. You can favourite a recipe by clicking on a recipe, and pressing the star icon. The weekly dishlist shows the recommended dinners for the week for example: Thursday lunch: crazy hamburger.

To add recipes to your weekly dinner list, you need to right click on the recipe's label( the pink label with the name of recipe) in homescreen and add it by choosing the date. Also if you hover on the label, you will see the short description of the recipe.

In the homescreen, there's a share recipe button in case you want to share your recipes to other users, you can do this by clicking "Share recipe" and then you may pick the user you want to share the recipe to, and then you can pick your desired recipe, alongside a comment of your choice. If someone sent you a message, it will appear in the "Show message" box.

In the fridge, you may add the ingredients you already have at home, so it acts as a virtual version of your existing fridge in real life, this is so for when you add ingredients to your shoppinglist, and you want the shoppinglist to create a PDF, you can sync the shoppinglist to your fridge, so it can reduce the needed ingredients if you already have ingredients at home.

Add density section is for scaling a recipe to specific portions, a user may add density to an ingredient but is not required to.

About the codes itself, We are using static methods in the Database Classes because they are Factory Methods, meaning they construct Objects given specific parameters like named Constructors. We need this because these Classes only represent a Database entry and are not meant to be used in any other way.

If there was any ambiguity, you may check the app tutorial, which is complete with slides, and you can search for specific portions of the slide if there is any ambiguity.

## Acknowledgement

This project was an assignemnt published by Linnaeus university.