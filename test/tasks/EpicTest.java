package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic = null;

    @BeforeEach
    void beforeEach() {
        epic = new Epic(1, "name", "description");
    }

    @Test
    void checkEquals() {
        int id = epic.getId();
        Epic otherEpic = new Epic(id, "name", "description");

        assertEquals(epic, otherEpic);
    }

    @Test
    void checkNotEqualsWithSameId() {
        int id = epic.getId();
        Epic otherEpic = new Epic(id, "name1", "description");

        assertNotEquals(epic, otherEpic);
    }

    @Test
    void addSubtask() {
        final int subtaskId = 2;
        boolean result = epic.addSubtask(subtaskId);

        assertTrue(result);
        assertTrue(epic.getSubtasks().contains(subtaskId));
        assertEquals(1, epic.getSubtasks().size());

    }

    @Test
    void removeSubtask() {
        final int subtaskId = epic.getId() + 1;
        epic.addSubtask(subtaskId);
        epic.removeSubtask(subtaskId);

        assertTrue(epic.getSubtasks().isEmpty());
    }
}