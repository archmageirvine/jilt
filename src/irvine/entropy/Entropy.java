package irvine.entropy;

/**
 * Defines a method for computing entropy.
 * @author Sean A. Irvine
 */
public interface Entropy {

  /**
   * Return an estimate of the entropy of the given string.  The implementation
   * is free to use whatever tactic it likes to makes this estimate.
   *
   * @param text text to compute entropy of
   * @return entropy
   */
  double entropy(String text);

}
