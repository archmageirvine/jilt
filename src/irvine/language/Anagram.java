package irvine.language;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.jilt.Dictionary;
import irvine.util.Casing;
import irvine.util.CliFlags;
import irvine.util.StringUtils;

/**
 * Anagram searcher with allowance for dits that can be replaced with
 * arbitrary letters. The pattern to match is supplied on the command
 * line and the list of target words appears on standard input.
 * @author Sean A. Irvine
 */
public final class Anagram extends Command {

  /** Construct the module. */
  public Anagram() {
    super("Find anagrams");
  }

  /** Symbol used for a dit. */
  public static final char DIT = '.';

  private static char[] makeTarget(final String pattern) {
    final char[] target = pattern.toLowerCase(Locale.getDefault()).toCharArray();
    // move dits to the end, so they are matched last
    int j = target.length - 1;
    while (j >= 0 && target[j] == DIT) {
      --j;
    }
    for (int i = 0; i < j; ++i) {
      if (target[i] == DIT) {
        target[i] = target[j];
        target[j--] = DIT;
      }
    }
    return target;
  }

  private static boolean compatible(final char[] target, final String candidate) {
    final int t = target.length;
    final int c = candidate.length();
    if (c <= t) {
      final boolean[] marks = new boolean[t];
      for (int i = 0; i <= c; ++i) {
        if (i == c) {
          return true; // Entire candidate occurs in target
        }
        for (int j = 0; j <= t; ++j) {
          if (j == t) {
            return false; // Letter in candidate does not occur in target
          }
          if (!marks[j] && (target[j] == candidate.charAt(i) || target[j] == DIT)) {
            marks[j] = true;
            break; // Found current letter
          }
        }
      }
    }
    return false;
  }

  private static boolean check(final char[] target, final String candidate) {
    final int t = target.length;
    return candidate.length() == t && compatible(target, candidate);
  }

  /**
   * Find anagrams matching a specified pattern.
   * @param pattern anagram target
   * @param reader input stream of target strings one per line
   * @param ranked true if the rank (position of solution in the dictionary) should be reported
   * @return matches if any
   * @exception IOException if an I/O error occurs
   */
  public static ArrayList<String> findAnagrams(final String pattern, final BufferedReader reader, final boolean ranked) throws IOException {
    final char[] target = makeTarget(pattern);
    int rank = 0;
    final ArrayList<String> results = new ArrayList<>();
      String current;
      while ((current = reader.readLine()) != null) {
        if (check(target, current.toLowerCase(Locale.getDefault()))) {
          results.add(ranked ? rank + " " + current : current);
        }
        ++rank;
      }
    return results;
  }

  /**
   * Find anagrams matching a specified pattern.
   * @param pattern anagram target
   * @param words word list
   * @param ranked true if the rank (position of solution in the dictionary) should be reported
   * @return matches if any
   */
  public static ArrayList<String> findAnagrams(final String pattern, final List<String> words, final boolean ranked) {
    final char[] target = makeTarget(pattern);
    int rank = 0;
    final ArrayList<String> results = new ArrayList<>();
    for (final String current : words) {
      if (check(target, current.toLowerCase(Locale.getDefault()))) {
        results.add(ranked ? rank + " " + current : current);
      }
      ++rank;
    }
    return results;
  }

  /**
   * Find anagrams matching a specified pattern.
   * @param pattern anagram target
   * @param words word list
   * @return matches if any
   */
  public static ArrayList<String> findAnagrams(final String pattern, final List<String> words) {
    return findAnagrams(pattern, words, false);
  }

  /**
   * Find anagrams matching specified pattern.
   * @param pattern anagram target
   * @param source input stream of target strings one per line
   * @return matches if any
   * @exception IOException if an I/O error occurs
   */
  public static ArrayList<String> findAnagrams(final String pattern, final InputStream source) throws IOException {
    try (final BufferedReader r = new BufferedReader(new InputStreamReader(source))) {
      return findAnagrams(pattern, r, false);
    }
  }

  /**
   * Find anagrams matching specified pattern. The collections of words are
   * assumed to be in lower case. Attempts to find anagrams using pairs of
   * words, one drawn from each list.  For example, this can be used to
   * find anagrams of people names to people names.
   * @param pattern anagram target
   * @param words1 words to try and match
   * @param words2 words to try and match
   * @param ranked true if a rank of the solution should be produced
   * @return matches if any
   */
  public static ArrayList<String> findAnagrams(final String pattern, final List<String> words1, final List<String> words2, final boolean ranked) {
    if (pattern == null) {
      return null;
    }
    final int length = pattern.length();
    final char[] target = makeTarget(pattern);
    final ArrayList<String> results = new ArrayList<>();
    int k = 0;
    for (final String current : words1) {
      final int cLength = current.length();
      if (cLength < length && compatible(target, current)) {
        // Initial word is compatible, try combos
        int j = 0;
        for (final String w2 : words2) {
          if (w2.length() + cLength == length) {
            final String key = current + w2;
            if (check(target, key)) {
              // Record k + j as a score for the word.  This is sometimes useful if
              // the input lists are sorted according to some metric, then the lower
              // the sum the "better" the solution according to some metric.
              if (ranked) {
                results.add((k + j) + " " + current + " " + w2);
              } else {
                results.add(current + " " + w2);
              }
            }
          }
          ++j;
        }
      }
      ++k;
    }
    return results;
  }

