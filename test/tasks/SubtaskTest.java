package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SubtaskTest {

    Subtask subtask;

    @BeforeEach
    void beforeEach() {
        subtask = new Subtask(1, "Subtask Name", "Subtask Description", Status.NEW, 2);
    }

    @Test
    void checkEpicId() {
        final int expected = 2;

        Assertions.assertEquals(expected, subtask.getEpicId());

        subtask.setEpicId(3);
        Assertions.assertEquals(3, subtask.getEpicId());
    }

    @Test
    void checkEquals() {
        Subtask other = new Subtask(1, "Subtask Name", "Subtask Description", Status.NEW, 2);

        assertEquals(subtask, other);
    }

    @Test
    void checkNotEquals() {
        Subtask other = new Subtask(1, "checkNotEquals", "Subtask Description", Status.NEW, 2);

        assertNotEquals(subtask, other);
    }

    @Test
    void checkClone() {
        Subtask other = subtask.clone();

        assertEquals(subtask, other);
    }
}