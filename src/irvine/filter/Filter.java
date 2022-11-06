package irvine.filter;

/**
 * Defines a word filter.
 * @author Sean A. Irvine
 */
public interface Filter {

  /**
   * Test if the given word is accepted by this filter.
   * @param word word to test
   * @return true iff the word is accepted
   */
  boolean is(final String word);
}
