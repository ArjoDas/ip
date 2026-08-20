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

    /** Returns the type identifying this task. */
    protected abstract TaskType getType();

    /** Returns the date or time details to append to the task description. */
    protected abstract String getDateTimeText();
}
