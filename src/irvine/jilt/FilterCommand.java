package irvine.jilt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import irvine.filter.AlphabetFilter;
import irvine.filter.AlphabeticalFilter;
import irvine.filter.ContainsFilter;
import irvine.filter.DecreasingFilter;
import irvine.filter.DiplogramFilter;
import irvine.filter.Filter;
import irvine.filter.IncreasingFilter;
import irvine.filter.LengthFilter;
import irvine.filter.MaxLengthFilter;
import irvine.filter.MinLengthFilter;
import irvine.filter.PalindromeFilter;
import irvine.filter.PatternFilter;
import irvine.filter.PyramidFilter;
import irvine.filter.RegexFilter;
import irvine.filter.ReverseAlphabeticalFilter;
import irvine.filter.SetFilter;
import irvine.filter.TautonymFilter;
import irvine.filter.TelephoneFilter;
import irvine.filter.TelephoneSumFilter;
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
  private static final String LONGEST_FLAG = "longest";
  private static final String ALPHABETICAL_FLAG = "alphabetical";
  private static final String REVERSE_ALPHABETICAL_FLAG = "reverse";
  private static final String INCREASING_FLAG = "increasing";
  private static final String DECREASING_FLAG = "decreasing";
  private static final String LENGTH_FLAG = "length";
  private static final String MIN_LENGTH_FLAG = "min-length";
  private static final String MAX_LENGTH_FLAG = "max-length";
  private static final String PALINDROME_FLAG = "palindrome";
  private static final String TAUTONYM_FLAG = "tautonym";
  private static final String DIPLOGRAM_FLAG = "diplogram";
  private static final String CONTAINS_FLAG = "contains";
  private static final String REGEX_FLAG = "regex";
  private static final String PATTERN_FLAG = "pattern";
  private static final String PYRAMID_FLAG = "pyramid";
  private static final String ALPHABET_FLAG = "alphabet";
  private static final String IN_DICT_FLAG = "in-dict";
  private static final String VOWELS_FLAG = "vowels";
  private static final String CONSONANTS_FLAG = "consonants";
  private static final String KB1_FLAG = "qwerty1";
  private static final String KB2_FLAG = "qwerty2";
  private static final String KB3_FLAG = "qwerty3";
  private static final String FIRST_FLAG = "first";
  private static final String SECOND_FLAG = "second";
  private static final String VERTICAL_FLAG = "vertical";
  private static final String HORIZONTAL_FLAG = "horizontal";
  private static final String TELEPHONE_FLAG = "telephone";
  private static final String TELEPHONE_SUM_FLAG = "telephone-sum";

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
    flags.registerOptional('v', VOWELS_FLAG, "word consists entirely of vowels");
    flags.registerOptional('c', CONSONANTS_FLAG, "word consists entirely of consonants");
    flags.registerOptional(KB1_FLAG, "word consists entirely of letters from \"qwertyuiop\"");
    flags.registerOptional(KB2_FLAG, "word consists entirely of letters from \"asdfghjkl\"");
    flags.registerOptional(KB3_FLAG, "word consists entirely of letters from \"zxcvbnm\"");
    flags.registerOptional(VERTICAL_FLAG, "word consists entirely of vertically symmetric letters");
    flags.registerOptional(HORIZONTAL_FLAG, "word consists entirely of horizontally symmetric letters");
    flags.registerOptional('f', FIRST_FLAG, "word consists entirely of letters from \"abcdefghijklm\"");
    flags.registerOptional('s', SECOND_FLAG, "word consists entirely of letters from \"nopqrstuvwxyz\"");
    flags.registerOptional('L', LONGEST_FLAG, "among the possible results report only a longest match");
    flags.registerOptional('l', LENGTH_FLAG, Integer.class, "INT", "exact word length");
    flags.registerOptional('m', MIN_LENGTH_FLAG, Integer.class, "INT", "minimum word length");
    flags.registerOptional('M', MAX_LENGTH_FLAG, Integer.class, "INT", "maximum word length");
    flags.registerOptional(TAUTONYM_FLAG, Integer.class, "INT", "word is a tautonym with given number of repeats");
    flags.registerOptional(DIPLOGRAM_FLAG, Integer.class, "INT", "word is a diplogram with given number of repeats");
    flags.registerOptional('C', CONTAINS_FLAG, String.class, "STRING", "word contains the specified string.").setMaxCount(Integer.MAX_VALUE);
    flags.registerOptional('e', REGEX_FLAG, String.class, "STRING", "word matches the specified regular expression.").setMaxCount(Integer.MAX_VALUE);
    flags.registerOptional(PATTERN_FLAG, String.class, "STRING", "word matches the specified letter pattern.");
    flags.registerOptional(PYRAMID_FLAG, String.class, "STRING", "word matches the specified frequency pattern.");
    flags.registerOptional('x', ALPHABET_FLAG, String.class, "STRING", "word consists entirely of characters in the specified string.");
    flags.registerOptional('t', TELEPHONE_FLAG, String.class, "INT", "word matches the specified number when dialed.");
    flags.registerOptional(TELEPHONE_SUM_FLAG, String.class, "INT", "word matches the specified sum of digits when dialed.");
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
      if (f.isSet(TAUTONYM_FLAG)) {
        final int repeats = (Integer) f.getValue(TAUTONYM_FLAG);
        if (repeats < 1) {
          f.setParseMessage("--" + TAUTONYM_FLAG + " must be at least 1");
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
      if (f.isSet(PYRAMID_FLAG) && !((String) f.getValue(PYRAMID_FLAG)).matches("[0-9]+")) {
        f.setParseMessage("--" + PYRAMID_FLAG + " requires a numerical pattern like 221");
        return false;
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
    if (flags.isSet(ALPHABET_FLAG)) {
      filters.add(new AlphabetFilter((String) flags.getValue(ALPHABET_FLAG)));
    }
    if (flags.isSet(VOWELS_FLAG)) {
      filters.add(new AlphabetFilter("aeiouAEIOU"));
    }
    if (flags.isSet(CONSONANTS_FLAG)) {
      filters.add(new AlphabetFilter("bcdfghjklmnpqrstvwxyzBCDFGHJKLMNPQRSTVWXYZ"));
    }
    if (flags.isSet(KB1_FLAG)) {
      filters.add(new AlphabetFilter("qwertyuiopQWERTYUIOP"));
    }
    if (flags.isSet(KB2_FLAG)) {
      filters.add(new AlphabetFilter("asdfghjklASDFGHJKL"));
    }
    if (flags.isSet(KB3_FLAG)) {
      filters.add(new AlphabetFilter("zxcvbnmZXCVBNM"));
    }
    if (flags.isSet(FIRST_FLAG)) {
      filters.add(new AlphabetFilter("abcdefghijklmABCDEFGHIJKLM"));
    }
    if (flags.isSet(SECOND_FLAG)) {
      filters.add(new AlphabetFilter("nopqrstuvwxyzNOPQRSTUVWXYZ"));
    }
    if (flags.isSet(VERTICAL_FLAG)) {
      filters.add(new AlphabetFilter("BCDEHIKOXclox"));
    }
    if (flags.isSet(HORIZONTAL_FLAG)) {
      filters.add(new AlphabetFilter("AHIMOTUVEXYilmovwx"));
    }
    if (flags.isSet(TELEPHONE_FLAG)) {
      filters.add(new TelephoneFilter((String) flags.getValue(TELEPHONE_FLAG)));
    }
    if (flags.isSet(TELEPHONE_SUM_FLAG)) {
      filters.add(new TelephoneSumFilter((String) flags.getValue(TELEPHONE_SUM_FLAG)));
    }
    for (final Object str : flags.getValues(CONTAINS_FLAG)) {
      filters.add(new ContainsFilter((String) str));
    }
    for (final Object str : flags.getValues(REGEX_FLAG)) {
      filters.add(new RegexFilter((String) str));
    }
    if (flags.isSet(PATTERN_FLAG)) {
      filters.add(new PatternFilter((String) flags.getValue(PATTERN_FLAG)));
    }
    if (flags.isSet(PYRAMID_FLAG)) {
      filters.add(new PyramidFilter((String) flags.getValue(PYRAMID_FLAG)));
    }
    if (flags.isSet(TAUTONYM_FLAG)) {
      filters.add(new TautonymFilter((Integer) flags.getValue(TAUTONYM_FLAG)));
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
        if (flags.isSet(LONGEST_FLAG)) {
          String longest = null;
          String line;
          while ((line = reader.readLine()) != null) {
            final String word = line.toLowerCase(Locale.getDefault());
            if (is(filters, word) && (longest == null || word.length() > longest.length())) {
              longest = word;
            }
          }
          out.println(longest);
        } else {
          String line;
          while ((line = reader.readLine()) != null) {
            final String word = line.toLowerCase(Locale.getDefault());
            if (is(filters, word)) {
              out.println(line);
            }
          }
        }
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
