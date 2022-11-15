package irvine.language;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

/**
 * Find multiple word anagrams for a sequence of letters.
 * @author Sean A. Irvine
 */
public final class MultiwordAnagram {

  private final PrintStream mOut;
  private final HashSet<String> mWords = new HashSet<>();
  private final int mLength;

  MultiwordAnagram(final PrintStream out, final int minLength) {
    mOut = out;
    mLength = minLength;
  }

  void addWord(final String line) {
    if (line.length() >= mLength) {
      final String s = line.trim().toLowerCase(Locale.getDefault());
      boolean bad = false;
      for (int k = 0; k < s.length(); ++k) {
        bad |= s.charAt(k) < 'a' || s.charAt(k) > 'z';
      }
      if (!bad) {
        mWords.add(s);
      }
    }
  }

  private static final String FREQ = "jzqxkvbypgwfmculdrhsinoate";

  static String reorderPattern(final String pattern) {
    final StringBuilder sb = new StringBuilder();
    final char[] p = pattern.toCharArray();
    for (int k = 0; k < FREQ.length(); ++k) {
      final char z = FREQ.charAt(k);
      for (final char c : p) {
        if (c == z) {
          sb.append(z);
        }
      }
    }
    return sb.toString();
  }

  private static String reducePattern(String pattern, final String word) {
    for (int k = 0; k < word.length(); ++k) {
      final int t = pattern.indexOf(word.charAt(k));
      if (t == -1) {
        throw new RuntimeException("Illegal call to reducePattern: " + pattern + " " + word);
      }
      pattern = pattern.substring(0, t) + pattern.substring(t + 1);
    }
    return pattern;
  }

  private static boolean match(final String pattern, final String word) {
    if (word.length() > pattern.length()) {
      return false;
    }
    final int[] indexOfStartPoints = new int[26];
    for (int k = 0; k < word.length(); ++k) {
      final char c = word.charAt(k);
      final int ci = c - 'a';
      final int t = pattern.indexOf(c, indexOfStartPoints[ci]);
      if (t == -1) {
        return false;
      }
      indexOfStartPoints[ci] = t + 1;
    }
    return true;
  }

  private void find(final String pattern, final ArrayList<String> solutionSoFar, final int maxWords) {
    if (pattern.isEmpty()) {
      final StringBuilder sb = new StringBuilder();
      for (final String s : solutionSoFar) {
        sb.append(s).append(' ');
      }
      mOut.println(sb.toString().trim());
    } else if (maxWords == -1 || solutionSoFar.size() < maxWords) {
      // force at least one letter to match early, makes it much
      // faster, especially if carefully ordered by frequency
      final char firstLetter = pattern.charAt(0);
      for (final String w : mWords) {
        if (w.indexOf(firstLetter) != -1 && match(pattern, w)) {
          final ArrayList<String> newSolution = new ArrayList<>(solutionSoFar);
          newSolution.add(w);
          find(reducePattern(pattern, w), newSolution, maxWords);
        }
      }
    }
  }

  /**
   * Find anagrams matching specified pattern. The pattern should
   * consist of lowercase letters and is matched against the preloaded
   * dictionary.
   *
   * @param pattern anagram target
   * @param maxWords maximum number of words in the solution
   */
  void search(final String pattern, final int maxWords) {
    find(reorderPattern(pattern.trim().toLowerCase(Locale.getDefault())), new ArrayList<>(), maxWords);
  }
}
