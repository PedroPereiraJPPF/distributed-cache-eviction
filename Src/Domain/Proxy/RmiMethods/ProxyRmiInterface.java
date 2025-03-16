package Src.Domain.Proxy.RmiMethods;

import java.rmi.Remote;
import java.rmi.RemoteException;
import Src.Domain.Structures.ServerData.ServerData;
import Src.Domain.Structures.ServiceOrder.ServiceOrderInterface;

public interface ProxyRmiInterface extends Remote {
    // Serve para que o servidor de localização registre o RMI no proxy
    public void registerProxyRmi(ServerData serverData) throws RemoteException;
    // Server para o de localização remover RMI que estão inativos
    public void removeProxyRmi(ServerData serverData) throws RemoteException;
    // Esses metodos são para comunicação entre os proxy
    public void updateCacheItem(ServiceOrderInterface serviceOrder) throws RemoteException;
    public void removeCacheItem(ServiceOrderInterface serviceOrder) throws RemoteException;
    public ServiceOrderInterface getServiceOrder(ServiceOrderInterface serviceOrder) throws RemoteException;
}
