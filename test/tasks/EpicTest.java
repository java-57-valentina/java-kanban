package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

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
    void checkNotEquals() {
        int id = epic.getId();
        Epic otherEpic = new Epic(id, "name1", "description");

        assertNotEquals(epic, otherEpic);
    }

    @Test
    void addSubtask() {
        int subtaskId = epic.getId() + 1;
        boolean result = epic.addSubtask(subtaskId);
        HashSet<Integer> expected = new HashSet<>();
        expected.add(subtaskId);

        assertTrue(result);
        assertEquals(epic.getSubtasks(), expected);
    }

    @Test
    void removeSubtask() {
        int subtaskId = epic.getId() + 1;
        epic.addSubtask(subtaskId);
        epic.removeSubtask(subtaskId);

        assertTrue(epic.getSubtasks().isEmpty());
    }
}