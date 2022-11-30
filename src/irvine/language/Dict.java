package irvine.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.jilt.Dictionary;
import irvine.util.CliFlags;

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
    flags.setValidator(f -> CommonFlags.validateDictionary(f)
      && CommonFlags.validateOutput(f));
    flags.setFlags(args);

    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      try (final BufferedReader reader = Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG))) {
        String line;
        while ((line = reader.readLine()) != null) {
          out.println(line);
        }
      } catch (final IOException e) {
        throw new RuntimeException("Problem reading dictionary", e);
      }
    }
  }
}
