package irvine.wordsmith;

import java.util.ArrayList;
import java.util.List;

/**
 * Check for constants and progressions in various ways of looking at letters.
 * @author Sean A. Irvine
 */
public class MetaValuationInspector implements Inspector {

  private final List<Inspector> mInspectors = new ArrayList<>();

  MetaValuationInspector() {
  }

  private void createInspectors() {
    // @formatter:off
    if (mInspectors.isEmpty()) {
      mInspectors.add(new ValuationInspector(4, "closed loops in capital letters",
        // A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
           1, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0));
      mInspectors.add(new ValuationInspector(4, "open letters in capital letters",
        // A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
           0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1));
      mInspectors.add(new ValuationInspector(4, "closed loops in lowercase letters",
        // a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z
           1, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0));
      mInspectors.add(new ValuationInspector(4, "open letters in lowercase letters",
        // a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z
           0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1));
      mInspectors.add(new ValuationInspector(6, "ascending lowercase letters",
        // a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z
           0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0));
      mInspectors.add(new ValuationInspector(6, "descending lowercase letters",
        // a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z
           0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0));
      mInspectors.add(new ValuationInspector(6, "horizontally symmetric capital letters",
        // A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
           1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0));
      mInspectors.add(new ValuationInspector(6, "vertically symmetric capital letters",
        // A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
           0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0));
      mInspectors.add(new ValuationInspector(4, "Morse code total symbols",
        // A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
           2, 4, 4, 3, 1, 4, 3, 4, 2, 4, 3, 4, 2, 2, 3, 4, 4, 3, 3, 1, 3, 4, 3, 4, 4, 4));
      mInspectors.add(new ValuationInspector(4, "Morse code dit symbols",
        // A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
           1, 3, 2, 2, 1, 3, 1, 4, 2, 1, 1, 3, 0, 1, 0, 2, 1, 2, 3, 0, 2, 3, 1, 2, 1, 2));
      mInspectors.add(new ValuationInspector(4, "Morse code dash symbols",
        // A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
           1, 1, 2, 1, 0, 1, 2, 0, 0, 3, 2, 1, 2, 1, 3, 2, 3, 1, 0, 1, 1, 1, 2, 2, 3, 2));
      mInspectors.add(new ValuationInspector(4, "Scrabble score",
        // A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P,  Q, R, S, T, U, V, W, X, Y,  Z
           1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10));
      mInspectors.add(new ValuationInspector(4, "telephone sum",
        // A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
           2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 7, 8, 8, 8, 9, 9, 9, 9));
      mInspectors.add(new ValuationInspector(4, "sum",
        // A, B, C, D, E, F, G, H, I,  J,  K,  L,  M,  N,  O,  P,  Q,  R,  S,  T,  U,  V,  W,  X,  Y,  Z
           1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26));
      mInspectors.add(new ValuationInspector(4, "Braille",
        // A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
           1, 2, 2, 3, 2, 3, 4, 3, 2, 3, 2, 3, 3, 4, 3, 4, 5, 4, 3, 4, 3, 4, 4, 4, 5, 4));
    }
    // @formatter:on
  }

  @Override
  public String inspect(final String... words) {
    final StringBuilder sb = new StringBuilder();
    createInspectors();
    for (final Inspector i : mInspectors) {
      final String res = i.inspect(words);
      if (res != null) {
        if (sb.length() > 0) {
          sb.append('\n');
        }
        sb.append(res);
      }
    }
    return sb.length() == 0 ? null : sb.toString();
  }
}
