package Src.Domain.Server.Message.RuffmanTree;
import java.util.Map;
import java.util.HashMap;

public class RuffmanTree {
    private Node node;
    private Map<Character, String> codesTable;

    public RuffmanTree(Map<Character, Integer> frequencyTable) {
        this.buildTree(frequencyTable);
        this.codesTable = new HashMap<>();
        this.buildCodesTable(node, "", this.codesTable);
    }

    private void buildTree(Map<Character, Integer> frequencyTable) {
        PriorityQueue minHeap = new PriorityQueue();
        Node newNode;

        for (Map.Entry<Character, Integer> value : frequencyTable.entrySet()) {
            newNode = new Node(value.getKey(), value.getValue());

            minHeap.insert(newNode);
        }

        if (minHeap.size() == 1) {
            Node x = minHeap.remove();

            Node z = new Node();

            z.frequency = x.frequency;
            z.left = x;

            this.node = z;

            return;
        }

        this.node = null;

        while (minHeap.size() > 1) {
            Node x = minHeap.remove();
            Node y = minHeap.remove();

            Node z = new Node();

            z.frequency = x.frequency + y.frequency;
            z.character = '-';
            z.left = x;
            z.right = y;
            node = z;

            minHeap.insert(z);
        }
    }

    private void buildCodesTable(Node node, String code, Map<Character, String> codesTable) {
        if (node == null) {
            return;
        }

        if (node.left == null && node.right == null) {
            codesTable.put(node.character, code);
        }

        this.buildCodesTable(node.left, code + "0", codesTable);
        this.buildCodesTable(node.right, code + "1", codesTable);
    }

    public Node getNode() {
        return this.node;
    }

    public Map<Character, String> getcodesTable() {
        return this.codesTable;
    }
}
