package alfred.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import alfred.AlfredException;
import alfred.command.Command;
import alfred.command.ExitCommand;
import alfred.command.ListCommand;
import alfred.command.OnCommand;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Tests {@link Parser#parse(String)} across valid commands, indexes, and error paths.
 */
public class ParserTest {
    @Test
    public void parse_bye_returnsExitCommand() throws AlfredException {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
        assertFalse(command.isMutating());
    }

    @Test
    public void parse_list_returnsListCommand() throws AlfredException {
        Command command = Parser.parse("list");
        assertInstanceOf(ListCommand.class, command);
        assertFalse(command.isExit());
        assertFalse(command.isMutating());
    }

    @Test
    public void parse_todoWithoutDescription_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse("todo"));
        assertEquals("a todo requires a description, sir.", exception.getMessage());
    }

    @Test
    public void parse_todoBlankDescription_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse("todo   "));
        assertEquals("a todo requires a description, sir.", exception.getMessage());
    }

    @Test
    public void parse_validTodo_addsTodoToList() throws AlfredException {
        TaskList tasks = new TaskList();
        Command command = Parser.parse("todo read book");
        assertTrue(command.isMutating());
        run(command, tasks);
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).getDisplayText());
    }

    @Test
    public void parse_deadlineWithoutBy_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse(
                "deadline submit report"));
        assertEquals("a deadline needs a description and a /by date or time, sir.",
                exception.getMessage());
    }

    @Test
    public void parse_deadlineMissingDescription_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse(
                "deadline /by 2019-10-15"));
        assertEquals("a deadline needs a description, sir.", exception.getMessage());
    }

    @Test
    public void parse_deadlineMissingByValue_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse(
                "deadline submit report /by"));
        assertEquals("a deadline needs a date or time after /by, sir.", exception.getMessage());
    }

    @Test
    public void parse_deadlineInvalidDate_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse(
                "deadline return book /by Sunday"));
        assertEquals("I need a date as yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm, sir.",
                exception.getMessage());
    }

    @Test
    public void parse_validDeadline_addsDeadlineToList() throws AlfredException {
        TaskList tasks = new TaskList();
        run(Parser.parse("deadline submit report /by 2019-10-15"), tasks);
        assertEquals("[D][ ] submit report (by: Oct 15 2019)", tasks.get(0).getDisplayText());
    }

    @Test
    public void parse_slashDateDeadline_parsesDayMonthYear() throws AlfredException {
        TaskList tasks = new TaskList();
        run(Parser.parse("deadline return book /by 2/12/2019"), tasks);
        assertEquals("[D][ ] return book (by: Dec 02 2019)", tasks.get(0).getDisplayText());
    }

    @Test
    public void parse_eventMissingFromTo_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse(
                "event project meeting /from 2019-10-15"));
        assertEquals("an event needs a description, a /from time, and a /to time, sir.",
                exception.getMessage());
    }

    @Test
    public void parse_eventEmptyFields_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse(
                "event meeting /from 2019-10-15 /to"));
        assertEquals("an event needs a description and both date/time fields, sir.",
                exception.getMessage());
    }

    @Test
    public void parse_eventEndBeforeStart_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse(
                "event camp /from 2019-10-16 /to 2019-10-15"));
        assertEquals("an event cannot end before it starts, sir.", exception.getMessage());
    }

    @Test
    public void parse_validEvent_addsEventToList() throws AlfredException {
        TaskList tasks = new TaskList();
        run(Parser.parse("event project meeting /from 2019-10-15 /to 2019-10-16"), tasks);
        assertEquals("[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)",
                tasks.get(0).getDisplayText());
    }

    @Test
    public void parse_unknownCommand_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse("blah"));
        assertEquals("I do not recognise that request, sir.", exception.getMessage());
    }

    @Test
    public void parse_onWithoutDate_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse("on"));
        assertEquals("an on command needs a date, sir.", exception.getMessage());
    }

    @Test
    public void parse_onInvalidDate_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse("on Sunday"));
        assertEquals("I need a date as yyyy-MM-dd or d/M/yyyy, optionally followed by HHmm, sir.",
                exception.getMessage());
    }

    @Test
    public void parse_onValidDate_returnsOnCommand() throws AlfredException {
        Command command = Parser.parse("on 2019-10-15");
        assertInstanceOf(OnCommand.class, command);
        assertFalse(command.isMutating());
    }

    @Test
    public void parse_markNonNumeric_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse("mark abc"));
        assertEquals("please provide a valid task number, sir.", exception.getMessage());
    }

    @Test
    public void parse_deleteNonNumeric_throwsException() {
        AlfredException exception = assertThrows(AlfredException.class, () -> Parser.parse("delete abc"));
        assertEquals("please provide a valid task number, sir.", exception.getMessage());
    }

    @Test
    public void parse_markAndUnmark_updatesCompletion() throws AlfredException {
        TaskList tasks = new TaskList();
        run(Parser.parse("todo read book"), tasks);
        run(Parser.parse("mark 1"), tasks);
        assertEquals("[T][X] read book", tasks.get(0).getDisplayText());
        run(Parser.parse("unmark 1"), tasks);
        assertEquals("[T][ ] read book", tasks.get(0).getDisplayText());
    }

    @Test
    public void parse_deleteValidIndex_removesTask() throws AlfredException {
        TaskList tasks = new TaskList();
        run(Parser.parse("todo keep this"), tasks);
        run(Parser.parse("todo remove this"), tasks);
        run(Parser.parse("delete 2"), tasks);
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] keep this", tasks.get(0).getDisplayText());
    }

    /**
     * Executes {@code command} while discarding chatbot output.
     */
    private static void run(Command command, TaskList tasks) throws AlfredException {
        PrintStream original = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        try {
            command.execute(tasks, new Ui());
        } finally {
            System.setOut(original);
        }
    }
}
