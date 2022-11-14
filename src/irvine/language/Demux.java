package irvine.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.jilt.Dictionary;
import irvine.util.CliFlags;

/**
 * Demultiplex words from a string.
 * @author Sean A. Irvine
 */
public final class Demux extends Command {

  private static final String COUNT_FLAG = "count";

  private PrintStream mOut = null;
  private String mText = null;
  private Set<String> mDict = null;
  private int mWordLength = 0;


  /** Construct the module. */
  public Demux() {
    super("Demultiplex words from a string");
  }

  private void search(final PrintStream out, final StringBuilder[] words, final int pos) {
    if (pos >= mText.length()) {
      // We have found a solution
      out.println(Arrays.toString(words));
      return;
    }
    final char c = mText.charAt(pos);
    for (int k = 0; k < words.length; ++k) {
      if (words[k].length() < mWordLength && (k == 0 || words[k - 1].length() > 0)) {
        // Try adding c to words[k]
        words[k].append(c); // play a letter
        if (words[k].length() < mWordLength || mDict.contains(words[k].toString())) {
          // The search can continue
          search(out, words, pos + 1);
        }
        words[k].setLength(words[k].length() - 1); // undo the played letter
      }
    }
  }

  /**
   * Demultiplex.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("Solve multiplexed (also called telescoped) word problems, where a string is composed of the letters of a specified number of words mixed together, but with the letters of the individual words in their natural order. For example, \"MINISTETSONS\" can be decomposed into \"MINT\", \"IONS\", and \"SETS\".");
    CommonFlags.registerDictionaryFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    flags.registerRequired('n', COUNT_FLAG, Integer.class, "INT", "number of words to extract");
    flags.registerRequired(String.class, "STRING", "string to demultiplex");
    flags.setValidator(f -> {
      if (!CommonFlags.validateOutput(f)) {
        return false;
      }
      if (!CommonFlags.validateDictionary(f)) {
        return false;
      }
      final int cnt = (Integer) f.getValue(COUNT_FLAG);
      final String text = (String) f.getAnonymousValue(0);
      if (text.length() % cnt != 0) {
        f.setParseMessage("--" + COUNT_FLAG + " (-n) is not a multiple of text length.");
        return false;
      }
      return true;
    });
    flags.setFlags(args);

    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      final String text = (String) flags.getAnonymousValue(0);
      final int cnt = (Integer) flags.getValue(COUNT_FLAG);
      assert text.length() % cnt == 0;
      final StringBuilder[] words = new StringBuilder[cnt];
      for (int k = 0; k < words.length; ++k) {
        words[k] = new StringBuilder();
      }
      mOut = out;
      mText = text.toLowerCase(Locale.getDefault());
      mWordLength = text.length() / cnt;
      try (final BufferedReader reader = Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG))) {
        mDict = Dictionary.getWordSet(reader, mWordLength, mWordLength);
      } catch (final IOException e) {
        throw new RuntimeException("Problem reading dictionary.", e);
      }
      search(out, words, 0);
    }
  }
}
