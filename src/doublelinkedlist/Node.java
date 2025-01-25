package doublelinkedlist;

public class Node<T> {
    private Node<T> previous;
    private Node<T> next;
    private T data;

    Node(Node<T> previous, Node<T> next, T data) {
        this.previous = previous;
        this.next = next;
        this.data = data;
    }

    void setNext(Node<T> node) {
        this.next = node;
    }

    void setPrev(Node<T> node) {
        this.previous = node;
    }

    T getData() {
        return data;
    }

    Node<T> getNext() {
        return next;
    }

    Node<T> getPrev() {
        return previous;
    }
}
