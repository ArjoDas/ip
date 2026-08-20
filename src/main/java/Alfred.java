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
    private static final String[] tasks = new String[MAX_TASKS];

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
            } else {
                addTask(command);
            }
        }
    }

    /** Stores a task and confirms that it was added. */
    private static void addTask(String task) {
        if (taskCount < MAX_TASKS) {
            tasks[taskCount] = task;
            taskCount++;
            showReply("added: " + task);
        } else {
            showReply("Task limit reached.");
        }
    }

    /** Displays all tasks in the order they were entered. */
    private static void showTaskList() {
        printLine();
        for (int i = 0; i < taskCount; i++) {
            printMessage((i + 1) + ". " + tasks[i]);
        }
        printLine();
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
