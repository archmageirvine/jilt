package irvine.crypto;

import java.io.IOException;

import irvine.entropy.Entropy;
import irvine.entropy.FourGramAlphabetModel;
import irvine.util.IOUtils;
import irvine.util.LimitedLengthPriorityQueue;


/**
 * Attempt to automatically insert spaces into text to improve readability.
 * Most useful for post-processing cryptograms without spaces.
 *
 * @author Sean A. Irvine
 */
public class Autospace {

  private static final Autospace DEFAULT;
  static {
    try {
      DEFAULT = new Autospace(FourGramAlphabetModel.loadModel());
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Convenience method to automatically insert spaces into a text string.
   * @param text string to put spaces in
   * @return string with spaces in
   */
  public static String autospace(final String text) {
    return DEFAULT.autospace(text, 1, 100).first().getValue();
  }

  private final Entropy mModel;

  /**
   * Construct a new spacer.
   *
   * @param model the model
   */
  public Autospace(final Entropy model) {
    mModel = model;
  }

  /**
   * Attempt to insert spaces.
   * @param message message to decrypt
   * @param maxResults maximum number of final results to retain
   * @param maxIntermediate maximum number of intermediate results to consider
   * @return best results
   */
  public LimitedLengthPriorityQueue<String> autospace(final String message, final int maxResults, final int maxIntermediate) {
    LimitedLengthPriorityQueue<String> q = new LimitedLengthPriorityQueue<>(maxIntermediate, false);
    double bestKnown = mModel.entropy(message);
    q.add(bestKnown, message);
    boolean improvmentDetected;
    do {
      improvmentDetected = false;
      final LimitedLengthPriorityQueue<String> newQ = new LimitedLengthPriorityQueue<>(maxIntermediate, false);
      for (final LimitedLengthPriorityQueue.Node<String> node : q) {
        final String s = node.getValue();
        for (int k = 1; k < s.length() - 1; ++k) {
          final String t = s.substring(0, k) + " " + s.substring(k);
          final double e = mModel.entropy(t);
          newQ.add(e, t);
          if (e < bestKnown) {
            System.out.println("New best: " + Math.round(e) + " " + t);
            improvmentDetected = true;
            bestKnown = e;
          }
        }
      }
      if (improvmentDetected) {
        q = newQ;
      }
    } while (improvmentDetected);

    // This reduction to final answer could be made more efficient...
    final LimitedLengthPriorityQueue<String> res = new LimitedLengthPriorityQueue<>(maxResults, false);
    for (final LimitedLengthPriorityQueue.Node<String> node : q) {
      res.add(node.getScore(), node.getValue());
    }
    return res;
  }

  /**
   * Insert spaces into standard input.
   * @param args ignored
   * @throws IOException if an I/O error occurs.
   */
  public static void main(final String[] args) throws IOException {
    System.out.println(autospace(IOUtils.readAll(System.in)));
  }
}
