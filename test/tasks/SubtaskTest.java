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
    void checkEqualsAllFields() {
        Subtask subtask1 = new Subtask(1, "Subtask Name", "Description", Status.NEW, 2);
        Subtask subtask2 = new Subtask(1, "Subtask Name", "Description", Status.NEW, 2);

        assertEquals(subtask1, subtask2);
    }

    @Test
    void checkEqualsID() {
        Subtask subtask1 = new Subtask(1, "Subtask Name 1", "Description 1", Status.NEW, 2);
        Subtask subtask2 = new Subtask(1, "Subtask Name 2", "Description 2", Status.NEW, 2);

        assertNotEquals(subtask1, subtask2);
    }

    @Test
    void checkEqualsNull() {
        Subtask subtask = new Subtask(1, "Subtask Name 1", "Description 1", Status.NEW, 2);
        assertNotEquals(subtask, null);
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