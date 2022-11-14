package irvine.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 *
 * @author Sean A. Irvine
 */
public class HamadPlayfairTest extends TestCase {

  public void testEncodeToDna() {
    final HamadPlayfair playfair = new HamadPlayfair();
    final byte[] message = "Meet me @ 3:30".getBytes(StandardCharsets.US_ASCII);
    assertEquals("CATCCGCCCGCCCTCAAGAACGTCCGCCAGAACAAAAGAAATATATGGATATATAA", playfair.toDna(message));
    assertTrue(Arrays.equals(message, playfair.toMessage("CATCCGCCCGCCCTCAAGAACGTCCGCCAGAACAAAAGAAATATATGGATATATAA")));
    //System.out.println(playfair.toDna("EgyRev@25Jan".getBytes()));
  }

}
