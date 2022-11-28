package irvine.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.util.CliFlags;

/**
 * Command for encoding and decoding Playfair ciphers with a known key.
 * @author Sean A. Irvine
 */
public class PlayfairCommand extends Command {

  private static final String KEY_FLAG = "key";
  private static final String DECODE_FLAG = "decode";
  private static final String DUMMY_FLAG = "dummy";
  private static final String NO_Q_FLAG = "no-q";
  private static final String SIX_FLAG = "6x6";
  private static final String SEVEN_BY_FOUR_FLAG = "7x4";
  private static final String EIGHT_FLAG = "8x8";
  private static final String SCD_FLAG = "scd";

  /**
   * The command for encoding and decoding Playfair with a known key.
   */
  public PlayfairCommand() {
    super("Encode and decode Playfair ciphers with a known key");
  }

  private static final class PlayfairFlagsValidator implements CliFlags.Validator {

    @Override
    public boolean isValid(final CliFlags flags) {
      if (!CommonFlags.validateInput(flags)) {
        return false;
      }
      if (!CommonFlags.validateOutput(flags)) {
        return false;
      }
      int c = 0;
      if (flags.isSet(SIX_FLAG)) {
        ++c;
      }
      if (flags.isSet(SEVEN_BY_FOUR_FLAG)) {
        ++c;
      }
      if (flags.isSet(EIGHT_FLAG)) {
        ++c;
      }
      if (c > 1) {
        flags.setParseMessage("At most one of --" + SIX_FLAG + ", --" + SEVEN_BY_FOUR_FLAG + ", and --" + EIGHT_FLAG + " can be set.");
        return false;
      }
      if (flags.isSet(DUMMY_FLAG) && flags.isSet(SEVEN_BY_FOUR_FLAG)) {
        flags.setParseMessage("There is no user selected dummy with 7x4.");
        return false;
      }
      if (flags.isSet(NO_Q_FLAG) && (flags.isSet(SEVEN_BY_FOUR_FLAG) || flags.isSet(SIX_FLAG))) {
        flags.setParseMessage("No Q doesn't make sense for grids larger than 5x5.");
        return false;
      }
      return true;
    }
  }

  /**
   * Main program for Playfair related ciphers.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("See also \"playmate\" for cracking Playfair ciphers");
    CommonFlags.registerInputFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    flags.registerRequired('k', KEY_FLAG, String.class, "key", "the key text");
    flags.registerOptional('d', DECODE_FLAG, "decode the message (default is to encode)");
    flags.registerOptional(DUMMY_FLAG, Character.class, "letter", "letter to use as the dummy", 'X');
    flags.registerOptional(NO_Q_FLAG, "use the variant with no Q rather than combining I and J");
    flags.registerOptional('6', SIX_FLAG, "use 6x6 Playfair");
    flags.registerOptional(SEVEN_BY_FOUR_FLAG, "use 7x4 Alam-Khalid-Salam Playfair");
    flags.registerOptional(EIGHT_FLAG, "use 8x8 Playfair");
    flags.registerOptional(SCD_FLAG, "use the Shrivastava-Chouhan-Dhawan key schedule (not recommended)");
    flags.setValidator(new PlayfairFlagsValidator());
    flags.setFlags(args);
    final String key = (String) flags.getValue(KEY_FLAG);

    final int width;
    final int height;
    final char dummy;
    final char padding;
    if (flags.isSet(SIX_FLAG)) {
      width = 6;
      height = 6;
      dummy = (Character) flags.getValue(DUMMY_FLAG);
      padding = dummy;
    } else if (flags.isSet(SEVEN_BY_FOUR_FLAG)) {
      width = 4;
      height = 7;
      dummy = '*';
      padding = '#';
    } else if (flags.isSet(EIGHT_FLAG)) {
      width = 8;
      height = 8;
      dummy = '*';
      padding = '#';
    } else {
      width = 5;
      height = 5;
      dummy = (Character) flags.getValue(DUMMY_FLAG);
      padding = dummy;
    }

    final Playfair playfair = new Playfair(key, height, width, dummy, padding, flags.isSet(NO_Q_FLAG), false);
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      try (final BufferedReader r = CommonFlags.getInput(flags)) {
        out.println(playfair.transform(r, !flags.isSet(DECODE_FLAG)));
      } catch (final IOException e) {
        throw new RuntimeException("Problem reading input.", e);
      }
    }
  }
}
