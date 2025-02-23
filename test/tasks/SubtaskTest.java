package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SubtaskTest {

    Subtask subtask;

    @BeforeEach
    void beforeEach() {
        subtask = new Subtask(1, "Subtask Name", "Subtask Description", Status.NEW, 2,
                LocalDateTime.of(2025,2, 3, 11, 20), Duration.ofMinutes(15));
    }

    @Test
    void checkEpicId() {
        final int newEpicId = 3;

        subtask.setEpicId(newEpicId);
        Assertions.assertEquals(newEpicId, subtask.getEpicId());
    }

    @Test
    void checkEqualsAllFields() {
        Subtask subtask1 = new Subtask(1, "Subtask Name", "Description", Status.NEW, 2,
                LocalDateTime.of(2025, 1,1, 12, 0), Duration.ofMinutes(20));
        Subtask subtask2 = new Subtask(1, "Subtask Name", "Description", Status.NEW, 2,
                LocalDateTime.of(2025, 1,1, 12, 0), Duration.ofMinutes(20));

        assertEquals(subtask1, subtask2);
    }

    @Test
    void checkEqualsID() {
        Subtask subtask1 = new Subtask(1, "Subtask Name 1", "Description 1", Status.NEW, 2,
                null, null);
        Subtask subtask2 = new Subtask(1, "Other sub name", "Description 2", Status.NEW, 2,
                null, null);

        assertNotEquals(subtask1, subtask2);
    }

    @Test
    void checkNotEquals() {
        Subtask other = new Subtask(1, "checkNotEquals", "Subtask Description", Status.NEW, 2, null, null);
        assertNotEquals(subtask, other);
    }

    @Test
    void checkClone() {
        Subtask other = subtask.clone();
        assertEquals(subtask, other);
    }
}