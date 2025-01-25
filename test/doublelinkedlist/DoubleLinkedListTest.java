package doublelinkedlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoubleLinkedListTest {

    private DoubleLinkedList<Integer, String> list;

    @BeforeEach
    void initManager() {
        list = new DoubleLinkedList<>();
    }

    @Test
    void add() {
        list.add(1, "First");
        assertEquals(1, list.size());

        list.add(2, "Second");
        assertEquals(2, list.size());

        list.add(1, "Replace first");
        assertEquals(2, list.size());
    }

    @Test
    void getElements() {
        assertEquals(0, list.getElements().size());

        list.add(1, "First");
        list.add(2, "Second");
        String[] expected1 = { "First", "Second" };
        assertArrayEquals(expected1, list.getElements().toArray());
        assertTrue(list.containsKey(1));
        assertTrue(list.containsKey(2));
        assertFalse(list.containsKey(44));

        list.add(1, "Replace first");
        String[] expected2 = { "Second", "Replace first" };
        assertTrue(list.containsKey(1));
        assertArrayEquals(expected2, list.getElements().toArray());
    }

    @Test
    void size() {
        assertEquals(0, list.size());

        list.add(1, "First");
        assertEquals(1, list.size());

        list.add(2, "Second");
        assertEquals(2, list.size());

        list.add(1, "Replace first");
        assertEquals(2, list.size());

        list.remove(2);
        assertEquals(1, list.size());

        list.remove(2);
        assertEquals(1, list.size());

        list.remove(1);
        assertEquals(0, list.size());
    }

    @Test
    void remove() {
        list.add(1, "First");

        boolean result = list.remove(1);
        assertTrue(result);
        assertFalse(list.containsKey(1));

        result = list.remove(1); // попытка повторного удаления
        assertFalse(result);
        assertFalse(list.containsKey(1));
    }

    @Test
    void iterator() {
    }

    @Test
    void forEach() {
    }

    @Test
    void spliterator() {
    }

    @Test
    void containsKey() {
        assertFalse(list.containsKey(1));

        list.add(1, "First");
        assertTrue(list.containsKey(1));
        assertFalse(list.containsKey(2));
    }
}