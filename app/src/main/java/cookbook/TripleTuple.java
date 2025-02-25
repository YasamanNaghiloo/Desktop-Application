package cookbook;

/**
 * Tuple of three.
 */
public class TripleTuple<F, S, T> {
  public F first;
  public S second;
  public T third;

  /**
   * Create a triple tuple.
   *
   * @param first The first value
   * @param second The second value
   * @param third The thrid value
   */
  public TripleTuple(F first, S second, T third) {
    this.first = first;
    this.second = second;
    this.third = third;
  }

  public void setThird(T value) {
    third = value;
  }

  public void setSecond(S value) {
    second = value;
  }

  public void setFirst(F value) {
    first = value;
  }
}
