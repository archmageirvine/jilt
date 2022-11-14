package irvine.util;

import java.text.NumberFormat;

/**
 * Double utilities.
 * @author Sean A. Irvine
 */
public final class DoubleUtils {

  private DoubleUtils() { }

  /** Printing with two decimal places. */
  public static final NumberFormat NF2 = NumberFormat.getNumberInstance();
  /** Printing with three decimal places. */
  public static final NumberFormat NF3 = NumberFormat.getNumberInstance();
  /** Printing with four decimal places. */
  public static final NumberFormat NF4 = NumberFormat.getNumberInstance();
  /** Printing with five decimal places. */
  public static final NumberFormat NF5 = NumberFormat.getNumberInstance();
  /** Printing with eight decimal places. */
  public static final NumberFormat NF8 = NumberFormat.getNumberInstance();
  static {
    NF2.setMinimumFractionDigits(2);
    NF2.setMaximumFractionDigits(2);
    NF2.setGroupingUsed(false);
    NF3.setMinimumFractionDigits(3);
    NF3.setMaximumFractionDigits(3);
    NF3.setGroupingUsed(false);
    NF4.setMinimumFractionDigits(4);
    NF4.setMaximumFractionDigits(4);
    NF4.setGroupingUsed(false);
    NF5.setMinimumFractionDigits(5);
    NF5.setMaximumFractionDigits(5);
    NF5.setGroupingUsed(false);
    NF8.setMinimumFractionDigits(8);
    NF8.setMaximumFractionDigits(8);
    NF8.setGroupingUsed(false);
  }
}
