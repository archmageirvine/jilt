package irvine.jilt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import irvine.transform.AddTransform;
import irvine.transform.IdentityTransform;
import irvine.transform.LetterTransform;
import irvine.transform.LoopsTransform;
import irvine.transform.LowercaseTransform;
import irvine.transform.NumbersTransform;
import irvine.transform.ReverseTransform;
import irvine.transform.ScrabbleTransform;
import irvine.transform.SortTransform;
import irvine.transform.SubtractTransform;
import irvine.transform.SumTransform;
import irvine.transform.TelephoneSumTransform;
import irvine.transform.TelephoneTransform;
import irvine.transform.TitlecaseTransform;
import irvine.transform.Transform;
import irvine.transform.UppercaseTransform;
import irvine.util.CliFlags;

/**
 * Transform words in various ways.
 * @author Sean A. Irvine
 */
public final class TransformCommand extends Command {

  /** Construct the module. */
  public TransformCommand() {
    super("Transform words");
  }

  private static final String DESC = "Apply each specified transform, generating a line of output for each selected transform for each input line.";
  private static final String NAME_FLAG = "name";
  private static final String IDENTITY_FLAG = "identity";
  private static final String UPPERCASE_FLAG = "uppercase";
  private static final String LOWERCASE_FLAG = "lowercase";
  private static final String TITLECASE_FLAG = "titlecase";
  private static final String LOOPS_FLAG = "loops";
  private static final String SCRABBLE_FLAG = "scrabble";
  private static final String TELEPHONE_FLAG = "telephone";
  private static final String TELEPHONE_SUM_FLAG = "telephone-sum";
  private static final String SORT_FLAG = "sort";
  private static final String REVERSE_FLAG = "reverse";
  private static final String SUM0_FLAG = "sum0";
  private static final String SUM1_FLAG = "sum1";
  private static final String LETTER_FLAG = "letters";
  private static final String NUMBER_FLAG = "numbers";
  private static final String ADD_FLAG = "add";
  private static final String SUBTRACT_FLAG = "subtract";

  /**
   * Transform words.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription(DESC);
    flags.registerOptional('n', NAME_FLAG, "prefix each line of output with the transform name");
    flags.registerOptional('U', UPPERCASE_FLAG, "uppercase the input");
    flags.registerOptional('L', LOWERCASE_FLAG, "lowercase the input");
    flags.registerOptional('T', TITLECASE_FLAG, "titlecase the input");
    flags.registerOptional(IDENTITY_FLAG, "the no operation transform leaving each input as is");
    flags.registerOptional(LOOPS_FLAG, "compute the number of closed loops of each input");
    flags.registerOptional(SCRABBLE_FLAG, "compute the raw Scrabble score of each input");
    flags.registerOptional('t', TELEPHONE_FLAG, "type each input on a telephone");
    flags.registerOptional(TELEPHONE_SUM_FLAG, "type each input on a telephone and sum the digits");
    flags.registerOptional(SORT_FLAG, "sort the letters of each input");
    flags.registerOptional('r', REVERSE_FLAG, "reverse each input");
    flags.registerOptional(SUM0_FLAG, "sum of letters in input 0=A, ..., 25=Z");
    flags.registerOptional(SUM1_FLAG, "sum of letters in input 1=A, ..., 26=Z");
    flags.registerOptional(LETTER_FLAG, "convert numbers to letters 1=A, ..., 26=Z");
    flags.registerOptional('N', NUMBER_FLAG, "convert letters to numbers A=1, ..., Z=26");
    flags.registerOptional(ADD_FLAG, String.class, "KEY", "cyclically add given key to the input");
    flags.registerOptional(SUBTRACT_FLAG, String.class, "KEY", "cyclically subtract given key to the input");
    CommonFlags.registerOutputFlag(flags);
    CommonFlags.registerInputFlag(flags);
    flags.setValidator(f -> {
      if (!CommonFlags.validateOutput(f)) {
        return false;
      }
      if (!CommonFlags.validateInput(f)) {
        return false;
      }
      return true;
    });
    flags.setFlags(args);

    final List<Transform> transforms = new ArrayList<>();
    if (flags.isSet(IDENTITY_FLAG)) {
      transforms.add(new IdentityTransform());
    }
    if (flags.isSet(LETTER_FLAG)) {
      transforms.add(new LetterTransform());
    }
    if (flags.isSet(NUMBER_FLAG)) {
      transforms.add(new NumbersTransform());
    }
    if (flags.isSet(UPPERCASE_FLAG)) {
      transforms.add(new UppercaseTransform());
    }
    if (flags.isSet(LOWERCASE_FLAG)) {
      transforms.add(new LowercaseTransform());
    }
    if (flags.isSet(TITLECASE_FLAG)) {
      transforms.add(new TitlecaseTransform());
    }
    if (flags.isSet(LOOPS_FLAG)) {
      transforms.add(new LoopsTransform());
    }
    if (flags.isSet(SCRABBLE_FLAG)) {
      transforms.add(new ScrabbleTransform());
    }
    if (flags.isSet(TELEPHONE_FLAG)) {
      transforms.add(new TelephoneTransform());
    }
    if (flags.isSet(TELEPHONE_SUM_FLAG)) {
      transforms.add(new TelephoneSumTransform());
    }
    if (flags.isSet(REVERSE_FLAG)) {
      transforms.add(new ReverseTransform());
    }
    if (flags.isSet(SORT_FLAG)) {
      transforms.add(new SortTransform());
    }
    if (flags.isSet(SUM0_FLAG)) {
      transforms.add(new SumTransform(0));
    }
    if (flags.isSet(SUM1_FLAG)) {
      transforms.add(new SumTransform(1));
    }
    if (flags.isSet(ADD_FLAG)) {
      transforms.add(new AddTransform((String) flags.getValue(ADD_FLAG)));
    }
    if (flags.isSet(SUBTRACT_FLAG)) {
      transforms.add(new SubtractTransform((String) flags.getValue(SUBTRACT_FLAG)));
    }

    final boolean includeTransformName = flags.isSet(NAME_FLAG);
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      try (final BufferedReader reader = CommonFlags.getInput(flags)) {
        String line;
        while ((line = reader.readLine()) != null) {
          for (final Transform t : transforms) {
            if (includeTransformName) {
              out.println(t.getName() + ": " + t.apply(line));
            } else {
              out.println(t.apply(line));
            }
          }
        }
      } catch (final IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
