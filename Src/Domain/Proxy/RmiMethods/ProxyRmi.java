package Src.Domain.Proxy.RmiMethods;

import java.rmi.server.UnicastRemoteObject;

import Src.Domain.Proxy.ProxyServer;
import Src.Domain.Structures.ServerData.ServerData;
import Src.Domain.Structures.ServiceOrder.ServiceOrderInterface;

import java.rmi.RemoteException;

public class ProxyRmi extends UnicastRemoteObject implements ProxyRmiInterface {
	public ProxyRmi() throws RemoteException {
		super();
	}

	@Override
	public void registerProxyRmi(ServerData data) throws RemoteException {
        if (!ProxyServer.rmiList.contains(data)) {
            ProxyServer.rmiList.add(data);
        }
	}

    @Override
	public void removeProxyRmi(ServerData data) throws RemoteException {
		ProxyServer.rmiList.removeIf(server -> data.IP.equals(server.IP) && data.port.equals(server.port));
	}

    // metodos para comunicação entre proxy
	@Override
	public void removeCacheItem(ServiceOrderInterface serviceOrder) throws RemoteException {
		ProxyServer.cache.delete(serviceOrder);
	}

    @Override
	public void updateCacheItem(ServiceOrderInterface serviceOrder) throws RemoteException {
		ProxyServer.cache.delete(serviceOrder);

		ProxyServer.cache.insert(serviceOrder);
	}

	@Override
	public ServiceOrderInterface getServiceOrder(ServiceOrderInterface serviceOrder) throws RemoteException {
		return ProxyServer.cache.find(serviceOrder.getCode());
	}
}
