package cookbook.searcher;

import cookbook.Unit;
import cookbook.db.UnitDao;

import java.util.ArrayList;

public class SearchInFile {

  /**
   * Convert to unit
   * 
   * @param ingredientName Ingredient name.
   * @param quantity       Ingredient quantity.
   * @param startUnit      Start unit.
   * @param endUnit        End unit.
   * 
   * @return Volume.
   */

  public double convertToUnit(String ingredientName, double quantity, Unit startUnit,
      Unit endUnit, Double density) {
    UnitDao unitDao = new UnitDao();
    ArrayList<Unit> volumeUnits = unitDao.getUnitsofType("volume");
    ArrayList<Unit> massUnits = unitDao.getUnitsofType("mass");
    double convertedQuantity;
    // Check if the units are the same
    if (startUnit.equals(endUnit)) {
      return quantity;
    }
    // Check if start unit is in volume or mass unit
    if (!volumeUnits.contains(startUnit) && !massUnits.contains(startUnit)) {
      throw new IllegalArgumentException("Could not convert, unit not possible to convert");
    }
    // Check if end unit is in volume or mass unit
    if (!volumeUnits.contains(endUnit) && !massUnits.contains(endUnit)) {
      throw new IllegalArgumentException("Could not convert, unit not possible to convert");
    }
    // Convert between volume units
    if (volumeUnits.contains(startUnit) && volumeUnits.contains(endUnit)) {
      VolumeConverter volumeConverter = new VolumeConverter();
      convertedQuantity = volumeConverter
          .convert(quantity, startUnit.getName(), endUnit.getName());
      return convertedQuantity;
      //Convert between mass units
    } else if (massUnits.contains(startUnit) && massUnits.contains(endUnit)) {
      MassConverter massConverter = new MassConverter();
      convertedQuantity = massConverter
          .convert(quantity, startUnit.getName(), endUnit.getName());
      return convertedQuantity;
    } else if (density == null || density == 0) {
      throw new IllegalArgumentException("Could not convert, no densitiy found");
      // Convert with density between mass and volume.
    } else {
      convertedQuantity = calculateVolume(density, quantity, startUnit.getName().toLowerCase(),
          endUnit.getName().toLowerCase());
      return convertedQuantity;
    }
  }

  private double calculateVolume(double density, double quantity, String startUnit, String endUnit) {
    // Convert quantity to grams or milliliters if necessary
    double quantityInGrams = 0;
    try {
      quantityInGrams = convertToGrams(quantity, startUnit);
    } catch (IllegalArgumentException e) {
      return -1;
    }

    // Convert grams or milliliters to the desired end unit
    double c = convertFromGramsOrMilliliters(quantityInGrams, endUnit, density);
    return c;
  }

  // Method to convert quantity to grams or milliliters
  private double convertToGrams(double quantity, String unit) {
    switch (unit.toLowerCase()) {
      case "kg":
        return quantity * 1000; // Convert kilograms to grams
      case "g":
        return quantity;
      case "gram":
        return quantity;
      case "mg":
        return quantity / 1000; // Convert milligrams to grams
      case "l":
        return quantity * 1000; // Convert liters to milliliters
      case "dl":
        return quantity * 100; // Convert deciliters to milliliters
      case "cl":
        return quantity * 10; // Convert centiliters to milliliters
      case "ml":
        return quantity;
      case "hg":
        return quantity * 100; // Convert hectograms to grams
      default:
        throw new IllegalArgumentException("Unknown unit: " + unit);
    }
  }

  // Method to convert grams or milliliters to the desired unit
  private static double convertFromGramsOrMilliliters(double quantity, String unit, double density) {
    switch (unit.toLowerCase()) {
      case "kg":
        return quantity * density / 1000; // Convert to kilograms
      case "g":
        return quantity * density;
      case "gram":
        return quantity * density;
      case "mg":
        return quantity * density * 1000; // Convert to milligrams
      case "l":
        return (quantity / density) / 1000; // Convert to liters
      case "dl":
        return (quantity / density) / 100; // Convert to deciliters
      case "cl":
        return (quantity / density) / 10; // Convert to centiliters
      case "ml":
        return quantity / density; // Convert to milliliters
      case "hg":
        return quantity * density / 100; // Convert to hectograms
      default:
        throw new IllegalArgumentException("Unknown unit: " + unit);
    }
  }
}
