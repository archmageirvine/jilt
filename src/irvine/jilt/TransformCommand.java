package irvine.jilt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import irvine.transform.IdentityTransform;
import irvine.transform.LoopsTransform;
import irvine.transform.LowercaseTransform;
import irvine.transform.ReverseTransform;
import irvine.transform.ScrabbleTransform;
import irvine.transform.SortTransform;
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
  private static final String SORT_FLAG = "sort";
  private static final String REVERSE_FLAG = "reverse";

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
    flags.registerOptional(SORT_FLAG, "sort the letters of each input");
    flags.registerOptional('r', REVERSE_FLAG, "reverse each input");
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
    if (flags.isSet(REVERSE_FLAG)) {
      transforms.add(new ReverseTransform());
    }
    if (flags.isSet(SORT_FLAG)) {
      transforms.add(new SortTransform());
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
