package irvine.crypto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import junit.framework.TestCase;

/**
 * Tests the corresponding class.
 *
 * @author Sean A. Irvine
 */
public class PlayfairTest extends TestCase {

  public void testEncode() throws IOException {
    final Playfair playfair = new Playfair("RANDOMKEY", 5, 5, 'X', 'X', false, false);
    try (final ByteArrayInputStream in = new ByteArrayInputStream("this is a message".getBytes(StandardCharsets.US_ASCII))) {
      assertEquals("SIHTHTRKYQPDQG", playfair.transform(in, true));
    }
  }

  public void testDecode() throws IOException {
    final Playfair playfair = new Playfair("RANDOMKEY", 5, 5, 'X', 'X', false, false);
    try (final ByteArrayInputStream in = new ByteArrayInputStream("SIHTHTRKYQPDQG".getBytes(StandardCharsets.US_ASCII))) {
      assertEquals("THISISAMESSAGE", playfair.transform(in, false));
    }
  }

  public void test7x4() throws IOException {
    final Playfair playfair = new Playfair("CIPHER", 7, 4, '*', '#', false, false);
    assertEquals("CIPHERABDFGJKLMNOQSTUVWXYZ*#", playfair.getKey());
    try (final ByteArrayInputStream in = new ByteArrayInputStream("LOVE ALL PEOPLE".getBytes(StandardCharsets.US_ASCII))) {
      assertEquals("KQURRMMIDUIMBY", playfair.transform(in, true));
    }
    try (final ByteArrayInputStream in = new ByteArrayInputStream("BALLOON".getBytes(StandardCharsets.US_ASCII))) {
      assertEquals("EBMZKQTK", playfair.transform(in, true));
    }
    try (final ByteArrayInputStream in = new ByteArrayInputStream("EBMZKQTK".getBytes(StandardCharsets.US_ASCII))) {
      assertEquals("BALLOON", playfair.transform(in, false));
    }
  }

  public void test8x8() throws IOException {
    final Playfair playfair = new Playfair("JON_465@GMAIL.COM", 8, 8, '*', '#', false, true);
    try (final ByteArrayInputStream in = new ByteArrayInputStream("A-143, City Center, Noida, India".getBytes(StandardCharsets.US_ASCII))) {
      assertEquals("M$7O7#BLUZMQOUFD$4_MFG#LJFLI", playfair.transform(in, true));
    }
    try (final ByteArrayInputStream in = new ByteArrayInputStream("M$7O7#BLUZMQOUFD$4_MFG#LJFLI".getBytes(StandardCharsets.US_ASCII))) {
      assertEquals("A-143,CITYCENTER,NOIDA,INDIA", playfair.transform(in, false));
    }
  }
}
