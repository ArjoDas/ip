/**
 * Represents a task without an attached date or time.
 */
public class ToDo extends Task {
    /** Creates an incomplete todo task. */
    public ToDo(String description) {
        super(description);
    }

    @Override
    protected String getTypeIcon() {
        return "T";
    }

    @Override
    protected String getDateTimeText() {
        return "";
    }
}
