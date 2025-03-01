package Database.Cache.Hash;

import Src.Domain.ServiceOrder.ServiceOrderInterface;
import Utils.Logger;

public class HashCache {
    private final float constA = 0.6180339887f;
    private int capacity;
    private int size;
    private ServiceOrderInterface[] table;
    private Logger logger = new Logger("Logs/ServerLogs.log"); 
    
    public HashCache(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.table = new ServiceOrderInterface[capacity];
    }

    public ServiceOrderInterface insert(ServiceOrderInterface order) {
        if (this.find(order.getCode()) != null) {
            return null;
        }

        int k = 0;
        int primaryHash = hash(order.getCode(), k);
        int index = primaryHash;

        if (this.size >= this.capacity) {
            this.table[index] = null;

            this.size--;
        }

        while (table[index] != null) {
            k += 1;
            index = hash(order.getCode(), k);
        }

        table[index] = order;
        this.size++;

        return order;
    }

    public ServiceOrderInterface find(int key) {
        int k = 0;
        int primaryHash = hash(key, k);
        int index = primaryHash;

        while (table[index] != null) {
            if (table[index].getCode() == key) {

                return table[index];
            }

            k++;
            index = hash(key, k);

            if (index == primaryHash) {
                return null;
            }
        }

        return null;
    }

    public boolean delete(int key) {
        int k = 0;
        int primaryHash = hash(key, k);
        int index = primaryHash;

        while (table[index] != null) {
            if (table[index].getCode() == key) {
                table[index] = null;
                this.size--;

                this.printElements();

                return true;
            }

            k++;
            index = hash(key, k);

            if (index == primaryHash) {
                return false;
            }
        }

        return false;
    }

    private int hash(int key, int k) {
        float temp = key * constA;
        temp = temp - (int) temp;
        int hash = (int) (capacity * temp);

        return Math.abs((hash + k) % capacity);
    }

    public void printElements() {
        for (int i = 0; i < capacity; i++) {
            if (this.table[i] != null) {
                this.logger.info(i + " - " + (table[i].getCode()));
            }
        }
    }
}