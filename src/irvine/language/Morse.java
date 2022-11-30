package irvine.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import irvine.entropy.Entropy;
import irvine.entropy.FourGramAlphabetModel;
import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.jilt.Dictionary;
import irvine.util.CliFlags;
import irvine.util.CollectionUtils;
import irvine.util.DoubleUtils;
import irvine.util.LimitedLengthPriorityQueue;
import irvine.util.Trie;

/**
 * Morse code.
 * @author Sean A. Irvine
 */
public final class Morse extends Command {

  /** Construct the module. */
  public Morse() {
    super("Encode or decode Morse code");
  }

  private static final String HARD_FLAG = "hard";
  private static final String DEEP_FLAG = "deep";
  private static final String RESULTS_FLAG = "results";
  private static final String START_FLAG = "start";
  private static final int LONGEST = 6;

  private static HashMap<String, String> alphabet() {
    final HashMap<String, String> alphabet = new HashMap<>();
    alphabet.put(".-", "A");
    alphabet.put("-...", "B");
    alphabet.put("-.-.", "C");
    alphabet.put("-..", "D");
    alphabet.put(".", "E");
    alphabet.put("..-.", "F");
    alphabet.put("--.", "G");
    alphabet.put("....", "H");
    alphabet.put("..", "I");
    alphabet.put(".---", "J");
    alphabet.put("-.-", "K");
    alphabet.put(".-..", "L");
    alphabet.put("--", "M");
    alphabet.put("-.", "N");
    alphabet.put("---", "O");
    alphabet.put(".--.", "P");
    alphabet.put("--.-", "Q");
    alphabet.put(".-.", "R");
    alphabet.put("...", "S");
    alphabet.put("-", "T");
    alphabet.put("..-", "U");
    alphabet.put("...-", "V");
    alphabet.put(".--", "W");
    alphabet.put("-..-", "X");
    alphabet.put("-.--", "Y");
    alphabet.put("--..", "Z");
    alphabet.put(".----", "1");
    alphabet.put("..---", "2");
    alphabet.put("...--", "3");
    alphabet.put("....-", "4");
    alphabet.put(".....", "5");
    alphabet.put("-....", "6");
    alphabet.put("--...", "7");
    alphabet.put("---..", "8");
    alphabet.put("----.", "9");
    alphabet.put("-----", "0");
    alphabet.put(".-.-.-", ".");
    alphabet.put("--..--", ",");
    alphabet.put("---...", ":");
    alphabet.put("..--..", "?");
    alphabet.put(".----.", "'");
    alphabet.put("-....-", "-");
    alphabet.put("-..-.", "/");
    alphabet.put("-.--.", "(");
    alphabet.put("-.--.-", ")");
    alphabet.put(".-..-.", "\"");
    alphabet.put("-...-", "=");
    alphabet.put(".-.-.", "+");
    alphabet.put(".--.-.", "@");
    alphabet.put("-.-.-", "[start]");
    alphabet.put("...-.-", "[end]");
    alphabet.put(".-...", "[wait]");
    alphabet.put("...-.", "[understood]");
    alphabet.put("........", "[error]");
    return alphabet;
  }

  private final Map<String, String> mMorseToLetter = alphabet();
  private final Map<String, String> mLetterToMorse = CollectionUtils.invert(mMorseToLetter);

  String morseEncode(final String line) {
    final StringBuilder sb = new StringBuilder();
    for (int k = 0; k < line.length(); ++k) {
      final char c = Character.toUpperCase(line.charAt(k));
      if (Character.isWhitespace(c)) {
        sb.append(' ');
      } else {
        final String morse = mLetterToMorse.get(String.valueOf(c));
        if (morse != null) {
          if (sb.length() > 0) {
            sb.append(' ');
          }
          sb.append(morse);
        }
      }
    }
    return sb.toString();
  }

  String morseDecode(final String line) {
    final StringBuilder sb = new StringBuilder();
    int s = 0;
    int e = line.indexOf(' ');
    if (e < 0) {
      e = line.length();
    }
    while (s < line.length()) {
      if (line.charAt(s) == ' ') {
        sb.append(' ');
        ++s;
      } else {
        final String morse = line.substring(s, e);
        final String letter = mMorseToLetter.get(morse);
        // If the Morse cannot be decoded then leave it as is
        sb.append(letter == null ? morse : letter);
        s = e + 1;
      }
      e = line.indexOf(' ', s + 1);
      if (e < 0) {
        e = line.length();
      }
    }
    return sb.toString().trim();
  }

  private LimitedLengthPriorityQueue<String> mPossibleDecodings = null;
  private Entropy mModel = null;

  private void smartMorseDecode(final String line, final String decode, final int pos) {
    if (pos >= line.length()) {
      mPossibleDecodings.add(mModel.entropy(decode), decode);
      return;
    }
    if (mPossibleDecodings.size() == mPossibleDecodings.maxSize() && decode.length() % 5 == 0) {
      // Don't want to do this too often ... hence mod 5
      final double e = mModel.entropy(decode);
      if (e > mPossibleDecodings.last().getScore()) {
        return;
      }
    }
    if (Character.isWhitespace(line.charAt(pos))) {
      smartMorseDecode(line, decode + " ", pos + 1);
      //smartMorseDecode(model, line, decode, pos + 1);
    } else {
      // Heuristically longer codes are more likely to be the right answer,
      // so consider those first.
      for (int end = Math.min(line.length(), pos + LONGEST); end > pos ; --end) {
        final String d = mMorseToLetter.get(line.substring(pos, end));
        if (d != null) {
          smartMorseDecode(line, decode + d, end);
        }
      }
    }
  }

  // Stores the dictionary for the situation where dictionaryMorseDecode is used
  private Trie mDict = null;

