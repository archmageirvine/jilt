package irvine.jilt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Locale;

import irvine.language.Anagram;
import irvine.util.CliFlags;
import irvine.util.IOUtils;
import irvine.util.StringUtils;

/**
 * Flags shared by multiple modules.
 * @author Sean A. Irvine
 */
public final class CommonFlags {

  private CommonFlags() {
  }

  /** Name of the dictionary flag. */
  public static final String DICTIONARY_FLAG = "dictionary";

  /** Register the dictionary flag. */
  public static CliFlags.Flag<String> registerDictionaryFlag(final CliFlags flags) {
    return flags.registerOptional('D', DICTIONARY_FLAG, String.class, "FILE", "override the default dictionary (with \"-\" for stdin)");
  }

  /** Validation for dictionary flag. */
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

  /** Register the output flag. */
  public static CliFlags.Flag<String> registerOutputFlag(final CliFlags flags) {
    return flags.registerOptional('o', OUTPUT_FLAG, String.class, "FILE", "where to write output (with \"-\" for stdout)", "-");
  }

  /** Validation for output flag. */
  public static boolean validateOutput(final CliFlags flags) {
    final String out = (String) flags.getValue(OUTPUT_FLAG);
    if (!"-".equals(out) && !new File(out).canWrite()) {
      flags.setParseMessage("Specified output file \"" + out + "\" is not writeable.");
      return false;
    }
    return true;
  }

  /** Get the output stream based on the flags. */
  public static PrintStream getOutput(final CliFlags flags) {
    final String out = (String) flags.getValue(OUTPUT_FLAG);
    try {
      return "-".equals(out) ? System.out : new PrintStream(out);
    } catch (final FileNotFoundException e) {
      throw new RuntimeException("Could not write output to \"" + out + "\".", e);
    }
  }

  /** Output file name. */
  public static final String INPUT_FLAG = "input";

  /** Register the input flag. */
  public static CliFlags.Flag<String> registerInputFlag(final CliFlags flags) {
    return flags.registerOptional('i', INPUT_FLAG, String.class, "FILE", "where to read from (with \"-\" for stdin)", "-");
  }

  /** Validation for input flag. */
  public static boolean validateInput(final CliFlags flags) {
    final String out = (String) flags.getValue(INPUT_FLAG);
    if (!"-".equals(out) && !new File(out).canRead()) {
      flags.setParseMessage("Specified input file \"" + out + "\" is not readable.");
      return false;
    }
    return true;
  }

  /** Get the output stream based on the flags. */
  public static BufferedReader getInput(final CliFlags flags) {
    final String in = (String) flags.getValue(INPUT_FLAG);
    try {
      return IOUtils.getReader(in);
    } catch (final IOException e) {
      throw new RuntimeException("Could not read from \"" + in + "\")");
    }
  }
}
