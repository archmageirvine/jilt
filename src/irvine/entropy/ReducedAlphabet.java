package irvine.entropy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Reduced alphabet.
 * @author Sean A. Irvine
 */
public final class ReducedAlphabet {

  private ReducedAlphabet() { }

  private static final int SPACE = ' ';
  private static final int DIGIT = '0';
  private static final int QUOTE = '\'';
  private static final int PUNCTUATION = '.';
  private static final int OTHER = '|';

  /** Mapping of bytes 0 to 127 onto a reduced alphabet. */
  private static final int[] ENCODING = {
    0, // 0
    OTHER, // 1
    OTHER, // 2
    OTHER, // 3
    OTHER, // 4
    OTHER, // 5
    OTHER, // 6
    OTHER, // 7
    OTHER, // 8
    SPACE, // 9
    SPACE, // 10
    SPACE, // 11
    SPACE, // 12
    SPACE, // 13
    OTHER, // 14
    OTHER, // 15
    OTHER, // 16
    OTHER, // 17
    OTHER, // 18
    OTHER, // 19
    OTHER, // 20
    OTHER, // 21
    OTHER, // 22
    OTHER, // 23
    OTHER, // 24
    OTHER, // 25
    OTHER, // 26
    OTHER, // 27
    OTHER, // 28
    OTHER, // 29
    OTHER, // 30
    OTHER, // 31
    SPACE, // 32
    PUNCTUATION, // 33
    QUOTE, // 34
    OTHER, // 35
    OTHER, // 36
    OTHER, // 37
    OTHER, // 38
    QUOTE, // 39
    OTHER, // 40
    OTHER, // 41
    OTHER, // 42
    OTHER, // 43
    PUNCTUATION, // 44
    SPACE, // 45
    PUNCTUATION, // 46
    SPACE, // 47
    DIGIT, // 48
    DIGIT, // 49
    DIGIT, // 50
    DIGIT, // 51
    DIGIT, // 52
    DIGIT, // 53
    DIGIT, // 54
    DIGIT, // 55
    DIGIT, // 56
    DIGIT, // 57
    PUNCTUATION, // 58
    PUNCTUATION, // 59
    OTHER, // 60
    OTHER, // 61
    OTHER, // 62
    PUNCTUATION, // 63
    OTHER, // 64
    'A', // 65
    'B', // 66
    'C', // 67
    'D', // 68
    'E', // 69
    'F', // 70
    'G', // 71
    'H', // 72
    'I', // 73
    'J', // 74
    'K', // 75
    'L', // 76
    'M', // 77
    'M', // 78
    'O', // 79
    'P', // 80
    'Q', // 81
    'R', // 82
    'S', // 83
    'T', // 84
    'U', // 85
    'V', // 86
    'W', // 87
    'X', // 88
    'Y', // 89
    'Z', // 90
    OTHER, // 91
    OTHER, // 92
    OTHER, // 93
    OTHER, // 94
    SPACE, // 95
    QUOTE, // 96
    'A', // 97
    'B', // 98
    'C', // 99
    'D', // 100
    'E', // 101
    'F', // 102
    'G', // 103
    'H', // 104
    'I', // 105
    'J', // 106
    'K', // 107
    'L', // 108
    'M', // 109
    'N', // 110
    'O', // 111
    'P', // 112
    'Q', // 113
    'R', // 114
    'S', // 115
    'T', // 116
    'U', // 117
    'V', // 118
    'W', // 119
    'X', // 120
    'Y', // 121
    'Z', // 122
    OTHER, // 123
    OTHER, // 124
    OTHER, // 125
    OTHER, // 126
    OTHER, // 127
  };

  /**
   * Return the reduced alphabet code for the given character.
   * @param s a character
   * @return reduced value in range 0 to 31.
   */
  public static int reducedAlphabetSymbol(final int s) {
    return s >= 0 && s < 128 ? ENCODING[s] : OTHER;
  }

  /**
   * Reduce a stream according to this alphabet.
   * @param in input stream
   * @param out output stream
   * @throws IOException if an I/O error occurs
   */
  public static void stream(final InputStream in, final OutputStream out) throws IOException {
    try (final BufferedInputStream is = new BufferedInputStream(in)) {
      int c;
      boolean lastWasSpace = true;
      while ((c = is.read()) != -1) {
        final int w = reducedAlphabetSymbol(c);
        if (w != SPACE || !lastWasSpace) {
          out.write(w);
          lastWasSpace = w == SPACE;
        }
      }
    }
  }

  /**
   * Stream standard input to output doing the alphabet reduction.
   * @param args ignored
   * @throws IOException if an I/O error occurs
   */
  public static void main(final String[] args) throws IOException {
    stream(System.in, System.out);
  }
}
