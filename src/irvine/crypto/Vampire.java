package irvine.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import irvine.entropy.Entropy;
import irvine.entropy.UniwordModel;
import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.util.CliFlags;

/**
 * Program to attempt automated solution of simple substitution ciphers.
 * Essentially uses the method described in "Compression and Cryptology",
 * Sean A. Irvine, PhD Thesis, School of Computing and Mathematical Sciences,
 * University of Waikato, New Zealand, 1997.
 *
 * @author Sean A. Irvine
 */
public class Vampire extends Command {

  private static final String PERMUTATION_FLAG = "permutation";
  private static final String QUIET_FLAG = "quiet";
  private static final String DIT_FLAG = "dits";
  private static final String FIX_FLAG = "fix";
  private static final String SPACE_FLAG = "ignore-spaces";
  private static final String RETAIN_FLAG = "retain";
  private static final String RESULTS_FLAG = "results";

  /**
   * Construct a simple substitution solver.
   */
  public Vampire() {
    super("Solve simple substitution ciphers");
  }

  @Override
  protected void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    CommonFlags.registerOutputFlag(flags);
    CommonFlags.registerInputFlag(flags);
    CommonFlags.registerModelFlag(flags);
    flags.registerOptional('a', RETAIN_FLAG, Integer.class, "INT", "maximum number of hypotheses to maintain at each stage", 1000);
    flags.registerOptional('r', RESULTS_FLAG, Integer.class, "INT", "maximum number of answers to print", 5);
    flags.registerOptional('f', FIX_FLAG, String.class, "pair", "fix a pair of symbols.").setMaxCount(Integer.MAX_VALUE);
    flags.registerOptional('d', DIT_FLAG, "indicates that \".\" should be treated as a dit rather than a period. No attempt is made to resolve such symbols");
    flags.registerOptional('q', QUIET_FLAG, "print only the answer");
    flags.registerOptional('p', PERMUTATION_FLAG, "print the permutation for the top solution");
    flags.registerOptional('s', SPACE_FLAG, "ignore whitespace in input");
    flags.setValidator(f -> {
      for (final Object pairs : flags.getValues(FIX_FLAG)) {
        if (((String) pairs).length() != 2) {
          f.setParseMessage("--" + FIX_FLAG + " must give a pair of symbols (for example, --" + FIX_FLAG + " AZ).");
          return false;
        }
      }
      return CommonFlags.validateOutput(f)
        && CommonFlags.validateInput(f)
        && CommonFlags.validateModel(f)
        && CommonFlags.checkPositive(f, RETAIN_FLAG)
        && CommonFlags.checkPositive(f, RESULTS_FLAG);
    });
    flags.setFlags(args);

    final Entropy model = CommonFlags.getEntropyModel(flags);
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      final VampireSolver vampire = new VampireSolver(out, model, UniwordModel.defaultEnglishModel(), flags.isSet(PERMUTATION_FLAG));
      vampire.setDitHandling(flags.isSet(DIT_FLAG));
      vampire.setVerbose(!flags.isSet(QUIET_FLAG));
      vampire.setMaximumHypothesisCount((Integer) flags.getValue(RETAIN_FLAG));
      vampire.setMaximumAnswers((Integer) flags.getValue(RESULTS_FLAG));
      for (final Object fix : flags.getValues(FIX_FLAG)) {
        final String p = (String) fix;
        vampire.fixPair(p.charAt(0), p.charAt(1));
      }

      try (final BufferedReader r = CommonFlags.getInput(flags)) {
        vampire.setCryptogram(r, flags.isSet(SPACE_FLAG));
      } catch (final IOException e) {
        throw new RuntimeException("Problem with cryptogram file.", e);
      }

      vampire.solve();
    }
  }
}
