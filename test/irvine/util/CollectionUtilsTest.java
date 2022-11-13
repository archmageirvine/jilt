package irvine.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class CollectionUtilsTest extends TestCase {

  public void testInvert() {
    final Map<String, Integer> map = Collections.singletonMap("hi", 42);
    assertEquals("{42=hi}", CollectionUtils.invert(map).toString());
  }

  public void testInvertIllegal() {
    final Map<String, Integer> map = new HashMap<>();
    map.put("hi", 42);
    map.put("hix", 42);
    try {
      CollectionUtils.invert(map);
      fail();
    } catch (final IllegalArgumentException e) {
      // ok
    }
  }
}
