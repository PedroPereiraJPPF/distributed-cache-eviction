package Src.Domain.Server.ApplicationServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Src.Domain.Server.Server;
import Utils.Logger;

public class ApplicationServer {
    public static void main(String[] args) {
        Logger logger = new Logger("Logs/ApplicationServerLogs.log");
        ServerSocket server;
        // Essa classe serve para controlar as operações no banco e descompressão das mensagens
        Server serverCore = new Server(); 

        try {
            server = new ServerSocket(5002);
            System.out.println("Servidor de aplicação iniciado");
            logger.info("Servidor de aplicação iniciado");

            while(true) {
                Socket client = server.accept();

                logger.info("Cliente conectado");
                System.out.println("Cliente conectado");

                new Thread(new RequestHandler(client, serverCore)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();

            logger.error("Erro ao iniciar o servidor");
            logger.error(e.getMessage());
        }
    }
}
