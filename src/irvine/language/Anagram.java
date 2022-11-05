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
    super("Find single word anagrams");
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
   * @return matches if any
   * @exception IOException if an I/O error occurs
   */
  public static ArrayList<String> findAnagrams(final String pattern, final BufferedReader reader) throws IOException {
    final char[] target = makeTarget(pattern);
    final ArrayList<String> results = new ArrayList<>();
      String current;
      while ((current = reader.readLine()) != null) {
        if (check(target, current.toLowerCase(Locale.getDefault()))) {
          results.add(current);
        }
      }
    return results;
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
      return findAnagrams(pattern, r);
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
   * @return matches if any
   */
  public static ArrayList<String> findAnagrams(final String pattern, final List<String> words1, final List<String> words2) {
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
              results.add((k + j) + " " + current + " " + w2);
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

  /**
   * Find anagrams.
   * @param args pattern (possibly containing dits).
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    CommonFlags.registerDictionaryFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    flags.registerRequired(String.class, "PATTERN", "pattern to anagram (with unknown letters denoted by \".\")");
    flags.registerOptional('A', FIRST_LIST_FLAG, String.class, "FILE", "first list of words (with \"-\" for stdin)");
    flags.registerOptional('B', SECOND_LIST_FLAG, String.class, "FILE", "second list of words (with \"-\" for stdin)");
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
      return true;
    });
    flags.setFlags(args);

    final PrintStream out = CommonFlags.getOutput(flags);
    final String pattern = (String) flags.getAnonymousValue(0);

    if (flags.isSet(FIRST_LIST_FLAG)) {
      // We are doing a two word list match
      try {
        final List<String> list1 = StringUtils.suckInWords((String) flags.getValue(FIRST_LIST_FLAG), Casing.LOWER);
        final List<String> list2 = StringUtils.suckInWords((String) flags.getValue(SECOND_LIST_FLAG), Casing.LOWER);
        for (final String r : findAnagrams(pattern, list1, list2)) {
          out.println(r);
        }
      } catch (final IOException e) {
        throw new RuntimeException("I/O problem reading from a word list", e);
      }
    } else {
      try (final BufferedReader reader = Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG))) {
        for (final String res : findAnagrams(pattern, reader)) {
          out.println(res);
        }
      } catch (final IOException e) {
        throw new RuntimeException("I/O problem with dictionary", e);
      }
    }
  }
}
