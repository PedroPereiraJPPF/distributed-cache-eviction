package Src.Domain.LocalizationServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import Utils.Logger;

// Este servidor será responsavel por receber a primeira conexão do cliente e enviar o endereço do proxy
// Irá retornar um objeto do tipo ServerData
public class LocalizationServer {
    public static void main(String[] args) {
        final String loadBalancerIP = "localhost";
        final Integer loadBalancerPort = 5001;
        ServerSocket server = null;
        Logger logger = new Logger("Logs/LocalizationServer.logs");

        try {
            server = new ServerSocket(5000);

            System.out.println("Servidor de localização iniciado");
            logger.info("Servidor de localização iniciado");

            while (true) {
                Socket cliente = server.accept();

                logger.info("Cliente de ip: " + cliente.getInetAddress().getHostAddress() + " conectou ao servidor de localização");

                new Thread(new RequestHandler(cliente, loadBalancerPort, loadBalancerIP)).start();;
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
