package irvine.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeSet;

import irvine.entropy.Entropy;
import irvine.entropy.FourGramAlphabetModel;
import irvine.util.CliFlags;
import irvine.util.DoubleUtils;
import irvine.util.IntegerUtils;
import irvine.util.Shuffle;

/**
 * Try to solve numerical homophonic ciphers.
 * @author Sean A. Irvine
 */
public class CrackHomophonic {

  private static final String MODEL_FLAG = "model";
  private static final String RETAIN_FLAG = "retain";
  private static final String SEED_FLAG = "seed";
  private static final String DISTRIBUTION_FLAG = "distribution";

  static final String DEFAULT_DISTRIBUTION = "AAAAAAAABBCCCDDDDEEEEEEEEEEEEFFGGHHHHHHIIIIIIJKLLLLMMNNNNNNOOOOOOOPPQRRRRRRSSSSSSTTTTTTTTTUUUVWWXYYZ";

  private static final class Key implements Comparable<Key> {

    private final double mEntropy;
    private final char[] mMap;
    private int mSurvivorCount;

    private Key(final double entropy, final char[] map, final int survivorCount) {
      mEntropy = entropy;
      mMap = map;
      mSurvivorCount = survivorCount;
    }

    @Override
    public int compareTo(final Key other) {
      final int c = Double.compare(mEntropy, other.mEntropy);
      if (c != 0) {
        return c;
      }
      for (int k = 0; k < mMap.length; ++k) {
        final int d = Character.compare(mMap[k], other.mMap[k]);
        if (d != 0) {
          return d;
        }
      }
      return 0;
    }

    @Override
    public boolean equals(final Object obj) {
      if (!(obj instanceof Key)) {
        return false;
      }
      final Key other = (Key) obj;
      return mEntropy == other.mEntropy && Arrays.equals(mMap, other.mMap);
    }

    @Override
    public int hashCode() {
      return Double.hashCode(mEntropy) ^ Arrays.hashCode(mMap);
    }
  }

  static char[] decrypt(final char[] map, final int[] cipher) {
    final char[] decrypt = new char[cipher.length];
    for (int k = 0; k < cipher.length; ++k) {
      decrypt[k] = map[cipher[k]];
    }
    return decrypt;
  }

  private final Entropy mModel;
  private final String mDistribution;
  private final int mRetain;
  // The largest entropy in the set under construction.  Maintained separately to avoid
  // repetitively requesting the last entropy of the set.
  private double mLastEntropy = Double.POSITIVE_INFINITY;
  private final Random mRandom = new Random();

  CrackHomophonic(final Entropy model, final String distribution, final int retain) {
    mModel = model;
    mDistribution = distribution;
    mRetain = retain;
  }

  /**
   * Set the seed of the random number generator.
   * @param seed seed value
   */
  public void setSeed(final long seed) {
    mRandom.setSeed(seed);
  }

  private int getMaximumHypothesesCount() {
    return mRetain;
  }

  private void printBestSolutions(final TreeSet<Key> currentKeys, final int max, final int[] cipher) {
    int j = 0;
    for (final Key k : currentKeys) {
      System.out.println(DoubleUtils.NF4.format(k.mEntropy) + " " + k.mSurvivorCount + " " + new String(decrypt(k.mMap, cipher)).replace('\0', '.'));
      if (++j >= max) {
        break;
      }
    }
  }

  private void update(final TreeSet<Key> result, final Key key) {
    // Last entropy is the entropy of the lowest scoring element in the result set.
    // It is maintained as a value to avoid having to look it up lots of times.
    if (result.size() < getMaximumHypothesesCount()) {
      result.add(key);
      mLastEntropy = Math.max(mLastEntropy, key.mEntropy);
    } else if (mLastEntropy > key.mEntropy) {
      result.pollLast(); // removes the last element
      result.add(key);
      mLastEntropy = result.last().mEntropy;
    }
  }

  private void swapRandomPair(final char[] map) {
    final int i = mRandom.nextInt(map.length);
    final int j = mRandom.nextInt(map.length);
    final char t = map[i];
    map[i] = map[j];
    map[j] = t;
  }

  private void swapRandomPairs(final char[] map, final int count) {
    for (int k = 0; k < count; ++k) {
      swapRandomPair(map);
    }
  }

