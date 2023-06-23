package irvine.associator;

/**
 * Holds a word and score.
 * @author Sean A. Irvine
 */
public class QueryState implements Comparable<QueryState> {

  private final int mWordIndex;
  private final float mWeight;

  QueryState(final int wordIndex, final float weight) {
    mWordIndex = wordIndex;
    mWeight = weight;
  }

  public int getWordIndex() {
    return mWordIndex;
  }

  public float getWeight() {
    return mWeight;
  }

  @Override
  public int compareTo(final QueryState queryState) {
    final int c = Float.compare(mWeight, queryState.mWeight);
    if (c != 0) {
      return -c;
    }
    return Integer.compare(mWordIndex, queryState.mWordIndex);
  }
}
