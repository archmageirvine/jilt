package irvine.language;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Set;

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

//  private final Map<String, Integer> mWordToIndex = new HashMap<>();
//  private final List<Set<String>> mWordsByLength = new ArrayList<>();
//  private int[] mMaxChainLength = null;
//  private List<List<String>> mExtensions = new ArrayList<>();
//
//  private void initUp() {
//    mMaxChainLength = new int[mWords.size()];
//    int k = 0;
//    for (final String w : mWords) {
//      mWordToIndex.put(w, k++);
//      while (w.length() >= mWordsByLength.size()) {
//        mWordsByLength.add(new HashSet<>());
//      }
//      mWordsByLength.get(w.length()).add(w);
//    }
//  }

  // todo this up solver is crappy and slow, better to do some kind of precompute by length
  private void solveUp(final PrintStream out, final String word, final String result, final int k) {
    if (mAnagrams) {
      // This is actually simpler to code -- just add one more unknown letter
      for (final String a : Anagram.findAnagrams(word + ".", mWords)) {
        solveUp(out, a, result + " -> " + a);
      }
    } else {
      // Most of the time 'a' .. 'z' would suffice, but we allow unusual use cases here
      for (char c = ' '; c <= '~'; ++c) {
        final String w = word.substring(0, k) + c + word.substring(k);
        if (mWords.contains(w)) {
          solveUp(out, w, result + " -> " + w);
        }
      }
    }
  }

  private void solveUp(final PrintStream out, final String word, final String result) {
    if (word.length() > mLength) {
      // New record length
      mLength = word.length();
      out.println(result);
    }
    if (mLeft) {
      solveUp(out, word, result, 0);
      if (mRight) {
        solveUp(out, word, result, word.length() - 1);
      }
    } else if (mRight) {
      solveUp(out, word, result, word.length() - 1);
    } else {
      for (int k = 0; k <= word.length(); ++k) {
        solveUp(out, word, result, k);
      }
    }
  }

  @Override
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("By default one letter anywhere in the word is deleted at each step. By using -L and -R this can be restrict to deleting letters from the left, right, or both ends of the word. With -A the remaining letters are anagrammed to find potential solutions. If -u is select, then print longer and longer words, incrementally printing the longest solutions as they are found.");
    CommonFlags.registerDictionaryFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    flags.registerOptional('A', ANAGRAM_FLAG, "allow anagrams at each step");
    flags.registerOptional('L', LEFT_FLAG, "truncate letters from the left");
    flags.registerOptional('R', RIGHT_FLAG, "truncate letters from the right");
    flags.registerOptional('u', UP_FLAG, "form longer and longer words rather than shorter words");
    flags.registerRequired(String.class, "word", "starting word");
    flags.setValidator(f -> CommonFlags.validateDictionary(f)
      && CommonFlags.validateOutput(f));
    flags.setFlags(args);
    setAnagrams(flags.isSet(ANAGRAM_FLAG));
    setLeft(flags.isSet(LEFT_FLAG));
    setRight(flags.isSet(RIGHT_FLAG));
    final boolean up = flags.isSet(UP_FLAG);
    final String start = ((String) flags.getAnonymousValue(0)).toLowerCase(Locale.getDefault());
    try {
      if (up) {
        mWords = Dictionary.getWordSet(Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG)), start.length() + 1, Integer.MAX_VALUE);
      } else {
        mWords = Dictionary.getWordSet(Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG)), 1, start.length() - 1);
      }
    } catch (final IOException e) {
      throw new RuntimeException("Problem reading word list.", e);
    }
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      if (up) {
        solveUp(out, start, start);
      } else {
        solveDown(out, start, start);
      }
    }
  }
}
