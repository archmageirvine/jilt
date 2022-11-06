package irvine.transform;

/**
 * Defines a word transform.
 * @author Sean A. Irvine
 */
public interface Transform {

  /**
   * Transform a word.
   * @param word word to transform
   * @return transformed word
   */
  String apply(final String word);

  /**
   * Return a short name of this transform.
   * @return name
   */
  String getName();
}
