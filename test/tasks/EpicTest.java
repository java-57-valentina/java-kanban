package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic = null;

    @BeforeEach
    void beforeEach() {
        epic = new Epic(1, "name", "description");
        epic.setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0));
        epic.setDuration(Duration.ofMinutes(20));
    }

    @Test
    void checkEquals() {
        int id = epic.getId();
        Epic otherEpic = new Epic(id, "name", "description");
        otherEpic.setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0));
        otherEpic.setDuration(Duration.ofMinutes(20));

        assertEquals(epic, otherEpic);
    }

    @Test
    void checkNotEquals1() {
        int id = epic.getId();
        Epic otherEpic = new Epic(id, "name", "description");
        otherEpic.setStartTime(LocalDateTime.of(2025, 1, 10, 12, 20));
        otherEpic.setDuration(Duration.ofMinutes(20));

        assertNotEquals(epic, otherEpic);
    }

    @Test
    void checkNotEquals2() {
        int id = epic.getId();
        Epic otherEpic = new Epic(id, "name", "description");
        otherEpic.setStartTime(LocalDateTime.of(2025, 1, 10, 12, 0));
        otherEpic.setDuration(Duration.ofMinutes(10));

        assertNotEquals(epic, otherEpic);
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

    @Test
    void testClone() {
        Epic clone = epic.clone();

        assertNotSame(epic, clone);
        assertEquals(epic, clone);
        assertNotSame(epic.getSubtasks(), clone.getSubtasks());
        assertEquals(epic.getSubtasks(), clone.getSubtasks());
    }
}