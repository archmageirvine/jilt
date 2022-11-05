package irvine.jilt;

import java.util.Arrays;
import java.util.Locale;

import irvine.util.StringUtils;

/**
 * Main launcher for JILT, calling individual modules as required.
 * @author Sean A. Irvine
 */
public final class Jilt {

  private Jilt() {
  }

  private static final class Help extends Command {

    private static final int SPACING = 12;

    private Help() {
      super("List available modules");
    }

    @Override
    protected void mainExec(final String... args) {
      System.out.println("Java Interactive Language Tools (JILT)");
      System.out.println();
      System.out.println("Available modules:");
      for (final Module mod : Module.values()) {
        final String modName = mod.toString();
        System.out.println(modName + StringUtils.rep(' ', SPACING - modName.length()) + mod.getCommand().getDescription());
      }
      System.out.println();
      System.out.println("For help on a specific module do \"jilt module-name --help\"");
    }
  }

  private enum Module {
    /** Dummy module that returns the list of possible modules. */
    HELP(new Help()),
    /** Simple anagrams. */
    ANAGRAM(new Command("Anagram") {
      @Override
      protected void mainExec(final String... args) {

      }
    }),
    ;

    private final Command mCommand;

    Module(final Command command) {
      mCommand = command;
    }

    Command getCommand() {
      return mCommand;
    }

    @Override
    public String toString() {
      return super.toString().toLowerCase(Locale.getDefault());
    }
  }

  /**
   * Main program.
   * @param args module followed by module arguments
   */
  public static void main(final String... args) {
    if (args == null || args.length == 0 || "-h".equals(args[0]) || "--help".equals(args[0])) {
      Module.HELP.getCommand().mainExec();
      return;
    }
    final String module = args[0];
    final Module mod = Module.valueOf(module.toUpperCase(Locale.getDefault()));
    mod.getCommand().mainExec(Arrays.copyOfRange(args, 1, args.length));
  }
}
