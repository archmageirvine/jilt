package irvine.wordsmith;

/**
 * Defines a method for trying to explain a word sequence.
 * @author Sean A. Irvine
 */
public interface Inspector {

  /**
   * Attempt to generate an explanation for a given sequence of words.
   * @param words words to be explained
   * @return an explanation or null
   */
  String inspect(final String... words);
}
