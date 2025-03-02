package Database.Data.Hash;

import Src.Domain.Structures.ServiceOrder.ServiceOrderInterface;

public class Node {
    public ServiceOrderInterface value;
    public Node next;

    public Node(ServiceOrderInterface value) {
        this.value = value;
        this.next = null;
    }
}
