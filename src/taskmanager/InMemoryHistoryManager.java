package taskmanager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_LIMIT = 10;
    private List<Task> viewedTasks;

    public InMemoryHistoryManager() {
        this.viewedTasks = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task == null)
            return;
        // Сохраняем копию таски в текущем состоянии, т.к. есть требование к тестам:
        // "убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных"
        viewedTasks.add(task.clone());
        if (viewedTasks.size() > HISTORY_LIMIT) {
            viewedTasks.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(viewedTasks);
    }
}
