package irvine.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * A bidirectional trie for characters.
 * @author Sean A. Irvine
 */
public class Trie {

  private boolean mIsTerminal;
  private final HashMap<Character, Trie> mChildren = new HashMap<>();
  private final Trie mParent;

  /**
   * Construct a trie node.
   * @param parent parent node (or null for the root)
   * @param isTerminal true if this node an end point
   */
  public Trie(final Trie parent, final boolean isTerminal) {
    mIsTerminal = isTerminal;
    mParent = parent;
  }

  public boolean isTerminal() {
    return mIsTerminal;
  }

  public Trie getParent() {
    return mParent;
  }

  public HashMap<Character, Trie> getChildren() {
    return mChildren;
  }

  /**
   * Get the child node for a particular character.
   * @param c character
   * @return node
   */
  public Trie getChild(final char c) {
    return mChildren.get(c);
  }

  /**
   * Add a string to the trie.
   * @param str string to add
   */
  public void add(final String str) {
    if (str.isEmpty()) {
      mIsTerminal = true;
    } else {
      final char c = str.charAt(0);
      final Trie t = mChildren.get(c);
      if (t == null) {
        final Trie u = new Trie(this, false);
        mChildren.put(c, u);
        u.add(str.substring(1));
      } else {
        t.add(str.substring(1));
      }
    }
  }

  /**
   * Test if the specified string is in the trie.
   * @param str string to test
   * @return true iff the word is in the trie.
   */
  public boolean contains(final String str) {
    if (str.isEmpty()) {
      return isTerminal();
    }
    final Trie t = getChild(str.charAt(0));
    if (t == null) {
      return false;
    }
    return t.contains(str.substring(1));
  }

  /**
   * Build a trie containing all the words from the specified source.
   * @param reader source of words
   * @param minLength minimum length of word to retain
   * @param maxLength maximum length of word to retain
   * @return set of words
   * @throws IOException if an I/O problem occurs
   */
  public static Trie buildTrie(final BufferedReader reader, final int minLength, final int maxLength) throws IOException {
    final Trie root = new Trie(null, false);
    String line;
    while ((line = reader.readLine()) != null) {
      if (line.length() >= minLength && line.length() <= maxLength) {
        root.add(line);
      }
    }
    return root;
  }
}
