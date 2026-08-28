package alfred.task;

import java.time.LocalDate;

/**
 * Represents a task with a specified start and end date or time.
 */
public class Event extends Task {
    /** Start date or time of the event. */
    private final TaskDateTime from;

    /** End date or time of the event. */
    private final TaskDateTime to;

    /** Creates an incomplete event task. */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected TaskType getType() {
        return TaskType.EVENT;
    }

    @Override
    protected String getDateTimeText() {
        return " (from: " + from.toDisplayString() + " to: " + to.toDisplayString() + ")";
    }

    @Override
    public String toSaveFormat() {
        return getType().getIcon() + " | " + getStatusBit() + " | " + description
                + " | " + from.toSaveString() + " | " + to.toSaveString();
    }

    /**
     * Returns {@code true} if {@code date} falls on or between this event's start and end dates.
     *
     * @param date Calendar date to test.
     * @return {@code true} if {@code date} is in the inclusive start-to-end range.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        LocalDate start = from.toLocalDate();
        LocalDate end = to.toLocalDate();
        return !date.isBefore(start) && !date.isAfter(end);
    }
}
