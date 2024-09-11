package irvine.entropy;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import irvine.util.DynamicLongArray;
import irvine.util.IrvineHashFunction;
import irvine.util.Sort;
import irvine.util.StringUtils;

/**
 * Reduced alphabet single word model.
 * @author Sean A. Irvine
 */
public class UniwordModel implements Entropy {

  /**
   * Return the default model for English language problems.
   * @return English model
   */
  public static UniwordModel defaultEnglishModel() {
    try {
      return new UniwordModel(UniwordModel.class.getClassLoader().getResourceAsStream("irvine/resources/uniword_english.model"));
    } catch (final IOException e) {
      throw new RuntimeException("Problem accessing unigram word model", e);
    }
  }

  private final long[] mHashes;
  private final byte[] mCounts;
  private final double mLogTotalCount;
  private final double mScalingFactor;
  private final IrvineHashFunction mHasher = new IrvineHashFunction();

  UniwordModel(final InputStream is) throws IOException {
    try (final DataInputStream dis = new DataInputStream(new GZIPInputStream(is))) {
      final int len = dis.readInt();
      mHashes = new long[len];
      for (int k = 0; k < mHashes.length; ++k) {
        mHashes[k] = dis.readLong();
      }
      mCounts = new byte[len];
      for (int k = 0; k < mCounts.length; ++k) {
        mCounts[k] = dis.readByte();
      }
      mLogTotalCount = dis.readDouble();
      mScalingFactor = dis.readDouble();
    }
  }

  private static final String[] EMPTY = new String[0];

  static String[] split(final String line) {
    if (line.length() > 3 && line.charAt(0) == '\\' && line.charAt(2) == '{' && line.charAt(1) != 'E' && line.charAt(1) != 'J') {
      // Skip non-English dictionary lines
      return EMPTY;
    }
    if (line.contains("\\PSQ")) {
      // Protein sequences in dictionary
      return EMPTY;
    }
    return line.split("[ \n\t\r\f\u000B/~=(){}\\[\\]]+");
  }

  UniwordModel(final File in) throws IOException {
    final IrvineHashFunction hf = new IrvineHashFunction();
    final DynamicLongArray hashes = new DynamicLongArray();
    final DynamicLongArray counts = new DynamicLongArray();
    long maxCount = 0;
    try (final BufferedReader r = new BufferedReader(new FileReader(in))) {
      long t = 0;
      int p = 0;
      String line;
      while ((line = r.readLine()) != null) {
        if (!line.isEmpty()) {
          final int sp = line.indexOf(' ');
          final long c = Long.parseLong(line.substring(0, sp));
          final long h = hf.hash(line.substring(sp + 1));
          hashes.set(p, h);
          counts.set(p, c);
          t += c;
          if (c > maxCount) {
            maxCount = c;
          }
          ++p;
        }
      }
      mLogTotalCount = Math.log(t + 1);
    }
    mHashes = hashes.toArray();
    final long[] lCounts = counts.toArray();
    assert mHashes.length == lCounts.length;
    Sort.sort(mHashes, lCounts);
    int collisions = 0;
    for (int k = 0; k < mHashes.length - 1; ++k) {
      if (mHashes[k] == mHashes[k + 1]) {
        ++collisions;
      }
    }
    System.out.println("There were " + collisions + " collisions (ideally 0)");
    mCounts = new byte[lCounts.length];
    mScalingFactor = Math.log(maxCount) / 255.0;
    assert -1 == (byte) (Math.log(maxCount) / mScalingFactor + 0.5);
    System.out.println("maxcount=" + maxCount + " logScalingFactor=" + mScalingFactor);
    for (int k = 0; k < mCounts.length; ++k) {
      mCounts[k] = (byte) (Math.log(lCounts[k]) / mScalingFactor + 0.5);
    }
  }

  /**
   * Load a model from the specified file name.
   * @param modelName file name of model
   * @exception IOException if an I/O error occurs
   */
  public UniwordModel(final String modelName) throws IOException {
    this(new FileInputStream(modelName));
  }

  private double penalty(final String w) {
    for (int k = 0; k < w.length(); ++k) {
      if (!Character.isLetter(w.charAt(k))) {
        return 1.5;
      }
    }
    return 1;
  }

  @Override
  public double entropy(final String text) {
    double e = 0;
    for (final String s : split(text)) {
      if (!s.isEmpty()) {
        final String clean = StringUtils.clean(s);
        if (!clean.isEmpty()) {
          final int p = Arrays.binarySearch(mHashes, mHasher.hash(clean));
          if (p >= 0) {
            e += mLogTotalCount - (mCounts[p] & 0xFF) * mScalingFactor;
          } else {
            // Unknown word, penalize those with non-letters to a greater extent
            e += mLogTotalCount * penalty(clean);
          }
        } else {
          e += mLogTotalCount;
        }
      }
    }
    return e;
  }

  private void saveModel(final String filename) throws IOException {
    try (final DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(filename)))) {
      dos.writeInt(mHashes.length);
      for (final long h : mHashes) {
        dos.writeLong(h);
      }
      for (final byte c : mCounts) {
        dos.writeByte(c & 0xFF);
      }
      dos.writeDouble(mLogTotalCount);
      dos.writeDouble(mScalingFactor);
    }
  }

  /**
   * Entropy via a unigram word model.
   * @param args source files
   * @exception IOException if an I/O error occurs
   */
  public static void main(final String[] args) throws IOException {
    if (args != null && args.length > 0 && "--build".equals(args[0])) {
      final String modelName = args[1];
      final UniwordModel model = new UniwordModel(new File(args[2]));
      model.saveModel(modelName);
      System.out.println("Model saved.");
    } else {
      final UniwordModel model;
      if (args == null || args.length == 0) {
        model = defaultEnglishModel();
      } else {
        try (final FileInputStream is = new FileInputStream(args[0])) {
          model = new UniwordModel(is);
        }
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
