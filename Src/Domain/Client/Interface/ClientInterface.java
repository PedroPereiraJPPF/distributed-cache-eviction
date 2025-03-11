package Src.Domain.Client.Interface;

import java.io.EOFException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import Src.Domain.Server.Interface.ServerInterface;
import Src.Domain.Server.Message.Message;
import Src.Domain.Structures.ServiceOrder.ServiceOrderInterface;

/**
 * ClientInterface
 */
public interface ClientInterface {
    public ServerInterface connectServer();
    public boolean authenticate(String userData) throws EOFException;
    public ServiceOrderInterface storeServiceOrder(Message message) throws ParseException, EOFException, IOException, ClassNotFoundException;
    public boolean deleteServiceOrder(Message message) throws EOFException, IOException, ClassNotFoundException;
    public ServiceOrderInterface getServiceOrder(Message orderId) throws ParseException, EOFException, IOException, ClassNotFoundException;
    public ServiceOrderInterface updateServiceOrder(Message message) throws EOFException, IOException, ClassNotFoundException; 
    public List<ServiceOrderInterface> listServiceOrders(Message message) throws ParseException, EOFException, IOException, ClassNotFoundException;
    public int countServiceOrders() throws EOFException, IOException, ClassNotFoundException;
}