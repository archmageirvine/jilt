package irvine.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Various I/O utility functions.
 * @author Sean A. Irvine
 */
public final class IOUtils {

  private IOUtils() { }

  /**
   * Get an input stream in a way that handles "-" denoting standard input and
   * automatically decompressing <code>.gz</code> files.
   * @param file file to open a stream to
   * @return stream
   * @throws IOException if an I/O error occurs.
   */
  public static InputStream getStream(final String file) throws IOException {
    if ("-".equals(file)) {
      return System.in;
    } else if (file.endsWith(".gz")) {
      return new GZIPInputStream(new FileInputStream(file));
    } else {
      return new FileInputStream(file);
    }
  }

  /**
   * Get a buffered reader in a way that handles "-" denoting standard input and
   * automatically decompressing <code>.gz</code> files.
   * @param file file to open a stream to
   * @return stream
   * @throws IOException if an I/O error occurs.
   */
  public static BufferedReader getReader(final String file) throws IOException {
    return new BufferedReader(new InputStreamReader(getStream(file)));
  }
}
