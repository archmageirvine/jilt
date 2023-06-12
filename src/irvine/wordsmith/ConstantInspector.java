package irvine.wordsmith;

/**
 * Check if the list is constant.
 * @author Sean A. Irvine
 */
public class ConstantInspector implements Inspector {

  @Override
  public String inspect(final String... words) {
    final String w0 = words[0];
    for (final String w : words) {
      if (!w.equals(w0)) {
        return null;
      }
    }
    return "Every word is: " + w0;
  }
}
