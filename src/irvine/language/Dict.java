package irvine.language;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.jilt.Dictionary;
import irvine.util.Casing;
import irvine.util.CliFlags;
import irvine.util.StringUtils;

/**
 * Command to stream the dictionary.
 * @author Sean A. Irvine
 */
public final class Dict extends Command {

  /** Construct the module. */
  public Dict() {
    super("Output the dictionary");
  }

  /**
   * Stream the dictionary.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    CommonFlags.registerDictionaryFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    flags.setValidator(f -> {
      if (!CommonFlags.validateDictionary(f)) {
        return false;
      }
      if (!CommonFlags.validateOutput(f)) {
        return false;
      }
      return true;
    });
    flags.setFlags(args);

    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      try (final BufferedReader reader = Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG))) {
        String line;
        while ((line = reader.readLine()) != null) {
          out.println(line);
        }
      } catch (final IOException e) {
        throw new RuntimeException("I/O problem with dictionary", e);
      }
    }
  }
}
