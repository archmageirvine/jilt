package irvine.entropy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Compute entropy via word grams.
 * @author Sean A. Irvine
 */
public class WordGramModel implements Entropy {

  private static final int LONGEST_SENSIBLE_WORD = 30;

  private final int mOrder;
  private final Node mRoot = new Node();
  private final String[] mContext;

  private static class Node {
    long mCount = 0;
    HashMap<String, Node> mChildren = null;
  }


  /**
   * Construct a new word gram model.
   *
   * @param order order of model
   * @exception IllegalArgumentException if <code>order</code> is negative.
   */
  public WordGramModel(final int order) {
    if (order < 0) {
      throw new IllegalArgumentException();
    }
    mOrder = order;
    mContext = new String[mOrder];
  }

  private void add(final int order) {
    final int offset = mOrder - 1 - order;
    if (mContext[offset] != null) {
      Node position = mRoot;
      // Initial scan down is guaranteed to find nodes
      for (int j = offset; j < mOrder - 1; ++j) {
        position = position.mChildren.get(mContext[j]);
      }
      if (position.mChildren == null) {
        position.mChildren = new HashMap<>();
      }
      final String s = mContext[mOrder - 1];
      Node n = position.mChildren.get(s);
      if (n == null) {
        n = new Node();
        n.mCount = 1;
        position.mChildren.put(s, n);
      } else {
        n.mCount++;
      }
    }
  }

  private void shiftAndInsert(final String[] a, final String s) {
    System.arraycopy(a, 1, a, 0, a.length - 1);
    a[a.length - 1] = s;
  }

  private void add(final String word) {
    shiftAndInsert(mContext, word);
    mRoot.mCount++; // increase zeroth order count
    for (int k = 0; k < mOrder; ++k) {
      add(k);
    }
  }

  private String clean(final String s) {
    if (s.length() > LONGEST_SENSIBLE_WORD) {
      return "";
    }
    // Strip to 27 letter English
    final StringBuilder sb = new StringBuilder();
    int bad = 0;
    for (int k = 0; k < s.length(); ++k) {
      final char c = Character.toUpperCase(s.charAt(k));
      if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
        sb.append(c);
      } else if (++bad == 2) {
        return "";
      }
    }
    return sb.toString();
  }

  void add(final InputStream in) throws IOException {
    try (final BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
      String line;
      while ((line = r.readLine()) != null) {
        for (final String word : line.split("\\s+")) {
          final String cw = clean(word);
          if (!cw.isEmpty()) {
            add(cw);
            //            System.out.println("Adding: ." + word + "." + " context: " + Arrays.toString(mContext));
            //            dump("_ROOT_", mRoot, "");
          }
        }
      }
    }
  }

//  private void dump(final String w, final Node n, final String indent) {
//    if (n == null) {
//      return;
//    }
//    System.out.println(indent + w + " (" + n.mCount + ")");
//    if (n.mChildren != null) {
//      final String id2 = indent + "  ";
//      for (final Map.Entry<String, Node> e : n.mChildren.entrySet()) {
//        dump(e.getKey(), e.getValue(), id2);
//      }
//    }
//  }

  private Node findContext(final String[] context, final int start) {
    Node position = mRoot;
    for (int k = start; k < context.length - 1; ++k) {
      final Node next = position.mChildren.get(context[k]);
      if (next == null) {
        return null;
      }
      position = next;
    }
    return position;
  }

  private double entropy(final String[] context, final int start) {
    if (start >= context.length) {
      // Zeroth order prediction
      //System.out.println("Not found: " + context[context.length - 1]);
      return Math.log(mRoot.mCount + 1);
    }
    int p = start;
    while (context[p] == null) {
      ++p;
    }
    for (int k = p; k < context.length; ++k) {
      final Node parent = findContext(context, k);
      if (parent != null) {
        final Node child = parent.mChildren.get(context[context.length - 1]);
        if (child != null) {
          return Math.log(parent.mCount + 1) - Math.log(child.mCount);
        } else {
          return Math.log(parent.mCount + 1) + entropy(context, start + 1);
        }
      }
    }
    return Double.NaN; // zero freq?
  }

  @Override
  public double entropy(final String text) {
    final String[] context = new String[mOrder];
    double e = 0;
    for (final String word : text.split("\\s+")) {
      if (!word.isEmpty()) {
        // After cleaning length can be zero, word will then be penalized
        // as an unknown word, but this is probably the right thing to do.
        final String w = clean(word);
        shiftAndInsert(context, w);
        e += entropy(context, 0);
      }
    }
    return e;
  }

  /**
   * Entropy via a word gram model.
   *
   * @param args source files
   * @exception IOException if an I/O error occurs
   */
  public static void main(final String[] args) throws IOException {
    // TODO usage
    final WordGramModel model = new WordGramModel(2);
    for (final String f : args) {
      System.err.println("Adding: " + f);
      try (final FileInputStream fis = new FileInputStream(f)) {
        model.add(fis);
      }
    }
    //model.dump("ROOT", model.mRoot, "");
    try (final BufferedReader r = new BufferedReader(new InputStreamReader(System.in))) {
      String line;
      while ((line = r.readLine()) != null) {
        System.out.println(model.entropy(line) + " " + line);
      }
    }
  }
}
