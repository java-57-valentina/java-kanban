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
            Task task = new Task(i, "Task Name " + i, "Task description " + i, Status.NEW, null, null);
            manager.add(task);
        }
        List<Task> history = manager.getHistory();

        assertNotNull(history);
        assertEquals(expected, history.size());
    }

    @Test
    void checkUniquenessOfTasksInHistory() {
        Task task1 = new Task(1, "Task1", "Task1 description", Status.NEW, null, null);
        Task task2 = new Task(2, "Task2", "Task2 description", Status.NEW, null, null);
        Task task3 = new Task(3, "Task3", "Task3 description", Status.NEW, null, null);
        Task task4 = new Task(2, "Task2", "Task2 description", Status.IN_PROGRESS, null, null);

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.add(task4);

        int expectedSize = 3;
        List<Task> history = manager.getHistory();
        assertEquals(expectedSize, history.size());

        assertTrue(history.contains(task1));
        assertFalse(history.contains(task2));
        assertTrue(history.contains(task3));
        assertTrue(history.contains(task4));
    }
}