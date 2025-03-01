package Src.Domain.Client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import Src.Domain.Client.Interface.ClientInterface;
import Src.Domain.Server.Server;
import Src.Domain.Server.Interface.ServerInterface;
import Src.Domain.Server.Message.CompressedObject;
import Src.Domain.Server.Message.CompressionManager;
import Src.Domain.Server.Message.Message;
import Src.Domain.ServiceOrder.ServiceOrder;
import Src.Domain.ServiceOrder.ServiceOrderInterface;

public class Client implements ClientInterface {
    private ServerInterface server;

    public Client() {
        this.connectServer();
    }

    @Override
    public ServerInterface connectServer() {
        this.server = new Server();

        return this.server;
    }

    @Override
    public ServiceOrderInterface storeServiceOrder(Message message) throws ParseException {
        Message serverMessage = this.server.storeServiceOrder(message);

        return this.messageToServiceOrder(serverMessage);
    }

    @Override
    public boolean deleteServiceOrder(Message message) {
        this.server.deleteServiceOrder(message);

        return true;
    }

    @Override
    public ServiceOrderInterface getServiceOrder(Message message) throws ParseException {
        Message serverMessage = this.server.getServiceOrder(message);

        if (serverMessage == null) {
            return null;
        }

        return this.messageToServiceOrder(serverMessage);
    }

    @Override
    public ServiceOrderInterface updateServiceOrder(Message message) {
        try {
            Message serverMessage = this.server.updateServiceOrder(message);

            if (serverMessage == null) {
                return null;
            }

            return this.messageToServiceOrder(serverMessage);
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public List<ServiceOrderInterface> listServiceOrders() throws ParseException {
        List<ServiceOrderInterface> orders = new ArrayList<>();

        List<Message> messages = this.server.listServiceOrders();

        for (Message message : messages) {
            orders.add(this.messageToServiceOrder(message));
        }

        return orders;
    }

    @Override
    public int countServiceOrders() {
        return this.server.listServiceOrders().size();
    }

    public int[] countOperations() {
        int[] operationsCount = {0 , 0, 0};

        operationsCount[0] = this.server.countOperation("INSERT");
        operationsCount[1] = this.server.countOperation("UPDATE");
        operationsCount[2] = this.server.countOperation("DELETE");

        return operationsCount;
    }

    private ServiceOrderInterface messageToServiceOrder(Message message) throws ParseException {
        CompressedObject data = message.getData();
        ServiceOrderInterface serviceOrder = new ServiceOrder();
        int code = Integer.valueOf(CompressionManager.decodeParameter(data.getValues()[0], data.getFrequencyTable()));

        serviceOrder.setCode(code);
        serviceOrder.setName(CompressionManager.decodeParameter(data.getValues()[1], data.getFrequencyTable()));
        serviceOrder.setDescription(CompressionManager.decodeParameter(data.getValues()[2], data.getFrequencyTable()));
        
        String decodedRequestTime = CompressionManager.decodeParameter(data.getValues()[3], data.getFrequencyTable());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        Date requestTime = dateFormat.parse(decodedRequestTime);
        serviceOrder.setRequestTime(requestTime);

        return serviceOrder;
    }
}
