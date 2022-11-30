package irvine.language;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.jilt.Dictionary;
import irvine.util.Casing;
import irvine.util.CliFlags;
import irvine.util.StringUtils;

/**
 * Solve word chain problems.
 * @author Sean A. Irvine
 */
public final class Chain extends Command {

  /** Construct the module. */
  public Chain() {
    super("Solve word chain problems");
  }

  private static final String ANAGRAM_FLAG = "anagram";
  private static final String INDELS_FLAG = "indels";
  private static final String SLIDE_FLAG = "slide";
  private static final int UNASSIGNED = -1;

  private List<String> mWords = null;
  private Map<String, Integer> mWordToIndex = null;
  private boolean mIndels = false;
  private boolean mAnagrams = false;
  private int mSlide = 0;
  private final Queue<Integer> mSearchQueue = new LinkedList<>();

  void setWords(final List<String> words) {
    mWords = words;
    mWordToIndex = new HashMap<>(mWords.size());
    for (int k = 0; k < mWords.size(); ++k) {
      mWordToIndex.put(mWords.get(k), k);
    }
  }

  void setIndels(final boolean indels) {
    mIndels = indels;
  }

  void setAnagrams(final boolean anagrams) {
    mAnagrams = anagrams;
  }

  void setSlide(final int slide) {
    mSlide = slide;
  }

  private void updateWord(final int wordNumber, final String word, final int[] parent) {
    final Integer j = mWordToIndex.get(word);
    if (j != null && parent[j] == UNASSIGNED) {
      parent[j] = wordNumber;
      mSearchQueue.add(j);
    }
  }

  // Breadth first search
  private void search(final int end, final int[] parent) {
    while (!mSearchQueue.isEmpty() && parent[end] == UNASSIGNED) {
      final int wordNumber = mSearchQueue.poll();
      final String word = mWords.get(wordNumber);
      if (mSlide > 0) {
        final String prefix = word.substring(mSlide);
        for (final String w : mWords) {
          if (w.length() == word.length() && !w.equals(word) && w.startsWith(prefix)) {
            updateWord(wordNumber, w, parent);
          }
        }
      } else {
        for (int k = 0; k < word.length(); ++k) {
          final char c = word.charAt(k);
          if (mAnagrams) {
            for (final String a : Anagram.findAnagrams(word.substring(0, k) + Anagram.DIT + word.substring(k + 1), mWords)) {
              updateWord(wordNumber, a, parent);
            }
          } else {
            for (char replacement = 'a'; replacement <= 'z'; ++replacement) {
              if (replacement != c) {
                updateWord(wordNumber, word.substring(0, k) + replacement + word.substring(k + 1), parent);
              }
            }
          }
          if (mIndels) {
            // Deletion
            final String deletion = word.substring(0, k) + word.substring(k + 1);
            if (mAnagrams) {
              for (final String a : Anagram.findAnagrams(deletion, mWords)) {
                updateWord(wordNumber, a, parent);
              }
            } else {
              updateWord(wordNumber, deletion, parent);
            }
            // Insertion
            if (mAnagrams) {
              for (final String a : Anagram.findAnagrams(word + Anagram.DIT, mWords)) {
                updateWord(wordNumber, a, parent);
              }
            } else {
              for (char replacement = 'a'; replacement <= 'z'; ++replacement) {
                updateWord(wordNumber, word.substring(0, k) + replacement + word.substring(k), parent);
              }
            }
          }
        }
        if (mIndels && !mAnagrams) {
          // Handle one remaining special case of appending a letter to the end
          for (char replacement = 'a'; replacement <= 'z'; ++replacement) {
            updateWord(wordNumber, word + replacement, parent);
          }
        }
      }
    }
  }

  List<String> solve(final String start, final String end) {
    if (start.length() != end.length() && !mIndels) {
      return Collections.emptyList(); // Impossible different lengths without indels
    }
    if (start.equals(end)) {
      return Collections.singletonList(start);
    }
    final Integer startIndex = mWordToIndex.get(start);
    final Integer endIndex = mWordToIndex.get(end);
    if (startIndex == null || endIndex == null) {
      return Collections.emptyList(); // Impossible start or end not present in words
    }
    final int[] parent = new int[mWords.size()];
    Arrays.fill(parent, UNASSIGNED);
    parent[startIndex] = startIndex; // Sentinel for end of search
    mSearchQueue.clear();
    mSearchQueue.add(startIndex);
    search(endIndex, parent);
    if (parent[endIndex] == UNASSIGNED) {
      return Collections.emptyList(); // No solution
    }
    final ArrayList<String> soln = new ArrayList<>();
    soln.add(end);
    int k = endIndex;
    do {
      k = parent[k];
      soln.add(mWords.get(k));
    } while (k != parent[k]);
    Collections.reverse(soln);
    return soln;
  }

  @Override
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("Find the shortest word chain (if such a chain exists) from the first word to the second word.");
    CommonFlags.registerDictionaryFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    flags.registerOptional('A', ANAGRAM_FLAG, "allow anagrams");
    flags.registerOptional('I', INDELS_FLAG, "allow insertions and deletions");
    flags.registerOptional('s', SLIDE_FLAG, Integer.class, "INT", "delete specified number of letters and slide left");
    flags.registerRequired(String.class, "word", "Starting word");
    flags.registerRequired(String.class, "word", "Finishing word");
    flags.setValidator(f -> {
      if (!CommonFlags.validateDictionary(f)) {
        return false;
      }
      if (!CommonFlags.validateOutput(f)) {
        return false;
      }
      final String start = ((String) f.getAnonymousValue(0)).toLowerCase(Locale.getDefault());
      final String end = ((String) f.getAnonymousValue(1)).toLowerCase(Locale.getDefault());
      if (start.length() != end.length() && !f.isSet(INDELS_FLAG)) {
        f.setParseMessage("Differing word lengths require --" + INDELS_FLAG + " to be set");
        return false;
      }
      if (f.isSet(SLIDE_FLAG)) {
        if (!CommonFlags.checkPositive(f, SLIDE_FLAG)) {
          return false;
        }
        final int slide = (Integer) f.getValue(SLIDE_FLAG);
        if (slide > start.length()) {
          f.setParseMessage("--" + SLIDE_FLAG + " cannot exceed word length.");
          return false;
        }
        if (f.isSet(ANAGRAM_FLAG)) {
          f.setParseMessage("Cannot set --" + ANAGRAM_FLAG + " in conjunction with --" + SLIDE_FLAG + ".");
          return false;
        }
        if (f.isSet(INDELS_FLAG)) {
          f.setParseMessage("Cannot set --" + ANAGRAM_FLAG + " in conjunction with --" + SLIDE_FLAG + ".");
          return false;
        }
      }
      return true;
    });
    flags.setFlags(args);
    setAnagrams(flags.isSet(ANAGRAM_FLAG));
    setIndels(flags.isSet(INDELS_FLAG));
    if (flags.isSet(SLIDE_FLAG)) {
      setSlide((Integer) flags.getValue(SLIDE_FLAG));
    }
    try {
      setWords(StringUtils.suckInWords(Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG)), Casing.LOWER));
    } catch (final IOException e) {
      throw new RuntimeException("Problem reading word list.", e);
    }
    final String start = ((String) flags.getAnonymousValue(0)).toLowerCase(Locale.getDefault());
    final String end = ((String) flags.getAnonymousValue(1)).toLowerCase(Locale.getDefault());
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      out.println(solve(start, end));
    }
  }
}
