package irvine.language;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.util.CliFlags;
import irvine.util.LongUtils;
import irvine.util.Permutation;

/**
 * Solve letter equation problems.
 * @author Sean A. Irvine
 */
public final class Equation extends Command {

  // RFE:
  //   - more operations -, /
  //   - support for constant numbers 3 * ABC, etc.
  //   -

  private static final String EQUALS = "=";
  private static final String ADD = "+";
  private static final String MULTIPLY = "*";
  private static final String POW = "^";

  private static final class Node {
    private final String mS;
    private final Node mLeft;
    private final Node mRight;

    private Node(final String s, final Node left, final Node right) {
      mS = s;
      mLeft = left;
      mRight = right;
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      if (mLeft != null) {
        sb.append(mLeft);
      }
      sb.append(mS);
      if (mRight != null) {
        sb.append(mRight);
      }
      return sb.toString();
    }
  }

  private Node parse(final String s) {
    // todo this is pretty minimal at the moment!
    final int eq = s.indexOf(EQUALS);
    if (eq >= 0) {
      if (s.indexOf(EQUALS, eq + 1) >= 0) {
        throw new IllegalArgumentException("Multiple \"=\" in single equation");
      }
      return new Node(EQUALS, parse(s.substring(0, eq).trim()), parse(s.substring(eq + 1).trim()));
    }
    // The order of operations matters here!
    final int add = s.indexOf(ADD);
    if (add >= 0) {
      return new Node(ADD, parse(s.substring(0, add).trim()), parse(s.substring(add + 1).trim()));
    }
    final int multiply = s.indexOf(MULTIPLY);
    if (multiply >= 0) {
      return new Node(MULTIPLY, parse(s.substring(0, multiply).trim()), parse(s.substring(multiply + 1).trim()));
    }
    final int pow = s.indexOf(POW);
    if (pow >= 0) {
      return new Node(POW, parse(s.substring(0, pow).trim()), parse(s.substring(pow + 1).trim()));
    }
    return new Node(s, null, null);
  }

  /** Construct the module. */
  public Equation() {
    super("Solve letter equation problems");
  }

  /**
   * Convert letters in the equations into a numerical code.
   * @param equations problem equations
   * @return code for permutation positions
   */
  private static Map<Character, Integer> toCode(final Collection<String> equations) {
    final HashMap<Character, Integer> code = new HashMap<>();
    int v = -1;
    for (final String str : equations) {
      for (int k = 0; k < str.length(); ++k) {
        final char c = str.charAt(k);
        if (Character.isLetter(c)) {
          final Integer d = code.get(c);
          if (d == null) {
            if (++v > 9) {
              return null;
            }
            code.put(c, v);
          }
        }
      }
    }
    return code;
  }

  private static final class InconsistentException extends Exception { }

  private long eval(final Node equation, final Map<Character, Integer> code, final int[] p) throws InconsistentException {
    // todo more operations
    // Note: there is no overflow check here which could be a problem!
    switch (equation.mS) {
      case EQUALS:
        // Note this uses 0 to indicate equation was consistent!
        return eval(equation.mLeft, code, p) == eval(equation.mRight, code, p) ? 0 : 1;
      case ADD:
        return eval(equation.mLeft, code, p) + eval(equation.mRight, code, p);
      case MULTIPLY:
        return eval(equation.mLeft, code, p) * eval(equation.mRight, code, p);
      case POW:
        return LongUtils.pow(eval(equation.mLeft, code, p), eval(equation.mRight, code, p));
      default:
        // Assume a literal
        if (p[code.get(equation.mS.charAt(0))] == 0) {
          throw new InconsistentException();
        }
        long v = 0;
        for (int k = 0; k < equation.mS.length(); ++k) {
          v *= 10;
          v += p[code.get(equation.mS.charAt(k))];
        }
        return v;
    }
  }

  private boolean isConsistent(final Node equation, final Map<Character, Integer> code, final int[] p) {
    try {
      return eval(equation, code, p) == 0;
    } catch (final InconsistentException e) {
      return false;
    }
  }

  private boolean isConsistent(final Collection<Node> equations, final Map<Character, Integer> code, final int[] p) {
    for (final Node equation : equations) {
      if (!isConsistent(equation, code, p)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Letter equations.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("Solve equations where each letter denotes a digit and no digit is represented by more than one letter. For example \"XXX + B = BAAAA\" has the solution \"999 + 1 = 1000\".");
    CommonFlags.registerOutputFlag(flags);
    final CliFlags.Flag<String> textFlag = flags.registerRequired(String.class, "EQN", "equation.");
    textFlag.setMaxCount(Integer.MAX_VALUE);
    flags.setValidator(f -> {
      if (!CommonFlags.validateOutput(f)) {
        return false;
      }
      return true;
    });
    flags.setFlags(args);
    // todo validate equations

    @SuppressWarnings("unchecked")
    final Collection<String> equations = (Collection<String>) flags.getAnonymousValues(0);
    final Map<Character, Integer> code = toCode(equations);
    if (code == null) {
      System.err.println("Equations contain more than 10 symbols");
      return;
    }
    final Collection<Node> parsedEquations = new ArrayList<>();
    for (final String eqn : equations) {
      parsedEquations.add(parse(eqn.trim()));
    }

    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      // There are only 10! = 3628800 possible assignments of digits.
      // We simply try them all, even though sometimes better strategies would exist.
      final Permutation permutation = new Permutation(10);
      final HashSet<String> solutions = new HashSet<>();
      int[] p;
      while ((p = permutation.next()) != null) {
        if (isConsistent(parsedEquations, code, p)) {
          final StringBuilder sb = new StringBuilder();
          for (final Map.Entry<Character, Integer> e : code.entrySet()) {
            if (sb.length() > 0) {
              sb.append(", ");
            }
            sb.append(e.getKey()).append(" = ").append(p[e.getValue()]);
          }
          final String soln = sb.toString();
          // With less than 10 variables the same solution can be found more than once.
          // The following set makes sure we only print such solutions once.
          if (solutions.add(soln)) {
            out.println(soln + ".");
            // todo should individual equations be printed
          }
        }
      }
      if (solutions.isEmpty()) {
        out.println("No solution found.");
      }
    }
  }
}
