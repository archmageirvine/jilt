package irvine.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Locale;
import java.util.TreeSet;

import irvine.entropy.Entropy;
import irvine.entropy.FourGramAlphabetModel;
import irvine.util.CliFlags;
import irvine.util.CliFlags.Flag;
import irvine.util.DoubleUtils;

/**
 * Crack a Vigenere cipher.
 * @author Sean A. Irvine
 */
public class Vigenere {

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

  private final Entropy mModel;
  private final boolean mReverse;
  private final boolean mIncludeKeyEntropy;
  private int mRetain = 1000;
  private int mAnswers = 5;

  /**
   * Construct a solver.
   * @param model the model
   * @param reverse assume reverse Vigenere
   * @param includeKeyEntropy should the key entropy be included in scoring
   */
  public Vigenere(final Entropy model, final boolean reverse, final boolean includeKeyEntropy) {
    mModel = model;
    mReverse = reverse;
    mIncludeKeyEntropy = includeKeyEntropy;
  }

  private void setMaximumHypothesisCount(final int retainCount) {
    mRetain = retainCount;
  }

  private void setMaximumAnswers(final int answers) {
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
      System.out.println("Best solutions after filling " + (k + 1) + "/" + key.length);
      printResults(cipher, best);
    }
  }

  private void printResults(final String cipher, final TreeSet<Node> best) {
    int cnt = 0;
    for (final Node n : best) {
      final String bk = toString(n.getPermutation());
      System.out.println(DoubleUtils.NF3.format(n.getScore()) + " " + bk + " " + toString(decrypt(bk, cipher, mReverse)));
      if (++cnt == mAnswers) {
        break;
      }
    }
  }

  private void dictionary(final String cipher) throws IOException {
    final TreeSet<Node> best = new TreeSet<>();
    try (final BufferedReader r = new BufferedReader(new InputStreamReader(System.in))) {
      String key;
      while ((key = r.readLine()) != null) {
        final String k = key.toUpperCase(Locale.getDefault());
        final String decrypt = decrypt(k, cipher, mReverse).toString();
        final double e = mModel.entropy(mIncludeKeyEntropy ? k + decrypt : decrypt);
        update(best, e, k.toCharArray());
      }
    }
    printResults(cipher, best);
  }

  /**
   * Decrypt Vigenere ciphers.
   * @param args key and cipher
   * @throws IOException if an I/O error occurs
   */
  public static void main(final String[] args) throws IOException {
    final CliFlags flags = new CliFlags("Vigenere");
    final Flag<Integer> retainFlag = flags.registerOptional('a', "retain", Integer.class, "int", "Maximum number of hypotheses to maintain at each stage.", 1000);
    final Flag<Integer> resultsFlag = flags.registerOptional('r', "results", Integer.class, "int", "Maximum number of answers to print.", 5);
    final Flag<Integer> keyLengthFlag = flags.registerOptional('k', "key-length", Integer.class, "int", "Length of key.", 6);
    final Flag<String> modelFlag = flags.registerOptional('m', "model", String.class, "model", "Model file.");
    //flags.registerOptional('q', QUIET_FLAG, "Quiet. Print only the answer.");
    flags.registerOptional("reverse", "Assume a reverse Vigenere.");
    flags.registerOptional("key-entropy", "Include the entropy of the key in the scoring.");
    flags.registerOptional('d', "dictionary", "Decrypt using keys supplied on standard input.");
    flags.registerOptional('b', "beaufort", "Assume Beaufort.");
    final Flag<String> cipherFlag = flags.registerRequired(String.class, "cryptogram", "Text of cipher.");

    flags.setFlags(args);
    if (flags.isSet("reverse") && flags.isSet("beaufort")) {
      flags.setParseMessage("Cannot used --reverse with --beaufort");
      return;
    }

    final Entropy model;
    if (modelFlag.isSet()) {
      model = FourGramAlphabetModel.loadModel(modelFlag.getValue());
    } else {
      model = FourGramAlphabetModel.loadModel();
    }
    final Vigenere vigenere = flags.isSet("beaufort")
      ? new Beaufort(model, flags.isSet("reverse"), flags.isSet("key-entropy"))
      : new Vigenere(model, flags.isSet("reverse"), flags.isSet("key-entropy"));
    vigenere.setMaximumHypothesisCount(retainFlag.getValue());
    vigenere.setMaximumAnswers(resultsFlag.getValue());
    final String cipher = cipherFlag.getValue().toUpperCase(Locale.getDefault());
    if (flags.isSet("dictionary")) {
      // For this mode it only makes sense to retain as many answers as will be displayed
      vigenere.setMaximumHypothesisCount(resultsFlag.getValue());
      vigenere.dictionary(cipher);
    } else {
      vigenere.solve(cipher, keyLengthFlag.getValue());
    }
  }
}
