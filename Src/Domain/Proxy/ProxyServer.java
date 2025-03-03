package Src.Domain.Proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Utils.Logger;

public class ProxyServer {
    public static String authName = "admin";
    public static String password = "123456";

    public static void main(String[] args) {
        final String applicationServerIp = "localhost";
        final int applicationServerPort = 5002;

        Logger logger = new Logger("Logs/ProxyLogs.log");
        ServerSocket server = null;

        try {
            server = new ServerSocket(5001);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (server == null) {
            System.out.println("Falha ao iniciar o proxy");
            logger.info("Falha ao iniciar o proxy");
        }

        System.out.println("Proxy iniciado");
        logger.info("Proxy iniciado");

        logger.info("Conectado ao servidor de aplicação: " + applicationServerIp + " port: " + applicationServerPort);

        while(true) {
            try {
                Socket client = server.accept();

                logger.info("Cliente de IP: " + client.getInetAddress().getHostAddress() + " conectado");

                new Thread(new RequestHandler(applicationServerIp, applicationServerPort, client)).start();
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
}
