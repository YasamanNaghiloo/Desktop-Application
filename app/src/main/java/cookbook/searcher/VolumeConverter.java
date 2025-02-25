package cookbook.searcher;

public class VolumeConverter {

  public double convert(double value, String fromUnit, String toUnit) {
    // Convert from source unit to liters
    double valueInLiters = convertToLiters(value, fromUnit);
    // Convert from liters to target unit
    return convertFromLiters(valueInLiters, toUnit);
  }

  private static double convertToLiters(double value, String startUnit) {
    switch (startUnit.toLowerCase()) {
      case "l":
        return value;
      case "ml":
        return value / 1000.0;
      case "dl":
        return value / 10.0;
      case "cl":
        return value / 100.0;
      default:
        throw new IllegalArgumentException("Unknown volume unit: " + startUnit);
    }
  }

  private static double convertFromLiters(double value, String endUnit) {
    switch (endUnit.toLowerCase()) {
      case "l":
        return value;
      case "ml":
        return value * 1000.0;
      case "dl":
        return value * 10.0;
      case "cl":
        return value * 100.0;
      default:
        throw new IllegalArgumentException("Unknown volume unit: " + endUnit);
    }
  }
}
