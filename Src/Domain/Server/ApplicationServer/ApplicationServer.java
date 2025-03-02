package Src.Domain.Server.ApplicationServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Utils.Logger;

public class ApplicationServer {
    public static void main(String[] args) {
        Logger logger = new Logger("Logs/AplicationServerLogs.log");
        ServerSocket server;
        ObjectOutputStream clientOutput;
        ObjectInputStream inputInput;

        try {
            server = new ServerSocket(5002);
            System.out.println("Servidor de aplicação iniciado");
            logger.info("Servidor de aplicação iniciado");

            while(true) {
                Socket client = server.accept();
                logger.info("Cliente conectado");
                System.out.println("Cliente conectado");

                new Thread(new RequestHandler(client)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();

            logger.error("Erro ao iniciar o servidor");
            logger.error(e.getMessage());
        }
    }
}
