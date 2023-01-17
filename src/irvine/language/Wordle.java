package irvine.language;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.jilt.Dictionary;
import irvine.util.Casing;
import irvine.util.CliFlags;
import irvine.util.DoubleUtils;

/**
 * Compute best moves for Wordle, Dordle, and similar.
 * @author Sean A. Irvine
 */
public final class Wordle extends Command {

  private static final int LIMIT = 20;

  static String syndrome(final String word, final String guess) {
    final int[] contains = new int[26];
    for (int k = 0; k < word.length(); ++k) {
      ++contains[word.charAt(k) - 'A'];
    }
    final StringBuilder sb = new StringBuilder();
    final int[] used = new int[26];
    // Prevent "1" matching of exact matches
    for (int k = 0; k < guess.length(); ++k) {
      final char c = guess.charAt(k);
      if (word.charAt(k) == c) {
        --contains[c - 'A'];
      }
    }
    for (int k = 0; k < guess.length(); ++k) {
      final char c = guess.charAt(k);
      if (word.charAt(k) == c) {
        sb.append('2');
      } else if (contains[c - 'A'] > used[c - 'A']) {
        ++used[c - 'A'];
        sb.append('1');
      } else {
        sb.append('0');
      }
    }
    return sb.toString();
  }

  private static boolean isPossibleWorld(final String word, final String[] guesses, final int box, final int mod) {
    for (int k = 0; k < guesses.length; k += mod) {
      if (guesses[k + 1 + box].length() != 5) {
        throw new RuntimeException("Invalid syndrome: " + guesses[k + 1 + box]);
      }
      if (!syndrome(word, guesses[k]).equals(guesses[k + 1 + box])) {
        return false;
      }
    }
    return true;
  }

  private static boolean contains(final List<Set<String>> possible, final String word) {
    for (final Set<String> p : possible) {
      if (p.contains(word)) {
        return true;
      }
    }
    return false;
  }

  private static void entropySolver(final int wordCount, final Set<String> words, final Collection<String> guesses) {
    // Determine number of boxes to solve
    int mod = wordCount + 1;
    // Guesses should be (word, pattern) pairs, possibly empty
    if ((guesses.size() % mod) != 0) {
      throw new RuntimeException("Expected (word pattern)*");
    }
    final String[] g = new String[guesses.size()];
    int k = 0;
    for (final String t : guesses) {
      g[k++] = t.toUpperCase(Locale.getDefault());
    }
    final ArrayList<Set<String>> possibles = new ArrayList<>();
    for (int box = 0; box < mod - 1; ++box) {
      if (guesses.size() == 0) {
        possibles.add(words); // Efficiency -- every word still in contention
      } else {
        final Set<String> p = new LinkedHashSet<>();
        for (final String word : words) {
          if (isPossibleWorld(word, g, box, mod)) {
            p.add(word);
          }
        }
        possibles.add(p);
      }
    }
    double best = Double.POSITIVE_INFINITY;
    String bestWord = null;
    int remaining = 0;
    for (final Set<String> pos : possibles) {
      remaining += pos.size();
    }
    System.out.println("Remaining words: " + remaining);
    for (final String word : words) {
      double sum = 0;
      for (final Set<String> pos : possibles) {
        final int n = pos.size();
        final double inv = 1.0 / n;
        final Map<String, Integer> counts = new HashMap<>();
        for (final String possible : pos) {
          final String s = Wordle.syndrome(possible, word);
          counts.merge(s, 1, Integer::sum);
        }
        // Compute the entropy
        for (final Integer i : counts.values()) {
          final double p = i * inv;
          sum += p * Math.log(p);
        }
      }
      if (remaining <= LIMIT && contains(possibles, word)) {
        // When there are not many words print all the scores for words in the list
        System.out.println(word + " " + DoubleUtils.NF2.format(sum));
      }
      if (sum < best && (possibles.size() > LIMIT || contains(possibles, word))) {
        best = sum;
        bestWord = word;
      }
    }
    System.out.println("Best: " + bestWord + " " + DoubleUtils.NF2.format(best));
  }

  /**
   * Construct a new World solver.
   */
  public Wordle() {
    super("Solve Wordle and related games");
  }

  private static final String WORDS_FLAG = "words";
  private static final String LENGTH_FLAG = "length";

  private static final class WordleValidator implements CliFlags.Validator {

    @Override
    public boolean isValid(final CliFlags flags) {
      if (!CommonFlags.validateOutput(flags)) {
        return false;
      }
      if (!CommonFlags.validateDictionary(flags)) {
        return false;
      }
      final int w = (int) flags.getValue(WORDS_FLAG);
      final int t = flags.getAnonymousValues(0).size();
      if (t % (w + 1) != 0) {
        flags.setParseMessage("Number of arguments should be a multiple of " + (w + 1));
        return false;
      }
      return true;
    }
  }

  /**
   * Main program for Wordle.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("This module provides suggestions based on an entropy calculation for words to play in Wordle and related games. It expects as input a series of words and their associated syndrome where a digit 0 indicates the letter is not used, 1 the letter is in the word, and 2 the letter is in the word and in the right place. For example, BRAIN 01220 means that A and I are in the correct place, R is in the word but in some other position, and B and N do not occur in the word. If you are solve more than one word at once, then you can give multiple syndromes, e.g. for quordle you might write TARES 00110 20000 01000 00000. Multiple guesses, then appear one after another, like BRAIN 01220 STAIR 00222. The output will consist of good options for subsequent guesses.");
    CommonFlags.registerDictionaryFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    flags.registerOptional('w', WORDS_FLAG, Integer.class, "int", "number of simultaneous words", 1);
    flags.registerOptional('l', LENGTH_FLAG, Integer.class, "int", "word length", 5);
    flags.registerRequired(String.class, "(word syndromes+)*", "words and syndromes.").setMaxCount(Integer.MAX_VALUE).setMinCount(0);
    //flags.setValidator(new PlayfairFlagsValidator());
    flags.setValidator(new WordleValidator());
    flags.setFlags(args);

    // Expects words and syndromes, e.g. TARES 00110 20000 01000 00000,
    // where 0 means letter not used, 1 letter in word, 2 letter in right place.
    final int len = (int) flags.getValue(LENGTH_FLAG);
    try {
      final Set<String> words = Dictionary.getWordSet(Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG)), len, len, Casing.UPPER);
      @SuppressWarnings("unchecked")
      final Collection<String> guesses = (Collection<String>) flags.getAnonymousValues(0);
      entropySolver((int) flags.getValue(WORDS_FLAG), words, guesses);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
