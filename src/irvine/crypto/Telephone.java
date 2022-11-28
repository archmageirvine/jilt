package irvine.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import irvine.entropy.Entropy;
import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.util.CliFlags;
import irvine.util.DoubleUtils;
import irvine.util.IOUtils;
import irvine.util.LimitedLengthPriorityQueue;

/**
 * Attempt to decode a telephone sequence.
 * @author Sean A. Irvine
 */
public final class Telephone extends Command {

  /**
   * Command for decoding telephone digit strings.
   */
  public Telephone() {
    super("Decode telephone numbers as text");
  }

  private static final String[] TELEPHONE = {"abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
  private static final String RETAIN_FLAG = "retain";
  private static final String RESULTS_FLAG = "results";

  /**
   * Attempt to interpret a sequence of numbers as words typed on a
   * telephone with a standard keyboard. It will also try to infer
   * spaces between words.
   * @param m model
   * @param maxResults maximum results to retain
   * @param text text to process
   * @return putative decodes
   */
  public static LimitedLengthPriorityQueue<String> telephoneCode(final Entropy m, final int maxResults, final String text) {
    LimitedLengthPriorityQueue<String> best = new LimitedLengthPriorityQueue<>(maxResults, false);
    best.add(0.0, "");
    final boolean hasSpaces = text.contains(" ");
    for (int k = 0; k < text.length(); ++k) {
      final char c = text.charAt(k);
      final LimitedLengthPriorityQueue<String> next = new LimitedLengthPriorityQueue<>(maxResults, false);
      for (final LimitedLengthPriorityQueue.Node<String> n : best) {
        if (c >= '2' && c <= '9') {
          final String p = TELEPHONE[c - '2'];
          for (int j = 0; j < p.length(); ++j) {
            final String tt = n.getValue() + p.charAt(j);
            next.add(m.entropy(tt), tt);
            if (!hasSpaces) {
              final String ss = tt + " ";
              next.add(m.entropy(ss), ss);
            }
          }
        } else {
          final String tt = n.getValue() + c;
          next.add(m.entropy(tt), tt);
        }
      }
      best = next;
    }
    return best;
  }

  /**
   * Main program.
   * @param args see usage
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("Interpret the input as a sequence of numbers as dialed on a telephone and attempt to reconstruct a plausible sequence of words from which were dialed. If the input contains spaces, then these are assumed to be correct and complete, otherwise spaces will also be inferred. See also \"jilt transform --telephone\" for the corresponding encoding, and \"jilt filter --telephone\" for determining single words matching a particular number.");
    CommonFlags.registerInputFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    CommonFlags.registerModelFlag(flags);
    flags.registerOptional('a', RETAIN_FLAG, Integer.class, "INT", "maximum number of hypotheses to maintain at each stage", 1000);
    flags.registerOptional('r', RESULTS_FLAG, Integer.class, "INT", "maximum number of answers to print", 10);
    flags.setValidator(f -> {
      if (!CommonFlags.validateInput(f)) {
        return false;
      }
      if (!CommonFlags.validateOutput(f)) {
        return false;
      }
      if (!CommonFlags.validateModel(f)) {
        return false;
      }
      if ((Integer) f.getValue(RETAIN_FLAG) < 1) {
        f.setParseMessage("--" + RETAIN_FLAG + " should be positive.");
        return false;
      }
      if ((Integer) f.getValue(RESULTS_FLAG) < 1) {
        f.setParseMessage("--" + RESULTS_FLAG + " should be positive.");
        return false;
      }
      return true;
    });
    final int retain = (Integer) flags.getValue(RETAIN_FLAG);
    final int results = (Integer) flags.getValue(RESULTS_FLAG);
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      try (final BufferedReader in = CommonFlags.getInput(flags)) {
        final String text = IOUtils.readAll(in).trim();
        final Entropy m = CommonFlags.getEntropyModel(flags);
        int k = 0;
        for (final LimitedLengthPriorityQueue.Node<String> r : telephoneCode(m, retain, text)) {
          out.println(DoubleUtils.NF3.format(r.getScore()) + " " + r.getValue());
          if (++k == results) {
            break;
          }
        }
      } catch (final IOException e) {
        throw new RuntimeException("Problem reading input.", e);
      }
    }
  }
}
