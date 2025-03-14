package Src.Domain.LocalizationServer.RmiMethods;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LocalizationInterface extends Remote {
    public void registerServer(String serverIP, Integer serverPort) throws RemoteException;
    public void serverInactive(String serverIP, Integer serverPort) throws RemoteException;
}
