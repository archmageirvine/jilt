package irvine.language;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

import irvine.jilt.Command;
import irvine.jilt.CommonFlags;
import irvine.jilt.Dictionary;
import irvine.util.CliFlags;
import irvine.util.Trie;

/**
 * Solve word search problems.
 * @author Sean A. Irvine
 */
public final class WordSearch extends Command {

  private static final String MIN_LENGTH_FLAG = "min-length";
  private static final String FREE_FLAG = "free";

  private static final int[] DELTA_X = {1, -1, 0, 0, 1, -1, 1, -1};
  private static final int[] DELTA_Y = {0, 0, 1, -1, 1, 1, -1, -1};
  private static final String[] DIR = {"E", "W", "S", "N", "SE", "SW", "NE", "NW"};

  /** Construct the module. */
  public WordSearch() {
    super("Solve word search problems");
  }

  private Trie mWords = null;
  private char[][] mGrid = null;
  private boolean[][] mUsed = null;

  // Search with usual direction restrictions
  private void search(final PrintStream out, final int sx, final int sy, final int x, final int y, final int d, final String soFar) {
    if (mWords.contains(soFar)) {
      out.println(soFar.toUpperCase(Locale.getDefault()) + " (" + sx + "," + sy + "," + DIR[d] + ")");
    }
    final int nx = x + DELTA_X[d];
    final int ny = y + DELTA_Y[d];
    if (ny >= 0 && ny < mGrid.length && nx >= 0 && nx < mGrid[ny].length) {
      search(out, sx, sy, nx, ny, d, soFar + mGrid[y][x]);
    }
  }

  // Search with freedom (apart from no reuse of positions)
  private void search(final PrintStream out, final int sx, final int sy, final int x, final int y, final Trie child, final String soFar) {
    if (child == null) {
      return; // No match
    }
    if (child.isTerminal()) {
      out.println(soFar.toUpperCase(Locale.getDefault()) + " (" + sx + "," + sy + ")");
    }
    for (int d = 0; d < DELTA_X.length; ++d) {
      final int nx = x + DELTA_X[d];
      final int ny = y + DELTA_Y[d];
      if (ny >= 0 && ny < mGrid.length && nx >= 0 && nx < mGrid[ny].length && !mUsed[ny][nx]) {
        mUsed[ny][nx] = true;
        search(out, sx, sy, nx, ny, child.getChild(mGrid[ny][nx]), soFar + mGrid[ny][nx]);
        mUsed[ny][nx] = false;
      }
    }
  }

  @Override
  public void mainExec(final String... args) {
    final CliFlags flags = new CliFlags(getDescription());
    flags.setDescription("Search for words in a grid of letters. By default, the words must appear in a straight line in a particular direction, but with --free the search can be made to allow for changes of direction at each step (although the same position cannot occur more than once in any particular word).");
    CommonFlags.registerDictionaryFlag(flags);
    CommonFlags.registerOutputFlag(flags);
    flags.registerOptional('m', MIN_LENGTH_FLAG, Integer.class, "INT", "minimum length of word", 3);
    flags.registerOptional(FREE_FLAG, "direction is allowed to change at each step");
    flags.registerRequired(String.class, "STRING", "rows of the grid").setMaxCount(Integer.MAX_VALUE);
    flags.setValidator(f -> {
      if (!CommonFlags.validateDictionary(f)) {
        return false;
      }
      if (!CommonFlags.validateOutput(f)) {
        return false;
      }
      final int minLength = (Integer) f.getValue(MIN_LENGTH_FLAG);
      if (minLength < 1) {
        f.setParseMessage("Minimum word length must be positive.");
        return false;
      }
      int len = -1;
      for (final Object row : flags.getAnonymousValues(0)) {
        final String s = (String) row;
        if (len == -1) {
          len = s.length();
        } else if (len != s.length()) {
          f.setParseMessage("All rows of the grid must be the same length: " + s);
          return false;
        }
      }
      return true;
    });
    flags.setFlags(args);

    final boolean free = flags.isSet(FREE_FLAG);

    final int minLength = (Integer) flags.getValue(MIN_LENGTH_FLAG);
    mGrid = new char[flags.getAnonymousValues(0).size()][];
    int k = 0;
    for (final Object row : flags.getAnonymousValues(0)) {
      mGrid[k++] = ((String) row).toLowerCase(Locale.getDefault()).toCharArray();
    }
    try {
      mWords = Trie.buildTrie(Dictionary.getDictionaryReader((String) flags.getValue(CommonFlags.DICTIONARY_FLAG)), minLength, Integer.MAX_VALUE);
    } catch (final IOException e) {
      throw new RuntimeException("Problem reading word list.", e);
    }
    try (final PrintStream out = CommonFlags.getOutput(flags)) {
      if (free) {
        mUsed = new boolean[mGrid.length][mGrid[0].length];
        for (int y = 0; y < mGrid.length; ++y) {
          for (int x = 0; x < mGrid[y].length; ++x) {
            mUsed[y][x] = true;
            search(out, x, y, x, y, mWords.getChild(mGrid[y][x]), String.valueOf(mGrid[y][x]));
            mUsed[y][x] = false;
          }
        }
      } else {
        for (int y = 0; y < mGrid.length; ++y) {
          for (int x = 0; x < mGrid[y].length; ++x) {
            for (int d = 0; d < DELTA_X.length; ++d) {
              search(out, x, y, x, y, d, "");
            }
          }
        }
      }
    }
  }

}
