package Src.Domain.Server;

import java.util.List;
import java.util.ArrayList;
import Database.Data.Hash.HashDatabase;
import Src.Domain.Server.Interface.ServerInterface;
import Src.Domain.Server.Message.CompressedObject;
import Src.Domain.Server.Message.CompressionManager;
import Src.Domain.Server.Message.Message;
import Src.Domain.Structures.ServiceOrder.ServiceOrder;
import Src.Domain.Structures.ServiceOrder.ServiceOrderInterface;
import Utils.Logger;

public class Server implements ServerInterface {
    private Object lock = new Object();
    private HashDatabase database;
    private Logger opLogger = new Logger("Logs/OperationsLogs.log");

    public Server() {
        this.database = new HashDatabase();

        for (int i = 1; i <= 70; i++) {
            ServiceOrder serviceOrder = new ServiceOrder();
            serviceOrder.setName("Ordem de Serviço " + i);
            serviceOrder.setDescription("Descrição da Ordem de Serviço " + i);

            this.storeServiceOrder(serviceOrder);
        }

        for (int i = 1; i <= 30; i++) {
            this.getServiceOrder(i);
        }
    }

    @Override
    public List<Message> listServiceOrders() {
        List<ServiceOrderInterface> orders = this.database.list();
        
        List<Message> messages = new ArrayList<>();

        for (ServiceOrderInterface order : orders) {
            messages.add(new Message(
                order.getCode(),
                order.getName(),
                order.getDescription(),
                order.getRequestTime()));
        }

        return messages;
    }

    @Override
    public Message getServiceOrder(Message message) {
        ServiceOrderInterface value = new ServiceOrder();

        CompressedObject data = message.getData();

        value.setCode(Integer.valueOf(CompressionManager.decodeParameter(data.getValues()[0], data.getFrequencyTable())));

        value = this.getServiceOrder(value);

        if (value == null)
            return null;

        Message compactedMessage = new Message(value.getCode(), value.getName(), value.getDescription(), value.getRequestTime());

        return compactedMessage;
    }

    @Override
    public ServiceOrderInterface getServiceOrder(int code) {
        ServiceOrderInterface value = null;

        value = this.database.search(code);

        if (value == null) 
            return null;

        return value;
    }

    @Override
    public ServiceOrderInterface getServiceOrder(ServiceOrderInterface serviceOrder) {
        return this.getServiceOrder(serviceOrder.getCode());
    }

    @Override
    public Message storeServiceOrder(Message message) {
        CompressedObject data = message.getData();

        ServiceOrderInterface serviceOrder = new ServiceOrder();
        serviceOrder.setName(CompressionManager.decodeParameter(data.getValues()[1], data.getFrequencyTable()));
        serviceOrder.setDescription(CompressionManager.decodeParameter(data.getValues()[2], data.getFrequencyTable()));

        synchronized (this.lock) {
            serviceOrder = this.database.insert(serviceOrder);
        }

        Message compactedMessage = new Message(serviceOrder.getCode(), serviceOrder.getName(), serviceOrder.getDescription(), serviceOrder.getRequestTime());
        
        this.database.printAll();

        return compactedMessage;
    }

    @Override
    public ServiceOrderInterface storeServiceOrder(ServiceOrderInterface serviceOrder) {
        if (this.database.search(serviceOrder.getCode()) != null) {
            return null;
        }

        return this.database.insert(serviceOrder);
    }

    @Override
    public boolean deleteServiceOrder(Message message) {
        CompressedObject data = message.getData();

        int code = Integer.valueOf(CompressionManager.decodeParameter(data.getValues()[0], data.getFrequencyTable()));

        boolean success = false;

        synchronized (this.lock) {
            success = this.database.delete(code);
        }

        this.database.printAll();

        return success;
    }

    @Override
    public void deleteServiceOrder(int code) {
        this.deleteServiceOrder(new ServiceOrder(code));
    }

    @Override
    public void deleteServiceOrder(ServiceOrderInterface serviceOrder) {
        this.database.delete(serviceOrder.getCode());
    }

    @Override
    public Message updateServiceOrder(Message message) {
        CompressedObject data = message.getData();

        int code = Integer.valueOf(CompressionManager.decodeParameter(data.getValues()[0], data.getFrequencyTable()));

        ServiceOrderInterface serviceOrder = this.database.search(code);

        if (serviceOrder == null)
            return null;

        synchronized (this.lock) {

            this.opLogger.info("UPDATE");

            serviceOrder.setCode(code);
            serviceOrder.setName(CompressionManager.decodeParameter(data.getValues()[1], data.getFrequencyTable()));
            serviceOrder.setDescription(CompressionManager.decodeParameter(data.getValues()[2], data.getFrequencyTable()));
        }

        Message compactedMessage = new Message(serviceOrder.getCode(), serviceOrder.getName(), serviceOrder.getDescription(), serviceOrder.getRequestTime());

        this.database.printAll();

        return compactedMessage;
    }

    @Override
    public ServiceOrderInterface updateServiceOrder(ServiceOrderInterface data) {
        ServiceOrderInterface serviceOrder = this.database.search(data.getCode());

        if (serviceOrder == null)
            return null;
            
        serviceOrder.setName(data.getName());
        serviceOrder.setDescription(data.getDescription());
        serviceOrder.setRequestTime(data.getRequestTime());

        return serviceOrder;
    }
    
    @Override
    public int countServiceOrders() {
        return this.database.countElements();    
    }

    public int countOperation(String search) {
        return this.opLogger.countOperation(search);
    }
}
