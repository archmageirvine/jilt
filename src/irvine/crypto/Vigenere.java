package irvine.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import irvine.entropy.Entropy;
import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.util.CliFlags;
import irvine.util.CliFlags.Flag;
import irvine.util.IOUtils;

/**
 * Crack a Vigenere cipher.
 * @author Sean A. Irvine
 */
public class Vigenere extends Command {

  private static final String KEYS_FLAG = "keys";
  private static final String KEY_LENGTH_FLAG = "key-length";
  private static final String KEY_ENTROPY_FLAG = "key-entropy";
  private static final String REVERSE_FLAG = "reverse";
  private static final String BEAUFORT_FLAG = "beaufort";

  /**
   * Construct a simple substitution solver.
   */
  public Vigenere() {
    super("Solve Vigenere and Beaufort ciphers");
  }

  @Override
  protected void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("The vigenere module attempts to solve Vigenere and Beaufort style ciphers. Given a known key or file containing potential keys, the --" + KEYS_FLAG + " can be used to attempt decryption with each key and the best results retained.  Otherwise, with --" + KEY_LENGTH_FLAG + " automatic solution for a given key length can be attempted.  If the key length is unknown, the module can be run with different plausible key lengths. In most cases --" + CommonFlags.MODEL_FLAG + " resources/nospace.model will work better than the default model.");
    CommonFlags.registerOutputFlag(flags);
    CommonFlags.registerInputFlag(flags);
    CommonFlags.registerModelFlag(flags);
    final Flag<Integer> retainFlag = flags.registerOptional('a', "retain", Integer.class, "INT", "maximum number of hypotheses to maintain at each stage", 1000);
    final Flag<Integer> resultsFlag = flags.registerOptional('r', "results", Integer.class, "INT", "maximum number of answers to print", 5);
    flags.registerOptional('k', KEY_LENGTH_FLAG, Integer.class, "int", "length of key", 6);
    flags.registerOptional(REVERSE_FLAG, "assume a reverse Vigenere cipher");
    flags.registerOptional(KEY_ENTROPY_FLAG, "Include the entropy of the key in the scoring");
    flags.registerOptional('K', KEYS_FLAG, String.class, "FILE", "dictionary attack using keys in given file (or \"-\" for standard input)");
    flags.registerOptional('b', BEAUFORT_FLAG, "assume a Beaufort cipher");
    flags.setValidator(f -> {
        if (!CommonFlags.validateOutput(f)) {
          return false;
        }
        if (!CommonFlags.validateInput(f)) {
          return false;
        }
        if (!CommonFlags.validateModel(f)) {
          return false;
        }
        if (retainFlag.getValue() < 1) {
          f.setParseMessage("--retain should be positive.");
          return false;
        }
        if (resultsFlag.getValue() < 1) {
          f.setParseMessage("--results should be positive.");
          return false;
        }
      if (f.isSet(REVERSE_FLAG) && f.isSet(BEAUFORT_FLAG)) {
        f.setParseMessage("Cannot use --" + REVERSE_FLAG + " with --" + BEAUFORT_FLAG + ".");
        return false;
      }
        return true;
      }
    );
    flags.setFlags(args);

    final Entropy model = CommonFlags.getEntropyModel(flags);
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      final VigenereSolver vigenere = flags.isSet(BEAUFORT_FLAG)
        ? new BeaufortSolver(out, model, flags.isSet(REVERSE_FLAG), flags.isSet(KEY_ENTROPY_FLAG))
        : new VigenereSolver(out, model, flags.isSet(REVERSE_FLAG), flags.isSet(KEY_ENTROPY_FLAG));
      vigenere.setMaximumHypothesisCount(retainFlag.getValue());
      vigenere.setMaximumAnswers(resultsFlag.getValue());

      final String cipher;
      try (final BufferedReader r = CommonFlags.getInput(flags)) {
        cipher = IOUtils.readAll(r).replaceAll("\\s+", "");
      } catch (final IOException e) {
        throw new RuntimeException("Problem reading cryptogram.", e);
      }

      if (flags.isSet(KEYS_FLAG)) {
        // For this mode it only makes sense to retain as many answers as will be displayed
        vigenere.setMaximumHypothesisCount(resultsFlag.getValue());
        try (final BufferedReader r = IOUtils.getReader((String) flags.getValue(KEYS_FLAG))) {
          vigenere.dictionaryAttack(r, cipher);
        } catch (final IOException e) {
          throw new RuntimeException("Problem reading keys.", e);
        }
      } else {
        vigenere.solve(cipher, (Integer) flags.getValue(KEY_LENGTH_FLAG));
      }
    }
  }
}
