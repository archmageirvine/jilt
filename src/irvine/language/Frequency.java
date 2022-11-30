package irvine.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.util.CliFlags;

/**
 * Perform simple character based frequency analysis.
 * @author Sean A. Irvine
 */
public final class Frequency extends Command {

  /** Construct the module. */
  public Frequency() {
    super("Frequency analysis");
  }

  private static final String IGNORE_CASE_FLAG = "ignore-case";
  private static final String TWENTY_SIX_FLAG = "26";
  private static final String SIZE_FLAG = "size";
  private static final String STEP_FLAG = "step";

  private static int read(final BufferedReader r, final boolean caseFold, final boolean english) throws IOException {
    while (true) {
      final int c = r.read();
      if (c < 0) {
        return -1;
      }
      final char t = caseFold || english ? Character.toLowerCase((char) c) : (char) c;
      if (!english || (t >= 'a' && t <= 'z')) {
        return t;
      }
    }
  }

  private static String protect(final String s) {
    final StringBuilder sb = new StringBuilder();
    for (int k = 0; k < s.length(); ++k) {
      final char c = s.charAt(k);
      if (c == ' ') {
        sb.append("[ ]");
      } else if (c == '\t') {
        sb.append("\\t");
      } else if (c == '\n') {
        sb.append("\\n");
      } else if (Character.isISOControl(c) || Character.isWhitespace(c)) {
        sb.append("[").append((int) c).append("]");
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
   * Compute frequency based on standard input.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    CommonFlags.registerInputFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    flags.registerOptional('n', SIZE_FLAG, Integer.class, "INT", "n-gram size", 1);
    flags.registerOptional('s', STEP_FLAG, Integer.class, "INT", "step size", 1);
    flags.registerOptional(TWENTY_SIX_FLAG, "only consider letters (forces ignoring of case)");
    flags.registerOptional('I', IGNORE_CASE_FLAG, "ignore case");
    flags.setValidator(f -> CommonFlags.validateInput(f)
      && CommonFlags.validateOutput(f)
      && CommonFlags.checkPositive(f, SIZE_FLAG)
      && CommonFlags.checkPositive(f, STEP_FLAG));
    flags.setFlags(args);

    final int size = (Integer) flags.getValue(SIZE_FLAG);
    final int step = (Integer) flags.getValue(STEP_FLAG);
    final boolean caseFold = flags.isSet(IGNORE_CASE_FLAG);
    final boolean english = flags.isSet(TWENTY_SIX_FLAG);

    final HashMap<String, Long> freq = new HashMap<>();
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      try (final BufferedReader r = CommonFlags.getInput(flags)) {
        final char[] buf = new char[size];
        for (int k = 0; k < buf.length; ++k) {
          final int c = read(r, caseFold, english);
          if (c < 0) {
            return; // insufficient input for even a single n-gram
          }
          buf[k] = (char) c;
        }
        outer:
        while (true) {
          freq.merge(new String(buf), 1L, Long::sum);
          if (step == size) {
           // This case only for efficiency
            for (int k = 0; k < buf.length; ++k) {
              final int c = read(r, caseFold, english);
              if (c < 0) {
                break outer;
              }
              buf[k] = (char) c;
            }
          } else {
            for (int k = 0; k < step; ++k) {
              System.arraycopy(buf, 1, buf, 0, buf.length - 1);
              final int c = read(r, caseFold, english);
              if (c < 0) {
                break outer;
              }
              buf[buf.length - 1] = (char) c;
            }
          }
        }
      } catch (final IOException e) {
        throw new RuntimeException("Problem reading input.", e);
      }
      // Swap to tree map for sorted output
      // todo we could provide more options controlling the style of output
      // todo in non-26 mode this could do with some handling of control characters etc.
      for (final Map.Entry<String, Long> e : new TreeMap<>(freq).entrySet()) {
        out.println(protect(e.getKey()) + " " + e.getValue());
      }
    }
  }
}
