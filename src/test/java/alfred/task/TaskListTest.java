package alfred.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import alfred.AlfredException;

/**
 * Tests add, get, delete, mark, unmark, and bounds checks on {@link TaskList}.
 */
public class TaskListTest {
    @Test
    public void add_appendsInInsertionOrder() throws AlfredException {
        TaskList tasks = new TaskList();
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        tasks.add(first);
        tasks.add(second);
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    @Test
    public void delete_validIndex_removesAndReturnsTask() throws AlfredException {
        TaskList tasks = new TaskList();
        Task keep = new ToDo("keep");
        Task remove = new ToDo("remove");
        tasks.add(keep);
        tasks.add(remove);
        Task deleted = tasks.delete(1);
        assertSame(remove, deleted);
        assertEquals(1, tasks.size());
        assertSame(keep, tasks.get(0));
    }

    @Test
    public void delete_invalidIndex_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("only"));
        AlfredException emptyIndex = assertThrows(AlfredException.class, () -> tasks.delete(1));
        assertEquals("that task number does not exist, sir.", emptyIndex.getMessage());
        AlfredException negative = assertThrows(AlfredException.class, () -> tasks.delete(-1));
        assertEquals("that task number does not exist, sir.", negative.getMessage());
    }

    @Test
    public void get_emptyList_throwsException() {
        TaskList tasks = new TaskList();
        AlfredException exception = assertThrows(AlfredException.class, () -> tasks.get(0));
        assertEquals("that task number does not exist, sir.", exception.getMessage());
    }

    @Test
    public void markAndUnmark_updateCompletion() throws AlfredException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        Task marked = tasks.mark(0);
        assertEquals("[T][X] read book", marked.getDisplayText());
        Task unmarked = tasks.unmark(0);
        assertEquals("[T][ ] read book", unmarked.getDisplayText());
    }

    @Test
    public void mark_invalidIndex_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        AlfredException exception = assertThrows(AlfredException.class, () -> tasks.mark(4));
        assertEquals("that task number does not exist, sir.", exception.getMessage());
    }

    @Test
    public void getTasks_returnsUnmodifiableView() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        List<Task> view = tasks.getTasks();
        assertEquals(1, view.size());
        assertThrows(UnsupportedOperationException.class, () -> view.add(new ToDo("other")));
    }

    @Test
    public void find_keywordInDescription_returnsMatchingTasksInOrder() {
        TaskList tasks = new TaskList();
        Task readBook = new ToDo("read book");
        Task meeting = new ToDo("project meeting");
        Task returnBook = new Deadline("return book", TaskDateTime.parseUserInput("2019-06-06"));
        tasks.add(readBook);
        tasks.add(meeting);
        tasks.add(returnBook);
        readBook.markAsDone();
        returnBook.markAsDone();

        List<Task> matches = tasks.find("book");
        assertEquals(2, matches.size());
        assertSame(readBook, matches.get(0));
        assertSame(returnBook, matches.get(1));
    }

    @Test
    public void find_ignoresCaseAndSkipsDateText() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("Read Book"));
        tasks.add(new Deadline("submit report", TaskDateTime.parseUserInput("2019-06-06")));

        List<Task> matches = tasks.find("BOOK");
        assertEquals(1, matches.size());
        assertEquals("Read Book", matches.get(0).getDescription());
        assertEquals(0, tasks.find("June").size());
    }

    @Test
    public void copyConstructor_doesNotAliasCallerList() throws AlfredException {
        List<Task> original = new ArrayList<>();
        original.add(new ToDo("kept"));
        TaskList tasks = new TaskList(original);
        original.add(new ToDo("extra"));
        assertEquals(1, tasks.size());
        assertEquals("kept", tasks.get(0).getDescription());
    }
}
