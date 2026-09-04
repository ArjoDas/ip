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
        ui.showWelcome();
        if (wasLoadError) {
            ui.showLoadingError();
        }
        isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui);
                if (command.isMutating()) {
                    persistTasks();
                }
                isExit = command.isExit();
            } catch (AlfredException exception) {
                ui.showError(exception.getMessage());
            }
        }
    }

    /**
     * Returns the opening greeting, without the ASCII banner.
     *
     * @return Welcome text, including a loading error when the save file cannot be read.
     */
    public String getGreeting() {
        ui.showWelcome();
        if (wasLoadError) {
            ui.showLoadingError();
        }
        return ui.consumeReply();
    }

    /**
     * Executes {@code input} as a command and returns Alfred's reply.
     *
     * @param input Raw command typed by the user.
     * @return Chatbot reply for the GUI.
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, ui);
            if (command.isMutating()) {
                persistTasks();
            }
            isExit = command.isExit();
        } catch (AlfredException exception) {
            ui.showError(exception.getMessage());
        }
        return ui.consumeReply();
    }

    public boolean isExit() {
        return isExit;
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
