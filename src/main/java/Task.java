/**
 * Represents a task entered into Alfred.
 */
public abstract class Task {
    /** Text describing the task. */
    protected String description;

    /** Completion status of the task. */
    protected TaskStatus status;

    /** Creates an incomplete task with the given description. */
    public Task(String description) {
        this.description = description;
        this.status = TaskStatus.PENDING;
    }

    /** Returns the display icon for this task's completion status. */
    public String getStatusIcon() {
        return status.getIcon();
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        status = TaskStatus.DONE;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        status = TaskStatus.PENDING;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns the task formatted with its completion status. */
    public String getDisplayText() {
        return "[" + getType().getIcon() + "][" + getStatusIcon() + "] "
                + description + getDateTimeText();
    }

    /**
     * Returns this task as one save-file line.
     *
     * @return Pipe-delimited save format for this task.
     */
    public abstract String toSaveFormat();

    /**
     * Returns the task represented by a save-file line, or {@code null} if the line
     * is not in the expected format.
     *
     * @param line One line from the save file.
     * @return Parsed task, or {@code null} if the line is corrupted.
     */
    public static Task fromSaveLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
            return null;
        }
        String typeIcon = parts[0].trim();
        String statusBit = parts[1].trim();
        boolean isDone;
        if (statusBit.equals("1")) {
            isDone = true;
        } else if (statusBit.equals("0")) {
            isDone = false;
        } else {
            return null;
        }
        Task task = switch (typeIcon) {
            case "T" -> parts.length == 3 && !parts[2].isEmpty()
                    ? new ToDo(parts[2])
                    : null;
            case "D" -> parts.length == 4 && !parts[2].isEmpty() && !parts[3].isEmpty()
                    ? new Deadline(parts[2], parts[3])
                    : null;
            case "E" -> parts.length == 5 && !parts[2].isEmpty() && !parts[3].isEmpty()
                    && !parts[4].isEmpty()
                    ? new Event(parts[2], parts[3], parts[4])
                    : null;
            default -> null;
        };
        if (task != null && isDone) {
            task.markAsDone();
        }
        return task;
    }

    /** Returns {@code 1} if this task is done, otherwise {@code 0}. */
    protected int getStatusBit() {
        return status == TaskStatus.DONE ? 1 : 0;
    }

    /** Returns the type identifying this task. */
    protected abstract TaskType getType();

    /** Returns the date or time details to append to the task description. */
    protected abstract String getDateTimeText();
}
