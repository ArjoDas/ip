import java.io.IOException;
import java.nio.file.Path;

/**
 * Entry point for the Alfred chatbot.
 * Coordinates the UI, parser, task list, and storage.
 */
public class Alfred {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final boolean wasLoadError;

    /**
     * Creates Alfred using tasks stored at {@code filePath}.
     *
     * @param filePath Relative path of the save file.
     */
    public Alfred(String filePath) {
        ui = new Ui();
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
    }

    /** Greets the user and handles commands until {@code bye}. */
    public void run() {
        ui.showWelcome();
        if (wasLoadError) {
            ui.showLoadingError();
        }
        boolean isExit = false;
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

    /** Writes the current task list to disk, reporting I/O failures to the user. */
    private void persistTasks() {
        try {
            storage.save(tasks.getTasks());
        } catch (IOException exception) {
            ui.showError("I could not save your tasks, sir.");
        }
    }

    public static void main(String[] args) {
        new Alfred("data/alfred.txt").run();
    }
}
