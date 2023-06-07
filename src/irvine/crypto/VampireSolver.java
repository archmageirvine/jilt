package irvine.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.TreeSet;

import irvine.entropy.Entropy;
import irvine.util.DoubleUtils;

/**
 * Program to attempt automated solution of simple substitution ciphers.
 * Essentially uses the method described in "Compression and Cryptology",
 * Sean A. Irvine, PhD Thesis, School of Computing and Mathematical Sciences,
 * University of Waikato, New Zealand, 1997.
 *
 * @author Sean A. Irvine
 */
public class VampireSolver {

  private static final int ALPHABET_SIZE = 128;
  private static final char[] VALID = " abcdefghijklmnopqrstuvwxyz0".toCharArray();

  /** Underlying language models, main model and final polishing model. */
  private final Entropy mModel;
  private final Entropy mPolish;
  private final PrintStream mOut;

  private boolean mDitHandling = false;
  private boolean mVerbose = true;
  private final boolean mPrintPermutation;
  /** Hypotheses maintained at each level. */
  private int mMaximumHypothesisCount = 1000;
  private int mMaximumAnswers = 5;

  /** The cryptogram itself. */
  private String mCrypt = null;
  /** The permutation. */
  private final char[] mPermute = new char[ALPHABET_SIZE];
  /** Order in which cryptogram characters are to be considered. */
  private final int[] mOrder = new int[ALPHABET_SIZE];

  /**
   * Construct a new cryptogram solver.  The first model is used for the
   * bulk of the solution. The polishing model (if specified) is used to
   * rank the final answers.  The first model should be a character based
   * model and the polishing model can be a word based model.
   * @param out where output should be sent
   * @param model the model
   * @param polish polishing model (may be null)
   * @param printPermutation should the permutation of the best solution be printed
   */
  public VampireSolver(final PrintStream out, final Entropy model, final Entropy polish, final boolean printPermutation) {
    mModel = model;
    mPolish = polish;
    mPrintPermutation = printPermutation;
    mOut = out;
  }

  static String cleanCryptogram(final BufferedReader reader, final boolean isDitHandling, final boolean ignoreWhitespace) throws IOException {
    final StringBuilder sb = new StringBuilder();
    boolean wasSpace = true;
    int c;
    while ((c = reader.read()) != -1) {
      if (Character.isWhitespace(c) || c == '-' || c == '/') {
        // Multiple consecutive whitespace like symbols are reduced to a single space
        if (wasSpace) {
          continue;
        }
        c = ' ';
        wasSpace = true;
      } else {
        wasSpace = false;
      }
      if (Character.isDigit(c)) {
        c = '0';
      } else if (isDitHandling && c == '.') {
        c = '\0';
      } else {
        c = Character.toUpperCase(c);
        if (c != ' ' && c != '\0' && (c < 'A' || c > 'Z')) {
          continue;
        }
      }
      if (ignoreWhitespace && Character.isWhitespace(c)) {
        continue;
      }
      sb.append((char) c);
    }
    return sb.toString().trim();
  }

  private void message(final String s, final boolean flag) {
    if (flag) {
      mOut.println(s);
    }
  }

  private void message(final String s) {
    message(s, mVerbose);
  }

  public void setDitHandling(final boolean value) {
    mDitHandling = value;
  }

  public boolean isDitHandling() {
    return mDitHandling;
  }

  public void setVerbose(final boolean value) {
    mVerbose = value;
  }

  public boolean isVerbose() {
    return mVerbose;
  }

  public void setMaximumHypothesisCount(final int value) {
    mMaximumHypothesisCount = value;
  }

  public int getMaximumHypothesisCount() {
    return mMaximumHypothesisCount;
  }

  public void setMaximumAnswers(final int value) {
    mMaximumAnswers = value;
  }

  public int getMaximumAnswers() {
    return mMaximumAnswers;
  }

  /**
   * Explicitly set a pair in the permutation.
   * @param crypt cryptogram character
   * @param plain equivalent plain character
   */
  public void fixPair(final char crypt, final char plain) {
    mPermute[Character.toUpperCase(crypt)] = Character.toLowerCase(plain);
  }


  /**
   * Read in the ciphertext to be analysed. Handles the
   * reduction to 27 letters and dit handling if necessary.
   * @param reader stream providing cryptogram
   * @param ignoreWhitespace should whitespace in input be ignored
   * @exception IOException if an I/O problem occurs
   */
  public void setCryptogram(final BufferedReader reader, final boolean ignoreWhitespace) throws IOException {
    mCrypt = cleanCryptogram(reader, isDitHandling(), ignoreWhitespace);
    message("Cryptogram: " + mCrypt);
    message("Cryptogram score: " + mModel.entropy(mCrypt));
  }

