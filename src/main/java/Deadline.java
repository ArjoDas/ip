/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** Date or time by which the task should be completed. */
    private final String deadline;

    /** Creates an incomplete deadline task. */
    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = deadline;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    @Override
    protected String getDateTimeText() {
        return " (by: " + deadline + ")";
    }
}
