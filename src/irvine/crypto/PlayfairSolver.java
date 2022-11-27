package irvine.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.TreeSet;

import irvine.entropy.Entropy;
import irvine.util.DoubleUtils;
import irvine.util.IOUtils;
import irvine.util.Shuffle;

/**
 * Automated cryptanalysis of Playfair.  Generates and initially random set of keys and
 * iteratively improves them according to the entropy of the putative decryption with
 * respect to the supplied model.  At each iteration the best hypothesis are retained to
 * form the basis of the next iteration.
 * @author Sean A. Irvine
 */
public class PlayfairSolver {

  private final PrintStream mOut;
  private final Entropy mModel;
  private final int mMaximumHypothesesCount;
  private final char[] mAlphabet;
  private final int mGridWidth;
  private final int mGridHeight;
  // Precomputed permutation position to (x,y) coordinates
  private final int[][] mPosToCoords;
  // Precomputed cyclic rotation
  private final int[] mPrevCoordX;
  private final int[] mPrevCoordY;

  // The ciphertext broken into pairs
  private char[][] mCipherPairs = null;
  // The largest entropy in the set under construction.  Maintained separately to avoid
  // repetitively requesting the last entropy of the set.
  private double mLastEntropy = Double.POSITIVE_INFINITY;

  // Keep track of keys that have been rejected from the search.  This prevents
  // the same local maximum being encountered multiple times.
  private final HashSet<Key> mRejected = new HashSet<>();
  private final Random mRandom = new Random();

  /**
   * Representation of a Playfair key.  The actual permutation is stored in <code>mPermutation</code>,
   * the entropy (when set) is the score of this key with respect to the ciphertext.  The
   * <code>mSurvivalCount</code> increments on each iteration that this key is retained in the set
   * of hypotheses, and the hash code depends only on the permutation is computed only once and
   * is used during the hash set of rejected keys.
   */
  final class Key implements Comparable<Key> {
    double mEntropy = Double.NaN;
    int mSurivivalCount = 0;
    private int mHashCode = 0;

    final char[] mPermutation;

    private Key(final char[] permutation) {
      mPermutation = permutation;
    }

    private Key() {
      this(new char[mAlphabet.length]);
    }

    private Key copy() {
      return new Key(Arrays.copyOf(mPermutation, mPermutation.length));
    }

    private int getPosition(final char c) {
      for (int k = 0; k < mPermutation.length; ++k) {
        if (c == mPermutation[k]) {
          return k;
        }
      }
      return -1;
    }

    private char[] decode(final int posA, final int posB) {
      assert posA != posB;
      final int xa = mPosToCoords[posA][0];
      final int ya = mPosToCoords[posA][1];
      final int xb = mPosToCoords[posB][0];
      final int yb = mPosToCoords[posB][1];
      final char[] res = new char[2];
      if (ya == yb) {
        // same row
        final int d = mGridWidth * ya;
        res[0] = mPermutation[d + mPrevCoordX[xa]];
        res[1] = mPermutation[d + mPrevCoordX[xb]];
      } else if (xa == xb) {
        // same column
        res[0] = mPermutation[xa + mPrevCoordY[ya]];
        res[1] = mPermutation[xb + mPrevCoordY[yb]];
      } else {
        // rectangle
        res[0] = mPermutation[xb + mGridWidth * ya];
        res[1] = mPermutation[xa + mGridWidth * yb];
      }
      return res;
    }

    @Override
    public int compareTo(final Key o) {
      final int c = Double.compare(mEntropy, o.mEntropy);
      if (c != 0) {
        return c;
      }
      for (int k = 0; k < mPermutation.length; ++k) {
        final int d = Character.compare(mPermutation[k], o.mPermutation[k]);
        if (d != 0) {
          return d;
        }
      }
      return 0;
    }

    @Override
    public boolean equals(final Object obj) {
      return obj instanceof Key && Arrays.equals(mPermutation, ((Key) obj).mPermutation);
    }

    @Override
    public int hashCode() {
      if (mHashCode == 0) {
        // Occasionally the hashcode itself will be zero, small price to pay
        // for recomputing in those cases.
        mHashCode = Arrays.hashCode(mPermutation);
      }
      return mHashCode;
    }
  }

