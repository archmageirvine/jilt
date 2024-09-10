package irvine.crypto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
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

    private final Map<String, String> mCode;
    private final double mScore;
    private final String mPrev;

    ScoreNode(final double score, final Map<String, String> code, final String prev) {
      mScore = score;
      mCode = code;
      mPrev = prev;
    }

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

    @Override
    public boolean equals(final Object o) {
      return o instanceof ScoreNode && mScore == ((ScoreNode) o).mScore && mCode.equals(((ScoreNode) o).mCode);
    }

    @Override
    public int hashCode() {
      return (int) Double.doubleToRawLongBits(mScore);
    }

  }

  private final PrintStream mOut;
  private final Entropy mModel;
  private int mRetain = 100000;

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

  private double score(final List<String> message, final Map<String, String> code) {
    return mModel.entropy(toString(message, code));
  }

  public void solve(final List<String> message) throws IOException {
    final Map<String, String> code = new HashMap<>();
    final TreeSet<String> sorted = new TreeSet<>();
    for (final String c : message) {
      code.put(c, "");
      sorted.add(c);
    }
    //final Set<String> dict = Dictionary.getDefaultDictionary();
    final Set<String> dict = Dictionary.getWordSet(new BufferedReader(new FileReader("/home/sean/Downloads/code3")), 1, 20);
    TreeSet<ScoreNode> solutions = new TreeSet<>();
    solutions.add(new ScoreNode(0.0, code, ""));
    for (final String c : sorted) {
      // Consider possible assignments for code word c
      mOut.println("Trying possibilities for " + c);
      final TreeSet<ScoreNode> nextSolutions = new TreeSet<>();
      for (final ScoreNode node : solutions) {
        final Map<String, String> existingCode = node.mCode;
        final String prev = node.mPrev;
        for (final String w : dict) {
          if (w.compareTo(prev) > 0) {
            final Map<String, String> copy = new HashMap<>(existingCode);
            copy.put(c, w);
            final double score = score(message, copy);
            if (nextSolutions.isEmpty() || score < nextSolutions.last().mScore) {
              nextSolutions.add(new ScoreNode(score, copy, w));
              if (nextSolutions.size() > mRetain) {
                nextSolutions.pollLast();
              }
            }
          }
        }
      }
      solutions = nextSolutions;
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
    final Entropy model = FourGramAlphabetModel.loadModel();
    final OnePartCodeSolver solver = new OnePartCodeSolver(System.out, model);
    solver.solve(Arrays.asList(message));
  }
}
