package alfred.command;

import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Lists every stored task in insertion order.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showTaskList(tasks.getTasks());
    }
}
