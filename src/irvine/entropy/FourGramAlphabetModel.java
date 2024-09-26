package irvine.entropy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import irvine.util.CliFlags;
import irvine.util.Date;
import irvine.util.IOUtils;
import irvine.util.IntegerUtils;

/**
 * Alphabetic character 4-gram model using PPMC with exclusions.
 * Any restriction in the alphabet (e.g. collapsing consecutive whitespace)
 * is the responsibility of the caller.
 * @author Sean A. Irvine
 */
public class FourGramAlphabetModel implements Entropy, Serializable {

  // For speed this model uses a massive array with counts for every possible
  // context, rather than a hashtable or trie storing counts only for those
  // contexts which actually occur.
  //
  // For an alphabet of size A, we in theory need (A+1)^(order+1) slots,
  // but to do this for an arbitrary alphabet bet size would require using
  // modulo when extracting symbols.  So instead, we round the alphabet
  // size up to the next power of 2 so that we can jump around the context
  // using shifts and simple bit masks.
  //
  // Also, ideally we would like to use arbitrary precision, but that is
  // expensive in memory. Instead, we use ints and rescale all counts
  // when we detect that overflow would otherwise occur.  The scaling is
  // done in such a way that rare events are retained.
  //
  // Finally, Java does not have an unsigned int type, but we don't want
  // to have to waste the top bit.  Therefore, negative counts are in fact
  // large positive counts, and we take care to make the number unsigned
  // when promoting to a long.

  private static final int MODEL_ORDER = 4;
  private static final long MAKE_UNSIGNED = 0xFFFFFFFFL;
  private static final String BUILD_FLAG = "build";
  private static final String MODEL_FLAG = "model";
  private static final String ALPHABET_FLAG = "alphabet";
  private static final String UPCASE_FLAG = "upcase";
  private static final String VERSION_FLAG = "version";
  private static final String DEFAULT_MODEL = "irvine/resources/default.model";
  private static final long serialVersionUID = -5678133404521443306L;

  // Construct a code mapping from character to internal code (for efficiency)
  private static int[] buildCharToCodeMapping(final String alphabet) {
    final int[] charToCode = new int[256]; // assume bytes
    for (int k = 0; k < alphabet.length(); ++k) {
      final char c = alphabet.charAt(k);
      if (c > 255) {
        throw new IllegalArgumentException("Sorry characters exceeding 255 not supported");
      }
      if (charToCode[c] != 0) {
        throw new IllegalArgumentException("Repeated characters in alphabet: " + c);
      }
      charToCode[c] = k + 1; // code 0 is reserved
    }
    return charToCode;
  }

  // Compute number of bits needed to hold alphabet.
  private static int bits(final String alphabet) {
    return IntegerUtils.lg(IntegerUtils.nextPowerOf2(alphabet.length()));
  }

  private final String mDate = Date.now();
  private final String mAlphabet;     // actual characters of the alphabet
  private final int[] mCharToCode;    // map alphabet symbol to internal code
  private final int mAlphabetBits;    // number of bits needed to hold a symbol in the alphabet
  private final int mAlphabetSize;    // power of 2 >= |alphabet|, for efficiency
  private final int mSymbolMask;      // mask for a single symbol
  private final int mContextMask;     // mask for entire context
  private final int[] mCounts;        // table of frequency counts
  private boolean mUpcase = false;    // should symbols be automatically upper-cased

  private String mBuildCommand = null;
  private long mTotalTraining = 0;

  FourGramAlphabetModel(final String alphabet) {
    mAlphabet = alphabet;
    mCharToCode = buildCharToCodeMapping(alphabet);
    mAlphabetBits = bits(alphabet);
    if (mAlphabetBits > 7) {
      // With care 8 could be made to work, but larger values would need to move
      // context etc. into a long rather than an int
      throw new IllegalArgumentException("Alphabet too large");
    }
    mAlphabetSize = 1 << mAlphabetBits;
    mSymbolMask = (1 << mAlphabetBits) - 1;
    mCounts = new int[1 << (mAlphabetBits * MODEL_ORDER)];
    mContextMask = mCounts.length - 1;
  }

