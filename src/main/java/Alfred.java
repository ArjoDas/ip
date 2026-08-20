import java.util.Scanner;

/**
 * Entry point for the Alfred chatbot.
 * Greets the user, echoes each command, and exits on {@code bye}.
 */
public class Alfred {
    /** Maximum number of tasks Alfred can store during one run. */
    private static final int MAX_TASKS = 100;

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

    /** Tasks entered by the user during the current run. */
    private static final Task[] tasks = new Task[MAX_TASKS];

    /** Number of tasks currently stored. */
    private static int taskCount = 0;

    public static void main(String[] args) {
        greet();
        echoUntilBye();
    }

    /** Prints the welcome banner and opening prompt. */
    private static void greet() {
        printLine();
        System.out.println(BANNER);
        printMessage("Hello! I'm Alfred.");
        printMessage("What can I do for you?");
        printLine();
    }

    /** Reads user commands and handles task storage, listing, and exit. */
    private static void echoUntilBye() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                showReply("Bye. Hope to see you again soon!");
                break;
            }
            if (command.equals("list")) {
                showTaskList();
            } else if (command.startsWith("mark ")) {
                updateTaskStatus(command, true);
            } else if (command.startsWith("unmark ")) {
                updateTaskStatus(command, false);
            } else if (command.startsWith("todo ")) {
                addTask(new ToDo(command.substring(5)));
            } else if (command.startsWith("deadline ")) {
                addDeadline(command);
            } else if (command.startsWith("event ")) {
                addEvent(command);
            } else {
                // catchall for undeclared task types
                addTask(new ToDo(command));
            }
        }
    }

    /** Stores a task and confirms that it was added. */
    private static void addTask(Task task) {
        if (taskCount < MAX_TASKS) {
            tasks[taskCount] = task;
            taskCount++;
            showReply("Got it. I've added this task:\n" + INDENT + "  "
                    + task.getDisplayText() + "\n"
                    + INDENT + "Now you have " + taskCount + " tasks in the list.");
        } else {
            showReply("Task limit reached.");
        }
    }

    /** Parses and stores a deadline command. */
    private static void addDeadline(String command) {
        int delimiter = command.indexOf(" /by ");
        if (delimiter < 0) {
            addTask(new ToDo(command.substring(9)));
            return;
        }
        addTask(new Deadline(command.substring(9, delimiter),
                command.substring(delimiter + 5)));
    }

    /** Parses and stores an event command. */
    private static void addEvent(String command) {
        int fromDelimiter = command.indexOf(" /from ");
        int toDelimiter = command.indexOf(" /to ");
        if (fromDelimiter < 0 || toDelimiter < 0 || toDelimiter < fromDelimiter) {
            addTask(new ToDo(command.substring(6)));
            return;
        }
        addTask(new Event(command.substring(6, fromDelimiter),
                command.substring(fromDelimiter + 7, toDelimiter),
                command.substring(toDelimiter + 5)));
    }

    /** Displays all tasks in the order they were entered. */
    private static void showTaskList() {
        printLine();
        printMessage("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            printMessage((i + 1) + "." + tasks[i].getDisplayText());
        }
        printLine();
    }

    /** Marks or unmarks the task identified by a one-based index. */
    private static void updateTaskStatus(String command, boolean isDone) {
        try {
            int taskNumber = Integer.parseInt(command.substring(command.indexOf(' ') + 1));
            if (taskNumber < 1 || taskNumber > taskCount) {
                showReply("That task number does not exist.");
                return;
            }
            int taskIndex = taskNumber - 1;
            if (isDone) {
                tasks[taskIndex].markAsDone();
            } else {
                tasks[taskIndex].markAsNotDone();
            }
            String response = isDone
                    ? "Nice! I've marked this task as done:\n" + INDENT + "  "
                    : "OK, I've marked this task as not done yet:\n" + INDENT + "  ";
                showReply(response + tasks[taskIndex].getDisplayText());
        } catch (NumberFormatException exception) {
            showReply("Please provide a valid task number.");
        }
    }

    /** Frames a single chatbot reply between divider lines. */
    private static void showReply(String message) {
        printLine();
        printMessage(message);
        printLine();
    }

    private static void printLine() {
        System.out.println(LINE);
    }

    private static void printMessage(String message) {
        System.out.println(INDENT + message);
    }
}
