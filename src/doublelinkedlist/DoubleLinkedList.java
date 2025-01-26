package doublelinkedlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoubleLinkedList<K, T> {

    private Node<T> head;
    private Node<T> tail;
    private int size;
    private final Map<K, Node<T>> map;

    public DoubleLinkedList() {
        this.map = new HashMap<>();
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void add(K key, T element) {
        remove(key);
        Node<T> newNode = new Node<>(null, null, element);
        if (head == null) {
            head = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
        }
        tail = newNode;
        map.put(key, newNode);
        size++;
    }

    public ArrayList<T> getElements() {
        ArrayList<T> list = new ArrayList<>(size);
        Node<T> node = head;
        while (node != null) {
            list.add(node.getData());
            node = node.getNext();
        }
        return list;
    }

    public long size() {
        return size;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean remove(K key) {
        Node<T> node = map.get(key);
        boolean removed = remove(node);
        if (removed)
            map.remove(key);
        return removed;
    }

    public boolean containsKey(K key) {
        Node<T> node = map.get(key);
        return node != null;
    }

    private boolean remove(Node<T> node) {
        if (node == null) {
            return false;
        }

        Node<T> prev = node.getPrev();
        Node<T> next = node.getNext();

        if (prev != null) {
            prev.setNext(next);
        }
        if (next != null) {
            next.setPrev(prev);
        }

        if (node == head)
            head = next;

        if (node == tail)
            tail = prev;

        size--;
        return true;
    }
}
