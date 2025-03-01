package Database.Cache.PriorityQueue;
import java.util.ArrayList;

import Src.Domain.ServiceOrder.ServiceOrderInterface;
import Utils.Logger;

public class PriorityQueueCache {
    private ArrayList<Node> heap;
    private int maxSize;
    private Logger logger = new Logger("Logs/ServerLogs.log");

    public PriorityQueueCache(int size) {
        heap = new ArrayList<>();
        this.maxSize = size;
    }

    public ServiceOrderInterface insert(ServiceOrderInterface value) {
        if (this.size() == this.maxSize) {
            this.remove(this.size() - 1);
        }

        this.heap.add(new Node(value, 1)); 
        int index = heap.size() - 1;

        while (index != 0 && heap.get(parent(index)).frequency < heap.get(index).frequency) {
            swap(index, parent(index));
            index = parent(index);
        }

        return value;
    }

    public ServiceOrderInterface removeFirst() {
        if (this.heap.isEmpty()) {
            return null;
        }

        if (this.heap.size() == 1) {
            return this.heap.remove(0).getServiceOrder();
        }

        Node removedElement = heap.get(0);

        this.heap.set(0, this.heap.get(this.heap.size() - 1));
        this.heap.remove(this.heap.size() - 1);
        maxHeapify(0);

        return removedElement.getServiceOrder();
    }

    public ServiceOrderInterface remove(int code) {
        if (this.heap.isEmpty()) {
            return null;
        }
    
        int indexToRemove = -1;
        
        for (int i = 0; i < this.heap.size(); i++) {
            if (this.heap.get(i).getServiceOrder().getCode() == code) {
                indexToRemove = i;
                break;
            }
        }
    
        if (indexToRemove == -1) {
            return null;
        }
    
        if (indexToRemove == this.heap.size() - 1) {
            return this.heap.remove(this.heap.size() - 1).getServiceOrder();
        }
    
        Node removedNode = this.heap.get(indexToRemove);
        Node lastNode = this.heap.remove(this.heap.size() - 1);
        
        this.heap.set(indexToRemove, lastNode);
    
        if (lastNode.frequency > removedNode.frequency) {
            siftUp(indexToRemove);
        } else {
            maxHeapify(indexToRemove);
        }
    
        this.printElements();
    
        return removedNode.getServiceOrder();
    }

    public ServiceOrderInterface find(int code) {
        if (this.heap.isEmpty()) {
            return null;
        }
    
        Node element = null;
        int indexToUpdate = -1;
    
        for (int i = 0; i < this.heap.size(); i++) {
            Node node = this.heap.get(i);
            if (node.getServiceOrder().getCode() == code) {
                indexToUpdate = i;
                node.frequency += 1;
                element = node;
                break;
            }
        }

        if (element == null) {
            return null;
        }
    
        if (indexToUpdate != -1) {
            siftUp(indexToUpdate);
        }
    
        this.printElements();
        return element.getServiceOrder();
    }

    public int size() {
        return this.heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    private void maxHeapify(int i) {
        int left = leftChild(i);
        int right = rightChild(i);
        int largest = i;

        if (left < heap.size() && heap.get(left).frequency > heap.get(largest).frequency) {
            largest = left;
        }

        if (right < heap.size() && heap.get(right).frequency > heap.get(largest).frequency) {
            largest = right;
        }

        if (largest != i) {
            swap(i, largest);
            maxHeapify(largest);
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

    private void siftUp(int index) {
        while (index != 0 && heap.get(parent(index)).frequency < heap.get(index).frequency) {
            swap(index, parent(index));
            index = parent(index);
        }
    }

    public void printElements() {
        this.logger.info("==== Cache Logs ====");

        for (int i = 0; i < this.size(); i++) {
            this.logger.info("posicao: " + i + " - " + this.heap.get(i).frequency + " - " + this.heap.get(i).getServiceOrder().getCode());
        }
    }
}
