package taskmanager;

import doublelinkedlist.DoubleLinkedList;
import tasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final DoubleLinkedList<Integer, Task> historyList;

    public InMemoryHistoryManager() {
        historyList = new DoubleLinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (task == null)
            return;
        historyList.add(task.getId(), task.clone());
    }

    @Override
    public void remove(int id) {
        historyList.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getElements();
    }
}
