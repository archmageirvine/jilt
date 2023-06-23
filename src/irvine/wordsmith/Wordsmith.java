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

  private static final String VERBOSE_FLAG = "verbose";
  private static final String LOO_FLAG = "loo";

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
    lst.add(new DictionaryInspector());
    lst.add(new ConstantInspector());
    lst.add(new LengthInspector());
    lst.add(new UniqueInspector());
    lst.add(new VowelPatternsInspector());
    lst.add(new AlphabeticalInspector());
    lst.add(new ReverseAlphabeticalInspector());
    lst.add(new AlphabeticalWordInspector());
    lst.add(new ReverseAlphabeticalWordInspector());
    lst.add(new LetterSequenceInspector());
    lst.add(new ReverseLetterSequenceInspector());
    lst.add(new ReverseDictionaryInspector());
    lst.add(new AlphabetInspector());
    lst.add(new IncrementLetterInspector());
    lst.add(new DecrementLetterInspector());
    lst.add(new ReplaceLetterInspector());
    lst.add(new DirListInspector(DirListInspector.LIST_DIR, true, verbose));
    lst.add(new DoubledLetterInspector());
    lst.add(new PrefixInspector());
    lst.add(new SuffixInspector());
    lst.add(new SliceInspector());
    lst.add(new ConsecutiveLettersInspector());
    lst.add(new ParityInspector());
    lst.add(new AddSingleLetterInspector());
    lst.add(new RomanInspector());
    lst.add(new ElementInspector());
    lst.add(new DoReMiInspector());
    lst.add(new MetaValuationInspector());
    return lst;
  }

  /**
   * Wordsmith.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription(DESC);
    flags.registerOptional('v', VERBOSE_FLAG, "increase the amount of output");
    flags.registerOptional(LOO_FLAG, "leave one out");
    final CliFlags.Flag<String> textFlag = flags.registerRequired(String.class, "TEXT", "words to be explained");
    textFlag.setMaxCount(Integer.MAX_VALUE);
    flags.setFlags(args);

    final boolean verbose = flags.isSet(VERBOSE_FLAG);
    final String[] words = getWords(flags);

    if (verbose) {
      System.out.println("Query: " + Arrays.toString(words));
    }

    if (flags.isSet(LOO_FLAG)) {
      if (words.length < 3) {
        System.out.println("Insufficient words for useful analysis");
        return;
      }
      // First run the full set of words
      final List<Inspector> inspectors = buildInspectors(verbose);
      final String[] fullResults = new String[inspectors.size()];
      int k = 0;
      for (final Inspector inspector : inspectors) {
        fullResults[k++] = inspector.inspect(words);
      }
      // Not leave each word out in turn and check for different response
      final String[] slice = new String[words.length - 1];
      for (int omit = 0; omit < words.length; ++omit) {
        System.arraycopy(words, 0, slice, 0, omit);
        System.arraycopy(words, omit + 1, slice, omit, words.length - omit - 1);
        System.out.println("Running: " + Arrays.toString(slice));
        int j = 0;
        for (final Inspector inspector : inspectors) {
          final String res = inspector.inspect(slice);
          if (res != null && !res.equals(fullResults[j])) {
            System.out.println(res);
          }
          ++j;
        }
      }
    } else {
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
}
