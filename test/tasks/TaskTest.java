package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskTest {

    Task task;

    @BeforeEach
    void beforeEach() {
        task = new Task(1, "Task Name", "Task Description", Status.NEW);
    }

    @Test
    void checkEquals() {
        int id = task.getId();
        Task other = new Task(id, "Task Name", "Task Description", Status.NEW);

        assertEquals(task, other);
    }

    @Test
    void checkNotEquals() {
        int id = task.getId();
        Task otherTask = new Task(id, "checkNotEquals", "description", Status.NEW);

        assertNotEquals(task, otherTask);
    }

    @Test
    void checkClone() {
        Task otherTask = task.clone();
        assertEquals(task, otherTask);
    }
}