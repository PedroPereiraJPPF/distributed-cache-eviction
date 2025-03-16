package Src.Domain.Server.ApplicationServer.Backup;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import Src.Domain.Server.Server;
import Src.Domain.Server.ApplicationServer.Backup.RmiMethods.BackupRmi;
import Src.Domain.Server.ApplicationServer.Backup.RmiMethods.BackupRmiInterface;
import Utils.Logger;

public class ApplicationServerB {
    public static void main(String[] args) {
        Logger logger = new Logger("Logs/ApplicationServerLogs-backup.log");
        ServerSocket server;
        // Essa classe serve para controlar as operações no banco e descompressão das mensagens
        Server serverCore = new Server("Logs/DatabaseLogs-backup.log");

        try {
            BackupRmiInterface obj = new BackupRmi(serverCore, logger);
            Registry registry = LocateRegistry.createRegistry(4567);

            registry.bind("backup", obj);
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }

        System.out.println("Servidor de rmi iniciado");

        // tudo isso vai ser mantido para o caso de precisar usar esse servidor como principal
        try {
            server = new ServerSocket(3456);
            System.out.println("Backup do servidor de aplicação iniciado");
            logger.info("Backup do servidor de aplicação iniciado");

            while(true) {
                Socket client = server.accept();

                logger.info("Cliente conectado");
                System.out.println("Cliente conectado");

                new Thread(new RequestHandler(client, serverCore, logger)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();

            logger.error("Erro ao iniciar o servidor");
            logger.error(e.getMessage());
        }
    }
}
