package irvine.entropy;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import irvine.jilt.CommonFlags;
import irvine.util.CliFlags;
import irvine.util.IOUtils;
import irvine.util.LongDynamicLongArray;
import irvine.util.StringUtils;

/**
 * Reduced alphabet character gram model.
 * @author Sean A. Irvine
 */
public class NGramModel implements Entropy, Serializable {

  private static final String ORDER_FLAG = "order";
  private static final int ROOT = 2;
  private static final int SPACE = 27;
  private static final int DIGIT = 28;
  private static final int ALPHABET_SIZE = 28;
  private static final long serialVersionUID = 1L;

  /*
   * Alphabet encoding: 0 unused (not stored)
   *                    1-26 A-Z
   *                    27 whitespace
   *                    28 digit
   */

  private final int mOrder;

  /**
   * Tree consists of pairs of longs.  The first long of the pair is
   * the count for the current node, and the second long points to
   * the index of the first child of the node (and the next 2&times;
   * <code>ALPHABET_SIZE</code> entries are pointers for the children).
   * Node 0 is unused. Root is at 2.
   */
  private final LongDynamicLongArray mModel = new LongDynamicLongArray();
  private final int[] mContext;
  private long mNextFree = 2 * ALPHABET_SIZE + 2;
  private long mOrder0Total = 0;

  /**
   * Construct a new word gram model.
   * @param order order of model
   * @exception IllegalArgumentException if <code>order</code> is negative.
   */
  public NGramModel(final int order) {
    if (order < 0) {
      throw new IllegalArgumentException();
    }
    mOrder = order;
    mContext = new int[mOrder];
  }

