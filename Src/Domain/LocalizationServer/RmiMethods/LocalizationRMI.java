package Src.Domain.LocalizationServer.RmiMethods;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import Src.Domain.LocalizationServer.LocalizationServer;
import Src.Domain.Structures.ServerData.ServerData;

public class LocalizationRMI extends UnicastRemoteObject implements LocalizationInterface {
    public LocalizationRMI() throws RemoteException {
        super();
    }

    @Override
    public void registerServer(String serverIP, Integer serverPort) {
        LocalizationServer.proxyList.add(new ServerData(serverIP, serverPort));
    }

    @Override
    public void serverInactive(String serverIP, Integer serverPort) {
        LocalizationServer.proxyList.removeIf(server -> server.IP.equals(serverIP) && server.port.equals(serverPort));
    }
}
