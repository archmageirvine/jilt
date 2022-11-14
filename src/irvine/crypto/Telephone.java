package irvine.crypto;

import irvine.entropy.Entropy;
import irvine.entropy.FourGramAlphabetModel;
import irvine.util.IOUtils;
import irvine.util.LimitedLengthPriorityQueue;

/**
 * Attempt to decode a telephone sequence.
 *
 * @author Sean A. Irvine
 */
public final class Telephone {

  private Telephone() { }

  private static final String[] TELEPHONE = {"abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};

  /**
   * Attempt to interpret a sequence of numbers as words typed on a
   * telephone with a standard keyboard. It will also try to infer
   * spaces between words.
   * @param m model
   * @param maxResults maximum results to retain
   * @param text text to process
   * @return putative decodes
   */
  public static LimitedLengthPriorityQueue<String> telephoneCode(final Entropy m, final int maxResults, final String text) {
    LimitedLengthPriorityQueue<String> best = new LimitedLengthPriorityQueue<>(maxResults, false);
    best.add(0.0, "");
    final boolean hasSpaces = text.contains(" ");
    for (int k = 0; k < text.length(); ++k) {
      final char c = text.charAt(k);
      final LimitedLengthPriorityQueue<String> next = new LimitedLengthPriorityQueue<>(maxResults, false);
      for (final LimitedLengthPriorityQueue.Node<String> n : best) {
        if (c >= '2' && c <= '9') {
          final String p = TELEPHONE[c - '2'];
          for (int j = 0; j < p.length(); ++j) {
            final String tt = n.getValue() + p.charAt(j);
            next.add(m.entropy(tt), tt);
            if (!hasSpaces) {
              final String ss = tt + " ";
              next.add(m.entropy(ss), ss);
            }
          }
        } else {
          final String tt = n.getValue() + c;
          next.add(m.entropy(tt), tt);
        }
      }
      best = next;
    }
    return best;
  }

  /**
   * Main program. Arguments ignored.
   * @param args ignored
   * @throws Exception if an error occurs.
   */
  public static void main(final String[] args) throws Exception {
    final String text = IOUtils.readAll(System.in).trim();
    final Entropy m = FourGramAlphabetModel.loadModel();
    int k = 0;
    for (final LimitedLengthPriorityQueue.Node<String> r : telephoneCode(m, 10000, text)) {
      System.out.println(Math.round(r.getScore()) + " " + r.getValue());
      if (++k == 20) {
        break;
      }
    }
  }
}