  /**
   * Serialize the current model.
   * @param filename output filename
   * @throws IOException if an I/O error occurs
   */
  void saveModel(final String filename) throws IOException {
    try (final ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filename)))) {
      oos.writeObject(this);
    }
  }

  private static NGramModel loadModel(final InputStream stream) throws IOException {
    try (final ObjectInputStream ois = new ObjectInputStream(stream)) {
      return (NGramModel) ois.readObject();
    } catch (final ClassNotFoundException e) {
      throw new RuntimeException("Incompatible model file", e);
    }
  }

  /**
   * Load a model.
   * @param filename file name of the model
   * @return the model
   * @exception IOException if an I/O error occurs
   */
  public static NGramModel loadModel(final String filename) throws IOException {
    try (final InputStream is = new GZIPInputStream(IOUtils.getStream(filename))) {
      return loadModel(is);
    }
  }

  private long childPtr(final long current, final int symbol) {
    return mModel.get(current + 2L * symbol - 1);
  }

  private long count(final long current, final int symbol) {
    return mModel.get(current + 2L * symbol - 2);
  }

  private void addAtOrder(final int order) {
    final int offset = mOrder - 1 - order;
    if (mContext[offset] != 0) {
      long position = ROOT;
      for (int j = offset; j < mOrder - 1; ++j) {
        final long child = childPtr(position, mContext[j]);
        if (child == 0) {
          // No such child, create space and link new child.
          mModel.set(position + 2L * mContext[j] - 1, mNextFree);
          position = mNextFree;
          mNextFree += 2 * ALPHABET_SIZE;
        } else {
          position = child;
        }
      }
      // Increment count
      final int s = mContext[mOrder - 1];
      final long nPos = position + 2L * s - 2;
      mModel.set(nPos, mModel.get(nPos) + 1);
    }
  }

  private void shiftAndInsert(final int[] a, final int s) {
    System.arraycopy(a, 1, a, 0, a.length - 1);
    a[a.length - 1] = s;
  }

  private void addSymbol(final int symbol) {
    shiftAndInsert(mContext, symbol);
    ++mOrder0Total;
    for (int k = 0; k < mOrder; ++k) {
      addAtOrder(k);
    }
  }

  private int clean(final int s) {
    if (s >= 'A' && s <= 'Z') {
      return s - 'A' + 1;
    } else if (s >= 'a' && s <= 'z') {
      return s - 'a' + 1;
    } else if (s >= '0' && s <= '9') {
      return DIGIT;
    } else if (Character.isWhitespace(s)) {
      return SPACE;
    } else if (s == 0) {
      return 0;
    } else {
      return -1;
    }
  }

  /**
   * Add contents of given stream into the model.
   * @param in input stream
   * @exception IOException if an I/O error occurs
   */
  public void add(final InputStream in) throws IOException {
    long s = 0;
    try (BufferedInputStream is = new BufferedInputStream(in)) {
      int c;
      boolean lastWasSpace = true;
      while ((c = is.read()) != -1) {
        final int v = clean(c);
        if (v > 0 && (v != SPACE || !lastWasSpace)) {
          addSymbol(v);
          lastWasSpace = v == SPACE;
        }
        if (++s % 100000000 == 0) {
          StringUtils.message("Processed " + s + " characters of input");
        }
      }
    }
  }

  private long findContext(final int[] context, final int start) {
    long position = ROOT;
    for (int k = start; k < context.length - 1; ++k) {
      final long next = childPtr(position, context[k]);
      if (next == 0) {
        return 0;
      }
      position = next;
    }
    return position;
  }

  private double entropy(final int[] context, final int start, final boolean[] exclusions) {
    if (start >= context.length || context[context.length - 1] == 0) {
      // Zeroth order prediction
      return Math.log(mOrder0Total + 1);
    }
    int p = context.length - 1;
    while (p > start && context[p] != 0) {
      --p;
    }
    for (int k = p; k < context.length; ++k) {
      final long parent = findContext(context, k);
      if (parent != 0) {
        final long cc = count(parent, context[context.length - 1]);
        long t = 0;
        for (int j = 1; j <= ALPHABET_SIZE; ++j) {
          if (!exclusions[j - 1]) {
            t += count(parent, j);
          }
        }
        if (cc != 0) {
          return Math.log(t + 1) - Math.log(cc);
        } else {
          for (int j = 0; j < ALPHABET_SIZE; ++j) {
            exclusions[j] |= count(parent, j + 1) != 0;
          }
          return Math.log(t + 1) + entropy(context, start + 1, exclusions);
        }
      }
    }
    return Math.log(ALPHABET_SIZE + 1);
  }

  @Override
  public double entropy(final String text) {
    final int[] context = new int[mOrder];
    double e = 0;
    boolean lastWasSpace = true;
    for (int k = 0; k < text.length(); ++k) {
      // After cleaning length can be zero, word will then be penalized
      // as an unknown word, but this is probably the right thing to do.
      final int w = clean(text.charAt(k));
      if (w != SPACE || !lastWasSpace) {
        shiftAndInsert(context, w);
        e += entropy(context, 0, new boolean[ALPHABET_SIZE]);
        lastWasSpace = w == SPACE;
      }
    }
    return e;
  }

  /**
   * Entropy via a word gram model.  Build a model using the supplied files,
   * then score each line of text on standard input.
   * @param args source files
   * @exception IOException if an I/O error occurs
   */
  public static void main(final String[] args) throws IOException {
    final CliFlags flags = new CliFlags("Build a character based entropy model");
    flags.registerRequired('o', CommonFlags.OUTPUT_FLAG, String.class, "FILE", "where to write the model");
    flags.registerOptional('i', CommonFlags.INPUT_FLAG, String.class, "FILE", "existing model to add content to");
    flags.registerOptional('O', ORDER_FLAG, Integer.class, "INT", "order of model to build", 4);
    flags.setValidator(f -> {
      if (flags.isSet(CommonFlags.INPUT_FLAG) && flags.isSet(ORDER_FLAG)) {
        flags.setParseMessage("Do not specify both existing model and order.");
        return false;
      }
      return true;
    });
    flags.setFlags(args);
    final NGramModel model;
    if (flags.isSet(CommonFlags.INPUT_FLAG)) {
      model = loadModel((String) flags.getValue(CommonFlags.INPUT_FLAG));
    } else {
      final int order = (Integer) flags.getValue(ORDER_FLAG);
      model = new NGramModel(order);
    }
    StringUtils.message("Adding input to order " + model.mOrder + " model");
    try (final InputStream fis = new BufferedInputStream(System.in)) {
      model.add(fis);
    }
    final String output = (String) flags.getValue(CommonFlags.OUTPUT_FLAG);
    StringUtils.message("Saving model to " + output);
    model.saveModel(output);
  }
}
