package Database.Data.Hash;

import Src.Domain.ServiceOrder.ServiceOrderInterface;

public class CustomLinkedList {
    private Node head;
    private Node tail;

    public CustomLinkedList() {
        this.head = null;
        this.tail = null;
    }

    public CustomLinkedList(ServiceOrderInterface order) {
        this.head = new Node(order);
        this.tail = this.head;
    }

    public Node insert(ServiceOrderInterface order) {
        Node node = new Node(order);

        if (this.head == null) {
            this.head = node;
            this.tail = node;
        } else {
            this.tail.next = node;
            this.tail = node;
        }

        return node;
    }

    public Node search(int key) {
        Node current = this.head;

        while (current != null) {
            if (current.value.getCode() == key) {
                return current;
            }

            current = current.next;
        }

        return null;
    }

    public boolean delete(int key) {
        if (this.head == null) {
            return false;
        }

        Node current = this.head;
        Node previous = null;

        while (current != null) {
            if (current.value.getCode() == key) {
                if (previous == null) {
                    this.head = current.next;
                } else {
                    previous.next = current.next;
                }

                if (current == this.tail) {
                    this.tail = previous;
                }

                return false;
            }

            previous = current;
            current = current.next;
        }

        return true;
    }

    public Node getHead() {
        return this.head;
    }

    public Node getTail() {
        return this.tail;
    }
}
