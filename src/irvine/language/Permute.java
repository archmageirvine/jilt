package irvine.language;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.util.CliFlags;
import irvine.util.CollectionUtils;
import irvine.util.Permutation;

/**
 * Generate permutations.
 * @author Sean A. Irvine
 */
public final class Permute extends Command {

  /** Construct the module. */
  public Permute() {
    super("Generate permutations");
  }

  private static Map<Character, Integer> toCode(final String str) {
    final HashMap<Character, Integer> code = new HashMap<>();
    int v = -1;
    for (int k = 0; k < str.length(); ++k) {
      final char c = str.charAt(k);
      final Integer d = code.get(c);
      if (d == null) {
        code.put(c, ++v);
      }
    }
    return code;
  }

  /**
   * Letter equations.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    CommonFlags.registerOutputFlag(flags);
    flags.registerRequired(String.class, "STRING", "string to generate permutations of");
    flags.setValidator(f -> {
      if (!CommonFlags.validateOutput(f)) {
        return false;
      }
      return true;
    });
    flags.setFlags(args);
    final String s = (String) flags.getAnonymousValue(0);
    final Map<Character, Integer> code = toCode(s);
    final int[] init = new int[s.length()];
    for (int k = 0; k < s.length(); ++k) {
      init[k] = code.get(s.charAt(k));
    }
    final Map<Integer, Character> inverse = CollectionUtils.invert(code);
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      final Permutation perm = new Permutation(init);
      int[] p;
      while ((p = perm.next()) != null) {
        final StringBuilder sb = new StringBuilder();
        for (final int v : p) {
          sb.append(inverse.get(v));
        }
        out.println(sb);
      }
    }
  }
}