  private void dictionaryMorseDecode(final Trie trie, final String line, final String decode, final int pos) {
    //System.out.println(decode + " " + line.substring(pos));
    if (pos >= line.length()) {
      if (trie.isTerminal()) {
        mPossibleDecodings.add(mModel.entropy(decode), decode);
      }
      return;
    }
    if (Character.isWhitespace(line.charAt(pos))) {
      // Try to do something vaguely sensible if a space is found
      if (trie.isTerminal()) {
        if (mPossibleDecodings.size() < mPossibleDecodings.maxSize() || mModel.entropy(decode) < mPossibleDecodings.last().getScore()) {
          dictionaryMorseDecode(trie, line, decode, pos + 1);
        }
      }
    } else {
      // Heuristically longer codes are more likely to be the right answer,
      // so consider those first.
      for (int end = Math.min(line.length(), pos + LONGEST); end > pos ; --end) {
        final String d = mMorseToLetter.get(line.substring(pos, end));
        if (d != null && d.length() == 1) {
          final Trie t = trie.getChild(Character.toLowerCase(d.charAt(0)));
          if (t != null) {
            dictionaryMorseDecode(t, line, decode + d, end);
          }
        }
      }
    }
    if (trie.isTerminal()) {
      if (mPossibleDecodings.size() < mPossibleDecodings.maxSize() || mModel.entropy(decode) < mPossibleDecodings.last().getScore()) {
        dictionaryMorseDecode(mDict, line, decode + " ", pos); // allow new word to start
      }
    }
  }

  void morseHardDecode(final PrintStream out, final String dict, final int results, final boolean useDictionary, final String line) {
    // If a model is not already available, use the default model.
    // In non-API uses, this will be set via the mainExec mathod.
    if (mModel == null) {
      try {
        mModel = FourGramAlphabetModel.loadModel();
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }
    mPossibleDecodings = new LimitedLengthPriorityQueue<>(results, false);
    if (useDictionary) {
      // Only load the dictionary if we do not already have it
      if (mDict == null) {
        try {
          mDict = Trie.buildTrie(Dictionary.getDictionaryReader(dict), 1, Integer.MAX_VALUE);
        } catch (final IOException e) {
          throw new RuntimeException("Problem reading word list.", e);
        }
      }
      dictionaryMorseDecode(mDict, line, "", 0);
    } else {
      smartMorseDecode(line, "", 0);
    }
    for (final LimitedLengthPriorityQueue.Node<String> node : mPossibleDecodings) {
      out.println(DoubleUtils.NF3.format(node.getScore()) + " " + node.getValue());
    }
  }

  @Override
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("If the input looks like text, the encode as Morse. Otherwise, if the input looks like Morse (using \".\" and \"-\" characters), then attempt to decode it. Any portion that cannot be decoded is left as is. If the Morse cannot be trivially decode (because the spacing between letters and words is missing), then the \"--" + HARD_FLAG + "\" and \"--" + DEEP_FLAG + "\" provide two ways to attempt decoding. Be aware without spacing information, there is typically a very large number of potential decodings. With \"--" + HARD_FLAG + "\" decoding is constrained by words in the dictionary, with \"--" + DEEP_FLAG + "\" the language model is used. Both methods are only likely to work with comparatively short pieces of Morse (corresponding to a 2 or 3 words). If you have a long segment, attempt to decode some initial portion using \"--" + DEEP_FLAG + "\" and work incrementally. Both searches can be made to try harder by increasing \"--" + RESULTS_FLAG + "\", but this can greatly slow the search.");
    CommonFlags.registerInputFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    CommonFlags.registerModelFlag(flags);
    CommonFlags.registerDictionaryFlag(flags);
    flags.registerOptional(DEEP_FLAG, "attempt to decode via the entropy model");
    flags.registerOptional(HARD_FLAG, "attempt to decode via the dictionary");
    flags.registerOptional(START_FLAG, "include start of transmission marker on each output when encoding");
    flags.registerOptional(RESULTS_FLAG, Integer.class, "INT", "maximum number of answers to print when --" + HARD_FLAG + " is used", 30);
    flags.setValidator(f -> {
      if (f.isSet(DEEP_FLAG) && f.isSet(HARD_FLAG)) {
        f.setParseMessage("At most one of --" + DEEP_FLAG + " and --" + HARD_FLAG + " can be used.");
        return false;
      }
      return CommonFlags.validateInput(f)
        && CommonFlags.validateOutput(f)
        && CommonFlags.validateModel(f)
        && CommonFlags.validateDictionary(f)
        && CommonFlags.checkPositive(f, RESULTS_FLAG);
    });
    flags.setFlags(args);

    final boolean useDictionary = flags.isSet(HARD_FLAG);
    final boolean deep = flags.isSet(DEEP_FLAG);
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      try (final BufferedReader in = CommonFlags.getInput(flags)) {
        String line;
        while ((line = in.readLine()) != null) {
          if (line.matches("[. -]+")) {
            if (useDictionary || deep) {
              // Lazy loading of model and dictionary (only done if we are using --hard)
              if (mModel == null) {
                mModel = CommonFlags.getEntropyModel(flags);
              }
              morseHardDecode(out, (String) flags.getValue(CommonFlags.DICTIONARY_FLAG), (Integer) flags.getValue(RESULTS_FLAG), useDictionary, line);
            } else {
              out.println(morseDecode(line));
            }
          } else {
            if (flags.isSet(START_FLAG)) {
              out.print("-.-.-  ");
            }
            out.println(morseEncode(line));
          }
        }
      } catch (final IOException e) {
        throw new RuntimeException("Problem reading input", e);
      }
    }
  }

}
