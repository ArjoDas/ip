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
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Appends {@code task} to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
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
        return tasks.remove(zeroBasedIndex);
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
        return tasks.get(zeroBasedIndex);
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
        String needle = keyword.toLowerCase(Locale.ENGLISH);
        List<Task> matches = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            String description = tasks.get(i).getDescription().toLowerCase(Locale.ENGLISH);
            if (description.contains(needle)) {
                matches.add(tasks.get(i));
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
