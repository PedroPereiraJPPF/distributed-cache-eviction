package Src.Domain.Server.Message.RuffmanTree;
import java.util.ArrayList;

public class PriorityQueue {
    private ArrayList<Node> heap;

    public PriorityQueue() {
        heap = new ArrayList<>();
    }

    public void insert(Node value) {
        this.heap.add(value); 
        int index = heap.size() - 1;

        while (index != 0 && heap.get(parent(index)).frequency > heap.get(index).frequency) {
            swap(index, parent(index));
            index = parent(index);
        }
    }

    public Node remove() {
        if (this.heap.isEmpty()) {
            return null;
        }

        if (this.heap.size() == 1) {
            return this.heap.remove(0);
        }

        Node removedElement = heap.get(0);

        this.heap.set(0, this.heap.get(this.heap.size() - 1));
        this.heap.remove(this.heap.size() - 1);
        minHeapify(0);

        return removedElement;
    }

    public int size() {
        return this.heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    private void minHeapify(int i) {
        int left = leftChild(i);
        int right = rightChild(i);
        int smallest = i;

        if (left < heap.size() && heap.get(left).frequency < heap.get(smallest).frequency) {
            smallest = left;
        }

        if (right < heap.size() && heap.get(right).frequency < heap.get(smallest).frequency) {
            smallest = right;
        }

        if (smallest != i) {
            swap(i, smallest);
            minHeapify(smallest);
        }
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int leftChild(int i) {
        return 2 * i + 1;
    }

    private int rightChild(int i) {
        return 2 * i + 2;
    }

    private void swap(int i, int j) {
        Node temp = this.heap.get(i);
        this.heap.set(i, this.heap.get(j));
        this.heap.set(j, temp);
    }
}
