package Src.Domain.LocalizationServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Src.Domain.Structures.ServerData.ServerData;
import Utils.Logger;

// vai gerenciar as requests do cliente quando ele não possui o ip do servidor de proxy/loadBalancer
class RequestHandler implements Runnable {
    // instancia do socket e output
    private Socket socket;
    private ObjectOutputStream output;
    private ServerData loadBalancerData;
    private Logger logger;

    public RequestHandler(Socket socket, Integer serverPort, String serverIP) {
        this.socket = socket;
        this.loadBalancerData = new ServerData(serverIP, serverPort);
        this.logger = new Logger("Logs/LocalizationServer.log");
    }

    public void run() {
        try {
            // manda o objeto de servidor para o cliente
            this.output = new ObjectOutputStream(socket.getOutputStream());

            this.output.writeObject(this.loadBalancerData);

            // salva os logs de acesso
            this.logger.info("Acesso cliente de ip: " + this.socket.getInetAddress().getHostAddress());
            this.logger.info("Solicitou servidor de IP: " + this.loadBalancerData.IP + " e porta: " + this.loadBalancerData.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}