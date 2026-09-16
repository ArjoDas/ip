package alfred;

import java.io.IOException;
import java.nio.file.Path;

import alfred.command.Command;
import alfred.parser.Parser;
import alfred.storage.Storage;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Entry point for the Alfred chatbot.
 * Coordinates the UI, parser, task list, and storage.
 */
public class Alfred {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final boolean wasLoadError;
    private boolean isExit;

    /**
     * Creates Alfred using tasks stored at {@code filePath}, with console output.
     *
     * @param filePath Relative path of the save file.
     */
    public Alfred(String filePath) {
        this(filePath, true);
    }

    /**
     * Creates Alfred using tasks stored at {@code filePath}.
     *
     * @param filePath Relative path of the save file.
     * @param isConsole {@code true} to print framed console output.
     */
    public Alfred(String filePath, boolean isConsole) {
        // Both the console and GUI entry points supply a save-file path.
        assert filePath != null && !filePath.isBlank() : "Save file path should be provided";
        ui = new Ui(isConsole);
        storage = new Storage(Path.of(filePath));
        TaskList loadedTasks;
        boolean isLoadError;
        try {
            loadedTasks = new TaskList(storage.load());
            isLoadError = false;
        } catch (IOException exception) {
            loadedTasks = new TaskList();
            isLoadError = true;
        }
        tasks = loadedTasks;
        wasLoadError = isLoadError;
        isExit = false;
    }

    /** Greets the user and handles commands until {@code bye}. */
    public void run() {
        showOpeningMessages();
        isExit = false;
        while (!isExit) {
            processCommand(ui.readCommand());
        }
    }

    /**
     * Returns the opening greeting, without the ASCII banner.
     *
     * @return Welcome text, including a loading error when the save file cannot be read.
     */
    public String getGreeting() {
        showOpeningMessages();
        return ui.consumeReply();
    }

    /**
     * Executes {@code input} as a command and returns Alfred's reply.
     *
     * @param input Raw command typed by the user.
     * @return Chatbot reply for the GUI.
     */
    public String getResponse(String input) {
        processCommand(input);
        String reply = ui.consumeReply();
        assert !reply.isEmpty() : "Every command path should produce a reply for the GUI";
        return reply;
    }

    public boolean isExit() {
        return isExit;
    }

    /** Shows the welcome text and a loading error when the save file cannot be read. */
    private void showOpeningMessages() {
        ui.showWelcome();
        if (wasLoadError) {
            ui.showLoadingError();
        }
    }

    /** Parses and executes {@code input}, persisting the list when the command mutates it. */
    private void processCommand(String input) {
        try {
            Command command = Parser.parse(input);
            assert command != null : "Parser returns a command when parsing succeeds";
            command.execute(tasks, ui);
            if (command.isMutating()) {
                persistTasks();
            }
            isExit = command.isExit();
        } catch (AlfredException exception) {
            ui.showError(exception.getMessage());
        }
    }

    /** Writes the current task list to disk, reporting I/O failures to the user. */
    private void persistTasks() {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException exception) {
            ui.showError("I could not save your tasks, sir.");
        }
    }

    /** Launches Alfred using the default save file at {@code data/alfred.txt}. */
    public static void main(String[] args) {
        new Alfred("data/alfred.txt").run();
    }
}
