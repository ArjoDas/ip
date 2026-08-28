import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes the task list as a text file on disk.
 */
public class Storage {
    /** Relative path of the save file, from the working directory. */
    private final Path filePath;

    /**
     * Creates storage for the given relative file path.
     *
     * @param filePath Relative path of the save file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns tasks loaded from disk.
     * Missing files are treated as an empty list. Corrupted lines are skipped.
     *
     * @return Tasks read from the save file, in file order.
     * @throws IOException If the save file exists but cannot be read.
     */
    public List<Task> load() throws IOException {
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return new ArrayList<>();
        }
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        List<Task> tasks = new ArrayList<>();
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            Task task = Task.fromSaveLine(line);
            if (task == null) {
                continue;
            }
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * Writes the given tasks to disk, creating the parent folder if needed.
     *
     * @param tasks Tasks to write, in the order they should be restored.
     * @throws IOException If the file cannot be created or written.
     */
    public void save(List<Task> tasks) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            lines.add(tasks.get(i).toSaveFormat());
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }
}
