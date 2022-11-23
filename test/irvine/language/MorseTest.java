package irvine.language;

import irvine.StandardIoTestCase;

/**
 * Tests the corresponding class.
 * @author Sean A. Irvine
 */
public class MorseTest extends StandardIoTestCase {

  public void testEncode() {
    final Morse morse = new Morse();
    assertEquals("... --- ...", morse.morseEncode("sos"));
    assertEquals("... --- ...", morse.morseEncode("SOS"));
    assertEquals("... --- ...", morse.morseEncode("S{}O[]^S"));
    assertEquals(".- -... -.-. -.. . ..-. --. .... .. .--- -.- .-.. -- -. --- .--. --.- .-. ... - ..- ...- .-- -..- -.-- --.. ----- .---- ..--- ...-- ....- ..... -.... --... ---.. ----.", morse.morseEncode("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"));
    assertEquals("-... .. --.  -.-. .- -", morse.morseEncode("big cat"));
  }

  public void testSimpleDecode() {
    final Morse morse = new Morse();
    assertEquals("SOS", morse.morseDecode("... --- ..."));
    assertEquals("BIG CAT", morse.morseDecode("-... .. --.  -.-. .- -"));
    assertEquals("BIG CAT", morse.morseDecode("  -... .. --.  -.-. .- - "));
  }

  public void testHardDecode1() {
    final Morse morse = new Morse();
    morse.morseHardDecode(System.out, null, 10, true, "-.....--.-.-..--");
    assertTrue(getOut(), getOut().contains("BIG CAT"));
  }

  public void testHardDecode2() {
    final Morse morse = new Morse();
    morse.morseHardDecode(System.out, null, 10, true, "-.....--. -.-..--");
    assertTrue(getOut(), getOut().contains("BIG CAT"));
  }
}