  /**
   * Construct a new bigram cracker with the given model.
   * @param out output stream
   * @param model the model
   * @param alphabet alphabet to use
   * @param maximumHypothesesCount maximum hypotheses to retain at each level
   * @param width grid width
   * @param height grid height
   */
  public PlayfairSolver(final PrintStream out, final Entropy model, final char[] alphabet, final int maximumHypothesesCount, final int width, final int height) {
    mOut = out;
    mModel = model;
    mAlphabet = Arrays.copyOf(alphabet, alphabet.length);
    mMaximumHypothesesCount = maximumHypothesesCount;
    mGridWidth = width;
    mGridHeight = height;
    mPosToCoords = new int[mGridWidth * height][2];

    // Precompute position to coordinates, avoid expensive mod operation during search
    for (int k = 0; k < mPosToCoords.length; ++k) {
      mPosToCoords[k][0] = k % mGridWidth;
      mPosToCoords[k][1] = k / mGridWidth;
    }

    // Precompute previous position for each coordinate (again to avoid mod)
    mPrevCoordX = new int[mGridWidth];
    for (int k = 0; k < mPrevCoordX.length; ++k) {
      mPrevCoordX[k] = (mGridWidth + k - 1) % mGridWidth;
    }
    mPrevCoordY = new int[mGridHeight];
    for (int k = 0; k < mPrevCoordY.length; ++k) {
      mPrevCoordY[k] = mGridWidth * ((mGridHeight + k - 1) % mGridHeight);
    }
  }

  /**
   * Set the seed of the random number generator.
   * @param seed seed value
   */
  public void setSeed(final long seed) {
    mRandom.setSeed(seed);
  }

  // Precompute all the ciphertext pairs to avoid excessive string operations later
  private char[][] initCipherPairs(final String ciphertext) {
    assert (ciphertext.length() & 1) == 0; // even length
    final char[][] pairs = new char[ciphertext.length() / 2][2];
    for (int k = 0, j = 0; k < ciphertext.length(); k += 2, ++j) {
      pairs[j][0] = ciphertext.charAt(k);
      pairs[j][1] = ciphertext.charAt(k + 1);
    }
    return pairs;
  }

  void swapCols(final char[] key, final int a, final int b) {
    for (int y = a, s = b; y < key.length; y += mGridWidth, s += mGridWidth) {
      final char t = key[y];
      key[y] = key[s];
      key[s] = t;
    }
  }

  void swapRows(final char[] key, final int a, final int b) {
    for (int x = mGridWidth * a, s = mGridWidth * b, q = 0; q < mGridWidth; ++q, ++x, ++s) {
      final char t = key[x];
      key[x] = key[s];
      key[s] = t;
    }
  }

  static char[] reverse(final char[] key) {
    final char[] res = new char[key.length];
    for (int k = 0; k < key.length; ++k) {
      res[k] = key[key.length - 1 - k];
    }
    return res;
  }

  static char[] reflectVertical(final char[] key, final int width, final int height) {
    final char[] res = new char[key.length];
    for (int k = 0; k < height; ++k) {
      System.arraycopy(key, (height - 1 - k) * width, res, k * width, width);
    }
    return res;
  }

  static char[] reflectHorizontal(final char[] key, final int width, final int height) {
    final char[] res = new char[key.length];
    for (int k = 0; k < width; ++k) {
      for (int j = 0; j < height; ++j) {
        res[j * width + k] = key[j * width + (width - 1 - k)]; // swap cols left-right
      }
    }
    return res;
  }

  String decode(final Key key) {
    final StringBuilder out = new StringBuilder();
    for (final char[] pair : mCipherPairs) {
      final int posA = key.getPosition(pair[0]);
      final int posB = key.getPosition(pair[1]);
      out.append(key.decode(posA, posB));
    }
    return out.toString();
  }

  private String printForm(final String s) {
    final StringBuilder sb = new StringBuilder();
    for (int k = 0; k < s.length(); ++k) {
      final char c = s.charAt(k);
      sb.append(c == 0 ? '.' : c);
    }
    return sb.toString();
  }

  private int getMaximumHypothesesCount() {
    return mMaximumHypothesesCount;
  }

  private void update(final TreeSet<Key> result, final Key key) {
    // Last entropy is the entropy of the lowest scoring element in the result set.
    // It is maintained as a value to avoid having to look it up lots of times.
    if (!mRejected.contains(key)) {
      if (result.size() < getMaximumHypothesesCount()) {
        result.add(key);
        mLastEntropy = Math.max(mLastEntropy, key.mEntropy);
      } else if (mLastEntropy > key.mEntropy) {
        result.pollLast(); // removes the last element
        result.add(key);
        mLastEntropy = result.last().mEntropy;
      }
    }
  }

