package Database.Data.Hash;

import Src.Domain.ServiceOrder.ServiceOrderInterface;

public class Node {
    public ServiceOrderInterface value;
    public Node next;

    public Node(ServiceOrderInterface value) {
        this.value = value;
        this.next = null;
    }
}
