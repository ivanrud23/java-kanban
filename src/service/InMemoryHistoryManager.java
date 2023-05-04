package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    static Map<Integer, Node> historyStorage = new HashMap<>();
    static CustomLinkedList<Task> customLinkedList = new CustomLinkedList<>();

    @Override
    public void add(Task task) {
        if (historyStorage.containsKey(task.getId())) {
            remove(task.getId());
        }
        customLinkedList.linkLast(task);
        historyStorage.put(task.getId(), customLinkedList.tail);
    }

    @Override
    public void remove(int id) {
        Node node = historyStorage.get(id);
        if (node == customLinkedList.tail) {
            customLinkedList.tail = node.prev;
            customLinkedList.tail.next = null;
        }
        if (node == customLinkedList.head) {
            customLinkedList.head = node.next;
            customLinkedList.head.prev = null;
        }
        node.removeNode(node);
        customLinkedList.size--;
        historyStorage.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("—  —  —  —  —  —  —  —  —  —  —  —");
        System.out.println("Истроия просмотренных задач:");
        for (Task task : customLinkedList.getTasks()) {
            System.out.println("Идентификатор — " + task.getId());
            System.out.println("Название — " + task.getName());
            System.out.println("Описание — " + task.getDescription());
            System.out.println("Статус — " + task.getStatus());
            System.out.println();
        }
        System.out.println("—  —  —  —  —  —  —  —  —  —  —  —");
        return customLinkedList.getTasks();
    }

    public static class CustomLinkedList<T>{
        private Node head;
        private Node<T> tail;
        private int size = 0;

        public void linkLast(T element) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>( element, null, tail);
            tail = newNode;
            if (oldTail != null) {
                oldTail.next = newNode;
            } else {
                head = newNode;
            }
            size++;
        }

        public List<T> getTasks() {
            List <T> listOfTasks = new ArrayList<>();
            if (size == 0) {
                return null;
            }
            Node<T> newNode = this.head;
            for (int i = 0; i < size; i++) {
                listOfTasks.add(newNode.data);
                newNode = newNode.next;
            }
            return  listOfTasks;
        }
    }
}


