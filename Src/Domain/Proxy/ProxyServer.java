package Src.Domain.Proxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Utils.Logger;

public class ProxyServer {
    public static void main(String[] args) {
        final String authName = "admin";
        final String password = "123456";

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

        while(true) {
            try {
                logger.info("Conectado ao servidor de aplicação: " + applicationServerIp + " port: " + applicationServerPort);

                while (true) {
                    Socket client = server.accept();

                    logger.info("Cliente de IP: " + client.getInetAddress().getHostAddress() + " conectado");

                    ObjectOutputStream clientOutput = new ObjectOutputStream(client.getOutputStream());
                    ObjectInputStream clientInput = new ObjectInputStream(client.getInputStream());

                    // clientOutput.writeObject(new String("auth:request"));

                    logger.info("Dados de authenticação solicitados");

                    String response = (String) clientInput.readObject();

                    logger.info("Dados de authenticação recebidos");

                    String[] userData = response.split(":");

                    if (!(userData[0].equals(authName) && userData[1].equals(password))) {
                        logger.info("Usuario não reconhecido");

                        clientOutput.writeObject(new String("auth:invalid"));

                        continue;
                    }

                    clientOutput.writeObject(new String("auth:valid"));

                    new Thread(new RequestHandler(applicationServerIp, applicationServerPort, client)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
                
                logger.error(e.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage());

                break;
            }
        }

        if (server != null) {
            logger.info("Servidor fechado");
        }
    }
}
