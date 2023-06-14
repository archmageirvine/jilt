package irvine.wordsmith;

/**
 * Check for specific alphabets.
 * @author Sean A. Irvine
 */
public class AlphabetInspector implements Inspector {

  private boolean is(final String alphabet, final String word) {
    for (int k = 0; k < word.length(); ++k) {
      if (alphabet.indexOf(word.charAt(k)) < 0) {
        return false;
      }
    }
    return true;
  }

  private void check(final String name, final String alphabet, final StringBuilder sb, final String... words) {
    for (final String w : words) {
      if (!is(alphabet, w)) {
        return;
      }
    }
    sb.append("Every word can be written using ").append(name);
  }

  @Override
  public String inspect(final String... words) {
    if (words.length < 3) {
      return null;
    }
    final StringBuilder sb = new StringBuilder();
    check("top row of keyboard", "QWERTYUIOP", sb, words);
    check("middle row of keyboard", "ASDFGHJKL", sb, words);
    check("bottom row of keyboard", "ZXCVBNM", sb, words);
    check("left side of keyboard", "QWERTASDFGZXCVB", sb, words);
    check("right side of keyboard", "YUIOPHJKLNM", sb, words);
    check("first half of the alphabet", "ABCDEFGHIJKLM", sb, words);
    check("second half of the alphabet", "NOPQRSTUVWXYZ", sb, words);
    return sb.length() == 0 ? null : sb.toString();
  }

}
