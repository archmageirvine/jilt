package irvine.crypto;

import java.util.Arrays;

/**
 * Contains current state during a cryptanalysis.
 * @author Sean A. Irvine
 */
class Node implements Comparable<Node> {

  private final char[] mPermutation;
  private final double mScore;

  Node(final char[] permutation, final double score) {
    mPermutation = Arrays.copyOf(permutation, permutation.length);
    mScore = score;
  }

  char[] getPermutation() {
    return mPermutation;
  }

  double getScore() {
    return mScore;
  }

  @Override
  public int compareTo(final Node c) {
    if (this == c) {
      return 0;
    }
    final int sc = Double.compare(mScore, c.mScore);
    if (sc != 0) {
      return sc;
    }

    // Usually the lengths will be the same, but in some cases (e.g. a dictionary
    // attack on Vigenere) we may simultaneously consider keys with different lengths.
    final int lengths = Integer.compare(mPermutation.length, c.mPermutation.length);
    if (lengths != 0) {
      return lengths;
    }
    for (int k = 0; k < mPermutation.length; ++k) {
      final int d = mPermutation[k] - c.mPermutation[k];
      if (d != 0) {
        return d;
      }
    }
    throw new RuntimeException();
  }

  @Override
  public boolean equals(final Object o) {
    return o instanceof Node && mScore == ((Node) o).mScore && Arrays.equals(mPermutation, ((Node) o).mPermutation);
  }

  @Override
  public int hashCode() {
    return (int) Double.doubleToRawLongBits(mScore);
  }
}
