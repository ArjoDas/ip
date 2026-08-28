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
     * Returns the number of tasks loaded into {@code tasks}.
     * Missing files are treated as an empty list. Corrupted lines are skipped.
     *
     * @param tasks Destination array to fill from the start.
     * @return Number of tasks loaded.
     * @throws IOException If the save file exists but cannot be read.
     */
    public int load(Task[] tasks) throws IOException {
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return 0;
        }
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        int taskCount = 0;
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            Task task = Task.fromSaveLine(line);
            if (task == null) {
                continue;
            }
            if (taskCount >= tasks.length) {
                break;
            }
            tasks[taskCount] = task;
            taskCount++;
        }
        return taskCount;
    }

    /**
     * Writes the given tasks to disk, creating the parent folder if needed.
     *
     * @param tasks Tasks to write.
     * @param taskCount Number of occupied slots in {@code tasks}.
     * @throws IOException If the file cannot be created or written.
     */
    public void save(Task[] tasks, int taskCount) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            lines.add(tasks[i].toSaveFormat());
        }
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }
}
