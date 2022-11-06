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
 * Generate Caesar shifts.
 * @author Sean A. Irvine
 */
public final class Caesar extends Command {

  /** Construct the module. */
  public Caesar() {
    super("Caesar shifts");
  }

  static String caesarShift(final String text, final int shift) {
    if (shift < 0 || shift > 25) {
      throw new IllegalArgumentException();
    }
    // Try to be somewhat smart about characters
    final StringBuilder sb = new StringBuilder();
    for (int j = 0; j < text.length(); ++j) {
      final char c = text.charAt(j);
      if (!Character.isLetter(c)) {
        sb.append(c);
      } else {
        final int g = c + shift;
        if (Character.isLowerCase(c)) {
          sb.append((char) (g > 'z' ? g - 26 : g));
        } else {
          sb.append((char) (g > 'Z' ? g - 26 : g));
        }
      }
    }
    return sb.toString();
  }

  private static final String SHIFT_FLAG = "shift";

  /**
   * Caesar shifts.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("Can read from a stream or a command line string.");
    CommonFlags.registerDictionaryFlag(flags);
    CommonFlags.registerInputFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    flags.registerOptional('s', SHIFT_FLAG, Integer.class, "INT", "specific shift");
    final CliFlags.Flag<String> textFlag = flags.registerRequired(String.class, "TEXT", "text to generate Caesar shifts of");
    textFlag.setMinCount(0);
    flags.setValidator(f -> {
      if (!CommonFlags.validateInput(f)) {
        return false;
      }
      if (!CommonFlags.validateOutput(f)) {
        return false;
      }
      if (f.isSet(SHIFT_FLAG)) {
        final int shift = (Integer) f.getValue(SHIFT_FLAG);
        if (shift < 0 || shift > 25) {
          f.setParseMessage("Shift must be in range 0..25");
          return false;
        }
      }
      return true;
    });
    flags.setFlags(args);

    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      if (textFlag.isSet()) {
        final String text = (String) flags.getAnonymousValue(0);
        if (flags.isSet(SHIFT_FLAG)) {
          out.println(caesarShift(text, (Integer) flags.getValue(SHIFT_FLAG)));
        } else {
          for (int shift = 0; shift < 26; ++shift) {
            out.println(caesarShift(text, shift));
          }
        }
      } else {
        try (final BufferedReader reader = CommonFlags.getInput(flags)) {
          String line;
          while ((line = reader.readLine()) != null) {
            if (flags.isSet(SHIFT_FLAG)) {
              out.println(caesarShift(line, (Integer) flags.getValue(SHIFT_FLAG)));
            } else {
              for (int shift = 0; shift < 26; ++shift) {
                out.println(caesarShift(line, shift));
              }
            }
          }
        } catch (IOException e) {
          throw new RuntimeException("Problem reading input", e);
        }
      }
    }
  }
}
