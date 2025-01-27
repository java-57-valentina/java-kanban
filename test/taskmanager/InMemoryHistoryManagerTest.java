package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void checkHistoryCapacityWhenTasksAdded_5() {
        final int count = 5;
        final int expected = 5;

        for (int i = 0; i < count; i++) {
            Task task = new Task(i, "Task Name " + i, "Task description " + i, Status.NEW);
            manager.add(task);
        }
        List<Task> history = manager.getHistory();

        assertNotNull(history);
        assertEquals(expected, history.size());
    }

    @Test
    void checkUniquenessOfTasksInHistory() {
        manager.add(new Task(1, "Task1", "Task1 description", Status.NEW));
        manager.add(new Task(2, "Task2", "Task2 description", Status.NEW));
        manager.add(new Task(3, "Task3", "Task3 description", Status.NEW));
        manager.add(new Task(2, "Task2", "Task2 description", Status.IN_PROGRESS));

        int expectedSize = 3;
        List<Task> history = manager.getHistory();
        assertEquals(expectedSize, history.size());

        assertTrue(history.contains(new Task(1, "Task1", "Task1 description", Status.NEW)));
        assertTrue(history.contains(new Task(2, "Task2", "Task2 description", Status.IN_PROGRESS)));
        assertTrue(history.contains(new Task(3, "Task3", "Task3 description", Status.NEW)));
        assertFalse(history.contains(new Task(2, "Task2", "Task2 description", Status.NEW)));
    }
}