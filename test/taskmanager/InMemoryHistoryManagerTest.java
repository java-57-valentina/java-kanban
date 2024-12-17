package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager manager;

    @BeforeEach
    void initManager() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    void checkAdd() {
        Task task = new Task("Task Name", "Task description", Status.NEW);
        manager.add(task);
        List<Task> history = manager.getHistory();

        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals(task, history.getLast());
    }

    void addTasks(int count) {
        for (int i = 0; i < count; i++) {
            Task task = new Task(i, "Task Name " + i, "Task description " + i, Status.NEW);
            manager.add(task);
        }
    }

    @Test
    void checkHistoryCapacityWhenTasksAdded_5() {
        final int tasks = 5;
        final int expected = 5;

        addTasks(tasks);
        List<Task> history = manager.getHistory();

        assertNotNull(history);
        assertEquals(expected, history.size());
    }

    @Test
    void checkHistoryCapacityWhenTasksAdded_10() {
        final int tasks = 10;
        final int expected = 10;

        addTasks(tasks);
        List<Task> history = manager.getHistory();

        assertNotNull(history);
        assertEquals(expected, history.size());
    }

    @Test
    void checkHistoryCapacityWhenTasksAdded_11() {
        final int tasks = 11;
        final int expected = 10;

        addTasks(tasks);
        List<Task> history = manager.getHistory();

        assertNotNull(history);
        assertEquals(expected, history.size());
    }
}