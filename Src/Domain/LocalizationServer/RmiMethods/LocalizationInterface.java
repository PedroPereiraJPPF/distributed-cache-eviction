package Src.Domain.LocalizationServer.RmiMethods;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Src.Domain.Structures.ServerData.ServerData;

public interface LocalizationInterface extends Remote {
    public void registerServer(String serverIP, Integer serverPort, ServerData rmiData) throws RemoteException;
    public void serverInactive(String serverIP, Integer serverPort) throws RemoteException;
    // Serve para receber o rmi dos proxy e enivar para todos os ativos
    public void broadcastProxyRMI(ServerData serverData) throws RemoteException;
    // Serve para remover um rmi da lista e avisar os proxy que ele foi removido
    public void broadcastProxyRMIRemove(ServerData serverData) throws RemoteException;
    // Serve para pegar a lista de rmi dos proxy
    public List<ServerData> getCurrentRMIList() throws RemoteException;
}
