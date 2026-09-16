package alfred.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import alfred.AlfredException;

/**
 * In-memory list of tasks, with operations to add, find, delete, and update them.
 */
public class TaskList {
    /** Tasks stored in the order they were added. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the given tasks, in the same order.
     *
     * @param tasks Tasks to copy into the list.
     */
    public TaskList(List<Task> tasks) {
        // Storage.load() always returns a concrete list, even when the file is missing.
        assert tasks != null : "Initial task list should not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Appends {@code task} to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        // AddCommand is only created with a parsed ToDo, Deadline, or Event.
        assert task != null : "Cannot add a null task to the list";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at {@code zeroBasedIndex}.
     *
     * @param zeroBasedIndex Index of the task to remove.
     * @return Removed task.
     * @throws AlfredException If the index is out of range.
     */
    public Task delete(int zeroBasedIndex) throws AlfredException {
        checkIndex(zeroBasedIndex);
        Task removed = tasks.remove(zeroBasedIndex);
        assert removed != null : "TaskList should not store null tasks";
        return removed;
    }

    /**
     * Returns the task at {@code zeroBasedIndex}.
     *
     * @param zeroBasedIndex Index of the task to retrieve.
     * @return Task at that index.
     * @throws AlfredException If the index is out of range.
     */
    public Task get(int zeroBasedIndex) throws AlfredException {
        checkIndex(zeroBasedIndex);
        Task task = tasks.get(zeroBasedIndex);
        assert task != null : "TaskList should not store null tasks";
        return task;
    }

    /**
     * Marks the task at {@code zeroBasedIndex} as done.
     *
     * @param zeroBasedIndex Index of the task to mark.
     * @return Updated task.
     * @throws AlfredException If the index is out of range.
     */
    public Task mark(int zeroBasedIndex) throws AlfredException {
        Task task = get(zeroBasedIndex);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at {@code zeroBasedIndex} as not done.
     *
     * @param zeroBasedIndex Index of the task to unmark.
     * @return Updated task.
     * @throws AlfredException If the index is out of range.
     */
    public Task unmark(int zeroBasedIndex) throws AlfredException {
        Task task = get(zeroBasedIndex);
        task.markAsNotDone();
        return task;
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable view of the tasks in insertion order.
     *
     * @return Tasks currently stored.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns tasks whose description contains {@code keyword}, ignoring case.
     * Date and time text is not searched.
     *
     * @param keyword Substring to look for in each description.
     * @return Matching tasks in insertion order.
     */
    public List<Task> find(String keyword) {
        // Parser rejects a missing or blank find keyword before this is called.
        assert keyword != null && !keyword.isBlank() : "Find keyword should already be validated";
        String normalizedKeyword = keyword.toLowerCase(Locale.ENGLISH);
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            String description = task.getDescription().toLowerCase(Locale.ENGLISH);
            if (description.contains(normalizedKeyword)) {
                matches.add(task);
            }
        }
        return matches;
    }

    private void checkIndex(int zeroBasedIndex) throws AlfredException {
        if (zeroBasedIndex < 0 || zeroBasedIndex >= tasks.size()) {
            throw new AlfredException("that task number does not exist, sir.");
        }
    }
}
