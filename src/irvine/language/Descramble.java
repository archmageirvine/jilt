package irvine.language;

import irvine.entropy.Entropy;
import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.util.CliFlags;
import irvine.util.DoubleUtils;
import irvine.util.Shuffle;

/**
 * Attempt to make text more readable by swapping letters.
 * @author Sean A. Irvine
 */
public final class Descramble extends Command {

  private Entropy mModel = null;
  private double mBestScore = 0;

  /** Construct the module. */
  public Descramble() {
    super("Descramble text");
  }

  private void hillClimb(final String text) {
    double best = Double.POSITIVE_INFINITY;
    String bestString = text;
    boolean improved;
    do {
      improved = false;
      final char[] c = bestString.toCharArray();
      for (int k = 1; k < c.length; ++k) {
        final char ck = c[k];
        for (int j = 0; j < k; ++j) {
          if (ck != c[j]) {
            c[k] = c[j];
            c[j] = ck;
            final String t = new String(c);
            final double e = mModel.entropy(t);
            if (e < best) {
              best = e;
              bestString = t;
              improved = true;
            }
            c[j] = c[k];
          }
          c[k] = ck;
        }
      }
      if (!improved) {
        for (int k = 2; k < c.length; ++k) {
          final char ck = c[k];
          for (int j = 1; j < k; ++j) {
            final char cj = c[j];
            if (ck != cj) {
              for (int i = 0; i < j; ++i) {
                if (c[i] != ck && c[i] != cj) {
                  c[k] = cj;
                  c[j] = c[i];
                  c[i] = ck;
                  final String t = new String(c);
                  final double e = mModel.entropy(t);
                  if (e < best) {
                    best = e;
                    bestString = t;
                    improved = true;
                  }
                  c[k] = c[j];
                  c[j] = ck;
                  c[i] = cj;
                  final String t2 = new String(c);
                  final double e2 = mModel.entropy(t);
                  if (e2 < best) {
                    best = e2;
                    bestString = t2;
                    improved = true;
                  }
                  c[i] = c[k];
                  c[j] = cj;
                  c[k] = ck;
                }
              }
            }
          }
        }
      }
      if (best < mBestScore) {
        mBestScore = best;
        System.out.println(DoubleUtils.NF3.format(mBestScore) + " " + bestString);
      }
    } while (improved);
  }

  /**
   * Descramble.
   * @param args see help
   */
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    CommonFlags.registerModelFlag(flags);
    flags.registerRequired(String.class, "TEXT", "text to process");
    flags.setValidator(f -> CommonFlags.validateModel(f));
    flags.setFlags(args);

    String text = (String) flags.getAnonymousValue(0);
    mModel = CommonFlags.getEntropyModel(flags);
    mBestScore = mModel.entropy(text);
    System.out.println(DoubleUtils.NF3.format(mBestScore) + " " + text);
    while (true) {
      hillClimb(text);
      final char[] t = text.toCharArray();
      Shuffle.shuffle(t);
      text = new String(t);
    }
  }
}
