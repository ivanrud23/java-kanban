package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
        private CustomLinkedList customLinkedList = new CustomLinkedList();

    @Override
    public void add(Task task) {
        if (customLinkedList.historyStorage.containsKey(task.getId())) {
            remove(task.getId());
        }
        customLinkedList.linkLast(task);
        customLinkedList.historyStorage.put(task.getId(), customLinkedList.tail);
    }

    @Override
    public void remove(int id) {
        Node node = customLinkedList.historyStorage.get(id);
        if (node == customLinkedList.tail) {
            customLinkedList.tail = node.getPrev();
            customLinkedList.tail.setNext(null);
        } else if (node == customLinkedList.head) {
            customLinkedList.head = node.getNext();
            customLinkedList.head.setPrev(null);
        } else {
            customLinkedList.removeNode(node);
        }
        customLinkedList.size--;
        customLinkedList.historyStorage.remove(id);
    }

    @Override
    public List getHistory() {
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

    private class CustomLinkedList {
        private Node head;
        private Node tail;
        private int size = 0;
        private Map<Integer, Node> historyStorage = new HashMap<>();

        private void linkLast(Task element) {
            final Node oldTail = tail;
            final Node newNode = new Node(element, null, tail);
            tail = newNode;
            if (oldTail != null) {
                oldTail.setNext(tail);
            } else {
                head = newNode;
            }
            size++;
        }

        private List<Task> getTasks() {
            List<Task> listOfTasks = new ArrayList<>();
            if (historyStorage.isEmpty()) {
                return Collections.emptyList();
            }
            Node newNode = this.head;
            for (int i = 0; i < size; i++) {
                Task newTask = newNode.getData();
                listOfTasks.add(newTask);
                newNode = newNode.getNext();
            }
            return  listOfTasks;
        }

        private void removeNode(Node removeNode) {
            if (removeNode.getPrev() == null) {
                removeNode.getNext().setPrev(null);
            } else if (removeNode.getNext() == null) {
                removeNode.getPrev().setNext(null);
            } else {
                Node prevNode = removeNode.getPrev();
                Node nextNode = removeNode.getNext();

                removeNode.getPrev().setNext(nextNode);
                removeNode.getNext().setPrev(prevNode);
            }
        }
    }
}


