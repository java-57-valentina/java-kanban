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
}