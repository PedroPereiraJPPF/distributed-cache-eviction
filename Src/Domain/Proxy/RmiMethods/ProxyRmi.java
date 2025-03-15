package Src.Domain.Proxy.RmiMethods;

import java.rmi.server.UnicastRemoteObject;

import Src.Domain.Proxy.ProxyServer;
import Src.Domain.Structures.ServerData.ServerData;

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
	public void removeCacheItem() throws RemoteException {
		System.out.println("deu certo vir ate aqui");
	}

    @Override
	public void updateCacheItem() throws RemoteException {
		// Implementation here
	}
}