  private static Entropy loadModel(final InputStream stream) throws IOException {
    try (final ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(stream))) {
      return (Entropy) ois.readObject();
    } catch (final ClassNotFoundException e) {
      throw new RuntimeException("Incompatible model file", e);
    }
  }

  /**
   * Load a model from the specified file.
   * @param filename file name of model
   * @return the model
   * @exception IOException if an I/O error occurs
   */
  public static Entropy loadModel(final String filename) throws IOException {
    try (final InputStream is = new FileInputStream(filename)) {
      return loadModel(is);
    }
  }

  /**
   * Load a model resource.
   * @param resource the Java package location of the model
   * @return the model
   * @exception IOException if an I/O error occurs
   */
  public static Entropy loadModelResource(final String resource) throws IOException {
    try (final InputStream is = FourGramAlphabetModel.class.getClassLoader().getResourceAsStream(resource)) {
      return loadModel(is);
    }
  }

  /**
   * Load the default model.
   * @return the model
   * @exception IOException if an I/O error occurs
   */
  public static Entropy loadModel() throws IOException {
    return loadModelResource(DEFAULT_MODEL);
  }

  /**
   * Serialize the current model.
   * @param filename output filename
   * @throws IOException if an I/O error occurs
   */
  void saveModel(final String filename) throws IOException {
    try (final ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filename)))) {
      oos.writeObject(this);
    }
  }

  @Override
  public String toString() {
    return "Model built on " + mDate + "training size " + mTotalTraining + "\n" + getBuildCommand();
  }

  public boolean isUpcase() {
    return mUpcase;
  }

  public void setUpcase(final boolean upcase) {
    mUpcase = upcase;
  }

  public String getBuildCommand() {
    return mBuildCommand;
  }

  public void setBuildCommand(final String buildCommand) {
    mBuildCommand = buildCommand;
  }

  /**
   * Get the alphabet used by this model.
   * @return the alphabet
   */
  public String getAlphabet() {
    return mAlphabet;
  }

  private int charToCode(final int s) {
    return mCharToCode[isUpcase() ? Character.toUpperCase(s) : s];
  }

  private double entropy(final int context, final int start, final boolean[] exclusions) {
    if (start >= MODEL_ORDER || (context & mSymbolMask) == 0) {
      // Zeroth order prediction
      return Math.log((mCounts[0] & MAKE_UNSIGNED) + 1L);
    }

    // Find leftmost defined symbol
    int subcontextMask = mSymbolMask;
    int p = MODEL_ORDER;
    while (p > start && (context & subcontextMask) != 0) {
      subcontextMask <<= mAlphabetBits;
      --p;
    }
    subcontextMask >>>= mAlphabetBits;

    // Propage subcontextMask to all valid symbols
    subcontextMask |= subcontextMask - 1;

    while (true) {
      assert subcontextMask != 0;
      final int parentContext = context & subcontextMask & ~mSymbolMask;
      long parentCount = mCounts[parentContext] & MAKE_UNSIGNED;
      if (parentCount > 0) {
        // Found a valid context.
        final long count = mCounts[context & subcontextMask];
        for (int j = 0; j < mAlphabetSize - 1; ++j) {
          if (exclusions[j]) {
            parentCount -= mCounts[parentContext + j + 1] & MAKE_UNSIGNED;
          }
        }
        assert parentCount >= count : parentCount + " " + count;
        if (count != 0) {
          // Can make a sensible prediciton at this order
          return Math.log(parentCount + 1) - Math.log(count);
        } else {
          // Update for new exclusions
          for (int j = 0; j < mAlphabetSize - 1; ++j) {
            exclusions[j] |= mCounts[parentContext + j + 1] != 0;
          }
          // Escape to next lower order
          return Math.log(parentCount + 1) + entropy(context, start + 1, exclusions);
        }
      }
      subcontextMask >>>= mAlphabetBits;
    }
  }

  @Override
  public double entropy(final String text) {
    int context = 0;
    double e = 0;
    final boolean[] exclusions = new boolean[mAlphabetSize];
    for (int k = 0; k < text.length(); ++k) {
      final int w = charToCode(text.charAt(k));
      // w == 0 corresponds to an unknown character, so will escape down the model
      context <<= mAlphabetBits;
      context |= w;
      context &= mContextMask;
      Arrays.fill(exclusions, false);
      e += entropy(context, 0, exclusions);
    }
    return e;
  }

  void add(final InputStream in) throws IOException {
    try (final BufferedInputStream is = new BufferedInputStream(in)) {
      int c;
      int context = 0;
      while ((c = is.read()) != -1) {
        final int w = charToCode(c);
        if (w != 0) { // ignore symbols outside the alphabet
          ++mTotalTraining;
          context <<= mAlphabetBits;
          context |= w;
          context &= mContextMask;
          for (int k = 0, m = mSymbolMask; k < MODEL_ORDER; ++k) {
            assert context != 0;
            // Update count
            ++mCounts[context & m];
            // Update parent count
            if (++mCounts[context & m & ~mSymbolMask] == 0) {
              // It is sufficient to check for overflow on the total, since this will
              // always overflow no later than any individual count.  When we detect
              // overflow we simply halve all the counts in the model.
              downScale();
            }
            m <<= mAlphabetBits;
            m += mSymbolMask;
          }
        }
      }
    }
  }

  private void downScale() {
    // Halve all the counts, rounding up odd counts.
    // Complicated by the fact that the total must remain accurate

    // Halve all counts
    for (int k = 0; k < mCounts.length; ++k) {
      mCounts[k] = (mCounts[k] + 1) >>> 1;
    }
    // Fix totals
    final int t = mAlphabet.length();
    for (int k = 0; k < mCounts.length; k += mAlphabetSize) {
      int s = 0;
      for (int j = 1; j <= t; ++j) {
        s += mCounts[k + j];
      }
      mCounts[k] = s;
    }
  }

  /**
   * Build or query a 4-gram PPMC model.
   * @param args source files
   * @exception IOException if an I/O error occurs
   */
  public static void main(final String[] args) throws IOException {
    final CliFlags flags = new CliFlags("Four-gram entropy models");
    flags.registerRequired('m', MODEL_FLAG, String.class, "model", "name of model to load or build");
    flags.registerOptional('b', BUILD_FLAG, "build model and save in specified file name");
    flags.registerOptional('u', UPCASE_FLAG, "convert lowercase letters to uppercase");
    flags.registerOptional('V', VERSION_FLAG, "display model information");
    flags.registerOptional('a', ALPHABET_FLAG, String.class, "string", "alphabet of characters to build", "ABCDEFGHIJKLMNOPQRSTUVWXYZ ");
    flags.registerRequired(String.class, "file", "input files to build or compute entropy for, or - for standard input")
      .setMinCount(0)
      .setMaxCount(Integer.MAX_VALUE);
    flags.setFlags(args);

    final String modelName = (String) flags.getValue(MODEL_FLAG);
    if (flags.isSet(BUILD_FLAG)) {
      final FourGramAlphabetModel model = new FourGramAlphabetModel((String) flags.getValue(ALPHABET_FLAG));
      model.setUpcase(flags.isSet(UPCASE_FLAG));
      model.setBuildCommand(Arrays.toString(args));
      for (final Object inputFile : flags.getAnonymousValues(0)) {
        System.out.println("Adding: " + inputFile);
        try (final InputStream fis = "-".equals(inputFile) ? System.in : IOUtils.getStream((String) inputFile)) {
          model.add(fis);
        }
      }
      model.saveModel(modelName);
      if (flags.isSet(VERSION_FLAG)) {
        System.out.println(model);
      }
      System.out.println("Model saved.");
    } else {
      final Entropy model = loadModel(modelName);
      if (flags.isSet(VERSION_FLAG)) {
        System.out.println(model.toString());
      }
      try (final BufferedReader r = new BufferedReader(new InputStreamReader(System.in))) {
        String line;
        while ((line = r.readLine()) != null) {
          System.out.println(model.entropy(line) + " " + line);
        }
      }
    }
  }
}
