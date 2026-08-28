package alfred.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import alfred.task.Task;
import alfred.task.TaskDateTime;

/**
 * Reads user input and prints chatbot replies.
 */
public class Ui {
    /** Horizontal divider used to frame chatbot messages. */
    private static final String LINE =
            "    ____________________________________________________________";

    /** Indent applied to chatbot message text. */
    private static final String INDENT = "     ";

    private static final String BANNER =
            "        _    _  __              _\n"
                    + "       / \\  | |/ _|_ __ ___  __| |\n"
                    + "      / _ \\ | | |_| '__/ _ \\/ _` |\n"
                    + "     / ___ \\| |  _| | |  __/ (_| |\n"
                    + "    /_/   \\_\\_|_| |_|  \\___|\\__,_|";

    /** Reads commands from standard input. */
    private final Scanner scanner;

    /** Creates a UI that reads from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Prints the welcome banner and opening prompt. */
    public void showWelcome() {
        printLine();
        System.out.println(BANNER);
        printMessage("Good day. I'm Alfred, at your service.");
        printMessage("How may I assist you?");
        printLine();
    }

    /** Prints the farewell message. */
    public void showGoodbye() {
        showReply("Until next time. I shall be here should you require me.");
    }

    /**
     * Returns the next command line typed by the user.
     *
     * @return Raw command text, not including the trailing newline.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Prints an error framed as a chatbot reply.
     *
     * @param message Explanation shown after the error prefix.
     */
    public void showError(String message) {
        showReply("I'm afraid I must report: " + message);
    }

    /** Prints the message used when the save file cannot be read. */
    public void showLoadingError() {
        showError("I could not read your saved tasks, sir.");
    }

    /**
     * Confirms that {@code task} was added.
     *
     * @param task Newly stored task.
     * @param taskCount Number of tasks after the add.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showReply("Very good. I've added this task:\n" + INDENT + "  "
                + task.getDisplayText() + "\n"
                + INDENT + "You now have " + taskCount + " tasks in your list.");
    }

    /**
     * Confirms that {@code task} was removed.
     *
     * @param task Removed task.
     * @param taskCount Number of tasks after the deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showReply("Noted. I've removed this task:\n" + INDENT + "  "
                + task.getDisplayText() + "\n"
                + INDENT + "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Confirms that {@code task} was marked or unmarked.
     *
     * @param task Updated task.
     * @param isDone {@code true} if the task was marked done.
     */
    public void showTaskMarked(Task task, boolean isDone) {
        String response = isDone
                ? "Very good. I've marked this task as done:\n" + INDENT + "  "
                : "Certainly. I've marked this task as not done:\n" + INDENT + "  ";
        showReply(response + task.getDisplayText());
    }

    /**
     * Prints every task in insertion order.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        printLine();
        printMessage("Certainly. Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            printMessage((i + 1) + "." + tasks.get(i).getDisplayText());
        }
        printLine();
    }

    /**
     * Prints deadlines and events that fall on {@code query}'s calendar date.
     *
     * @param tasks Full task list, so original indices can be shown.
     * @param query Date typed after the {@code on} command.
     */
    public void showTasksOn(List<Task> tasks, TaskDateTime query) {
        LocalDate date = query.toLocalDate();
        printLine();
        printMessage("Certainly. Here are the deadlines and events on "
                + query.toDisplayDate() + ":");
        int matchCount = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                printMessage((i + 1) + "." + tasks.get(i).getDisplayText());
                matchCount++;
            }
        }
        if (matchCount == 0) {
            printMessage("None, sir.");
        }
        printLine();
    }

    /** Frames a single chatbot reply between divider lines. */
    private void showReply(String message) {
        printLine();
        printMessage(message);
        printLine();
    }

    private void printLine() {
        System.out.println(LINE);
    }

    private void printMessage(String message) {
        System.out.println(INDENT + message);
    }
}
