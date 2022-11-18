package irvine.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.TreeSet;

import irvine.entropy.Entropy;
import irvine.util.DoubleUtils;

/**
 * Crack a Vigenere cipher.
 * @author Sean A. Irvine
 */
public class VigenereSolver {

  protected StringBuilder decrypt(final String key, final String cipher, final boolean reverse) {
    final StringBuilder sb = new StringBuilder();
    for (int k = 0; k < cipher.length(); ++k) {
      final int shift = key.charAt(k % key.length()) - 'A';
      if (shift < 0) {
        sb.append('\0');
      } else {
        final int plain = reverse
          ? ((cipher.charAt(k) - 'A') + shift) % 26
          : ((cipher.charAt(k) - 'A') + 26 - shift) % 26;
        sb.append((char) (plain + 'A'));
      }
    }
    return sb;
  }

  private static String toString(final char[] permute) {
    final StringBuilder sb = new StringBuilder();
    for (final char c : permute) {
      sb.append(c == 0 ? '.' : c);
    }
    return sb.toString();
  }

  private static String toString(final CharSequence s) {
    final StringBuilder sb = new StringBuilder();
    for (int k = 0; k < s.length(); ++k) {
      final char c = s.charAt(k);
      sb.append(c == 0 ? '.' : c);
    }
    return sb.toString();
  }

  private final PrintStream mOut;
  private final Entropy mModel;
  private final boolean mReverse;
  private final boolean mIncludeKeyEntropy;
  private int mRetain = 1000;
  private int mAnswers = 5;

  /**
   * Construct a solver.
   * @param out output stream
   * @param model the model
   * @param reverse assume reverse Vigenere
   * @param includeKeyEntropy should the key entropy be included in scoring
   */
  public VigenereSolver(final PrintStream out, final Entropy model, final boolean reverse, final boolean includeKeyEntropy) {
    mOut = out;
    mModel = model;
    mReverse = reverse;
    mIncludeKeyEntropy = includeKeyEntropy;
  }

  void setMaximumHypothesisCount(final int retainCount) {
    mRetain = retainCount;
  }

  void setMaximumAnswers(final int answers) {
    mAnswers = answers;
  }

  private void update(final TreeSet<Node> result, final double score, final char[] permute) {
    final Node q;
    if (result.size() < mRetain) {
      final Node n = new Node(permute, score);
      result.add(n);
    } else if ((q = result.last()).getScore() > score) {
      result.remove(q);
      final Node n = new Node(permute, score);
      result.add(n);
    }
  }

  /**
   * Solve a Vigenere cipher
   * @param cipher the cipher
   * @param keyLength the key length
   */
  public void solve(final String cipher, final int keyLength) {
    final char[] key = new char[keyLength];
    TreeSet<Node> best = new TreeSet<>();
    best.add(new Node(key, 0.0));
    for (int k = 0; k < key.length; ++k) {
      final TreeSet<Node> next = new TreeSet<>();
      for (final Node n : best) {
        for (char c = 'A'; c <= 'Z'; ++c) {
          final char[] copy = Arrays.copyOf(n.getPermutation(), n.getPermutation().length);
          copy[k] = c;
          final String decrypt = decrypt(new String(copy), cipher, mReverse).toString();
          final double e = mModel.entropy(mIncludeKeyEntropy ? new String(copy) + decrypt : decrypt);
          update(next, e, copy);
        }
      }
      best = next;
      mOut.println("Best solutions after filling " + (k + 1) + "/" + key.length);
      printResults(cipher, best);
    }
  }

  private void printResults(final String cipher, final TreeSet<Node> best) {
    int cnt = 0;
    for (final Node n : best) {
      final String bk = toString(n.getPermutation());
      mOut.println(DoubleUtils.NF3.format(n.getScore()) + " " + bk + " " + toString(decrypt(bk, cipher, mReverse)));
      if (++cnt == mAnswers) {
        break;
      }
    }
  }

  void dictionaryAttack(final BufferedReader r, final String cipher) throws IOException {
    final TreeSet<Node> best = new TreeSet<>();
    String key;
    while ((key = r.readLine()) != null) {
      final String k = key.toUpperCase(Locale.getDefault());
      final String decrypt = decrypt(k, cipher, mReverse).toString();
      final double e = mModel.entropy(mIncludeKeyEntropy ? k + decrypt : decrypt);
      update(best, e, k.toCharArray());
    }
    printResults(cipher, best);
  }
}
