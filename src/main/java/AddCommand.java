/**
 * Adds a task to the list and confirms the addition.
 */
public class AddCommand extends Command {
    /** Task to store. */
    private final Task task;

    /**
     * Creates a command that will add {@code task}.
     *
     * @param task Task produced by {@link Parser}.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
