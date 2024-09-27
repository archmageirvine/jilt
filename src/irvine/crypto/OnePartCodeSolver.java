package irvine.crypto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import irvine.entropy.Entropy;
import irvine.entropy.FourGramAlphabetModel;
import irvine.jilt.Dictionary;
import irvine.util.DoubleUtils;

/**
 * Crack a one-part code.
 * @author Sean A. Irvine
 */
public class OnePartCodeSolver {

  private static final class ScoreNode implements Comparable<ScoreNode> {

    private final Map<String, String> mCode; // code word -> dictionary word
    private final double mScore; // entropy of the message using this mapping

    ScoreNode(final double score, final Map<String, String> code) {
      mScore = score;
      mCode = code;
    }

    // This comparator is used by TreeSet to maintain solutions sorted by the score
    @Override
    public int compareTo(final ScoreNode other) {
      if (this == other) {
        return 0;
      }
      final int sc = Double.compare(mScore, other.mScore);
      if (sc != 0) {
        return sc;
      }
      // In reality the two maps should have the same keys
      for (final Map.Entry<String, String> e : mCode.entrySet()) {
        final String vThis = e.getValue();
        final String vOther = other.mCode.getOrDefault(e.getKey(), "");
        final int c = vThis.compareTo(vOther);
        if (c != 0) {
          return c;
        }
      }
      return 0;
    }

    // You can ignore these next three methods -- Java boilerplate stuff
    @Override
    public boolean equals(final Object o) {
      return o instanceof ScoreNode && mScore == ((ScoreNode) o).mScore && mCode.equals(((ScoreNode) o).mCode);
    }

    @Override
    public int hashCode() {
      return (int) Double.doubleToRawLongBits(mScore);
    }

    @Override
    public String toString() {
      return DoubleUtils.NF3.format(mScore) + " " + mCode;
    }
  }

  private final PrintStream mOut;
  private final Entropy mModel;
  private int mRetain = 100;

  /**
   * Construct a solver.
   * @param out output stream
   * @param model the model
   */
  public OnePartCodeSolver(final PrintStream out, final Entropy model) {
    mOut = out;
    mModel = model;
  }

  void setMaximumHypothesisCount(final int retainCount) {
    mRetain = retainCount;
  }

  private String toString(final List<String> message, final Map<String, String> code) {
    final StringBuilder sb = new StringBuilder();
    for (final String c : message) {
      final String t = code.get(c);
      if (!t.isEmpty()) {
        sb.append(t).append(' ');
      } else {
        sb.append(". ");
      }
    }
    return sb.toString();
  }

  /**
   * Compute the entropy of the message with respect to a particular code
   * @param message the encoded message (codewords)
   * @param code the mapping of codes to dictionary words
   * @return entropy
   */
  private double score(final List<String> message, final Map<String, String> code, final HashMap<String, Integer> inverse) {
    final String s = toString(message, code);
    // sum(log(1+delta)) -- note this is currently only right for a 10000 word dict
    double d = 1;
    for (final String w : message) {
      final int v = Integer.parseInt(w);
      final int u = inverse.get(code.get(w));
      d += Math.log(1 + Math.abs(v - u) / 10000.0);
    }
    return mModel.entropy(s) * (1 + d);
  }

  private String[] getWords() throws IOException {
    // Loads a set of words, puts them in alphabetical order
    final Set<String> dict = Dictionary.getWordSet(new BufferedReader(new FileReader("/home/sean/Downloads/code2")), 1, 20);
    final String[] words = new String[dict.size()];
    int k = 0;
    for (final String w : dict) {
      words[k++] = w;
    }
    Arrays.sort(words);
    return words;
  }

