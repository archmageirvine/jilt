package irvine.crypto;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import irvine.entropy.Entropy;
import irvine.entropy.FourGramAlphabetModel;
import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.util.CliFlags;
import irvine.util.IOUtils;

/**
 * Automated cryptanalysis of Playfair.  Generates and initially random set of keys and
 * iteratively improves them according to the entropy of the putative decryption with
 * respect to the supplied model.  At each iteration the best hypothesis are retained to
 * form the basis of the next iteration.
 * @author Sean A. Irvine
 */
public class Playmate extends Command {

  private static final String SIX_FLAG = "six";
  private static final String SEVEN_BY_FOUR_FLAG = "7x4";
  private static final String RETAIN_FLAG = "retain";
  private static final String SEED_FLAG = "seed";
  private static final String KEYS_FLAG = "keys";
  private static final String ITERATIONS_FLAG = "iterations";

  static final char[] FIVE_ALPHABET_NO_J = Playfair.FIVE_ALPHABET_NO_J.toCharArray();
  private static final char[] SIX_ALPHABET = Playfair.SIX_ALPHABET.toCharArray();
  private static final char[] SEVEN_BY_FOUR_ALPHABET = Playfair.SEVEN_BY_FOUR_ALPHABET.toCharArray();

  /**
   * Construct a new command.
   */
  public Playmate() {
    super("Solve Playfair ciphers");
  }

  private static final class PlaymateValidator implements CliFlags.Validator {

    @Override
    public boolean isValid(final CliFlags flags) {
      if (!CommonFlags.validateInput(flags)) {
        return false;
      }
      if (!CommonFlags.validateOutput(flags)) {
        return false;
      }
      if (!CommonFlags.validateModel(flags)) {
        return false;
      }
      if (flags.isSet(SIX_FLAG) && flags.isSet(SEVEN_BY_FOUR_FLAG)) {
        flags.setParseMessage("At most one of --" + SIX_FLAG + " and --" + SEVEN_BY_FOUR_FLAG + " can be set.");
        return false;
      }
      if ((Integer) flags.getValue(RETAIN_FLAG) < 1) {
        flags.setParseMessage("Number of hypotheses must be at least 1.");
        return false;
      }
      if ((Integer) flags.getValue(ITERATIONS_FLAG) < 0) {
        flags.setParseMessage("Number of iterations must be at least 0.");
        return false;
      }
      if (flags.isSet(KEYS_FLAG) && !((File) flags.getValue(KEYS_FLAG)).canRead()) {
        flags.setParseMessage("Specified dictionary file does not exist.");
        return false;
      }
      return true;
    }
  }

  /**
   * Command for cracking Playfair ciphers.
   * @param args see help message
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags("Automated cryptanalysis of Playfair ciphers");
    flags.setDescription("Attempt to solve a Playfair cipher when the key is unknown. Potential solutions can be optionally seeded with keys from a list specified with the --" + KEYS_FLAG + " flag. Thereafter a randomized search is made attempting to arrive at better and better solutions. The default models selected based on the Playfair variant are appropriate for English.");
    CommonFlags.registerInputFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    CommonFlags.registerModelFlag(flags);
    flags.registerOptional('6', SIX_FLAG, "6-by-6 Playfair");
    flags.registerOptional(SEVEN_BY_FOUR_FLAG, "7-by-4 Playfair");
    flags.registerOptional('a', RETAIN_FLAG, Integer.class, "INT", "maximum number of hypotheses to maintain at each iteration", 10000);
    flags.registerOptional(SEED_FLAG, Long.class, "INT", "seed for random number generator");
    flags.registerOptional('I', ITERATIONS_FLAG, Integer.class, "INT", "number of iterations", 1000);
    flags.registerOptional('K', KEYS_FLAG, String.class, "FILE", "dictionary attack using keys in given file (or \"-\" for standard input)");
    flags.setValidator(new PlaymateValidator());
    flags.setFlags(args);

    final char[] alphabet;
    final int width;
    final int height;
    final String modelRes;
    if (flags.isSet(SIX_FLAG)) {
      alphabet = SIX_ALPHABET;
      width = 6;
      height = 6;
      modelRes = "irvine/resources/playfair6.model";
    } else if (flags.isSet(SEVEN_BY_FOUR_FLAG)) {
      alphabet = SEVEN_BY_FOUR_ALPHABET;
      width = 4;
      height = 7;
      modelRes = "irvine/resources/playfair7x4.model";
    } else {
      alphabet = FIVE_ALPHABET_NO_J;
      width = 5;
      height = 5;
      modelRes = "irvine/resources/playfair5.model";
    }

    final Entropy model;
    try {
      if (flags.isSet(CommonFlags.MODEL_FLAG)) {
        model = FourGramAlphabetModel.loadModel((String) flags.getValue(CommonFlags.MODEL_FLAG));
      } else {
        model = FourGramAlphabetModel.loadModelResource(modelRes);
      }
    } catch (final IOException e) {
      throw new RuntimeException("Could to load entropy model.", e);
    }

    final int hypotheses = (Integer) flags.getValue(RETAIN_FLAG);
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      final PlayfairSolver cracker = new PlayfairSolver(out, model, alphabet, hypotheses, width, height);
      if (flags.isSet(SEED_FLAG)) {
        cracker.setSeed((Long) flags.getValue(SEED_FLAG));
      }
      try (final BufferedReader r = CommonFlags.getInput(flags)) {
        final String ciphertext = PlayfairSolver.clean(alphabet, IOUtils.readAll(r));
        cracker.percolate(ciphertext, (Integer) flags.getValue(ITERATIONS_FLAG), (String) flags.getValue(KEYS_FLAG));
      } catch (final IOException e) {
        throw new RuntimeException("Problem reading ciphertext.", e);
      }
    }
  }

}
