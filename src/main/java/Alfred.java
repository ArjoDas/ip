import java.util.Scanner;

/**
 * Entry point for the Alfred chatbot.
 * Greets the user, echoes each command, and exits on {@code bye}.
 */
public class Alfred {
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

    /**
     * Reads user commands from standard input, echoes each one,
     * and stops when the user types {@code bye}.
     */
    private static void echoUntilBye() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                showReply("Bye. Hope to see you again soon!");
                break;
            }
            showReply(command);
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
