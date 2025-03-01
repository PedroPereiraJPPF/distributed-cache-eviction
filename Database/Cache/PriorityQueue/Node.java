package Database.Cache.PriorityQueue;

import Src.Domain.ServiceOrder.ServiceOrderInterface;

public class Node {
    Integer frequency;
    ServiceOrderInterface serviceOrder;
    public Node left, right;

    public Node() {
        this.frequency = null;
        this.serviceOrder = null;
        this.left = this.right = null; 
    }

    public Node(ServiceOrderInterface serviceOrder, Integer frequency) {
        this.frequency = frequency;
        this.serviceOrder = serviceOrder;
        this.left = this.right = null;
    }

    public Node(ServiceOrderInterface serviceOrder, Integer frequency, Node left, Node right) {
        this.frequency = frequency;
        this.serviceOrder = serviceOrder;
        this.left = left;
        this.right = right;
    }

    public ServiceOrderInterface getServiceOrder() {
        return this.serviceOrder;
    }

    public Integer getFrequency() {
        return this.frequency;
    }
}
