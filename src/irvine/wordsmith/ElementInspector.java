package irvine.wordsmith;

/**
 * Check for Roman numerals.
 * @author Sean A. Irvine
 */
public class ElementInspector implements Inspector {

  private static final String[] SYMBOLS = {"H", "He", "Li", "Be", "B", "C", "N", "O", "F", "Ne", "Na", "Mg", "Al", "Si", "P", "S", "Cl", "Ar", "K", "Ca", "Sc", "Ti", "V", "Cr", "Mn", "Fe", "Co", "Ni", "Cu", "Zn", "Ga", "Ge", "As", "Se", "Br", "Kr", "Rb", "Sr", "Y", "Zr", "Nb", "Mo", "Tc", "Ru", "Rh", "Pd", "Ag", "Cd", "In", "Sn", "Sb", "Te", "I", "Xe", "Cs", "Ba", "La", "Ce", "Pr", "Nd", "Pm", "Sm", "Eu", "Gd", "Tb", "Dy", "Ho", "Er", "Tm", "Yb", "Lu", "Hf", "Ta", "W", "Re", "Os", "Ir", "Pt", "Au", "Hg", "Tl", "Pb", "Bi", "Po", "At", "Rn", "Fr", "Ra", "Ac", "Th", "Pa", "U", "Np", "Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm", "Md", "No", "Lr", "Rf", "Db", "Sg", "Bh", "Hs", "Mt", "Ds", "Rg", "Cp", "Nh", "Fl", "Mc", "Lv", "Ts", "Og"};
  private static final String[] UPPER_SYMBOLS = {"H", "HE", "LI", "BE", "B", "C", "N", "O", "F", "NE", "NA", "MG", "AL", "SI", "P", "S", "CL", "AR", "K", "CA", "SC", "TI", "V", "CR", "MN", "FE", "CO", "NI", "CU", "ZN", "GA", "GE", "AS", "SE", "BR", "KR", "RB", "SR", "Y", "ZR", "NB", "MO", "TC", "RU", "RH", "PD", "AG", "CD", "IN", "SN", "SB", "TE", "I", "XE", "CS", "BA", "LA", "CE", "PR", "ND", "PM", "SM", "EU", "GD", "TB", "DY", "HO", "ER", "TM", "YB", "LU", "HF", "TA", "W", "RE", "OS", "IR", "PT", "AU", "HG", "TL", "PB", "BI", "PO", "AT", "RN", "FR", "RA", "AC", "TH", "PA", "U", "NP", "PU", "AM", "CM", "BK", "CF", "ES", "FM", "MD", "NO", "LR", "RF", "DB", "SG", "BH", "HS", "MT", "DS", "RG", "CP", "NH", "FL", "MC", "LV", "TS", "OG"};

  private static boolean contains(final String word, final String symbol) {
    final int a = word.indexOf(symbol.charAt(0));
    if (a < 0) {
      return false;
    }
    return symbol.length() == 1 || word.indexOf(symbol.charAt(1), a) >= 0;
  }

  private static boolean isIncreasing(final int start, final String[] words) {
    for (int k = 0; k < words.length; ++k) {
      if (!contains(words[k], UPPER_SYMBOLS[start + k])) {
        return false;
      }
    }
    return true;
  }

  private static boolean isDecreasing(final int start, final String[] words) {
    for (int k = 0; k < words.length; ++k) {
      if (!contains(words[k], UPPER_SYMBOLS[start - k])) {
        return false;
      }
    }
    return true;
  }

  private static boolean isChemicalSequence(final String word, final int pos) {
    if (pos >= word.length()) {
      return true;
    }
    for (final String symbol : UPPER_SYMBOLS) {
      if (word.startsWith(symbol, pos) && isChemicalSequence(word, pos + symbol.length())) {
        return true;
      }
    }
    return false;
  }

  private static boolean isChemicalSequence(final String... words) {
    for (final String word : words) {
      if (!isChemicalSequence(word, 0)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String inspect(final String... words) {
    if (isChemicalSequence(words)) {
      return "Every word can be written as a concatenation of chemical element symbols";
    }
    if (words.length < 3) {
      return null;
    }
    for (int start = 0; start < SYMBOLS.length - words.length; ++start) {
      if (isIncreasing(start, words)) {
        return "Contains increasing sequence of chemical elements symbols starting with " + SYMBOLS[start];
      }
      if (isDecreasing(start + words.length - 1, words)) {
        return "Contains decreasing sequence of chemical elements symbols starting with " + SYMBOLS[start + words.length - 1];
      }
    }
    return null;
  }
}
