package irvine.wordsmith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An inspector that consults an existing word list.
 * @author Sean A. Irvine
 */
public class ListInspector implements Inspector {

  private final WordList mList;
  private final boolean mVerbose;

  ListInspector(final String file, final boolean verbose) {
    mList = new WordList(file);
    mVerbose = verbose;
  }

  @Override
  public String inspect(final String... words) {
    for (final String w : words) {
      if (!mList.containsKey(w)) {
        return null;
      }
    }

    // This list contains every word in the list.
    final StringBuilder res = new StringBuilder("All words are in ").append(mList.getName());
    // See if we can get a deeper explanation by looking at associated data
    final List<List<String>> satellite = new ArrayList<>();
    int numSatelliteFields = Integer.MAX_VALUE;
    for (final String w : words) {
      final List<String> s = mList.get(w);
      numSatelliteFields = Math.min(numSatelliteFields, s.size());
      satellite.add(s);
    }
    // There are "max" different satellite data associated with the list
    final Inspector cons = new ConstantInspector();
    final Inspector alpha = new AlphabeticalInspector();
    final Inspector reverse = new ReverseAlphabeticalInspector();
    for (int k = 0; k < numSatelliteFields; ++k) {
      final String[] vec = new String[words.length];
      for (int j = 0; j < vec.length; ++j) {
        vec[j] = satellite.get(j).get(k);
      }
      if (mVerbose) {
        System.out.println("Checking associated data: " + Arrays.toString(vec));
      }
      final String c = cons.inspect(vec);
      if (c != null) {
        res.append("\nEvery word is associated with: ").append(vec[0]);
      } else {
        final String a = alpha.inspect(vec);
        if (a != null) {
          res.append("\nAssociated words are in alphabetical order:\n").append(Arrays.toString(vec));
        } else {
          final String r = reverse.inspect(vec);
          if (r != null) {
            res.append("\nAssociated words are in reverse alphabetical order:\n").append(Arrays.toString(vec));
          }
        }
      }
    }
    return res.toString();
  }
}
