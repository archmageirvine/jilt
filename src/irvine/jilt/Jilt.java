package irvine.jilt;

import java.util.Arrays;
import java.util.Locale;

import irvine.crypto.PlayfairCommand;
import irvine.crypto.Playmate;
import irvine.crypto.Vampire;
import irvine.crypto.Vigenere;
import irvine.language.Anagram;
import irvine.language.Caesar;
import irvine.language.Chain;
import irvine.language.Demux;
import irvine.language.Dict;
import irvine.language.Entropy;
import irvine.language.Equation;
import irvine.language.Morse;
import irvine.language.Permute;
import irvine.language.WordSearch;
import irvine.util.StringUtils;

/**
 * Main launcher for JILT, calling individual modules as required.
 * @author Sean A. Irvine
 */
public final class Jilt {

  private Jilt() {
  }

  private static final class Help extends Command {

    private static final int SPACING = 12;

    private Help() {
      super("List available modules");
    }

    @Override
    protected void mainExec(final String... args) {
      System.out.println("Java Interactive Language Tools (JILT)");
      System.out.println();
      System.out.println("Available modules:");
      for (final Module mod : Module.values()) {
        final String modName = mod.toString();
        System.out.println(modName + StringUtils.rep(' ', SPACING - modName.length()) + mod.getCommand().getDescription());
      }
      System.out.println();
      System.out.println("For help on a specific module do \"jilt module-name --help\"");
    }
  }

  private enum Module {
    /** Dummy module that returns the list of possible modules. */
    HELP(new Help()),
    /** Output the dictionary. */
    DICT(new Dict()),
    /** Filtering by various simple criteria. */
    FILTER(new FilterCommand()),
    /** Transform the input. */
    TRANSFORM(new TransformCommand()),
    /** Morse encoding and decoding. */
    MORSE(new Morse()),
    /** Simple anagrams. */
    ANAGRAM(new Anagram()),
    /** Generate permutations. */
    PERMUTE(new Permute()),
    /** Solve word chain problems. */
    CHAIN(new Chain()),
    /** Solve grid word search problems. */
    WORDSEARCH(new WordSearch()),
    /** Solve word equation problems. */
    EQUATION(new Equation()),
    /** Solve word multiplexing problems. */
    DEMUX(new Demux()),
    /** Compute entropy. */
    ENTROPY(new Entropy()),
    /** Caesar shifts. */
    CAESAR(new Caesar()),
    /** Solve simple substitution ciphers. */
    VAMPIRE(new Vampire()),
    /** Solve Vigenere and Beaufort ciphers. */
    VIGENERE(new Vigenere()),
    /** Encode and decode Playfair ciphers. */
    PLAYFAIR(new PlayfairCommand()),
    /** Solve Playfair ciphers. */
    PLAYMATE(new Playmate()),
    ;

    private final Command mCommand;

    Module(final Command command) {
      mCommand = command;
    }

    Command getCommand() {
      return mCommand;
    }

    @Override
    public String toString() {
      return super.toString().toLowerCase(Locale.getDefault());
    }
  }

  /**
   * Main program.
   * @param args module followed by module arguments
   */
  public static void main(final String... args) {
    if (args == null || args.length == 0 || "-h".equals(args[0]) || "--help".equals(args[0])) {
      Module.HELP.getCommand().mainExec();
      return;
    }
    final String module = args[0];
    final Module mod;
    try {
      mod = Module.valueOf(module.toUpperCase(Locale.getDefault()));
    } catch (final IllegalArgumentException e) {
      System.err.println("No module named \"" + module + "\" is available.");
      return;
    }
    mod.getCommand().mainExec(Arrays.copyOfRange(args, 1, args.length));
  }
}
