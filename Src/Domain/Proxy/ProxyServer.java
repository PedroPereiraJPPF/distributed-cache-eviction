package Src.Domain.Proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Database.Cache.Cache;
import Src.Domain.LocalizationServer.RmiMethods.LocalizationInterface;
import Src.Domain.Proxy.RmiMethods.ProxyRmi;
import Src.Domain.Structures.ServerData.ServerData;
import Src.Domain.Structures.ServiceOrder.ServiceOrder;
import Utils.Logger;

public class ProxyServer {
    public static final Map<String, String> users = Map.of(
        "user", "123456",
        "user2", "123456",
        "user3", "123456"
    );
    
    // inicia a cache como uma tabela hash que usa remoção aleatoria
    public static Cache cache = new Cache(30);

    // cria uma lista para armazenar o rmi dos outros proxy
    // isso vai servir para que esse proxy possa se comunicar com todos os outros
    public static List<ServerData> rmiList = new ArrayList<>();

    public static void main(String[] args) throws UnknownHostException {
        final String applicationServerIp = "localhost";
        final int applicationServerPort = 5002;        
        int rmiPort = 1233;
        int applicationPort = 5001;
        boolean active = false;
        boolean connectedToLocalizationServer = false;
        boolean rmiActive = false;
        Registry registry = null;
        String applicationIp = InetAddress.getLocalHost().getHostAddress();

        // Faz uma copia dos dados da aplicação para a cache
        fullFillCache();

        Logger logger = new Logger("Logs/ProxyLogs.log");
        ServerSocket server = null;

        while (!active) {
            try {
                server = new ServerSocket(applicationPort);

                active = true;
            } catch (IOException e) {
                System.out.println("Porta " + applicationPort + " ocupada");

                applicationPort++;
            }
        }

        System.out.println("Proxy iniciado na porta: " + applicationPort);
        logger.info("Proxy iniciado na porta: " + applicationPort);

        if (server == null) {
            System.out.println("Falha ao iniciar o proxy");
            logger.info("Falha ao iniciar o proxy");
        }

        System.out.println("Iniciando rmi do proxy");
        logger.info("Iniciando rmi do proxy");

        // inicia o rmi do servidor 
        while (!rmiActive) {
            try {
                registry = LocateRegistry.createRegistry(rmiPort);
                registry.rebind("proxy", new ProxyRmi());
                rmiActive = true;

                System.out.println("RMI iniciado na porta: " + rmiPort);
            } catch (RemoteException e) {
                System.out.println("Porta " + rmiPort + " já está em uso. Tentando próxima porta...");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    System.out.println("Erro ao tentar colocar a thread em sleep: " + e1.getMessage());
                    logger.info("Erro ao tentar colocar a thread em sleep: " + e1.getMessage());    
                }
                
                rmiPort++;
            }
        }

        System.out.println("Tentando conectar ao RMI do servidor de localização");
        logger.info("Tentando conectar ao RMI do servidor de localização");

        while (!connectedToLocalizationServer) {
            try {
                setActiveInLocalizationServer(applicationIp, applicationPort, new ServerData(applicationIp, rmiPort));

                connectedToLocalizationServer = true;
            } catch (RemoteException | NotBoundException e) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    System.out.println("Erro ao tentar colocar a thread em sleep: " + e1.getMessage());
                    logger.info("Erro ao tentar colocar a thread em sleep: " + e1.getMessage());    
                }
            }
        }

        System.out.println("Conectado ao servidor de localização");
        logger.info("Conectado ao servidor de localização");

        System.out.println("Conectado ao servidor de aplicação: " + applicationServerIp + " port: " + applicationServerPort);
        logger.info("Conectado ao servidor de aplicação: " + applicationServerIp + " port: " + applicationServerPort);

        while(true) {
            try {
                Socket client = server.accept();

                logger.info("Cliente de IP: " + client.getInetAddress().getHostAddress() + " conectado");

                new Thread(new RequestHandler(applicationServerIp, applicationServerPort, client, logger)).start();
            } catch (IOException e) {
                e.printStackTrace();
                
                logger.error(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();

                logger.error(e.getMessage());

                break;
            }
        }

        if (server != null) {
            logger.info("Servidor fechado");
        }
    }

    // Informa ao servidor de localização que a conexão foi iniciada
    private static void setActiveInLocalizationServer(String applicationIp, int applicationPort, ServerData rmiData) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(applicationIp, 4000);

        LocalizationInterface localization = (LocalizationInterface) registry.lookup("localization");
    
        localization.registerServer(applicationIp, applicationPort, rmiData);
    }

    public static void fullFillCache() {
        for (int i = 1; i <= 30; i++) {
            ServiceOrder serviceOrder = new ServiceOrder();
            serviceOrder.setName("Ordem de Serviço " + i);
            serviceOrder.setDescription("Descrição da Ordem de Serviço " + i);

            cache.insert(serviceOrder);
        }
    }
}
