import java.io.IOException;
import java.nio.file.Path;
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

    /** Persists tasks to {@code data/alfred.txt} relative to the working directory. */
    private static final Storage storage = new Storage(Path.of("data", "alfred.txt"));

    /** Number of tasks currently stored. */
    private static int taskCount = 0;

    public static void main(String[] args) {
        boolean wasLoadError = restoreTasks();
        greet();
        if (wasLoadError) {
            showError("I could not read your saved tasks, sir.");
        }
        echoUntilBye();
    }

    /** Prints the welcome banner and opening prompt. */
    private static void greet() {
        printLine();
        System.out.println(BANNER);
        printMessage("Good day. I'm Alfred, at your service.");
        printMessage("How may I assist you?");
        printLine();
    }

    /** Reads user commands and handles task storage, listing, and exit. */
    private static void echoUntilBye() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                showReply("Until next time. I shall be here should you require me.");
                break;
            }
            if (command.equals("list")) {
                showTaskList();
            } else if (command.startsWith("mark ")) {
                updateTaskStatus(command, true);
            } else if (command.startsWith("unmark ")) {
                updateTaskStatus(command, false);
            } else if (command.startsWith("delete ")) {
                deleteTask(command);
            } else if (command.equals("todo")) {
                showError("a todo requires a description, sir.");
            } else if (command.startsWith("todo ")) {
                addTodo(command.substring("todo ".length()));
            } else if (command.equals("deadline")) {
                showError("a deadline requires a description, sir.");
            } else if (command.startsWith("deadline ")) {
                addDeadline(command);
            } else if (command.equals("event")) {
                showError("an event requires a description and its times, sir.");
            } else if (command.startsWith("event ")) {
                addEvent(command);
            } else {
                showError("I do not recognise that request, sir.");
            }
        }
    }

    /** Stores a task and confirms that it was added. */
    private static void addTask(Task task) {
        if (taskCount < MAX_TASKS) {
            tasks[taskCount] = task;
            taskCount++;
            showReply("Very good. I've added this task:\n" + INDENT + "  "
                    + task.getDisplayText() + "\n"
                    + INDENT + "You now have " + taskCount + " tasks in your list.");
            persistTasks();
        } else {
            showReply("I'm afraid your task list is full, sir.");
        }
    }

    /** Validates and stores a todo description. */
    private static void addTodo(String description) {
        if (description.trim().isEmpty()) {
            showError("a todo requires a description, sir.");
            return;
        }
        addTask(new ToDo(description));
    }

    /** Deletes the task identified by a one-based index. */
    private static void deleteTask(String command) {
        try {
            int taskNumber = Integer.parseInt(command.substring("delete ".length()));
            if (taskNumber < 1 || taskNumber > taskCount) {
                showError("that task number does not exist, sir.");
                return;
            }
            int taskIndex = taskNumber - 1;
            Task deletedTask = tasks[taskIndex];
            for (int i = taskIndex; i < taskCount - 1; i++) {
                tasks[i] = tasks[i + 1];
            }
            tasks[taskCount - 1] = null;
            taskCount--;
            showReply("Noted. I've removed this task:\n" + INDENT + "  "
                    + deletedTask.getDisplayText() + "\n"
                    + INDENT + "Now you have " + taskCount + " tasks in the list.");
            persistTasks();
        } catch (NumberFormatException exception) {
            showError("please provide a valid task number, sir.");
        }
    }

    /** Displays an error without changing the task list. */
    private static void showError(String message) {
        showReply("I'm afraid I must report: " + message);
    }

    /** Parses and stores a deadline command. */
    private static void addDeadline(String command) {
        String body = command.substring("deadline ".length());
        int delimiter = body.indexOf("/by");
        if (delimiter < 0) {
            showError("a deadline needs a description and a /by date or time, sir.");
            return;
        }
        String description = body.substring(0, delimiter).trim();
        String deadline = body.substring(delimiter + 3).trim();
        if (description.trim().isEmpty() || deadline.trim().isEmpty()) {
            if (description.trim().isEmpty()) {
                showError("a deadline needs a description, sir.");
            } else {
                showError("a deadline needs a date or time after /by, sir.");
            }
            return;
        }
        addTask(new Deadline(description, deadline));
    }

    /** Parses and stores an event command. */
    private static void addEvent(String command) {
        String body = command.substring("event ".length());
        int fromDelimiter = body.indexOf("/from");
        int toDelimiter = body.indexOf("/to");
        if (fromDelimiter < 0 || toDelimiter < 0 || toDelimiter < fromDelimiter) {
            showError("an event needs a description, a /from time, and a /to time, sir.");
            return;
        }
        String description = body.substring(0, fromDelimiter).trim();
        String from = body.substring(fromDelimiter + 5, toDelimiter).trim();
        String to = body.substring(toDelimiter + 3).trim();
        if (description.trim().isEmpty() || from.trim().isEmpty() || to.trim().isEmpty()) {
            showError("an event needs a description and both date/time fields, sir.");
            return;
        }
        addTask(new Event(description, from, to));
    }

    /** Displays all tasks in the order they were entered. */
    private static void showTaskList() {
        printLine();
        printMessage("Certainly. Here are the tasks in your list:");
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
                showError("that task number does not exist, sir.");
                return;
            }
            int taskIndex = taskNumber - 1;
            if (isDone) {
                tasks[taskIndex].markAsDone();
            } else {
                tasks[taskIndex].markAsNotDone();
            }
            String response = isDone
                    ? "Very good. I've marked this task as done:\n" + INDENT + "  "
                    : "Certainly. I've marked this task as not done:\n" + INDENT + "  ";
            showReply(response + tasks[taskIndex].getDisplayText());
            persistTasks();
        } catch (NumberFormatException exception) {
            showError("please provide a valid task number, sir.");
        }
    }

    /**
     * Loads saved tasks into memory.
     *
     * @return {@code true} if the save file existed but could not be read.
     */
    private static boolean restoreTasks() {
        try {
            taskCount = storage.load(tasks);
            return false;
        } catch (IOException exception) {
            taskCount = 0;
            return true;
        }
    }

    /** Writes the current task list to disk, reporting I/O failures to the user. */
    private static void persistTasks() {
        try {
            storage.save(tasks, taskCount);
        } catch (IOException exception) {
            showError("I could not save your tasks, sir.");
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