  private void doAllRowSwaps(final TreeSet<Key> next, final Key key) {
    for (int row0 = 0; row0 < mGridWidth; ++row0) {
      for (int row1 = row0 + 1; row1 < mGridWidth; ++row1) {
        final Key newKey = new Key();
        System.arraycopy(key.mPermutation, 0, newKey.mPermutation, 0, mAlphabet.length);
        swapRows(newKey.mPermutation, row0, row1);
        newKey.mEntropy = score(decode(newKey));
        newKey.mSurivivalCount = 0;
        update(next, newKey);
      }
    }
  }

  private void doAllColSwaps(final TreeSet<Key> next, final Key key) {
    for (int col0 = 0; col0 < mGridWidth; ++col0) {
      for (int col1 = col0 + 1; col1 < mGridWidth; ++col1) {
        final Key newKey = key.copy();
        swapCols(newKey.mPermutation, col0, col1);
        newKey.mEntropy = score(decode(newKey));
        newKey.mSurivivalCount = 0;
        update(next, newKey);
      }
    }
  }

  private void doReverse(final TreeSet<Key> next, final Key key) {
    final Key newKey = new Key(reverse(key.mPermutation));
    newKey.mEntropy = score(decode(newKey));
    newKey.mSurivivalCount = 0;
    update(next, newKey);
  }

  private void doReflectVertical(final TreeSet<Key> next, final Key key) {
    final Key newKey = new Key(reflectVertical(key.mPermutation, mGridWidth, mGridHeight));
    newKey.mEntropy = score(decode(newKey));
    newKey.mSurivivalCount = 0;
    update(next, newKey);
  }

  private void doReflectHorizontal(final TreeSet<Key> next, final Key key) {
    final Key newKey = new Key(reflectHorizontal(key.mPermutation, mGridWidth, mGridHeight));
    newKey.mEntropy = score(decode(newKey));
    newKey.mSurivivalCount = 0;
    update(next, newKey);
  }

  void percolate(final String ciphertext, final int iterations, final String dictionary) {
    mCipherPairs = initCipherPairs(ciphertext);
    TreeSet<Key> currentKeys = initial(dictionary);
    printBestSolutions(currentKeys, 5);
    for (int i = 1; i <= iterations; ++i) {
      mOut.println("Doing iteration: " + i + "/" + iterations);
      final TreeSet<Key> next = new TreeSet<>();
      mLastEntropy = Double.POSITIVE_INFINITY;
      int soln = 0;
      for (final Key key : currentKeys) {
        ++soln;
        if (++key.mSurivivalCount <= 10 || soln <= 1) {
          update(next, key); // Retain current solution as an option
          if (key.mSurivivalCount == 1) {
            // This is the first time we have considered this entry for expansion.
            // Try a selection of systematic key modifications.
            doAllPairSwaps(next, key);
            doAllRowSwaps(next, key);
            doAllColSwaps(next, key);
            doReverse(next, key);
            doReflectVertical(next, key);
            doReflectHorizontal(next, key);
          } else {
            // Try swapping random pairs
            for (int b = 0; b < 100; ++b) {
              final Key newKey = key.copy();
              for (int c = 0; c < Math.min(key.mSurivivalCount, 10); ++c) { // swap depending on survival time
                final int q = mRandom.nextInt(mAlphabet.length);
                final int r = mRandom.nextInt(mAlphabet.length);
                final char t = newKey.mPermutation[q];
                newKey.mPermutation[q] = newKey.mPermutation[r];
                newKey.mPermutation[r] = t;
              }
              newKey.mEntropy = score(decode(newKey));
              newKey.mSurivivalCount = 0;
              update(next, newKey);
            }
          }
        } else {
          mRejected.add(key);
        }
      }
      currentKeys = next;
      printBestSolutions(currentKeys, 5);
    }
  }

