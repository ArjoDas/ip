/**
 * Represents a task entered into Alfred.
 */
public abstract class Task {
    /** Text describing the task. */
    protected String description;

    /** Whether the task has been completed. */
    protected boolean isDone;

    /** Creates an incomplete task with the given description. */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Returns the display icon for this task's completion status. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns the task formatted with its completion status. */
    public String getDisplayText() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] "
                + description + getDateTimeText();
    }

    /** Returns the icon identifying this task type. */
    protected abstract String getTypeIcon();

    /** Returns the date or time details to append to the task description. */
    protected abstract String getDateTimeText();
}
