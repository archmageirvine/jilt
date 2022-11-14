package irvine.crypto;

import java.util.HashSet;

import irvine.util.IntegerUtils;
import irvine.util.Sort;

/**
 * A Playfair key encoding scheme.
 * @author Sean A. Irvine
 */
final class ShrivastavaChouhanDhawan {

  // "A Modified Version of Extended Playfair Cipher (8x8)"
  // G. Shrivastava, M. Chouhan, M. Dhawan
  // Int. J. of Engineering and Computer Science, 2 (2013), pp. 956-961

  // Although originally described for 8x8 we generalize to other sizes.
  // Paper talks about "dictionary order" but does not properly specify the
  // ordering for symbols, here we use the order as given in the supplied
  // "alphabet".

  private ShrivastavaChouhanDhawan() { }

  static char[] buildKey(final String key, final String alphabet, final int width) {
    final HashSet<Character> seen = new HashSet<>();
    char[] grid = new char[alphabet.length()]; // To be interpreted as width x height later
    // Use characters appearing in key
    int j = 0;
    for (int k = 0; k < key.length(); ++k) {
      final char c = Character.toUpperCase(key.charAt(k));
      if (alphabet.indexOf(c) >= 0 && seen.add(c)) {
        grid[j++] = c;
      }
    }
    // Use remaining alphabet characters
    for (int k = 0; k < alphabet.length(); ++k) {
      final char c = alphabet.charAt(k);
      if (seen.add(c)) {
        grid[j++] = c;
      }
    }
    // Apply the rounds
    final int height = alphabet.length() / width;
    for (int row = 0; row < height; ++row) {
      final int[] order = IntegerUtils.identity(new int[width]);
      final long[] pos = new long[width];
      for (int k = 0; k < width; ++k) {
        pos[k] = alphabet.indexOf(grid[row * width + k]);
      }
      // Order the columns
      Sort.sort(pos, order);
      final char[] newGrid = new char[grid.length];
      for (int k = 0, i = 0; k < height; ++k) {
        for (int u = 0, c = order[k]; u < width; ++u, ++i, c += width) {
          // row k is column order[k]
          newGrid[i] = grid[c];
        }
      }
      grid = newGrid;
    }
    return grid;
  }
}
