import java.time.LocalDate;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** Date or time by which the task should be completed. */
    private final TaskDateTime deadline;

    /** Creates an incomplete deadline task. */
    public Deadline(String description, TaskDateTime deadline) {
        super(description);
        this.deadline = deadline;
    }

    @Override
    protected TaskType getType() {
        return TaskType.DEADLINE;
    }

    @Override
    protected String getDateTimeText() {
        return " (by: " + deadline.toDisplayString() + ")";
    }

    @Override
    public String toSaveFormat() {
        return getType().getIcon() + " | " + getStatusBit() + " | " + description
                + " | " + deadline.toSaveString();
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return deadline.toLocalDate().equals(date);
    }
}
