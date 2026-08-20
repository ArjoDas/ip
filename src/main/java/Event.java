/**
 * Represents a task with a specified start and end date or time.
 */
public class Event extends Task {
    /** Start date or time of the event. */
    private final String from;

    /** End date or time of the event. */
    private final String to;

    /** Creates an incomplete event task. */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    @Override
    protected String getDateTimeText() {
        return " (from: " + from + " to: " + to + ")";
    }
}
