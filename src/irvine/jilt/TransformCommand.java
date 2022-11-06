package irvine.jilt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import irvine.filter.AlphabeticalFilter;
import irvine.filter.DecreasingFilter;
import irvine.filter.Filter;
import irvine.filter.IncreasingFilter;
import irvine.filter.LengthFilter;
import irvine.filter.MaxLengthFilter;
import irvine.filter.MinLengthFilter;
import irvine.filter.PalindromeFilter;
import irvine.filter.ReverseAlphabeticalFilter;
import irvine.filter.SetFilter;
import irvine.filter.TautonymFilter;
import irvine.transform.LoopsTransform;
import irvine.transform.LowercaseTransform;
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
  private static final String UPPERCASE_FLAG = "uppercase";
  private static final String LOWERCASE_FLAG = "lowercase";
  private static final String TITLECASE_FLAG = "titlecase";
  private static final String LOOPS_FLAG = "loops";

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
    flags.registerOptional(LOOPS_FLAG, "compute the number of closed loops in the input");
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
