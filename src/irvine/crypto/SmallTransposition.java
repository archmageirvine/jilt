package irvine.crypto;

import java.io.File;

import irvine.entropy.Entropy;
import irvine.entropy.FourGramAlphabetModel;
import irvine.util.CliFlags;
import irvine.util.CliFlags.Flag;
import irvine.util.IOUtils;
import irvine.util.LimitedLengthPriorityQueue;
import irvine.util.Permutation;

/**
 * Attempt to decrypt a putative transposition of small order.  Works by
 * exhaustively trying all possible permutations.
 *
 * @author Sean A. Irvine
 */
public class SmallTransposition {

  /** Underlying language model. */
  private final Entropy mModel;

  /**
   * Construct a new cryptogram solver.
   *
   * @param model the model
   */
  public SmallTransposition(final Entropy model) {
    mModel = model;
  }

  /**
   * Attempt cryptanalysis of message using a transposition of specified size
   * by exhaustively computing all possible transpositions.
   * @param message message to decrypt
   * @param size putative size of transposition block
   * @param maxResults maximum number of results to retain
   * @return best results
   */
  public LimitedLengthPriorityQueue<String> solve(final String message, final int size, final int maxResults) {
    if (size < 1) {
      throw new IllegalArgumentException();
    }
    if (message.length() % size != 0) {
      throw new IllegalArgumentException("Message length not a multiple of size");
    }
    final LimitedLengthPriorityQueue<String> q = new LimitedLengthPriorityQueue<>(maxResults, false);
    final Permutation permuter = new Permutation(size);
    int[] p;
    while ((p = permuter.next()) != null) {
      final StringBuilder plaintext = new StringBuilder();
      for (int k = 0; k < message.length(); k += size) {
        for (int j = 0; j < size; ++j) {
          plaintext.append(message.charAt(k + p[j]));
        }
      }
      final String plain = plaintext.toString();
      q.add(mModel.entropy(plain), plain);
    }
    return q;
  }

  /**
   * Solve a simple transposition.
   * @param model language model
   * @param text cipher
   * @param maxOrder maximum order of transposition
   * @param maxResults maximum number of results to return
   * @param verbose print extra progress information
   * @return list of potential decryptions
   */
  public static LimitedLengthPriorityQueue<String> solve(final Entropy model, final String text, final int maxOrder, final int maxResults, final boolean verbose) {
    final SmallTransposition transpo = new SmallTransposition(model);
    final Autospace spacer = new Autospace(model);

    final String cipertext = text.trim().replace(" ", "");

    // Try and solve at each order dividing the cryptogram length
    final LimitedLengthPriorityQueue<String> overall = new LimitedLengthPriorityQueue<>(maxResults, false);
    for (int order = 1; order <= maxOrder; ++order) {
      if (cipertext.length() % order == 0) {
        if (verbose) {
          System.out.println("Considering order " + order);
        }
        for (final LimitedLengthPriorityQueue.Node<String> res : transpo.solve(cipertext, order, maxResults)) {
          overall.add(res.getScore(), res.getValue());
        }
      }
    }

    final LimitedLengthPriorityQueue<String> answer = new LimitedLengthPriorityQueue<>(maxResults, false);
    for (final LimitedLengthPriorityQueue.Node<String> res : overall) {
      final String unspaced = res.getValue();
      final LimitedLengthPriorityQueue.Node<String> spaced = spacer.autospace(unspaced, 1, 10).iterator().next();
      answer.add(spaced.getScore(), spaced.getValue());
    }
    return answer;
  }

  /**
   * Run the small transposition solver.
   *
   * @param args see help message
   * @exception Exception if an error occurs
   */
  public static void main(final String... args) throws Exception {
    final CliFlags flags = new CliFlags("SmallTransposition");
    final Flag<?> resultsFlag = flags.registerOptional('r', "results", Integer.class, "int", "Maximum number of answers to print.", 5);
    final Flag<?> orderFlag = flags.registerOptional("order", Integer.class, "int", "Maximum order to consider.", 9);
    final Flag<?> modelFlag = flags.registerOptional('m', "model", String.class, "model", "Model file.");
    final Flag<File> textFlag = flags.registerRequired(File.class, "cryptogram", "Filename of cryptogram.");

    flags.setFlags(args);

    final Entropy model;
    if (modelFlag.isSet()) {
      model = FourGramAlphabetModel.loadModel((String) modelFlag.getValue());
     } else {
      model = FourGramAlphabetModel.loadModel();
    }

    final String cipertext = IOUtils.readAll(textFlag.getValue()).trim().replace(" ", "");
    final int maxOrder = (Integer) orderFlag.getValue();
    final int maxResults = (Integer) resultsFlag.getValue();

    // Print the results in order and try and insert spacing to make it readable
    for (final LimitedLengthPriorityQueue.Node<String> res : solve(model, cipertext, maxOrder, maxResults, true)) {
      System.out.println(Math.round(res.getScore()) + " " + res.getValue());
    }
  }

}
