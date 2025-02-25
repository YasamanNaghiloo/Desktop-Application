package cookbook.searcher;

public class MassConverter {

  public double convert(double value, String fromUnit, String toUnit) {
    // Convert from source unit to grams
    double valueInGrams = convertToGrams(value, fromUnit);
    // Convert from grams to target unit
    return convertFromGrams(valueInGrams, toUnit);
  }

  private static double convertToGrams(double value, String startUnit) {
    switch (startUnit.toLowerCase()) {
      case "g":
        return value;
      case "mg":
        return value / 1000.0;
      case "hg":
        return value * 100.0;
      case "kg":
        return value * 1000.0;
      default:
        throw new IllegalArgumentException("Unknown mass unit: " + startUnit);
    }
  }

  private static double convertFromGrams(double value, String endUnit) {
    switch (endUnit.toLowerCase()) {
      case "g":
        return value;
      case "mg":
        return value * 1000.0;
      case "hg":
        return value / 100.0;
      case "kg":
        return value / 1000.0;
      default:
        throw new IllegalArgumentException("Unknown mass unit: " + endUnit);
    }
  }
}
