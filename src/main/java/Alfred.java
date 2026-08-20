/**
 * Entry point for the Alfred chatbot.
 * Prints a greeting and immediately exits.
 */
public class Alfred {
    /** Horizontal divider used to frame chatbot messages. */
    private static final String LINE =
            "____________________________________________________________";

    private static final String BANNER =
            "     _    _     _____ ____  _____ ____  \n"
                    + "    / \\  | |   |  ___|  _ \\| ____|  _ \\ \n"
                    + "   / _ \\ | |   | |_  | |_) |  _| | | | |\n"
                    + "  / ___ \\| |___|  _| |  _ <| |___| |_| |\n"
                    + " /_/   \\_\\_____|_|   |_| \\_\\_____|____/";

    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Alfred.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }
}
