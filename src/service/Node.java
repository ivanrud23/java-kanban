package service;

public class Node<E> {
    public E data;
    public Node<E> next;
    public Node<E> prev;

    public Node(E data, Node<E> next, Node<E> prev) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public void removeNode(Node removeNode) {
        if (removeNode.prev == null) {
            removeNode.next.prev = null;
        } else if (removeNode.next == null) {
            removeNode.prev.next = null;
        } else {
            Node prevNode = this.prev;
            Node nextNode = this.next;

            removeNode.prev.next = nextNode;
            removeNode.next.prev = prevNode;
        }
    }
}

