package Src.Domain.Proxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Src.Domain.Server.Message.Message;
import Utils.Logger;

public class RequestHandler implements Runnable {
    private Socket client;
    private Socket server;
    private Logger logger;
    private ObjectInputStream inputClient;
    private ObjectOutputStream outputClient;
    private ObjectInputStream inputServer;
    private ObjectOutputStream outputServer;

    public RequestHandler(String serverIP, Integer serverPort, Socket client, ObjectInputStream in, ObjectOutputStream out) throws IOException {
        this.logger = new Logger("Logs/ProxyLogs.log");

        // instancia que controla o cliente
        this.client = client;

        // instancia que controla o servidor que o cliente vai se conectar
        try {
            this.server = new Socket(serverIP, serverPort);

            this.logger.info("Conectado ao servidor de aplicação: " + serverIP + " port: " + serverPort);

            this.outputClient = out;
            this.inputClient = in;
            this.outputServer = new ObjectOutputStream(this.server.getOutputStream());
            this.inputServer = new ObjectInputStream(this.server.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();

            this.logger.error("Erro ao criar entradas e saidas nos sockets");

            throw e;
        } catch (Exception e) {
            this.logger.error("Erro ao se conectar ao servidor de ip: " + serverIP + " e porta: " + serverPort);

            throw e;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                // recebe a mensagem do cliente e reenvia para o servidor
                Message clientMessage = (Message) this.inputClient.readObject();

                this.logger.info("Mensagem recebida IP: " + this.client.getInetAddress().getHostAddress());

                this.outputServer.writeObject(clientMessage);

                this.logger.info("Mensagem enviada para servidor de IP: " + this.server.getInetAddress().getHostAddress());

                // recebe a resposta do servidor e reenvia para o cliente
                Message serverMessage = (Message) this.inputServer.readObject();

                this.logger.info("Mensagem recebida do servidor de IP: " + this.server.getInetAddress().getHostAddress());

                this.outputClient.writeObject(serverMessage);

                this.logger.info("Mensagem reenviada para o cliente de IP: " + this.client.getInetAddress().getHostAddress());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();

                this.logger.error("Classe message não encontrada: " + this.client.getInetAddress().getHostAddress());
                this.logger.error(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();

                this.logger.error("Erro interno no proxy para o cliente: " + this.client.getInetAddress().getHostAddress());
                this.logger.error(e.getMessage());
            }
        }
    }
}