  /**
   * Attempt to solve a one-part code message.
   * @param message code groups
   * @throws IOException if an I/O error occurs
   */
  public void solve(final List<String> message) throws IOException {
    final Map<String, String> code = new HashMap<>();
    final TreeSet<String> sorted = new TreeSet<>(message);
    final String[] dict = getWords();
    if (sorted.size() > dict.length) {
      throw new UnsupportedEncodingException();
    }
    // Invert dictionary, goes from word to position in the dictionary
    final HashMap<String, Integer> inverse = new HashMap<>();
    for (int k = 0; k < dict.length; ++k) {
      inverse.put(dict[k], k);
    }

    // This naively spaces the code words out along the dictionary
    // todo: more could be done here to make it closer to expected locations
//    final double r = dict.length / (sorted.size() + 1.0);
//    double s = 0;
//    for (final String w : sorted) {
//      s += r;
//      code.put(w, dict[(int) s]);
//    }

    // Try to place at roughly the right position
    for (final String w : sorted) {
      final long codeValue = Long.parseLong(w);
      code.put(w, dict[(int) (codeValue * dict.length / 10000)]);
    }

    // Put that assignment in as the initial solution
    TreeSet<ScoreNode> solutions = new TreeSet<>();
    solutions.add(new ScoreNode(score(message, code, inverse), code));

    // Iteratively try and find the solution
    // Note: this is only one idea for doing this, might be better ways and ideas here
    // todo: detect convergence and exit loop
    System.out.println("Starting: " + dict.length + " " + sorted.size());
    while (true) {
      // Try moving each word in turn
      final TreeSet<ScoreNode> nextSolutions = new TreeSet<>();
      // Step over each of the solutions from the previous iterations
      for (final ScoreNode node : solutions) {
        final Map<String, String> existingCode = node.mCode;
        // Consider each code in the existing solution
        for (final String c : sorted) {
          // Try moving that code to each possible word between the previous and next code
          final int prev = inverse.getOrDefault(existingCode.get(sorted.lower(c)), -1);
          final int next = inverse.getOrDefault(existingCode.get(sorted.higher(c)), dict.length);
          for (int k = prev + 1; k < next; ++k) {
            final Map<String, String> copy = new HashMap<>(existingCode);
            copy.put(c, dict[k]);
            final double score = score(message, copy, inverse);
            if (nextSolutions.size() < mRetain || score < nextSolutions.last().mScore) {
              nextSolutions.add(new ScoreNode(score, copy));
              // Throw away the worst solution if we have too many
              if (nextSolutions.size() > mRetain) {
                nextSolutions.pollLast();
              }
            }
          }
        }
      }
      solutions = nextSolutions;
      // Note: DoubleUtils.NF3 is simply printing number to 3 dp
      System.out.println("Score range: " + DoubleUtils.NF3.format(solutions.first().mScore) + " to " + DoubleUtils.NF3.format(solutions.last().mScore) + " no solutions: " + solutions.size());
      // Actually this next check doesn't ever happen with this approach
      if (solutions.isEmpty()) {
        mOut.println("Could not continue with current constraints");
        break;
      }
      final ScoreNode best = solutions.first();
      mOut.println(DoubleUtils.NF3.format(best.mScore) + " " + toString(message, best.mCode));
    }
  }

  /**
   * Temporary.
   * @param args ignored
   */
  public static void main(final String[] args) throws IOException {
    final String[] message = {"4372", "5464", "7656", "5861", "0000", "5432", "9672", "2765", "0813", "9981", "1192", "4275", "0000", "5432", "4372", "5464", "7909", "5861", "0000", "9808", "2764", "7782", "0813", "8074", "8244", "4506", "9041", "7811", "9972", "0361", "4372", "5464", "9144", "5861", "9041", "1224", "3626", "0813", "9071", "3668", "0735", "9041", "0395", "5865", "3755", "4792", "0955", "4506", "9041", "9847", "9041", "0395", "4792", "0955", "4506", "9041", "9847"};
    //final Entropy model = FourGramAlphabetModel.loadModel();
    final Entropy model = FourGramAlphabetModel.loadModel("/home/sean/Downloads/model6.out");
    final OnePartCodeSolver solver = new OnePartCodeSolver(System.out, model);
    solver.solve(Arrays.asList(message));
  }
}
