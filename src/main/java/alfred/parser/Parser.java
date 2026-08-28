package alfred.parser;

import alfred.AlfredException;
import alfred.command.AddCommand;
import alfred.command.Command;
import alfred.command.DeleteCommand;
import alfred.command.ExitCommand;
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
        if (fullCommand.equals("on")) {
            throw new AlfredException("an on command needs a date, sir.");
        }
        if (fullCommand.startsWith("on ")) {
            return parseOn(fullCommand.substring("on ".length()));
        }
        if (fullCommand.startsWith("mark ")) {
            return new MarkCommand(parseTaskIndex(fullCommand), true);
        }
        if (fullCommand.startsWith("unmark ")) {
            return new MarkCommand(parseTaskIndex(fullCommand), false);
        }
        if (fullCommand.startsWith("delete ")) {
            return new DeleteCommand(parseDeleteIndex(fullCommand));
        }
        if (fullCommand.equals("todo")) {
            throw new AlfredException("a todo requires a description, sir.");
        }
        if (fullCommand.startsWith("todo ")) {
            return parseTodo(fullCommand.substring("todo ".length()));
        }
        if (fullCommand.equals("deadline")) {
            throw new AlfredException("a deadline requires a description, sir.");
        }
        if (fullCommand.startsWith("deadline ")) {
            return parseDeadline(fullCommand);
        }
        if (fullCommand.equals("event")) {
            throw new AlfredException("an event requires a description and its times, sir.");
        }
        if (fullCommand.startsWith("event ")) {
            return parseEvent(fullCommand);
        }
        throw new AlfredException("I do not recognise that request, sir.");
    }

    private static Command parseOn(String dateText) throws AlfredException {
        TaskDateTime query = TaskDateTime.parseUserInput(dateText);
        if (query == null) {
            throw new AlfredException(DATE_FORMAT_HINT);
        }
        return new OnCommand(query);
    }

    private static Command parseTodo(String description) throws AlfredException {
        if (description.trim().isEmpty()) {
            throw new AlfredException("a todo requires a description, sir.");
        }
        return new AddCommand(new ToDo(description));
    }

    private static Command parseDeadline(String command) throws AlfredException {
        String body = command.substring("deadline ".length());
        int delimiter = body.indexOf("/by");
        if (delimiter < 0) {
            throw new AlfredException("a deadline needs a description and a /by date or time, sir.");
        }
        String description = body.substring(0, delimiter).trim();
        String deadline = body.substring(delimiter + 3).trim();
        if (description.trim().isEmpty() || deadline.trim().isEmpty()) {
            if (description.trim().isEmpty()) {
                throw new AlfredException("a deadline needs a description, sir.");
            }
            throw new AlfredException("a deadline needs a date or time after /by, sir.");
        }
        TaskDateTime by = TaskDateTime.parseUserInput(deadline);
        if (by == null) {
            throw new AlfredException(DATE_FORMAT_HINT);
        }
        return new AddCommand(new Deadline(description, by));
    }

    private static Command parseEvent(String command) throws AlfredException {
        String body = command.substring("event ".length());
        int fromDelimiter = body.indexOf("/from");
        int toDelimiter = body.indexOf("/to");
        if (fromDelimiter < 0 || toDelimiter < 0 || toDelimiter < fromDelimiter) {
            throw new AlfredException(
                    "an event needs a description, a /from time, and a /to time, sir.");
        }
        String description = body.substring(0, fromDelimiter).trim();
        String from = body.substring(fromDelimiter + 5, toDelimiter).trim();
        String to = body.substring(toDelimiter + 3).trim();
        if (description.trim().isEmpty() || from.trim().isEmpty() || to.trim().isEmpty()) {
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
     * Returns the zero-based index from a {@code mark} or {@code unmark} command.
     */
    private static int parseTaskIndex(String command) throws AlfredException {
        try {
            int taskNumber = Integer.parseInt(command.substring(command.indexOf(' ') + 1));
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new AlfredException("please provide a valid task number, sir.");
        }
    }

    /**
     * Returns the zero-based index from a {@code delete} command.
     */
    private static int parseDeleteIndex(String command) throws AlfredException {
        try {
            int taskNumber = Integer.parseInt(command.substring("delete ".length()));
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new AlfredException("please provide a valid task number, sir.");
        }
    }
}
