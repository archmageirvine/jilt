package irvine.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Integer utilities.
 * @author Sean A. Irvine
 */
public class CollectionUtils {

  private CollectionUtils() { }

  /**
   * Invert a map.
   * @param map map to invert
   * @param <V> key type
   * @param <K> value type
   * @return inverted map
   */
  public static <V, K> Map<V, K> invert(final Map<K, V> map) {
    final Map<V, K> inv = new HashMap<>(map.size());
    for (final Map.Entry<K, V> entry : map.entrySet()) {
      final V value = entry.getValue();
      if (inv.put(value, entry.getKey()) != null) {
        throw new IllegalArgumentException("Map is not invertible");
      }
    }
    return inv;
  }
}
