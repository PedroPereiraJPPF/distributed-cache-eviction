package Database.Cache;

import java.util.Iterator;
import java.util.LinkedList;

import Src.Domain.ServiceOrder.ServiceOrderInterface;
import Utils.Logger;

public class Cache {
    private LinkedList<ServiceOrderInterface> queue;
    private int size;
    private int maxSize;
    private Logger logger;
    
    public Cache() {
        this.queue = new LinkedList<ServiceOrderInterface>();
        this.size = 0;
        this.maxSize = 20;
        this.logger = new Logger("Logs/ServerLogs.log");
    }

    public Cache(int maxSize) {
        this.queue = new LinkedList<ServiceOrderInterface>();
        this.size = 0;
        this.maxSize = maxSize;
        this.logger = new Logger("Logs/ServerLogs.log");
    }

    public ServiceOrderInterface insert(ServiceOrderInterface value) {
        if (this.queue.size() == this.maxSize) {
            this.queue.remove();
        }

        queue.add(value);

        this.size++;

        this.logger.info("Cache atual: \n" + this);

        return value;
    }

    public ServiceOrderInterface delete() {
        ServiceOrderInterface value = this.queue.remove();

        if (value != null) {
            this.size--;
        }

        this.logger.info("Cache atual: \n" + this);

        return value;
    }

    public ServiceOrderInterface delete(ServiceOrderInterface value) {
        Iterator<ServiceOrderInterface> iterator = this.queue.iterator();
        while (iterator.hasNext()) {
            ServiceOrderInterface current = iterator.next();
            if (current.getCode() == value.getCode()) {
                iterator.remove();
                this.size--;

                this.logger.info("Cache atual: \n" + this);
                
                return current;
            }
        }
        return null;
    }


    public ServiceOrderInterface find(int code) {
        Iterator<ServiceOrderInterface> iterator = this.queue.iterator();
        while (iterator.hasNext()) {
            ServiceOrderInterface current = iterator.next();
            if (current.getCode() == code) {  
                return current;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Code: ");
        Iterator<ServiceOrderInterface> iterator = this.queue.iterator();
        while (iterator.hasNext()) {
            ServiceOrderInterface current = iterator.next();
            sb.append(current.getCode()).append(" - ");
        }
        return sb.toString();
    }
    
    public int getSize() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void clear() {
        this.queue.clear();
        this.size = 0;
    }
}