  /**
   * Examine the cryptogram to determine the zeroth order frequencies.
   * This is used to determine the order in which the permutation
   * will be filled.
   * @return number of distinct characters in the cryptogram.
   */
  private int zerothFreq() {
    final int[] freq = new int[ALPHABET_SIZE];
    // Get raw frequencies
    for (int k = 0; k < mCrypt.length(); ++k) {
      ++freq[mCrypt.charAt(k)];
    }
    // Zero those which are already fixed
    for (int k = 0; k < freq.length; ++k) {
      if (mPermute[k] != 0) {
        freq[k] = 0;
      }
    }
    // Compute non-zero (exclude dits i.e. char 0)
    int r = 0;
    for (int k = 1; k < freq.length; ++k) {
      if (freq[k] != 0) {
        ++r;
      }
    }
    // Determine the order array, this is not the most efficient way
    // to compute this array, but this is not a time critical step.
    for (int k = 0; k < freq.length; ++k) {
      int maxi = 1;
      for (int j = 1; j < freq.length; ++j) {
        if (freq[j] >= freq[maxi]) {
          maxi = j;
        }
      }
      freq[maxi] = -1; // mark so we don't find it again
      mOrder[k] = maxi;
    }
    return r;
  }

  private void update(final TreeSet<Node> result, final double score, final char[] permute) {
    final Node q;
    if (result.size() < getMaximumHypothesisCount()) {
      final Node n = new Node(permute, score);
      result.add(n);
    } else if ((q = result.last()).getScore() > score) {
      result.remove(q);
      final Node n = new Node(permute, score);
      result.add(n);
    }
  }

  private double queryModel(final Entropy model, final String s, final char[] permute) {
    final StringBuilder sb = new StringBuilder();
    for (int k = 0; k < s.length(); ++k) {
      sb.append(permute[s.charAt(k)]);
    }
    return model.entropy(sb.toString());
  }

  private TreeSet<Node> polish(final TreeSet<Node> nodes) {
    if (mPolish == null) {
      return nodes;
    }
    message("Polishing");
    final TreeSet<Node> t = new TreeSet<>();
    for (final Node n : nodes) {
      t.add(new Node(n.getPermutation(), queryModel(mPolish, mCrypt, n.getPermutation())));
    }
    return t;
  }

  private TreeSet<Node> percolate(final TreeSet<Node> nodes, final char next) {
    final TreeSet<Node> result = new TreeSet<>();
    final int[] used = new int[ALPHABET_SIZE];
    for (final Node n : nodes) {
      // Consider all possible extensions
      Arrays.fill(used, 0);
      for (int k = 1; k < ALPHABET_SIZE; ++k) {
        used[n.getPermutation()[k]] = n.getPermutation()[k];
      }
      for (final char c : VALID) {
        if (used[c] == 0) {
          n.getPermutation()[next] = c;
          final double score = queryModel(mModel, mCrypt, n.getPermutation());
          update(result, score, n.getPermutation());
        }
      }
    }
    return result;
  }

  private void percolate(final int limit) {
    TreeSet<Node> nodes = new TreeSet<>();
    final Node root = new Node(mPermute, Double.POSITIVE_INFINITY);
    nodes.add(root);
    for (int level = 0; level < limit; ++level) {
      message("Starting percolate for " + (char) mOrder[level]);
      nodes = percolate(nodes, (char) mOrder[level]);
      message("TreeSet has " + nodes.size() + " nodes");
      int v = 0;
      for (final Node node : nodes) {
        if (++v > getMaximumAnswers()) {
          break;
        }
        print(node, mVerbose);
      }
    }
    int answers = 0;
    Node first = null;
    for (final Node result : polish(nodes)) {
      if (first == null) {
        first = result;
      }
      if (++answers > getMaximumAnswers()) {
        break;
      }
      print(result, true);
    }
    if (mPrintPermutation && first != null) {
      printPermutation(first);
    }
  }

  void print(final Node n, final boolean flag) {
    final char[] map = n.getPermutation();
    final StringBuilder sb = new StringBuilder();
    sb.append(DoubleUtils.NF3.format(n.getScore())).append(' ');
    for (int k = 0; k < mCrypt.length(); ++k) {
      final char x = map[mCrypt.charAt(k)];
      sb.append(x == 0 ? '.' : x);
    }
    message(sb.toString(), flag);
  }

  private void printPermutation(final Node n) {
    final StringBuilder a = new StringBuilder();
    final StringBuilder b = new StringBuilder();
    for (int k = 'A'; k <= 'Z'; ++k) {
      final char cipher = n.getPermutation()[k];
      a.append(cipher == 0 ? '.' : cipher);
      boolean found = false;
      for (char j = 'A'; j <= 'Z'; ++j) {
        if (n.getPermutation()[j] == Character.toLowerCase(k)) {
          b.append(j); // what if not found
          found = true;
          break;
        }
      }
      if (!found) {
        b.append('.');
      }
    }
    mOut.println();
    mOut.println("Keys:");
    mOut.println("Ciphertext: ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    mOut.println("Plaintext:  " + a.toString());
    mOut.println();
    mOut.println("Plaintext:  abcdefghijklmnopqrstuvwxyz");
    mOut.println("Ciphertext: " + b.toString());
  }

  /**
   * Solve the cryptogram.
   */
  public void solve() {
    if (mCrypt != null) {
      final int levels = zerothFreq();
      // zeroth order
      percolate(levels);
    }
  }
}