  private static final String FIRST_LIST_FLAG = "first";
  private static final String SECOND_LIST_FLAG = "second";
  private static final String RANK_FLAG = "rank";
  private static final String MAX_WORDS_FLAG = "max-words";
  private static final String MIN_LENGTH_FLAG = "min-length";

  private static final String DESC = "The anagram module can be used to find simple single word anagrams including cases where the query pattern contains unknown letters. For example, \"anagram cat\" will find the words \"act\" and \"cat\"; similarly, \"anagram c.t\" will find all anagrams of \"c\", \"t\", and one other letter. With the options -A and -B, anagram can be used to split an anagram across two word lists. This is useful, for example, in trying to solve anagrams of personal names where -A is a file containing first names and -B is a file containing surnames. With -m larger than 1, this module can also find multiword anagrams, but performance is much lower and long patterns or numbers of words can result in very slow execution.";

  /**
   * Find anagrams.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription(DESC);
    CommonFlags.registerDictionaryFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    flags.registerRequired(String.class, "PATTERN", "pattern to anagram (with unknown letters denoted by \".\")");
    flags.registerOptional('A', FIRST_LIST_FLAG, String.class, "FILE", "first list of words (with \"-\" for stdin)");
    flags.registerOptional('B', SECOND_LIST_FLAG, String.class, "FILE", "second list of words (with \"-\" for stdin)");
    flags.registerOptional('M', MAX_WORDS_FLAG, Integer.class, "INT", "maximum number of words in the solution", 1);
    flags.registerOptional('m', MIN_LENGTH_FLAG, Integer.class, "INT", "minimum length of word in multiple word solution", 3);
    flags.registerOptional(RANK_FLAG, "report the rank of the solution");
    flags.setValidator(f -> {
      if (!CommonFlags.validateDictionary(f)) {
        return false;
      }
      if (!CommonFlags.validateOutput(f)) {
        return false;
      }
      if (f.isSet(FIRST_LIST_FLAG) != f.isSet(SECOND_LIST_FLAG)) {
        f.setParseMessage("--" + FIRST_LIST_FLAG + " (-A) and --" + SECOND_LIST_FLAG + " (-B) must be used together or not at all.");
        return false;
      }
      if (f.isSet(FIRST_LIST_FLAG) && f.isSet(MAX_WORDS_FLAG)) {
        f.setParseMessage("Setting -A and -B lists incompatible with use of -m.");
        return false;
      }
      if (f.isSet(FIRST_LIST_FLAG)) {
        final String first = (String) f.getValue(FIRST_LIST_FLAG);
        final String second = (String) f.getValue(SECOND_LIST_FLAG);
        if ("-".equals(first) && "-".equals(second)) {
          f.setParseMessage("--" + FIRST_LIST_FLAG + " (-A) and --" + SECOND_LIST_FLAG + " (-B) cannot both read from stdin");
          return false;
        }
        if (!"-".equals(first) && !new File(first).canRead()) {
          f.setParseMessage("Specified file \"" + first + "\" is not readable.");
          return false;
        }
        if (!"-".equals(second) && !new File(second).canRead()) {
          f.setParseMessage("Specified file \"" + first + "\" is not readable.");
          return false;
        }
      }
      return CommonFlags.checkPositive(f, MIN_LENGTH_FLAG)
        && CommonFlags.checkPositive(f, MAX_WORDS_FLAG);
    });
    flags.setFlags(args);

    final String pattern = (String) flags.getAnonymousValue(0);
    final boolean ranked = flags.isSet(RANK_FLAG);
    final int minWordLength = (Integer) flags.getValue(MIN_LENGTH_FLAG);
    final int maxWords = (Integer) flags.getValue(MAX_WORDS_FLAG);

    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      if (maxWords > 1) {
        // Multiple word anagrams
        final MultiwordAnagram ma = new MultiwordAnagram(out, minWordLength);
        // Load in allowable words
        try (final BufferedReader reader = Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG))) {
          String line;
          while ((line = reader.readLine()) != null) {
            ma.addWord(line);
          }
        } catch (final IOException e) {
          throw new RuntimeException("I/O problem with dictionary", e);
        }
        ma.search(pattern, maxWords);
      } else if (flags.isSet(FIRST_LIST_FLAG)) {
        // We are doing a two word list match
        try {
          final List<String> list1 = StringUtils.suckInWords((String) flags.getValue(FIRST_LIST_FLAG), Casing.LOWER);
          final List<String> list2 = StringUtils.suckInWords((String) flags.getValue(SECOND_LIST_FLAG), Casing.LOWER);
          for (final String r : findAnagrams(pattern, list1, list2, ranked)) {
            out.println(r);
          }
        } catch (final IOException e) {
          throw new RuntimeException("I/O problem reading from a word list", e);
        }
      } else {
        try (final BufferedReader reader = Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG))) {
          for (final String res : findAnagrams(pattern, reader, ranked)) {
            out.println(res);
          }
        } catch (final IOException e) {
          throw new RuntimeException("I/O problem with dictionary", e);
        }
      }
    }
  }
}
