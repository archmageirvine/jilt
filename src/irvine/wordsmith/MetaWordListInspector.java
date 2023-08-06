package irvine.wordsmith;

/**
 * Check for specific sets of words.
 * @author Sean A. Irvine
 */
public class MetaWordListInspector implements Inspector {

  private static final String[][] LISTS = {
    {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN", "TWELVE"},
    {"ZERO", "ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN", "TWELVE"},
    {"UN", "DEUX", "TROIS", "QUATRE", "CINQ", "SIX", "SEPT", "HUIT", "NEUF", "DIX", "ONZE", "DOUZE"},
    {"EINS", "ZWEI", "DREI", "VIER", "FUNF", "SECHS", "SIEBEN", "ACHT", "NEUN", "ZEHN", "ELF", "ZWOLF"},
    {"ALPHA", "BETA", "GAMMA", "DELTA", "EPSILON", "ZETA", "ETA", "THETA", "IOTA", "KAPPA", "LAMBDA", "MU", "NU", "XI", "OMICRON", "PI", "RHO", "SIGMA", "TAU", "UPSILON", "PHI", "CHI", "PSI", "OMEGA"}
  };

  @Override
  public String inspect(final String... words) {
    final StringBuilder sb = new StringBuilder();
    for (final String[] lst : LISTS) {
      final String res = new SpecificWordListInspector(lst).inspect(words);
      if (res != null) {
        if (sb.length() > 0) {
          sb.append('\n');
        }
        sb.append(res);
      }
    }
    return sb.length() > 0 ? sb.toString() : null;
  }
}
