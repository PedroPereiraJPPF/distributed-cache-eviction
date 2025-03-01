package Database.Data;

import java.io.Serializable;

import Src.Domain.ServiceOrder.ServiceOrderInterface;

public class Node implements Serializable {
    int key;
    ServiceOrderInterface value;
    int height;
    Node left, right;

    Node(int key, ServiceOrderInterface v) {
        this.key = key;
        this.value = v;
    }

    Node(int key, ServiceOrderInterface value, int height, Node left, Node right) {
        this.key = key;
        this.value = value;
        this.height = height;
        this.left = left;
        this.right = right;   
    }
}
