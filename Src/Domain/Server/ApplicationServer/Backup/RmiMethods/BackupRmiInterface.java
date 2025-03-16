package Src.Domain.Server.ApplicationServer.Backup.RmiMethods;

import java.rmi.Remote;
import java.rmi.RemoteException;
import Src.Domain.Server.Message.Message;

public interface BackupRmiInterface extends Remote {
    public void synchronizeDatabase() throws RemoteException;
    public void addServiceOrder(Message message) throws RemoteException;
    public void updateServiceOrder(Message message) throws RemoteException;
    public void removeServiceOrder(Message message) throws RemoteException;
    public void synchronizeLogs(String message, String type) throws RemoteException;
}
