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

  private Set<String> mWords = null;
  private boolean mAnagrams = false;

  void setAnagrams(final boolean anagrams) {
    mAnagrams = anagrams;
  }

  private void solve(final PrintStream out, final String word, final String result) {
    if (word.length() == 1) {
      out.println(result);
      return;
    }
    for (int k = 0; k < word.length(); ++k) {
      final String w = word.substring(0, k) + word.substring(k + 1);
      if (mAnagrams) {
        for (final String a : Anagram.findAnagrams(w, mWords)) {
          solve(out, a, result + " -> " + a);
        }
      } else if (mWords.contains(w)) {
        solve(out, w, result + " -> " + w);
      }
    }
  }

  @Override
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("Find word ladders.");
    CommonFlags.registerDictionaryFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    flags.registerOptional('A', ANAGRAM_FLAG, "allow anagrams at each step");
    flags.registerRequired(String.class, "word", "starting word");
    flags.setValidator(f -> CommonFlags.validateDictionary(f)
      && CommonFlags.validateOutput(f));
    flags.setFlags(args);
    setAnagrams(flags.isSet(ANAGRAM_FLAG));
    final String start = ((String) flags.getAnonymousValue(0)).toLowerCase(Locale.getDefault());
    try {
      mWords = Dictionary.getWordSet(Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG)), 1, start.length() - 1);
    } catch (final IOException e) {
      throw new RuntimeException("Problem reading word list.", e);
    }
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      solve(out, start, start);
    }
  }
}
