package service.InMemory;

import model.Task;
import service.managers.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> viewedTasks = new HashMap<>();
    Node head;
    Node tail;

    public InMemoryHistoryManager() {
        head = null;
        tail = null;
    }

    private static class Node {
        Task element;
        Node next;
        Node prev;

        public Node(Task element) {
            this.element = element;
        }

        public Node(Node prev, Task element, Node next) {
            this.element = element;
            this.next = next;
            this.prev = prev;
        }
    }

    public void linkLast(Task task) { //добавление задачи в конец списка
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        viewedTasks.put(task.getId(), newNode);
    }

    @Override
    public void add(Task task) {  //добавление задачи в историю просмотров
        if (viewedTasks.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
        viewedTasks.put(task.getId(), tail);
    }

    public List<Task> getTasks() { // собирает все задачи в ArrayList
        List<Task> historyList = new ArrayList<>();
        Node node = head;
        while (node != null) {
            historyList.add(node.element);
            node = node.next;
        }
        return historyList; //получение списка 10 последних просмотренных задач
    }

    @Override
    public List<Task> getHistory() { //получение списка просмотренных задач
        return getTasks();
    }

    @Override
    public void remove(int id) { //удаление задачи из просмотра
        if (viewedTasks.containsKey(id)) {
            removeNode(viewedTasks.get(id));
            viewedTasks.remove(id);
        }
    }

    public void removeNode(Node node) { //удаление узла связного списка

        if (node == null) {
            return;
        }

        Node next = node.next;
        Node prev = node.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
    }
}