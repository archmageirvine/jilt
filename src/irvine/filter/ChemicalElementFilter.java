package irvine.filter;

import java.util.Locale;

import irvine.wordsmith.ElementInspector;

/**
 * Test if the word can be written as a sequence of chemical symbols.
 * @author Sean A. Irvine
 */
public class ChemicalElementFilter implements Filter {

  @Override
  public boolean is(final String word) {
    return ElementInspector.isChemicalSequence(word.toUpperCase(Locale.getDefault()).replace(" ", ""), 0);
  }
}
