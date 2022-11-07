package irvine.transform;

import java.util.HashMap;
import java.util.Map;

/**
 * Report the number of closed loops in the input.
 * @author Sean A. Irvine
 */
public class LoopsTransform implements Transform {

  private static final Map<Character, Integer> LOOPS = new HashMap<>();
  static {
    LOOPS.put('A', 1);
    LOOPS.put('B', 2);
    LOOPS.put('C', 0);
    LOOPS.put('D', 1);
    LOOPS.put('E', 0);
    LOOPS.put('F', 0);
    LOOPS.put('G', 0);
    LOOPS.put('H', 0);
    LOOPS.put('I', 0);
    LOOPS.put('J', 0);
    LOOPS.put('K', 0);
    LOOPS.put('L', 0);
    LOOPS.put('M', 0);
    LOOPS.put('N', 0);
    LOOPS.put('O', 1);
    LOOPS.put('P', 1);
    LOOPS.put('Q', 1);
    LOOPS.put('R', 1);
    LOOPS.put('S', 0);
    LOOPS.put('T', 0);
    LOOPS.put('U', 0);
    LOOPS.put('V', 0);
    LOOPS.put('W', 0);
    LOOPS.put('X', 0);
    LOOPS.put('Y', 0);
    LOOPS.put('Z', 0);
    LOOPS.put('a', 1);
    LOOPS.put('b', 1);
    LOOPS.put('c', 0);
    LOOPS.put('d', 1);
    LOOPS.put('e', 1);
    LOOPS.put('f', 0);
    LOOPS.put('g', 2);
    LOOPS.put('h', 0);
    LOOPS.put('i', 0);
    LOOPS.put('j', 0);
    LOOPS.put('k', 0);
    LOOPS.put('l', 0);
    LOOPS.put('m', 0);
    LOOPS.put('n', 0);
    LOOPS.put('o', 1);
    LOOPS.put('p', 1);
    LOOPS.put('q', 1);
    LOOPS.put('r', 0);
    LOOPS.put('s', 0);
    LOOPS.put('t', 0);
    LOOPS.put('u', 0);
    LOOPS.put('v', 0);
    LOOPS.put('w', 0);
    LOOPS.put('x', 0);
    LOOPS.put('y', 0);
    LOOPS.put('z', 0);
    LOOPS.put('0', 1);
    LOOPS.put('1', 0);
    LOOPS.put('2', 0);
    LOOPS.put('3', 0);
    LOOPS.put('4', 1);
    LOOPS.put('5', 0);
    LOOPS.put('6', 1);
    LOOPS.put('7', 0);
    LOOPS.put('8', 2);
    LOOPS.put('9', 1);
    LOOPS.put(' ', 0);
    LOOPS.put('.', 0);
    LOOPS.put(',', 0);
    LOOPS.put(';', 0);
    LOOPS.put('-', 0);
    LOOPS.put('?', 0);
    LOOPS.put('\'', 0);
  }

  @Override
  public String getName() {
    return "LOOPS";
  }

  @Override
  public String apply(final String s) {
    int cnt = 0;
    for (int k = 0; k < s.length(); ++k) {
      final char c = s.charAt(k);
      final Integer v = LOOPS.get(c);
      if (v == null) {
        throw new IllegalArgumentException("Character " + c + " unsupported in " + s);
      }
      cnt += v;
    }
    return String.valueOf(cnt);
  }
}
