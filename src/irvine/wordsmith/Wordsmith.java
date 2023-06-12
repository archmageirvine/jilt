package irvine.wordsmith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import irvine.jilt.Command;
import irvine.util.CliFlags;

/**
 * Apply a collection of tests to a word list in an attempt to explain the list.
 * @author Sean A. Irvine
 */
public final class Wordsmith extends Command {

  // todo make this a CLI parameter option
  private static final String LIST_DIR = System.getProperty("lists.dir", "lists");
  private static final String VERBOSE_FLAG = "verbose";

  /** Construct the module. */
  public Wordsmith() {
    super("Explain a list of words");
  }

  private static final String DESC = "Look for explanations for a list of words.";

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

  private List<Inspector> buildInspectors(final boolean verbose) {
    final ArrayList<Inspector> lst = new ArrayList<>();
    lst.add(new ConstantInspector());
    lst.add(new AlphabeticalInspector());
    lst.add(new ReverseAlphabeticalInspector());
    lst.add(new DirListInspector(LIST_DIR, verbose));
    return lst;
  }

  /**
   * Wordsmith.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription(DESC);
    //CommonFlags.registerDictionaryFlag(flags);
    final CliFlags.Flag<String> textFlag = flags.registerRequired(String.class, "TEXT", "words to be explained");
    flags.registerOptional('v', VERBOSE_FLAG, "increase the amount of output");
    textFlag.setMaxCount(Integer.MAX_VALUE);
    flags.setFlags(args);

    final boolean verbose = flags.isSet(VERBOSE_FLAG);
    final String[] words = getWords(flags);
    
    if (verbose) {
      System.out.println("Query: " + Arrays.toString(words));
    }
    if (words.length < 2) {
      System.out.println("Insufficient words for useful analysis");
      return;
    }

    for (final Inspector inspector : buildInspectors(verbose)) {
      final String res = inspector.inspect(words);
      if (res != null) {
        System.out.println(res);
      }
    }
  }
}
