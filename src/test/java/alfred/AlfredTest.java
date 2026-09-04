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

    private Alfred newAlfred() {
        return new Alfred(tempDir.resolve("alfred.txt").toString(), false);
    }
}
