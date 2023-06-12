package irvine.wordsmith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A meta-inspector that creates and attempts a list inspect for each
 * list file found in a directory.  Individual inspectors are only
 * created the first time that an inspection request is made.
 * @author Sean A. Irvine
 */
public class DirListInspector implements Inspector {

  private final String mDir;
  private final boolean mVerbose;
  private final List<Inspector> mInspectors = new ArrayList<>();

  DirListInspector(final String dir, final boolean verbose) {
    mDir = dir;
    mVerbose = verbose;
  }

  @Override
  public String inspect(final String... words) {
    if (mInspectors.isEmpty()) {
      final File[] files = new File(mDir).listFiles(file -> file.getName().endsWith(".lst"));
      if (files != null) {
        for (final File f : files) {
          mInspectors.add(new ListInspector(f.getPath(), mVerbose));
        }
      }
    }
    final StringBuilder sb = new StringBuilder();
    for (final Inspector inspector : mInspectors) {
      final String res = inspector.inspect(words);
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
