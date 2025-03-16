package Src.Domain.Server.ApplicationServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import Src.Domain.Server.Server;
import Src.Domain.Server.ApplicationServer.Backup.RmiMethods.BackupRmiInterface;
import Utils.Logger;

public class ApplicationServer {
    public static void main(String[] args) {
        Logger logger = new Logger("Logs/ApplicationServerLogs.log");
        ServerSocket server;
        // Essa classe serve para controlar as operações no banco e descompressão das mensagens
        Server serverCore = new Server();
        String backupServerRMIIp = "localhost";
        int backupServerRMIPort = 4567;
        BackupRmiInterface stub = null;
        
        try {
            Registry registry = LocateRegistry.getRegistry(backupServerRMIIp, backupServerRMIPort);

            stub = (BackupRmiInterface) registry.lookup("backup");
        } catch (RemoteException | NotBoundException e) {
            System.out.println("Falha ao se comunicar com o servidor de backup");
        }

        try {
            server = new ServerSocket(2345);
            System.out.println("Servidor de aplicação iniciado");
            logger.info("Servidor de aplicação iniciado");

            while(true) {
                Socket client = server.accept();

                logger.info("Cliente conectado");
                System.out.println("Cliente conectado");

                new Thread(new RequestHandler(client, serverCore, logger, stub)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();

            logger.error("Erro ao iniciar o servidor");
            logger.error(e.getMessage());
        }
    }
}
