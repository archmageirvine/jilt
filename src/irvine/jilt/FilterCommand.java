package irvine.jilt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import irvine.filter.AlphabeticalFilter;
import irvine.filter.DecreasingFilter;
import irvine.filter.Filter;
import irvine.filter.IncreasingFilter;
import irvine.filter.ReverseAlphabeticalFilter;
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
//    CommonFlags.registerDictionaryFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    CommonFlags.registerInputFlag(flags);
    flags.registerOptional('a', ALPHABETICAL_FLAG, "letters are in alphabetical order");
    flags.registerOptional('r', REVERSE_ALPHABETICAL_FLAG, "letters are in reverse alphabetical order");
    flags.registerOptional('A', INCREASING_FLAG, "letters are in strictly increasing alphabetical order");
    flags.registerOptional('R', DECREASING_FLAG, "letters are in strictly decreasing alphabetical order");
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
      return true;
    });
    flags.setFlags(args);
    final List<Filter> filters = new ArrayList<>();
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
