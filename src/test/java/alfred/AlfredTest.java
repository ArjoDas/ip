package alfred;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link Alfred#getResponse(String)} and {@link Alfred#isExit()} for the GUI.
 */
public class AlfredTest {
    @TempDir
    Path tempDir;

    @Test
    public void getResponse_listWhenEmpty_showsListHeader() {
        Alfred alfred = newAlfred();
        String response = alfred.getResponse("list");
        assertTrue(response.contains("Certainly. Here are the tasks in your list:"));
        assertFalse(alfred.isExit());
    }

    @Test
    public void getResponse_todoThenList_includesAddedTask() {
        Alfred alfred = newAlfred();
        String added = alfred.getResponse("todo read book");
        assertTrue(added.contains("[T][ ] read book"));
        assertTrue(added.contains("You now have 1 tasks in your list."));

        String listed = alfred.getResponse("list");
        assertTrue(listed.contains("1.[T][ ] read book"));
    }

    @Test
    public void getResponse_unknownCommand_showsError() {
        Alfred alfred = newAlfred();
        String response = alfred.getResponse("blah");
        assertEquals("I'm afraid I must report: I do not recognise that request, sir.",
                response);
    }

    @Test
    public void getResponse_bye_returnsFarewellAndMarksExit() {
        Alfred alfred = newAlfred();
        String response = alfred.getResponse("bye");
        assertEquals("Until next time. I shall be here should you require me.", response);
        assertTrue(alfred.isExit());
    }

    @Test
    public void getGreeting_withoutBanner_containsOpeningLines() {
        Alfred alfred = newAlfred();
        String greeting = alfred.getGreeting();
        assertTrue(greeting.contains("Good day. I'm Alfred, at your service."));
        assertTrue(greeting.contains("How may I assist you?"));
        assertFalse(greeting.contains("_    _  __"));
    }

    @Test
    public void getResponse_archiveAll_clearsLiveList() {
        Alfred alfred = newAlfred();
        alfred.getResponse("todo read book");
        alfred.getResponse("todo return book");
        String archived = alfred.getResponse("archive all");
        assertTrue(archived.contains("I've archived 2 tasks."));
        assertTrue(archived.contains("Your list is empty."));

        String liveList = alfred.getResponse("list");
        assertFalse(liveList.contains("read book"));
        String archiveList = alfred.getResponse("list archive");
        assertTrue(archiveList.contains("1.[T][ ] read book"));
        assertTrue(archiveList.contains("2.[T][ ] return book"));
    }

    @Test
    public void getResponse_restoreAndDeleteArchive() {
        Alfred alfred = newAlfred();
        alfred.getResponse("todo keep");
        alfred.getResponse("todo gone");
        alfred.getResponse("archive 2");
        String restored = alfred.getResponse("restore 1");
        assertTrue(restored.contains("I've restored 1 tasks."));
        assertTrue(restored.contains("You now have 2 tasks in your list."));
        alfred.getResponse("archive 2");
        String deleted = alfred.getResponse("delete archive 1");
        assertTrue(deleted.contains("I've removed this task:"));
        assertTrue(deleted.contains("Now you have 1 tasks in the list."));
        assertFalse(alfred.getResponse("list archive").contains("gone"));
    }

    @Test
    public void getResponse_archiveEmptyList_showsError() {
        Alfred alfred = newAlfred();
        String response = alfred.getResponse("archive all");
        assertEquals("I'm afraid I must report: there are no tasks to archive, sir.", response);
    }

    private Alfred newAlfred() {
        return new Alfred(tempDir.resolve("alfred.txt").toString(), false);
    }
}
