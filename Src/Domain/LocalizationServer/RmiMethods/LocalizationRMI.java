package Src.Domain.LocalizationServer.RmiMethods;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Src.Domain.LocalizationServer.LocalizationServer;
import Src.Domain.Proxy.RmiMethods.ProxyRmiInterface;
import Src.Domain.Structures.ServerData.ServerData;

public class LocalizationRMI extends UnicastRemoteObject implements LocalizationInterface {
    public LocalizationRMI() throws RemoteException {
        super();
    }

    @Override
    public void registerServer(String serverIP, Integer serverPort, ServerData RmiData) {
        ServerData serverData = new ServerData(serverIP, serverPort);

        LocalizationServer.proxyList.add(serverData);

        // informa a todos que um novo rmi foi cadastrado
        this.broadcastProxyRMI(RmiData);
    }

    @Override
    public void serverInactive(String serverIP, Integer serverPort) {
        // remove da lista de proxy ativos
        LocalizationServer.proxyList.removeIf(server -> server.IP.equals(serverIP) && server.port.equals(serverPort));

        // informa todos para remover um rmi da lista
        // this.broadcastProxyRMIRemove(RmiData);
    }

    @Override
    public void broadcastProxyRMI(ServerData serverData) {
        // manda esse rmi para todos os proxys ativos
        for (ServerData rmi : LocalizationServer.proxyRmiList) {
            try {
                Registry registry = LocateRegistry.getRegistry(rmi.IP, rmi.port);

                ProxyRmiInterface stub = (ProxyRmiInterface) registry.lookup("proxy");

                stub.registerProxyRmi(serverData);
            } catch (RemoteException | NotBoundException e) {
                System.out.println("Erro ao enviar rmi para IP: " + serverData.IP + " port: " + serverData.port);

                // todo implementar regra para recuperação de falhas
            }
        }

        LocalizationServer.proxyRmiList.add(serverData);
    }

    @Override
    public void broadcastProxyRMIRemove(ServerData serverData) {
        // remove o proxy da lista de rmi
        LocalizationServer.proxyRmiList.removeIf(server -> server.IP.equals(serverData.IP) && server.port.equals(serverData.port));

        // informa aos servidores de proxy que um rmi deve ser removido da lista
        for (ServerData rmi : LocalizationServer.proxyRmiList) {
            try {
                Registry registry = LocateRegistry.getRegistry(rmi.IP, rmi.port);

                ProxyRmiInterface stub = (ProxyRmiInterface) registry.lookup("proxy");

                stub.removeProxyRmi(serverData);
            } catch (RemoteException | NotBoundException e) {
                System.out.println("Erro ao enviar rmi para IP: " + serverData.IP + " port: " + serverData.port);

                // todo implementar regra para recuperação de falhas
            }
        }
    }
}
