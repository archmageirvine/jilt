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

  static final String LIST_DIR = System.getProperty("lists.dir", "lists");
  private final String mDir;
  private final boolean mVerbose;
  private final boolean mTryHard;
  private final List<Inspector> mInspectors = new ArrayList<>();
  private final List<Inspector> mSubstringInspectors = new ArrayList<>();

  DirListInspector(final String dir, final boolean tryHard, final boolean verbose) {
    mDir = dir;
    mTryHard = tryHard;
    mVerbose = verbose;
  }

  DirListInspector(final boolean tryHard, final boolean verbose) {
    this(LIST_DIR, tryHard, verbose);
  }

  @Override
  public String inspect(final String... words) {
    if (mInspectors.isEmpty()) {
      final File[] files = new File(mDir).listFiles(file -> file.getName().endsWith(".lst"));
      if (files != null) {
        for (final File f : files) {
          mInspectors.add(new AnagramInspector(f.getPath()));
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
    // Only if we didn't find an explanation above, try a harder more general search
    if (mTryHard && sb.length() == 0) {
      if (mSubstringInspectors.isEmpty()) {
        final File[] files = new File(mDir).listFiles(file -> file.getName().endsWith(".lst"));
        if (files != null) {
          for (final File f : files) {
            mSubstringInspectors.add(new SubstringInspector(f.getPath(), mVerbose));
          }
        }
      }
      for (final Inspector inspector : mSubstringInspectors) {
        final String res = inspector.inspect(words);
        if (res != null) {
          if (sb.length() > 0) {
            sb.append('\n');
          }
          sb.append(res);
        }
      }
    }
    return sb.length() > 0 ? sb.toString() : null;
  }
}
