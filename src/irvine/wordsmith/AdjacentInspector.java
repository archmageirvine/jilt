package irvine.wordsmith;

import java.util.HashMap;
import java.util.Map;

/**
 * Check if the words are all composed of adjacent keyboard letters
 * @author Sean A. Irvine
 */
public class AdjacentInspector implements Inspector {

  private static final Map<Character, String> ADJACENT = new HashMap<>();
  static {
    ADJACENT.put('A', "QWSZ");
    ADJACENT.put('B', "VGHN");
    ADJACENT.put('C', "XDFV");
    ADJACENT.put('D', "SERFCX");
    ADJACENT.put('E', "W34RDS");
    ADJACENT.put('F', "DRTGVC");
    ADJACENT.put('G', "FTYHBV");
    ADJACENT.put('H', "GYUJNB");
    ADJACENT.put('I', "U89OKJ");
    ADJACENT.put('J', "HUIKMN");
    ADJACENT.put('K', "JIOL,M");
    ADJACENT.put('L', "KOP;.,");
    ADJACENT.put('M', "NJK,");
    ADJACENT.put('N', "BHJM");
    ADJACENT.put('O', "I90PLK");
    ADJACENT.put('P', "O0-[;L");
    ADJACENT.put('Q', "12WA");
    ADJACENT.put('R', "E45TFD");
    ADJACENT.put('S', "AWEDXZ");
    ADJACENT.put('T', "R56YGF");
    ADJACENT.put('U', "Y78IJH");
    ADJACENT.put('V', "CFGB");
    ADJACENT.put('W', "Q23ESA");
    ADJACENT.put('X', "ZSDC");
    ADJACENT.put('Y', "T67UHG");
    ADJACENT.put('Z', "ASX");
    ADJACENT.put('0', "9OP-");
    ADJACENT.put('1', "Q2`");
    ADJACENT.put('2', "1QW3");
    ADJACENT.put('3', "2WE4");
    ADJACENT.put('4', "3ER5");
    ADJACENT.put('5', "4RT6");
    ADJACENT.put('6', "5TY7");
    ADJACENT.put('7', "6YU8");
    ADJACENT.put('8', "7UI9");
    ADJACENT.put('9', "8IO0");

  }

  private boolean is(final String word) {
    for (int k = 1; k < word.length(); ++k) {
      final char a = Character.toUpperCase(word.charAt(k - 1));
      final char b = Character.toUpperCase(word.charAt(k));
      if (ADJACENT.get(a).indexOf(b) == -1) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String inspect(final String... words) {
    for (final String w : words) {
      if (!is(w)) {
        return null;
      }
    }
    return "All words can be typed with adjacent letters on the keyboard";
  }
}
