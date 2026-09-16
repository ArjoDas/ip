package alfred.parser;

import alfred.AlfredException;
import alfred.command.AddCommand;
import alfred.command.Command;
import alfred.command.DeleteCommand;
import alfred.command.ExitCommand;
import alfred.command.FindCommand;
import alfred.command.ListCommand;
import alfred.command.MarkCommand;
import alfred.command.OnCommand;
import alfred.task.Deadline;
import alfred.task.Event;
import alfred.task.TaskDateTime;
import alfred.task.ToDo;

/**
 * Interprets a raw user command as an executable {@link Command}.
 */
public class Parser {
    /** Hint shown when the user types a date Alfred cannot parse. */
    private static final String DATE_FORMAT_HINT =
            "I need a date as yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm, sir.";

    /** Deadline delimiter that introduces the due date. */
    private static final String PREFIX_BY = "/by";

    /** Event delimiter that introduces the start date or time. */
    private static final String PREFIX_FROM = "/from";

    /** Event delimiter that introduces the end date or time. */
    private static final String PREFIX_TO = "/to";

    /** Prevents instantiation; {@link #parse(String)} is the only entry point. */
    private Parser() {
    }

    /**
     * Returns the command represented by {@code fullCommand}.
     *
     * @param fullCommand Entire line typed by the user.
     * @return Command ready to execute.
     * @throws AlfredException If the line is not a recognized, well-formed command.
     */
    public static Command parse(String fullCommand) throws AlfredException {
        if (fullCommand.equals("bye")) {
            return new ExitCommand();
        }
        if (fullCommand.equals("list")) {
            return new ListCommand();
        }
        if (isCommand(fullCommand, "find")) {
            return parseFind(argumentsAfter(fullCommand, "find"));
        }
        if (isCommand(fullCommand, "on")) {
            return parseOn(argumentsAfter(fullCommand, "on"));
        }
        if (isCommand(fullCommand, "mark")) {
            return new MarkCommand(parseTaskIndex(argumentsAfter(fullCommand, "mark")), true);
        }
        if (isCommand(fullCommand, "unmark")) {
            return new MarkCommand(parseTaskIndex(argumentsAfter(fullCommand, "unmark")), false);
        }
        if (isCommand(fullCommand, "delete")) {
            return new DeleteCommand(parseTaskIndex(argumentsAfter(fullCommand, "delete")));
        }
        if (isCommand(fullCommand, "todo")) {
            return parseTodo(argumentsAfter(fullCommand, "todo"));
        }
        if (isCommand(fullCommand, "deadline")) {
            return parseDeadline(argumentsAfter(fullCommand, "deadline"));
        }
        if (isCommand(fullCommand, "event")) {
            return parseEvent(argumentsAfter(fullCommand, "event"));
        }
        throw new AlfredException("I do not recognise that request, sir.");
    }

    /**
     * Returns {@code true} if {@code fullCommand} is {@code commandWord}, or starts with that
     * word followed by a space.
     */
    private static boolean isCommand(String fullCommand, String commandWord) {
        return fullCommand.equals(commandWord)
                || fullCommand.startsWith(commandWord + " ");
    }

    /**
     * Returns the text after {@code commandWord}, or an empty string when the word stands alone.
     */
    private static String argumentsAfter(String fullCommand, String commandWord) {
        if (fullCommand.equals(commandWord)) {
            return "";
        }
        return fullCommand.substring((commandWord + " ").length());
    }

    private static Command parseFind(String keyword) throws AlfredException {
        String trimmed = keyword.trim();
        if (trimmed.isEmpty()) {
            throw new AlfredException("a find command needs a keyword, sir.");
        }
        return new FindCommand(trimmed);
    }

    private static Command parseOn(String dateText) throws AlfredException {
        if (dateText.trim().isEmpty()) {
            throw new AlfredException("an on command needs a date, sir.");
        }
        TaskDateTime query = TaskDateTime.parseUserInput(dateText);
        if (query == null) {
            throw new AlfredException(DATE_FORMAT_HINT);
        }
        return new OnCommand(query);
    }

    private static Command parseTodo(String description) throws AlfredException {
        String trimmedDescription = description.trim();
        if (trimmedDescription.isEmpty()) {
            throw new AlfredException("a todo requires a description, sir.");
        }
        return new AddCommand(new ToDo(trimmedDescription));
    }

    private static Command parseDeadline(String body) throws AlfredException {
        if (body.isEmpty()) {
            throw new AlfredException("a deadline requires a description, sir.");
        }
        int delimiter = body.indexOf(PREFIX_BY);
        if (delimiter < 0) {
            throw new AlfredException("a deadline needs a description and a /by date or time, sir.");
        }
        String description = body.substring(0, delimiter).trim();
        String deadline = body.substring(delimiter + PREFIX_BY.length()).trim();
        if (description.isEmpty()) {
            throw new AlfredException("a deadline needs a description, sir.");
        }
        if (deadline.isEmpty()) {
            throw new AlfredException("a deadline needs a date or time after /by, sir.");
        }
        TaskDateTime by = TaskDateTime.parseUserInput(deadline);
        if (by == null) {
            throw new AlfredException(DATE_FORMAT_HINT);
        }
        return new AddCommand(new Deadline(description, by));
    }

    private static Command parseEvent(String body) throws AlfredException {
        if (body.isEmpty()) {
            throw new AlfredException("an event requires a description and its times, sir.");
        }
        int fromDelimiter = body.indexOf(PREFIX_FROM);
        int toDelimiter = body.indexOf(PREFIX_TO);
        if (fromDelimiter < 0 || toDelimiter < 0 || toDelimiter < fromDelimiter) {
            throw new AlfredException(
                    "an event needs a description, a /from time, and a /to time, sir.");
        }
        String description = body.substring(0, fromDelimiter).trim();
        String from = body.substring(fromDelimiter + PREFIX_FROM.length(), toDelimiter).trim();
        String to = body.substring(toDelimiter + PREFIX_TO.length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new AlfredException("an event needs a description and both date/time fields, sir.");
        }
        TaskDateTime fromDateTime = TaskDateTime.parseUserInput(from);
        TaskDateTime toDateTime = TaskDateTime.parseUserInput(to);
        if (fromDateTime == null || toDateTime == null) {
            throw new AlfredException(DATE_FORMAT_HINT);
        }
        if (fromDateTime.isAfter(toDateTime)) {
            throw new AlfredException("an event cannot end before it starts, sir.");
        }
        return new AddCommand(new Event(description, fromDateTime, toDateTime));
    }

    /**
     * Returns the zero-based index from a {@code mark}, {@code unmark}, or {@code delete}
     * argument.
     */
    private static int parseTaskIndex(String argument) throws AlfredException {
        try {
            int taskNumber = Integer.parseInt(argument);
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new AlfredException("please provide a valid task number, sir.");
        }
    }
}
