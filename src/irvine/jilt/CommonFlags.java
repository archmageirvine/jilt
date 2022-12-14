package irvine.jilt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import irvine.entropy.Entropy;
import irvine.entropy.FourGramAlphabetModel;
import irvine.util.CliFlags;
import irvine.util.IOUtils;

/**
 * Flags shared by multiple modules.
 * @author Sean A. Irvine
 */
public final class CommonFlags {

  private CommonFlags() {
  }

  /** Name of the dictionary flag. */
  public static final String DICTIONARY_FLAG = "dictionary";

  /**
   * Register the dictionary flag.
   * @param flags where to register
   * @return the flag
   */
  public static CliFlags.Flag<String> registerDictionaryFlag(final CliFlags flags) {
    return flags.registerOptional('D', DICTIONARY_FLAG, String.class, "FILE", "override the default dictionary (with \"-\" for stdin)");
  }

  /**
   * Validation for the dictionary flag.
   * @param flags source of flags
   * @return status
   */
  public static boolean validateDictionary(final CliFlags flags) {
    if (flags.isSet(DICTIONARY_FLAG)) {
      final String dict = (String) flags.getValue(DICTIONARY_FLAG);
      if (!"-".equals(dict) && !new File(dict).canRead()) {
        flags.setParseMessage("Specified dictionary file \"" + dict + "\" is not readable.");
        return false;
      }
    }
    return true;
  }

  /** Output file name. */
  public static final String OUTPUT_FLAG = "output";

  /**
   * Register the output flag.
   * @param flags where to register
   * @return the flag
   */
  public static CliFlags.Flag<String> registerOutputFlag(final CliFlags flags) {
    return flags.registerOptional('o', OUTPUT_FLAG, String.class, "FILE", "where to write output (with \"-\" for stdout)", "-");
  }

  /**
   * Validation for the output flag.
   * @param flags source of flags
   * @return status
   */
  public static boolean validateOutput(final CliFlags flags) {
    final String out = (String) flags.getValue(OUTPUT_FLAG);
    if (!"-".equals(out) && !new File(out).canWrite()) {
      flags.setParseMessage("Specified output file \"" + out + "\" is not writeable.");
      return false;
    }
    return true;
  }

  /**
   * Get the output stream.
   * @param flags source of flags
   * @return output stream
   */
  public static PrintStream getOutput(final CliFlags flags) {
    final String out = (String) flags.getValue(OUTPUT_FLAG);
    try {
      return "-".equals(out) ? System.out : new PrintStream(out);
    } catch (final FileNotFoundException e) {
      throw new RuntimeException("Could not write output to \"" + out + "\".", e);
    }
  }

  /** Input file name. */
  public static final String INPUT_FLAG = "input";

  /**
   * Register the input flag.
   * @param flags where to register
   * @return the flag
   */
  public static CliFlags.Flag<String> registerInputFlag(final CliFlags flags) {
    return flags.registerOptional('i', INPUT_FLAG, String.class, "FILE", "where to read from (with \"-\" for stdin)", "-");
  }

  /**
   * Validation for the input flag.
   * @param flags source of flags
   * @return status
   */
  public static boolean validateInput(final CliFlags flags) {
    final String out = (String) flags.getValue(INPUT_FLAG);
    if (!"-".equals(out) && !new File(out).canRead()) {
      flags.setParseMessage("Specified input file \"" + out + "\" is not readable.");
      return false;
    }
    return true;
  }

  /**
   * Get the input stream.
   * @param flags source of flags
   * @return input stream
   */
  public static BufferedReader getInput(final CliFlags flags) {
    final String in = (String) flags.getValue(INPUT_FLAG);
    try {
      return IOUtils.getReader(in);
    } catch (final IOException e) {
      throw new RuntimeException("Could not read from \"" + in + "\")");
    }
  }

  /** Model file name. */
  public static final String MODEL_FLAG = "model";

  /**
   * Register the model flag.
   * @param flags where to register
   * @return the flag
   */
  public static CliFlags.Flag<String> registerModelFlag(final CliFlags flags) {
    return flags.registerOptional('m', MODEL_FLAG, String.class, "FILE", "path to entropy model file");
  }

  /**
   * Validation for the model flag.
   * @param flags source of flags
   * @return status
   */
  public static boolean validateModel(final CliFlags flags) {
    if (flags.isSet(MODEL_FLAG)) {
      final String model = (String) flags.getValue(MODEL_FLAG);
      if (!"-".equals(model) && !new File(model).canRead()) {
        flags.setParseMessage("Specified model file \"" + model + "\" is not readable.");
        return false;
      }
    }
    return true;
  }

  /**
   * Get the model.
   * @param flags source of flags
   * @return entropy model
   */
  public static Entropy getEntropyModel(final CliFlags flags) {
    try {
      if (flags.isSet(MODEL_FLAG)) {
        return FourGramAlphabetModel.loadModel((String) flags.getValue(MODEL_FLAG));
      } else {
        return FourGramAlphabetModel.loadModel();
      }
    } catch (final IOException e) {
      throw new RuntimeException("Could to load entropy model.", e);
    }
  }

  /**
   * Check that the specified flag (if set) is positive.
   * @param flags flags object
   * @param flag specific flag name
   * @return true iff the check passes
   */
  public static boolean checkPositive(final CliFlags flags, final String flag) {
    if (flags.isSet(flag) && (Integer) flags.getValue(flag) < 1) {
      flags.setParseMessage("--" + flag + " should be positive.");
      return false;
    }
    return true;
  }
}
