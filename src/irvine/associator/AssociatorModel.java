package irvine.associator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
  private final DynamicArray<Set<Integer>> mAssociates = new DynamicArray<>();

  private int getOrCreateIndex(final String word) {
    final int index = mWordToIndex.computeIfAbsent(word, v -> mWordToIndex.size());
    if (index >= mIndexToWord.length()) {
      if (index % 100000 == 0) {
        System.out.println("Index now has " + index + " items");
      }
      mIndexToWord.set(index, word);
    }
    return index;
  }

  private void update(final int a, final int b) {
    final Set<Integer> v = mAssociates.get(a);
    if (v == null) {
      final Set<Integer> set = new HashSet<>();
      set.add(b);
      mAssociates.set(a, set);
      return;
    }
    v.add(b);
  }

  /**
   * Add an association between two words.
   * @param a first word
   * @param b second word
   */
  public void add(final String a, final String b) {
    if (a.equals(b)) {
      return; // Ignore self associations
    }
    final int u = getOrCreateIndex(a);
    final int v = getOrCreateIndex(b);
    update(u, v);
    update(v, u);
  }

  private static final float SCALE_FACTOR = 0.5F;
  private static final int ITERATIONS = 10;

  private void merge(final int maxResults, final TreeSet<QueryState> state, final Map<Integer, Float> current) {
    for (final Map.Entry<Integer, Float> q : current.entrySet()) {
      if (state.size() < maxResults) {
        state.add(new QueryState(q.getKey(), q.getValue()));
      }
      if (q.getValue() > state.last().getWeight()) {
        state.add(new QueryState(q.getKey(), q.getValue()));
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
    final HashMap<Integer, Float> total = new HashMap<>();
    for (final String w : words) {
      final Integer index = mWordToIndex.get(w);
      if (index != null) {
        total.put(index, 1F);
      }
    }
    HashMap<Integer, Float> next = new HashMap<>(total);
    float scale = 1;
    for (int k = 0; k < ITERATIONS; ++k) {
      final Map<Integer, Float> current = next;
      //final Map<Integer, Float> current = new HashMap<>(total);
      next = new HashMap<>();
      for (final Map.Entry<Integer, Float> q : current.entrySet()) {
        final Set<Integer> v = mAssociates.get(q.getKey());
        //final float w = scale * q.getValue();
        final float w = scale * q.getValue() / v.size();
        //final float w = scale / v.size();
        for (final int key : v) {
          //final float x = w * e.getValue();
          //final float x = w * e.getValue() / mPackedVectors.get(key).size();
          final float x = w; // / mPackedVectors.get(key).size();
          final float u = total.merge(key, x, Float::sum);
          //next.merge(key, x, Float::sum);
          next.put(key, u);
//          if (mIndexToWord.get(key).equals("CARRIE FISHER")) {
//            System.err.println(k + " saw CARRIE FISHER with weight " + x + " total " + total.get(key));
//          }
        }
      }
      scale *= SCALE_FACTOR;
//      if (k <= 0) {
//        for (final Map.Entry<Integer, Float> e : total.entrySet()) {
//          System.out.println("   " + k + " " + mIndexToWord.get(e.getKey()) + " " + e.getValue());
//        }
//      }
    }
    final TreeSet<QueryState> sorted = new TreeSet<>();
    merge(maxResults, sorted, total);
    final LinkedHashMap<String, Float> res = new LinkedHashMap<>();
    for (final QueryState q : sorted) {
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
