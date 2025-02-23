package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    Task task;

    @BeforeEach
    void beforeEach() {
        task = new Task(1, "Task Name", "Task Description", Status.NEW,
                LocalDateTime.of(2025, 1, 5, 12, 0), Duration.ofMinutes(30));
    }

    @Test
    void checkEquals() {
        int id = task.getId();
        Task other = new Task(1, "Task Name", "Task Description", Status.NEW,
                LocalDateTime.of(2025, 1, 5, 12, 0), Duration.ofMinutes(30));

        assertEquals(task, other);
    }

    @Test
    void checkNotEquals() {
        int id = task.getId();
        Task otherTask = new Task(1, "checkNotEquals", "Task Description", Status.NEW,
                LocalDateTime.of(2025, 1, 5, 12, 0), Duration.ofMinutes(30));

        assertNotEquals(task, otherTask);
    }

    @Test
    void checkClone() {
        Task clone = task.clone();
        assertEquals(task, clone);
        assertNotSame(task, clone);
    }
}