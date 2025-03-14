package Src.Domain.LocalizationServer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Random;

import Src.Domain.Structures.ServerData.ServerData;
import Utils.Logger;

// vai gerenciar as requests do cliente quando ele não possui o ip do servidor de proxy/loadBalancer
class RequestHandler implements Runnable {
    // instancia do socket e output
    private Socket socket;
    private ObjectOutputStream output;
    private List<ServerData> proxyList;
    private Logger logger;

    public RequestHandler(Socket socket, List<ServerData> proxyList, Logger logger) {
        this.socket = socket;
        this.proxyList = proxyList;
        this.logger = logger;
    }

    public void run() {
        try {
            // manda o objeto de servidor para o cliente
            this.output = new ObjectOutputStream(socket.getOutputStream());

            Random random = new Random();

            int currentIndex = random.nextInt(proxyList.size());
            ServerData loadBalancerData = proxyList.get(currentIndex);

            System.out.println("Enviando servidor de IP: " + loadBalancerData.IP + " e porta: " + loadBalancerData.port);

            this.output.writeObject(loadBalancerData);

            // salva os logs de acesso
            this.logger.info("Acesso cliente de ip: " + this.socket.getInetAddress().getHostAddress());
            this.logger.info("Solicitou servidor de IP: " + loadBalancerData.IP + " e porta: " + loadBalancerData.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}