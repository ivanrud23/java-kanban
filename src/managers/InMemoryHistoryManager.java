package managers;

import model.Node;
import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private CustomLinkedList customLinkedList = new CustomLinkedList();

    public CustomLinkedList getCustomLinkedList() {
        return customLinkedList;
    }

    public void setCustomLinkedList(CustomLinkedList customLinkedList) {
        this.customLinkedList = customLinkedList;
    }

    @Override
    public void add(Task task) {
        if (customLinkedList.historyStorage.containsKey(task.getId())) {
            try {
                remove(task.getId());
            } catch (NullPointerException e) {
            }
        }
        customLinkedList.linkLast(task);
    }

    @Override
    public void addHistory(Task task) {
        if (customLinkedList.historyStorage.containsKey(task.getId())) {
            remove(task.getId());
        }
        customLinkedList.linkLast(task);
    }

    @Override
    public void remove(int id) throws NullPointerException {
        if (!getCustomLinkedList().getTasks().isEmpty()) {
            Node node = customLinkedList.historyStorage.get(id);
            if (customLinkedList.historyStorage.size() == 1) {
                customLinkedList.historyStorage.remove(id);
            } else if (node == customLinkedList.tail) {
                customLinkedList.tail = node.getPrev();
                customLinkedList.tail.setNext(null);
                customLinkedList.historyStorage.remove(id);
            } else if (node == customLinkedList.head) {
                customLinkedList.head = node.getNext();
                customLinkedList.head.setPrev(null);
                customLinkedList.historyStorage.remove(id);
            } else {
                customLinkedList.removeNode(node);
                customLinkedList.historyStorage.remove(id);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTasks();
    }

    class CustomLinkedList {
        private Node head;
        private Node tail;
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
            customLinkedList.historyStorage.put(element.getId(), customLinkedList.tail);
        }

        private List<Task> getTasks() {
            List<Task> listOfTasks = new ArrayList<>();
            if (historyStorage.isEmpty()) {
                return Collections.emptyList();
            }
            Node newNode = this.head;
            for (int i = 0; i < historyStorage.size(); i++) {
                Task newTask = newNode.getData();
                listOfTasks.add(newTask);
                newNode = newNode.getNext();
            }
            return listOfTasks;
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


