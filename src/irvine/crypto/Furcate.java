package irvine.crypto;

import irvine.entropy.Entropy;
import irvine.util.LimitedLengthPriorityQueue;

/**
 * Split messages into streams based on position in the message.
 * That is, a kind of demultiplexing.
 *
 * @author Sean A. Irvine
 */
public class Furcate {

  private final Entropy mModel;

  /**
   * Construct a new rearrangement.
   * @param model the model
   */
  public Furcate(final Entropy model) {
    mModel = model;
  }

  /**
   * Attempt to split the message and rearrange.
   * @param message message to decrypt
   * @param maxResults maximum number of final results to retain
   * @return best results
   */
  public LimitedLengthPriorityQueue<String> furcate(final String message, final int maxResults) {
    final LimitedLengthPriorityQueue<String> q = new LimitedLengthPriorityQueue<>(maxResults, false);
    for (int k = 2; k <= message.length() / 2; ++k) {
      if (message.length() % k == 0) {
        final StringBuilder[] components = new StringBuilder[k];
        for (int j = 0; j < k; ++j) {
          components[j] = new StringBuilder();
        }
        for (int j = 0; j < message.length(); j += k) {
          for (int i = 0; i < k; ++i) {
            components[i].append(message.charAt(j + i));
          }
        }
        final StringBuilder sb = new StringBuilder();
        for (final StringBuilder h : components) {
          sb.append(h);
        }
        final String s = sb.toString();
        q.add(mModel.entropy(s), s);
      }
    }
    final Autospace spacer = new Autospace(mModel);
    final LimitedLengthPriorityQueue<String> answer = new LimitedLengthPriorityQueue<>(maxResults, false);
    for (final LimitedLengthPriorityQueue.Node<String> res : q) {
      final String unspaced = res.getValue();
      final LimitedLengthPriorityQueue<String> spaced = spacer.autospace(unspaced, 1, 20);
      answer.add(res.getScore(), spaced.iterator().next().getValue());
    }
    return answer;
  }
}
