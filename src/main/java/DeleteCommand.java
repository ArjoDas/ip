/**
 * Removes a task identified by a zero-based index.
 */
public class DeleteCommand extends Command {
    /** Zero-based index of the task to remove. */
    private final int index;

    /**
     * Creates a command that will delete the task at {@code index}.
     *
     * @param index Zero-based index parsed from the user command.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) throws AlfredException {
        Task deletedTask = tasks.delete(index);
        ui.showTaskDeleted(deletedTask, tasks.size());
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
