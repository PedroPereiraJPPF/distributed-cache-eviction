package Database.Data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import Src.Domain.ServiceOrder.ServiceOrderInterface;
import Utils.Logger;

public class Database implements Serializable {
    private Node node;
    private Logger logger;

    public Database() {
        this.node = null;
        this.logger = new Logger("Logs/ServerLogs.log");
    }

    public Database(Node node) {
        this.node = node;
    }

    public ServiceOrderInterface insert(int key, ServiceOrderInterface value) {
        try {
            this.node = insert(this.node, key, value);

            this.logger.info("Altura atual: " + height(this.node));

            return value;
        } catch (Exception e) {
            this.logger.error(e.getMessage());

            return null;
        }
    }

    public void delete(int key) {
        this.node = delete(this.node, key);

        this.logger.info("Altura atual: " + height(this.node));
    }

    public List<ServiceOrderInterface> list() {
        List<ServiceOrderInterface> orders = new LinkedList<>();
        
        this.list(this.node, orders);

        return orders;
    }

    public ServiceOrderInterface search(int key) {
        return this.search(key, this.node);
    }

    protected ServiceOrderInterface search(int key, Node node) {
        if (node == null)
            return null;
        if (key == node.key) 
            return node.value;
        if (key > node.key)
            return search(key, node.right);
        if (key < node.key) 
            return search(key, node.left);

        return null;
    }

    protected void list(Node node, List<ServiceOrderInterface> orders) {
        if (node == null) {
            return;
        }
            
        list(node.left, orders);
        orders.add(node.value);
        list(node.right, orders);
    }

    protected Node insert(Node node, int key, ServiceOrderInterface value) {
        if (node == null) {
            return new Node(key, value);
        }

        if (key < node.key) {
            node.left = insert(node.left, key, value);
        } else if (key > node.key) {
            node.right = insert(node.right, key, value);
        } else {
            return node;
        }

        node.height = 1 + greaterThan(height(node.left), height(node.right));

        int balanceFactor = this.balanceFactor(node);
        int balanceFactorLeft = this.balanceFactor(node.left);
        int balanceFactorRight = this.balanceFactor(node.right);

        if (balanceFactor > 1 && balanceFactorLeft >= 0)
            return simpleRightRotation(node);

        if (balanceFactor < -1 && balanceFactorRight >= 0)
            return simpleLeftRotation(node);

        if (balanceFactor > 1 && balanceFactorLeft < 0) {
            this.logger.info("Rotação dupla - Direita");
            node.left = simpleLeftRotation(node);
            return simpleRightRotation(node);
        }

        if (balanceFactor < -1 && balanceFactorRight > 0) {
            this.logger.info("Rotação dupla - Esquerda");
            node.right = simpleRightRotation(node.right);
            return simpleLeftRotation(node);
        } 
            
        return node;
    }   

    protected Node delete(Node node, int key) {
        if (node == null)
            return node;

        if (key < node.key) {
            node.left = delete(node.left, key);
        } else if (key > node.key) {
            node.right = delete(node.right, key);
        } else {
            if (node.left == null || node.right == null) {
                Node temp = null;

                if (temp == node.left)
                    temp = node.right;
                else
                    temp = node.left;

                if (temp == null) {
                    temp = node;
                    node = null;
                } else {
                    node = temp;
                }
            } else {
                Node temp = smallestKey(node.right);
                node.key = temp.key;
                node.right = delete(node.right, temp.key);
            }
        }

        if (node == null)
            return node;

        node.height = 1 + greaterThan(height(node.left), height(node.right));

        int balanceFactor = this.balanceFactor(node);
        int balanceFactorLeft = this.balanceFactor(node.left);
        int balanceFactorRight = this.balanceFactor(node.right);

        if (balanceFactor > 1 && balanceFactorLeft >= 0)
            return simpleRightRotation(node);

        if (balanceFactor < -1 && balanceFactorRight >= 0)
            return simpleLeftRotation(node);

        if (balanceFactor > 1 && balanceFactorLeft < 0) {
            this.logger.info("Rotação dupla - Direita");
            node.left = simpleLeftRotation(node);
            return simpleRightRotation(node);
        }

        if (balanceFactor < -1 && balanceFactorRight > 0) {
            this.logger.info("Rotação dupla - Esquerda");
            node.right = simpleRightRotation(node.right);
            return simpleLeftRotation(node);
        } 
            
        return node;

    }

    public int countElements() {
        return countElements(this.node);
    }

    protected int countElements(Node node) {
        if (node == null) {
            return 0;
        }
        return 1 + countElements(node.left) + countElements(node.right);
    }

    protected Node simpleRightRotation(Node tree) {
        this.logger.info("Rotação simples - Direita");

        Node subTreeLeft = tree.left;
        Node subTreeRight = subTreeLeft.right;

        subTreeLeft.right = tree;
        tree.left = subTreeRight;
        tree.height = greaterThan(height(tree.left), height(tree.right));
        subTreeLeft.height = greaterThan(height(subTreeLeft.left), height(subTreeLeft.right));

        return subTreeLeft;
    }

    protected Node simpleLeftRotation(Node tree) {
        this.logger.info("Rotação simples - Esquerda");

        Node subTreeRight = tree.right;
        Node subTreeLeft = subTreeRight.left;

        subTreeRight.left = tree;
        tree.right = subTreeLeft;
        tree.height = greaterThan(height(tree.left), height(tree.right));
        subTreeRight.height = greaterThan(height(subTreeRight.left), height(subTreeRight.right));
         
        return subTreeRight;
    }

    protected Node smallestKey(Node node) {
        if (node == null)
            return null;

        Node temp = node;

        while (temp != null)
            temp = temp.left;

        return temp;
    }

    protected int balanceFactor(Node node) {
        if (node == null)
            return 0;

        return height(node.left) - height(node.right);
    }   

    protected int height(Node node) {
        if (node == null) {
            return -1;
        }

        return node.height;
    }

    protected int greaterThan(int n1, int n2) {
        return n1 > n2 ? n1 : n2;
    }
}
