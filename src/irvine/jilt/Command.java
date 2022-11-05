package irvine.jilt;

/**
 * Provides an execution entry point for an individual command.
 * @author Sean A. Irvine
 */
public abstract class Command {

  private final String mDescription;

  /**
   * Construct a new command.
   * @param description description of the command
   */
  protected Command(final String description) {
    mDescription = description;
  }

  String getDescription() {
    return mDescription;
  }

  /**
   * Run the command with the specified arguments.
   * @param args arguments
   */
  protected abstract void mainExec(final String... args);
}
