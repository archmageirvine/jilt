package irvine.associator;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import irvine.jilt.Command;
import irvine.util.CliFlags;
import irvine.util.DoubleUtils;

/**
 * Entry point for the word associator.
 * @author Sean A. Irvine
 */
public final class Associator extends Command {

  /** Construct the module. */
  public Associator() {
    super("List associations for a query");
  }

  private static final String DESC = "List words associated with another word of set of words.";
  private static final String RESULTS_FLAG = "results";

  private String[] getWords(final CliFlags flags) {
    final Collection<?> args = flags.getAnonymousValues(0);
    final String[] words = new String[args.size()];
    int k = 0;
    for (final Object w : args) {
      final String upper = ((String) w).toUpperCase(Locale.getDefault());
      // Convenience to strip any trailing commas on input terms
      words[k++] = upper.endsWith(",") ? upper.substring(0, upper.length() - 1) : upper;
    }
    return words;
  }

  /**
   * Run the associator.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription(DESC);
    // todo user selection of model
    flags.registerOptional('n', RESULTS_FLAG, Integer.class, "INT", "maximum number of results to print", 30);
    final CliFlags.Flag<String> textFlag = flags.registerRequired(String.class, "TEXT", "words to be explained");
    textFlag.setMaxCount(Integer.MAX_VALUE);
    flags.setFlags(args);

    try {
      final AssociatorModel am = AssociatorModel.loadModel();
      final Map<String, Float> res = am.query((int) flags.getValue(RESULTS_FLAG), getWords(flags));
      for (final Map.Entry<String, Float> e : res.entrySet()) {
        System.out.println(DoubleUtils.NF2.format(e.getValue()) + " " + e.getKey());
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }
}
