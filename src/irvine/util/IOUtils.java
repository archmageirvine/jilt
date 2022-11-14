package irvine.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
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

  private static final int BUFFER_LENGTH = 16384;
  private static final int EOF = -1;

  /**
   * Read all of an input stream into a String.
   *
   * @param input input stream being read.
   * @return a String containing the contents of the input stream.
   * @exception IOException If there is a problem during reading.
   * @exception NullPointerException if <code>input</code> is null.
   */
  public static String readAll(final InputStream input) throws IOException {
    return readAll(new InputStreamReader(input));
  }

  /**
   * Read all of a Reader into a String.
   *
   * @param input Reader being read.
   * @return a String containing the contents of the input stream.
   * @exception IOException If there is a problem during reading.
   * @exception NullPointerException if <code>input</code> is null.
   */
  public static String readAll(final Reader input) throws IOException {
    final char[] b = new char[BUFFER_LENGTH];
    final StringWriter str = new StringWriter(BUFFER_LENGTH);
    try {
      while (true) {
        final int length = input.read(b);
        if (length == EOF) {
          break;
        } else if (length == 0) {
          throw new IOException("Read was 0 bytes");
        } else {
          str.write(b, 0, length);
        }
      }
    } finally {
      str.close();
    }
    return str.toString();
  }

  /**
   * Read all of a file into a String.
   * @param file the file to read.
   * @return a String containing the contents of the URL
   * @exception IOException If there is a problem during reading.
   * @exception NullPointerException if <code>url</code> is null.
   */
  public static String readAll(final File file) throws IOException {
    try (final FileInputStream input = new FileInputStream(file)) {
      return readAll(input);
    }
  }

  /**
   * Return a reader of a resource.
   * @param resource the resource to read
   * @return reader for resource
   * @throws IOException if an I/O error occurs.
   */
  public static BufferedReader reader(final String resource) throws IOException {
    final InputStream is = IOUtils.class.getClassLoader().getResourceAsStream(resource);
    return new BufferedReader(new InputStreamReader(resource.endsWith(".gz") ? new GZIPInputStream(is) : is));
  }

}
