package alfred.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import alfred.task.Task;
import alfred.task.TaskDateTime;

/**
 * Reads user input and presents chatbot replies for the console or GUI.
 */
public class Ui {
    /** Horizontal divider used to frame chatbot messages. */
    private static final String LINE =
            "    ____________________________________________________________";

    /** Indent applied to chatbot message text on the console. */
    private static final String INDENT = "     ";

    private static final String BANNER =
            "        _    _  __              _\n"
                    + "       / \\  | |/ _|_ __ ___  __| |\n"
                    + "      / _ \\ | | |_| '__/ _ \\/ _` |\n"
                    + "     / ___ \\| |  _| | |  __/ (_| |\n"
                    + "    /_/   \\_\\_|_| |_|  \\___|\\__,_|";

    /** Reads commands from standard input when running as a console app. */
    private final Scanner scanner;

    /** {@code true} to print framed console output; {@code false} for GUI replies only. */
    private final boolean isConsole;

    /** Accumulates the latest reply so the GUI can display it. */
    private final StringBuilder replyBuffer;

    /** Creates a UI that reads from standard input and prints to the console. */
    public Ui() {
        this(true);
    }

    /**
     * Creates a UI for the console or for collecting GUI replies.
     *
     * @param isConsole {@code true} to print framed console output.
     */
    public Ui(boolean isConsole) {
        this.isConsole = isConsole;
        this.scanner = isConsole ? new Scanner(System.in) : null;
        this.replyBuffer = new StringBuilder();
    }

    /** Prints the welcome banner and opening prompt. */
    public void showWelcome() {
        startFrame();
        if (isConsole) {
            System.out.println(BANNER);
        }
        appendLine("Good day. I'm Alfred, at your service.");
        appendLine("How may I assist you?");
        endFrame();
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
     * Returns the accumulated reply and clears the buffer.
     *
     * @return Latest chatbot reply, without console dividers or indent.
     */
    public String consumeReply() {
        String reply = replyBuffer.toString();
        replyBuffer.setLength(0);
        return reply;
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
        showReply("Very good. I've added this task:\n  "
                + task.getDisplayText() + "\n"
                + "You now have " + taskCount + " tasks in your list.");
    }

    /**
     * Confirms that {@code task} was removed.
     *
     * @param task Removed task.
     * @param taskCount Number of tasks after the deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showReply("Noted. I've removed this task:\n  "
                + task.getDisplayText() + "\n"
                + "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Confirms that {@code task} was marked or unmarked.
     *
     * @param task Updated task.
     * @param isDone {@code true} if the task was marked done.
     */
    public void showTaskMarked(Task task, boolean isDone) {
        String response = isDone
                ? "Very good. I've marked this task as done:\n  "
                : "Certainly. I've marked this task as not done:\n  ";
        showReply(response + task.getDisplayText());
    }

    /**
     * Prints every task in insertion order.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        startFrame();
        appendLine("Certainly. Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            appendLine((i + 1) + "." + tasks.get(i).getDisplayText());
        }
        endFrame();
    }

    /**
     * Prints deadlines and events that fall on {@code query}'s calendar date.
     *
     * @param tasks Full task list, so original indices can be shown.
     * @param query Date typed after the {@code on} command.
     */
    public void showTasksOn(List<Task> tasks, TaskDateTime query) {
        LocalDate date = query.toLocalDate();
        startFrame();
        appendLine("Certainly. Here are the deadlines and events on "
                + query.toDisplayDate() + ":");
        int matchCount = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                appendLine((i + 1) + "." + tasks.get(i).getDisplayText());
                matchCount++;
            }
        }
        if (matchCount == 0) {
            appendLine("None, sir.");
        }
        endFrame();
    }

    /**
     * Prints tasks whose descriptions matched a {@code find} keyword.
     * Matches are numbered from 1 in the order they appear in the list.
     *
     * @param tasks Matching tasks to display.
     */
    public void showFoundTasks(List<Task> tasks) {
        startFrame();
        appendLine("Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            appendLine((i + 1) + "." + tasks.get(i).getDisplayText());
        }
        if (tasks.isEmpty()) {
            appendLine("None, sir.");
        }
        endFrame();
    }

    /** Frames a single chatbot reply between divider lines. */
    private void showReply(String message) {
        startFrame();
        String[] lines = message.split("\n", -1);
        for (String line : lines) {
            appendLine(line);
        }
        endFrame();
    }

    private void startFrame() {
        if (isConsole) {
            System.out.println(LINE);
        }
    }

    private void endFrame() {
        if (isConsole) {
            System.out.println(LINE);
        }
    }

    private void appendLine(String line) {
        if (replyBuffer.length() > 0) {
            replyBuffer.append('\n');
        }
        replyBuffer.append(line);
        if (isConsole) {
            System.out.println(INDENT + line);
        }
    }
}