  private TreeSet<Key> percolateExchange(final TreeSet<Key> current, final int[] cipher, final int j) {
    final TreeSet<Key> res = new TreeSet<>();
    for (final Key key : current) {
      // replace each occurrence of c in cipher for each character of alphabet in turn.
      // already known to occur at least once
      final int cnt = key.mSurvivorCount++ / 100; // scales number of random swaps to do
      update(res, key); // todo think on this, should current soln be retained as a possibility
      for (int k = j + 1; k < 100; ++k) { // todo think on this, perhaps should start at 0
        if (key.mMap[j] != key.mMap[k]) {
          // swap symbols
          final char[] m = Arrays.copyOf(key.mMap, key.mMap.length);
          m[j] = key.mMap[k];
          m[k] = key.mMap[j];
          swapRandomPairs(m, cnt);
          final char[] d = decrypt(m, cipher);
          final double e = mModel.entropy(new String(d));
          update(res, new Key(e, m, 0));
        }
      }
    }
    return res;
  }

  void randomDistributionInit(final TreeSet<Key> res, final int[] cipher) {
    for (int k = 0; k < mRetain; ++k) {
      final char[] map = mDistribution.toCharArray();
      Shuffle.shuffle(map, mRandom);
      final char[] dec = new char[cipher.length];
      for (int j = 0; j < dec.length; ++j) {
        dec[j] = map[cipher[j]];
      }
      res.add(new Key(mModel.entropy(new String(dec)), map, 0));
    }
  }

  void solveByExchange(final int[] cipher, final int cycles) {
    TreeSet<Key> res = new TreeSet<>();
    randomDistributionInit(res, cipher);
    for (int cycle = 0; cycle < cycles; ++cycle) { // todo parameter
      for (int c = 0; c < 100; ++c) {
        System.out.println("Doing " + c + " cycle=" + cycle);
        res = percolateExchange(res, cipher, c);
        printBestSolutions(res, 5, cipher); // todo parameter
      }
    }
  }

  private static final class HomophonicFlagsValidator implements CliFlags.Validator {

    @Override
    public boolean isValid(final CliFlags flags) {
      if ((Integer) flags.getValue(RETAIN_FLAG) < 1) {
        flags.setParseMessage("Number of hypotheses must be at least 1.");
        return false;
      }
      if (((String) flags.getValue(DISTRIBUTION_FLAG)).length() != 100) {
        flags.setParseMessage("Distribution should contain 100 characters.");
        return false;
      }
      return true;
    }
  }
  /**
   * Main program.
   * @param args see help
   * @throws IOException if an I/O error occurs.
   */
  public static void main(final String[] args) throws IOException {
    final CliFlags flags = new CliFlags("CrackHomophonic", "Try and solve 2-digit homophonic ciphers");
    flags.registerOptional('m', MODEL_FLAG, String.class, "model", "model file");
    flags.registerOptional('a', RETAIN_FLAG, Integer.class, "int", "maximum number of hypotheses to maintain at each stage", 10000);
    flags.registerOptional('d', DISTRIBUTION_FLAG, String.class, "string", "an initial assignment of symbols (100 characters)", DEFAULT_DISTRIBUTION);
    flags.registerOptional(SEED_FLAG, Long.class, "long", "seed for random number generator");
    flags.setValidator(new HomophonicFlagsValidator());
    flags.setFlags(args);

    final Entropy model;
    if (flags.isSet(MODEL_FLAG)) {
      model = FourGramAlphabetModel.loadModel((String) flags.getValue(MODEL_FLAG));
    } else {
      model = FourGramAlphabetModel.loadModel();
    }
    final CrackHomophonic homophonic = new CrackHomophonic(model, (String) flags.getValue(DISTRIBUTION_FLAG), (Integer) flags.getValue(RETAIN_FLAG));
    if (flags.isSet(SEED_FLAG)) {
      homophonic.setSeed((Long) flags.getValue(SEED_FLAG));
    }

    // Read cipher from standard input, assumes one number per line
    try (final BufferedReader r = new BufferedReader(new InputStreamReader(System.in))) {
      final int[] cipher = IntegerUtils.suckInNumbers(r);
      for (final int v : cipher) {
        if (v < 0 || v >= 100) {
          throw new IllegalArgumentException("Expected all cipher values in range 00 to 99");
        }
      }
      homophonic.solveByExchange(cipher, 20);
    }
  }
}
