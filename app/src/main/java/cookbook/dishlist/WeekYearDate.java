package cookbook.dishlist;

import java.util.Objects;

public class WeekYearDate {

  private int year;
  private int week;

  public WeekYearDate(int year, int week) {
    setWeek(week);
    setYear(year);
  }

  private void setYear(int year) {
    this.year = year;
  }

  private void setWeek(int week) {
    this.week = week;
  }

  public int getWeek() {
    return week;
  }

  public int getYear() {
    return year;
  }

  public String getDate() {
    return getYear() + ":" + getWeek();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    WeekYearDate other = (WeekYearDate) obj;
    return year == other.year && week == other.week;
  }

  @Override
    public int hashCode() {
    return Objects.hash(year, week);
  }

    
}
