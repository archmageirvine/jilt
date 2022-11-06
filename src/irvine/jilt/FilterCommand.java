package irvine.jilt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import irvine.filter.AlphabeticalFilter;
import irvine.filter.DecreasingFilter;
import irvine.filter.DiplogramFilter;
import irvine.filter.Filter;
import irvine.filter.IncreasingFilter;
import irvine.filter.LengthFilter;
import irvine.filter.MaxLengthFilter;
import irvine.filter.MinLengthFilter;
import irvine.filter.PalindromeFilter;
import irvine.filter.ReverseAlphabeticalFilter;
import irvine.filter.SetFilter;
import irvine.filter.TautonymFilter;
import irvine.util.CliFlags;

/**
 * Filter words based on a variety of criteria.
 * @author Sean A. Irvine
 */
public final class FilterCommand extends Command {

  /** Construct the module. */
  public FilterCommand() {
    super("Filter words");
  }

  private static final String DESC = "Filter words based on a variety of simple criteria.";
  private static final String ALPHABETICAL_FLAG = "alphabetical";
  private static final String REVERSE_ALPHABETICAL_FLAG = "reverse";
  private static final String INCREASING_FLAG = "increasing";
  private static final String DECREASING_FLAG = "decreasing";
  private static final String LENGTH_FLAG = "length";
  private static final String MIN_LENGTH_FLAG = "min-length";
  private static final String MAX_LENGTH_FLAG = "max-length";
  private static final String PALINDROME_FLAG = "palindrome";
  private static final String TAUTONUM_FLAG = "tautonym";
  private static final String DIPLOGRAM_FLAG = "diplogram";
  private static final String IN_DICT_FLAG = "in-dict";

  private boolean is(final List<Filter> filters, final String word) {
    for (final Filter f : filters) {
      if (!f.is(word)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Filter words.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription(DESC);
    CommonFlags.registerDictionaryFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    CommonFlags.registerInputFlag(flags);
    flags.registerOptional('a', ALPHABETICAL_FLAG, "letters are in alphabetical order");
    flags.registerOptional('r', REVERSE_ALPHABETICAL_FLAG, "letters are in reverse alphabetical order");
    flags.registerOptional('A', INCREASING_FLAG, "letters are in strictly increasing alphabetical order");
    flags.registerOptional('R', DECREASING_FLAG, "letters are in strictly decreasing alphabetical order");
    flags.registerOptional('p', PALINDROME_FLAG, "word is a palindrome");
    flags.registerOptional('d', IN_DICT_FLAG, "word is in the dictionary");
    flags.registerOptional('l', LENGTH_FLAG, Integer.class, "INT", "exact word length");
    flags.registerOptional('m', MIN_LENGTH_FLAG, Integer.class, "INT", "minimum word length");
    flags.registerOptional('M', MAX_LENGTH_FLAG, Integer.class, "INT", "maximum word length");
    flags.registerOptional(TAUTONUM_FLAG, Integer.class, "INT", "word is a tautonym with given number of repeats");
    flags.registerOptional(DIPLOGRAM_FLAG, Integer.class, "INT", "word is a diplogram with given number of repeats");
    flags.setValidator(f -> {
      if (!CommonFlags.validateDictionary(f)) {
        return false;
      }
      if (!CommonFlags.validateOutput(f)) {
        return false;
      }
      if (!CommonFlags.validateInput(f)) {
        return false;
      }
      if (f.isSet(TAUTONUM_FLAG)) {
        final int repeats = (Integer) f.getValue(TAUTONUM_FLAG);
        if (repeats < 1) {
          f.setParseMessage("--" + TAUTONUM_FLAG + " must be at least 1");
          return false;
        }
      }
      if (f.isSet(DIPLOGRAM_FLAG)) {
        final int repeats = (Integer) f.getValue(DIPLOGRAM_FLAG);
        if (repeats < 1) {
          f.setParseMessage("--" + DIPLOGRAM_FLAG + " must be at least 1");
          return false;
        }
      }
      return true;
    });
    flags.setFlags(args);
    final List<Filter> filters = new ArrayList<>();
    if (flags.isSet(LENGTH_FLAG)) {
      filters.add(new LengthFilter((Integer) flags.getValue(LENGTH_FLAG)));
    }
    if (flags.isSet(MIN_LENGTH_FLAG)) {
      filters.add(new MinLengthFilter((Integer) flags.getValue(MIN_LENGTH_FLAG)));
    }
    if (flags.isSet(MAX_LENGTH_FLAG)) {
      filters.add(new MaxLengthFilter((Integer) flags.getValue(MAX_LENGTH_FLAG)));
    }
    if (flags.isSet(PALINDROME_FLAG)) {
      filters.add(new PalindromeFilter());
    }
    if (flags.isSet(ALPHABETICAL_FLAG)) {
      filters.add(new AlphabeticalFilter());
    }
    if (flags.isSet(REVERSE_ALPHABETICAL_FLAG)) {
      filters.add(new ReverseAlphabeticalFilter());
    }
    if (flags.isSet(INCREASING_FLAG)) {
      filters.add(new IncreasingFilter());
    }
    if (flags.isSet(DECREASING_FLAG)) {
      filters.add(new DecreasingFilter());
    }
    if (flags.isSet(TAUTONUM_FLAG)) {
      filters.add(new TautonymFilter((Integer) flags.getValue(TAUTONUM_FLAG)));
    }
    if (flags.isSet(DIPLOGRAM_FLAG)) {
      filters.add(new DiplogramFilter((Integer) flags.getValue(DIPLOGRAM_FLAG)));
    }
    if (flags.isSet(IN_DICT_FLAG)) {
      try (final BufferedReader reader = Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG))) {
        filters.add(new SetFilter(reader));
      } catch (final IOException e) {
        throw new RuntimeException("Problem reading words from the specified dictionary.", e);
      }
    }

    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      try (final BufferedReader reader = CommonFlags.getInput(flags)) {
        String line;
        while ((line = reader.readLine()) != null) {
          final String word = line.toLowerCase(Locale.getDefault());
          if (is(filters, word)) {
            out.println(line);
          }
        }
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
