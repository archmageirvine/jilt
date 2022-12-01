package irvine.language;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.jilt.Dictionary;
import irvine.util.CliFlags;

/**
 * Solve word ladder problems.
 * @author Sean A. Irvine
 */
public final class Ladder extends Command {

  /** Construct the module. */
  public Ladder() {
    super("Solve word ladder problems");
  }

  private static final String ANAGRAM_FLAG = "anagram";
  private static final String LEFT_FLAG = "left";
  private static final String RIGHT_FLAG = "right";
  private static final String UP_FLAG = "up";

  private Set<String> mWords = null;
  private boolean mAnagrams = false;
  private boolean mLeft = false;
  private boolean mRight = false;
  private int mLength = 0;

  void setWords(final Set<String> words) {
    mWords = words;
  }

  void setAnagrams(final boolean anagrams) {
    mAnagrams = anagrams;
  }

  void setLeft(final boolean left) {
    mLeft = left;
  }

  void setRight(final boolean right) {
    mRight = right;
  }

  private void solveDown(final PrintStream out, final String word, final String result, final int k) {
    final String w = word.substring(0, k) + word.substring(k + 1);
    if (mAnagrams) {
      for (final String a : Anagram.findAnagrams(w, mWords)) {
        solveDown(out, a, result + " -> " + a);
      }
    } else if (mWords.contains(w)) {
      solveDown(out, w, result + " -> " + w);
    }
  }

  private void solveDown(final PrintStream out, final String word, final String result) {
    if (word.length() == 1) {
      out.println(result);
      return;
    }
    if (mLeft) {
      solveDown(out, word, result, 0);
      if (mRight) {
        solveDown(out, word, result, word.length() - 1);
      }
    } else if (mRight) {
      solveDown(out, word, result, word.length() - 1);
    } else {
      for (int k = 0; k < word.length(); ++k) {
        solveDown(out, word, result, k);
      }
    }
  }

  private final Map<String, Integer> mWordToIndex = new HashMap<>();
  private final List<Set<String>> mWordsByLength = new ArrayList<>();
  private final List<Collection<String>> mExtensions = new ArrayList<>();

  private static String sort(final String s) {
    final char[] c = s.toCharArray();
    Arrays.sort(c);
    return new String(c);
  }

  private static Map<String, List<String>> patterns(final Set<String> keys) {
    final Map<String, List<String>> patterns = new HashMap<>();
    for (final String k : keys) {
      final String pattern = sort(k);
      final List<String> values = patterns.get(pattern);
      if (values == null) {
        final ArrayList<String> v = new ArrayList<>();
        v.add(k);
        patterns.put(pattern, v);
      } else {
        values.add(k);
      }
    }
    return patterns;
  }

  void initUp() {
    final int[] maxChainLength = new int[mWords.size()];
    int j = 0;
    for (final String w : mWords) {
      mWordToIndex.put(w, j++);
      while (w.length() >= mWordsByLength.size()) {
        mWordsByLength.add(new HashSet<>());
      }
      mWordsByLength.get(w.length()).add(w);
      mExtensions.add(new TreeSet<>());
    }
    // Consider shorter and shorter words build the extensions as we go
    for (int len = mWordsByLength.size() - 2; len > 0; --len) {
      final Set<String> longer = mWordsByLength.get(len + 1);
      final Set<String> shorter = mWordsByLength.get(len);
      final Map<String, List<String>> patterns = mAnagrams ? patterns(shorter) : null;
      for (final String word : longer) {
        final int wordIndex = mWordToIndex.get(word);
        final int wordChainLength = maxChainLength[wordIndex] + 1;
        if (mAnagrams) {
          final String key = sort(word);
          for (int k = 0; k < key.length(); ++k) {
            if (k == 0 || key.charAt(k) != key.charAt(k - 1)) {
              final String w = key.substring(0, k) + key.substring(k + 1);
              final List<String> matches = patterns.get(w);
              if (matches != null) {
                for (final String a : matches) {
                  final int wi = mWordToIndex.get(a);
                  final int wcl = maxChainLength[wi];
                  if (wordChainLength >= wcl) {
                    final Collection<String> ext = mExtensions.get(wi);
                    if (wordChainLength > wcl) {
                      ext.clear();
                      maxChainLength[wi] = wordChainLength;
                    }
                    ext.add(word);
                  }
                }
              }
            }
          }
        } else {
          for (int k = 0; k < word.length(); ++k) {
            final String w = word.substring(0, k) + word.substring(k + 1);
            if (shorter.contains(w)) {
              final int wi = mWordToIndex.get(w);
              final int wcl = maxChainLength[wi];
              if (wordChainLength >= wcl) {
                final Collection<String> ext = mExtensions.get(wi);
                if (wordChainLength > wcl) {
                  ext.clear();
                  maxChainLength[wi] = wordChainLength;
                }
                ext.add(word);
              }
            }
          }
        }
      }
    }
  }

  void solveUp(final PrintStream out, final String word, final String result) {
    final Integer i = mWordToIndex.get(word);
    if (i != null) {
      final Collection<String> ext = mExtensions.get(i);
      if (ext.isEmpty()) {
        out.println(result);
      } else {
        for (final String e : mExtensions.get(i)) {
          solveUp(out, e, result + " -> " + e);
        }
      }
    }
  }

  @Override
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("By default one letter anywhere in the word is deleted at each step. By using -L and -R this can be restrict to deleting letters from the left, right, or both ends of the word. With -A the remaining letters are anagrammed to find potential solutions. If -u is select, then print longer and longer words.");
    CommonFlags.registerDictionaryFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    flags.registerOptional('A', ANAGRAM_FLAG, "allow anagrams at each step");
    flags.registerOptional('L', LEFT_FLAG, "truncate letters from the left");
    flags.registerOptional('R', RIGHT_FLAG, "truncate letters from the right");
    flags.registerOptional('u', UP_FLAG, "form longer and longer words rather than shorter words");
    flags.registerRequired(String.class, "word", "starting word");
    flags.setValidator(f -> {
      if (f.isSet(UP_FLAG) && (f.isSet(LEFT_FLAG) || f.isSet(RIGHT_FLAG))) {
        // todo the following probably could be added easy enough, if a bit fiddly
        f.setParseMessage("Searching up with -L and/or -R not currently supported");
        return false;
      }
      return CommonFlags.validateDictionary(f)
        && CommonFlags.validateOutput(f);
    });
    flags.setFlags(args);
    setAnagrams(flags.isSet(ANAGRAM_FLAG));
    setLeft(flags.isSet(LEFT_FLAG));
    setRight(flags.isSet(RIGHT_FLAG));
    final boolean up = flags.isSet(UP_FLAG);
    final String start = ((String) flags.getAnonymousValue(0)).toLowerCase(Locale.getDefault());
    try {
      if (up) {
        setWords(Dictionary.getWordSet(Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG)), start.length(), Integer.MAX_VALUE));
      } else {
        setWords(Dictionary.getWordSet(Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG)), 1, start.length() - 1));
      }
    } catch (final IOException e) {
      throw new RuntimeException("Problem reading word list.", e);
    }
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      if (up) {
        initUp();
        solveUp(out, start, start);
      } else {
        solveDown(out, start, start);
      }
    }
  }
}
