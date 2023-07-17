package irvine.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.util.CliFlags;
import irvine.util.DoubleUtils;
import irvine.util.LimitedLengthPriorityQueue;

/**
 * Compute entropy of supplied inputs.
 * @author Sean A. Irvine
 */
public final class Entropy extends Command {

  private static final String RESULTS_FLAG = "results";
  private static final String BEST_FLAG = " best";

  /** Construct the module. */
  public Entropy() {
    super("Compute the entropy of each line");
  }

  /**
   * Entropy.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("Can read from a stream or a command line string.");
    CommonFlags.registerInputFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    CommonFlags.registerModelFlag(flags);
    flags.registerOptional('r', RESULTS_FLAG, Integer.class, "INT", "retain this many solutions");
    flags.registerOptional('B', BEST_FLAG, "print only incrementally better results");
    final CliFlags.Flag<String> textFlag = flags.registerRequired(String.class, "TEXT", "text to compute entropy of");
    textFlag.setMinCount(0);
    flags.setValidator(f -> {
      if (f.isSet(BEST_FLAG) && f.isSet(RESULTS_FLAG)) {
        f.setParseMessage("Only one of --" + BEST_FLAG + " and --" + RESULTS_FLAG + " can be selected.");
        return false;
      }
      return CommonFlags.validateInput(f)
        && CommonFlags.validateOutput(f)
        && CommonFlags.validateModel(f)
        && CommonFlags.checkPositive(f, RESULTS_FLAG);
    });
    flags.setFlags(args);

    final boolean isBestMode = flags.isSet(BEST_FLAG);
    double bestScore = Double.POSITIVE_INFINITY;
    final LimitedLengthPriorityQueue<String> results = flags.isSet(RESULTS_FLAG) ? new LimitedLengthPriorityQueue<>((Integer) flags.getValue(RESULTS_FLAG), false) : null;
    final irvine.entropy.Entropy model = CommonFlags.getEntropyModel(flags);
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      try (final BufferedReader reader = CommonFlags.getInput(flags)) {
        String line;
        while ((line = reader.readLine()) != null) {
          final double e = model.entropy(line);
          if (isBestMode) {
            if (e < bestScore) {
              bestScore = e;
              out.println(DoubleUtils.NF3.format(e) + " " + line);
            }
          } else if (results != null) {
            results.add(e, line);
          } else {
            out.println(DoubleUtils.NF3.format(e) + " " + line);
          }
        }
      }
      if (results != null) {
        for (final LimitedLengthPriorityQueue.Node<String> r : results) {
          out.println(DoubleUtils.NF3.format(r.getScore()) + " " + r.getValue());
        }
      }
    } catch (final IOException e) {
      throw new RuntimeException("Problem reading input", e);
    }
  }
}
