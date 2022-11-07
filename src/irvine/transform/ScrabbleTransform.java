package irvine.transform;

/**
 * Report the Scrabble score for each word.
 * @author Sean A. Irvine
 */
public class ScrabbleTransform implements Transform {

  /** Raw score for each letter in Scrabble, A through Z. */
  private static final int[] SCRABBLE_SCORE = {
    1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10
  };

  @Override
  public String getName() {
    return "SCRABBLE";
  }

  @Override
  public String apply(final String word) {
    int score = 0;
    if (word != null) {
      for (int i = 0; i < word.length(); ++i) {
        final char d;
        if ((d = Character.toLowerCase(word.charAt(i))) >= 'a' && d <= 'z') {
          score += SCRABBLE_SCORE[d - 'a'];
        }
      }
    }
    return String.valueOf(score);
  }
}