  private void doAllPairSwaps(final TreeSet<Key> next, final Key key) {
    // Profiling indicates this is where all the time goes ...

    // Performs a swap of each possible pair of values in the permutation, evaluating
    // the decrypt of each new permutation with respect to the model.

    // For speed we temporarily modify the existing key and only make a copy if we
    // discover this is a solution we want to keep.  In this way we avoid excessive
    // object churn, but need to be very careful to reset the state on the original.
    // In particular, we must be very careful to reset the original key state before
    // updating a tree set that might already contain that key.
    for (int a = 0; a < mAlphabet.length; ++a) {
      final char ca = key.mPermutation[a];
      for (int b = a + 1; b < mAlphabet.length; ++b) {
        final char cb = key.mPermutation[b];
        key.mPermutation[a] = cb;
        key.mPermutation[b] = ca;
        final double score = score(decode(key));
        key.mPermutation[b] = cb; // reset to original value
        if (score < mLastEntropy) {
          // This looks like something we want to retain, duplicate the key
          final Key newKey = key.copy();
          newKey.mPermutation[b] = ca;
          newKey.mEntropy = score;
          key.mPermutation[a] = ca; // reset to original value
          update(next, newKey);
        }
      }
      key.mPermutation[a] = ca; // reset to original value
    }
  }

  private void printBestSolutions(final TreeSet<Key> currentKeys, final int max) {
    int j = 0;
    for (final Key k : currentKeys) {
      mOut.println(DoubleUtils.NF3.format(k.mEntropy) + " (" + k.mSurivivalCount + ") " + Arrays.toString(k.mPermutation).replace(", ", "") + " " + printForm(decode(k)));
      if (++j >= max) {
        break;
      }
    }
  }

  private static boolean contains(final char[] alphabet, final char c) {
    for (final char a : alphabet) {
      if (c == a) {
        return true;
      }
    }
    return false;
  }

  static String clean(final char[] alphabet, final String s) {
    final StringBuilder sb = new StringBuilder();
    char prev = 0;
    for (int k = 0; k < s.length(); ++k) {
      final char c = Character.toUpperCase(s.charAt(k));
      if (contains(alphabet, c)) {
        if (c == prev && (sb.length() & 1) == 1) { // a pair is allowed if they are in different doublets
          throw new IllegalArgumentException("Cannot possibly be Playfair since " + c + " is repeated in ciphertext");
        }
        prev = c;
        sb.append(c);
      }
    }
    return sb.toString();
  }

  private double score(final String text) {
    if (text.isEmpty()) {
      return Double.POSITIVE_INFINITY;
    }
    return mModel.entropy(text);
  }

  private Key buildKey(final String phrase) {
    // This could be made more efficient
    final Key key = new Key();
    final boolean[] used = new boolean[mAlphabet.length];
    int i = 0;
    for (int k = 0; k < phrase.length(); ++k) {
      final char c = phrase.charAt(k);
      for (int j = 0; j < mAlphabet.length; ++j) {
        if (c == mAlphabet[j]) {
          if (!used[j]) {
            used[j] = true;
            key.mPermutation[i++] = c;
          }
        }
      }
    }
    for (int k = 0; k < used.length; ++k) {
      if (!used[k]) {
        key.mPermutation[i++] = mAlphabet[k];
      }
    }
    assert i == mAlphabet.length;
    return key;
  }

  private TreeSet<Key> initial(final String dictionary) {
    final Key defaultKey = getKey(); // Default A-Z style key
    defaultKey.mEntropy = score(decode(defaultKey));
    final TreeSet<Key> res = new TreeSet<>();
    res.add(defaultKey);

    for (int j = 0; j < mMaximumHypothesesCount; ++j) {
      final Key key = getRandomKey();
      key.mEntropy = score(decode(key));
      update(res, key);
    }

    if (dictionary != null) {
      // Treat each line of the dictionary as a key to be tried.
      try (final BufferedReader r = IOUtils.getReader(dictionary)) {
        String keyPhrase;
        while ((keyPhrase = r.readLine()) != null) {
          if (!keyPhrase.isEmpty()) {
            // Strictly speaking the following uppercase should be depend on alphabet, but
            // all our current alphabets are uppercase anyway
            final Key key = buildKey(keyPhrase.toUpperCase(Locale.getDefault()));
            key.mEntropy = score(decode(key));
            update(res, key);
          }
        }
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }

    return res;
  }

  private Key getKey() {
    final Key key = new Key();
    int k = 0;
    for (final char a : mAlphabet) {
      key.mPermutation[k++] = a;
    }
    return key;
  }

  private Key getRandomKey() {
    final Key key = getKey();
    Shuffle.shuffle(key.mPermutation, mRandom);
    return key;
  }

}
