package irvine.associator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import irvine.util.DynamicArray;

/**
 * Hold a model suitable for use with the associator.
 * @author Sean A. Irvine
 */
public class AssociatorModel implements Serializable {

  private static final long serialVersionUID = 123456789L;
  private static final String DEFAULT_MODEL = "irvine/resources/associator.model";

  /**
   * Load a model.
   * @param stream containing the model
   * @return the model
   * @exception IOException if an I/O error occurs
   */
  public static AssociatorModel loadModel(final InputStream stream) throws IOException {
    try (final ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(stream))) {
      return (AssociatorModel) ois.readObject();
    } catch (final ClassNotFoundException e) {
      throw new RuntimeException("Incompatible model file", e);
    }
  }

  /**
   * Load a model.
   * @param resource the Java package location of the model
   * @return the model
   * @exception IOException if an I/O error occurs
   */
  public static AssociatorModel loadModelResource(final String resource) throws IOException {
    try (final InputStream is = Objects.requireNonNull(AssociatorModel.class.getClassLoader().getResourceAsStream(resource))) {
      return loadModel(is);
    }
  }

  /**
   * Load the default model.
   * @return the model
   * @exception IOException if an I/O error occurs
   */
  public static AssociatorModel loadModel() throws IOException {
    return loadModelResource(DEFAULT_MODEL);
  }

  // Map words to an integer index.
  private final Map<String, Integer> mWordToIndex = new HashMap<>();
  private final DynamicArray<String> mIndexToWord = new DynamicArray<>();
  // Pairs of associations.  Each word has a packed integer array containing
  // known associations. The integer array consists of pairs with even indices
  // corresponding to the integer index of the association, and odd indices
  // corresponding to the weight (stored as raw bits of the float).
  private final DynamicArray<int[]> mPackedVectors = new DynamicArray<>();

  private int getOrCreateIndex(final String word) {
    final int index = mWordToIndex.computeIfAbsent(word, v -> mWordToIndex.size());
    if (index >= mIndexToWord.length()) {
      mIndexToWord.set(index, word);
    }
    return index;
  }

  private void update(final int a, final int b, final float weight) {
    final int[] v = mPackedVectors.get(a);
    if (v == null) {
      mPackedVectors.set(a, new int[] {b, Float.floatToRawIntBits(weight)});
      return;
    }
    // Check if association already exists
    for (int k = 0; k < v.length; k += 2) {
      if (v[k] == b) {
        // Strengthen existing connection
        v[k + 1] = Float.floatToRawIntBits(Float.intBitsToFloat(v[k + 1]) + weight);
        return;
      }
    }
    // Otherwise, lengthen the array to handle the new association.
    final int[] longer = Arrays.copyOf(v, v.length + 2);
    longer[v.length] = b;
    longer[v.length + 1] = Float.floatToRawIntBits(weight);
    mPackedVectors.set(a, longer);
  }

  /**
   * Add an association between two words.
   * @param a first word
   * @param b second word
   * @param weight weight of connection
   */
  public void add(final String a, final String b, final float weight) {
    if (a.equals(b)) {
      return; // Ignore self associations
    }
    final int u = getOrCreateIndex(a);
    final int v = getOrCreateIndex(b);
    update(u, v, weight);
    update(v, u, weight);
  }

  /**
   * Add an association between two words with weight 1.
   * @param a first word
   * @param b second word
   */
  public void add(final String a, final String b) {
    add(a, b, 1.0F);
  }

  private static final float SCALE_FACTOR = 0.5F;
  private static final int ITERATIONS = 10;

  private void merge(final int maxResults, final TreeSet<QueryState> state, final TreeSet<QueryState> current) {
    for (final QueryState q : current) {
      if (state.size() < maxResults) {
        state.add(q);
      }
      if (q.getWeight() > state.last().getWeight()) {
        state.add(q);
        state.pollLast();
      }
    }
  }

  /**
   * Perform a query for the given words.
   * @param maxResults maximum number of results to return
   * @param words query terms
   * @return associated words
   */
  public Map<String, Float> query(final int maxResults, final String... words) {
    final HashSet<Integer> seen = new HashSet<>();
    final TreeSet<QueryState> state = new TreeSet<>();
    final TreeSet<QueryState> current = new TreeSet<>();
    for (final String w : words) {
      final Integer index = mWordToIndex.get(w);
      if (index != null) {
        seen.add(index);
        current.add(new QueryState(index, 1F));
      }
    }
    state.addAll(current);
    float scale = 1;
    for (int k = 0; k < ITERATIONS; ++k) {
      final HashMap<Integer, Float> next = new HashMap<>();
      for (final QueryState q : current) {
        final int[] v = mPackedVectors.get(q.getWordIndex());
        final float w = q.getWeight();
        for (int j = 0; j < v.length; j += 2) {
          if (!seen.contains(v[j])) {
            final float x = w * scale;
            next.merge(v[j], x, Float::sum);
          }
        }
      }
      current.clear();
      for (final Map.Entry<Integer, Float> e : next.entrySet()) {
        current.add(new QueryState(e.getKey(), e.getValue()));
        seen.add(e.getKey());
      }
      merge(maxResults, state, current);
      scale *= SCALE_FACTOR;
    }
    final LinkedHashMap<String, Float> res = new LinkedHashMap<>();
    for (final QueryState q : state) {
      res.put(mIndexToWord.get(q.getWordIndex()), q.getWeight());
    }
    return res;
  }

  /**
   * Serialize the current model.
   * @param filename output filename
   * @throws IOException if an I/O error occurs
   */
  public void saveModel(final String filename) throws IOException {
    try (final ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filename)))) {
      oos.writeObject(this);
    }
  }
}
