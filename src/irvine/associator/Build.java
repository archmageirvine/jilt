package irvine.associator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.util.CliFlags;
import irvine.util.IOUtils;

/**
 * Entry point for building associator models.
 * @author Sean A. Irvine
 */
public final class Build extends Command {

  /** Construct the module. */
  public Build() {
    super("Build or add to an associator model");
  }

  private static final String DESC = "Build or an add to an associator model.";
  private static final String TSV_FLAG = "tsv";

  private static String clean(final String s) {
    return s.trim().toUpperCase(Locale.getDefault());
  }

  private void process(final AssociatorModel am, final BufferedReader r) throws IOException {
    long cnt = 0;
    String line;
    while ((line = r.readLine()) != null) {
      final String[] parts = line.split("\t");
      if (parts.length != 2) {
        System.err.println("Skipping: " + line);
      } else {
        am.add(clean(parts[0]), clean(parts[1]));
        ++cnt;
      }
    }
    System.out.println("Added " + cnt + " associations");
  }

  /**
   * Build an associator model.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription(DESC);
    CommonFlags.registerInputFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    //CommonFlags.registerDictionaryFlag(flags);
    flags.registerRequired('f', TSV_FLAG, String.class, "FILE", "file name of data to augment model with");
    flags.setFlags(args);

    final AssociatorModel am;
    if (flags.isSet(CommonFlags.INPUT_FLAG)) {
      final String file = (String) flags.getValue(CommonFlags.INPUT_FLAG);
      try (final InputStream is = IOUtils.getStream(file)) {
        am = AssociatorModel.loadModel(is);
      } catch (final IOException e) {
        System.err.println("Problem reading associator model from " + file);
        return;
      }
    } else {
      am = new AssociatorModel();
    }

    try (final BufferedReader r = IOUtils.getReader((String) flags.getValue(TSV_FLAG))) {
      process(am, r);
    } catch (final IOException e) {
      System.err.println("Problem reading data from " + flags.getValue(TSV_FLAG));
    }

    if (flags.isSet(CommonFlags.OUTPUT_FLAG)) {
      final String outFile = (String) flags.getValue(CommonFlags.OUTPUT_FLAG);
      try {
        am.saveModel(outFile);
      } catch (final IOException e) {
        System.err.println("Failed to serialize model to " + outFile);
      }
    }
  }
}
