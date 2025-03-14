package Src.Domain.LocalizationServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import Src.Domain.LocalizationServer.RmiMethods.LocalizationInterface;
import Src.Domain.LocalizationServer.RmiMethods.LocalizationRMI;
import Src.Domain.Structures.ServerData.ServerData;
import Utils.Logger;

// Este servidor será responsavel por receber a primeira conexão do cliente e enviar o endereço do proxy
// Irá retornar um objeto do tipo ServerData
public class LocalizationServer {
    public static List<ServerData> proxyList = new ArrayList<ServerData>();

    private static void startRMI() throws RemoteException {
        LocalizationInterface localizationRMI = new LocalizationRMI();

        Registry registry = LocateRegistry.createRegistry(4000);

        registry.rebind("localization", localizationRMI);
    }

    public static void main(String[] args) throws RemoteException {
        ServerSocket server = null;
        Logger logger = new Logger("Logs/LocalizationServer.log");

        startRMI();
        
        System.out.println("Serviço de RMI inciado");
        logger.info("Serviço de RMI inciado");

        try {
            server = new ServerSocket(5000);

            System.out.println("Servidor de localização iniciado");
            logger.info("Servidor de localização iniciado");

            while (true) {
                Socket cliente = server.accept();

                logger.info("Cliente de ip: " + cliente.getInetAddress().getHostAddress() + " conectou ao servidor de localização");

                new Thread(new RequestHandler(cliente, proxyList, logger)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());

            if (server != null)
                try {
                    server.close();
                    logger.info("Servidor fechado");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }
    }
}
