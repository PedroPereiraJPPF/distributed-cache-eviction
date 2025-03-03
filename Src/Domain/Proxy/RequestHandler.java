package Src.Domain.Proxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Utils.Logger;

public class RequestHandler implements Runnable {
    private Socket client;
    private Socket server;
    private Logger logger;
    private ObjectInputStream inputClient;
    private ObjectOutputStream outputClient;
    private ObjectInputStream inputServer;
    private ObjectOutputStream outputServer;

    public RequestHandler(String serverIP, Integer serverPort, Socket client) throws IOException {
        this.logger = new Logger("Logs/ProxyLogs.log");

        // instancia que controla o cliente
        this.client = client;

        // instancia que controla o servidor que o cliente vai se conectar
        try {
            this.server = new Socket(serverIP, serverPort);

            this.logger.info("Conectado ao servidor de aplicação: " + serverIP + " port: " + serverPort);

            this.outputClient = new ObjectOutputStream(client.getOutputStream());
            this.inputClient = new ObjectInputStream(client.getInputStream());
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
        boolean authenticated = false;

        try {
            logger.info("Dados de authenticação solicitados");

            String response = (String) this.inputClient.readObject();

            logger.info("Dados de authenticação recebidos");

            String[] userData = response.split(":");

            if (!(userData[0].equals(ProxyServer.authName) && userData[1].equals(ProxyServer.password))) {
                logger.info("Usuario: " + this.client.getInetAddress().getHostAddress() + " não reconhecido");

                this.outputClient.writeObject(new String("auth:invalid"));

                client.close();

                return;
            }

            this.logger.info("Usuario: " + this.client.getInetAddress().getHostAddress() + " Authenticado");

            this.outputClient.writeObject(new String("auth:valid"));

            authenticated = true;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();

            this.logger.error("Erro ao realizar a authenticação");
        }

        while (authenticated) {
            try {
                // recebe a mensagem do cliente e reenvia para o servidor
                Object clientMessage = this.inputClient.readObject();

                this.logger.info("Mensagem recebida IP: " + this.client.getInetAddress().getHostAddress());

                this.outputServer.writeObject(clientMessage);

                this.logger.info("Mensagem enviada para servidor de IP: " + this.server.getInetAddress().getHostAddress());

                // recebe a resposta do servidor e reenvia para o cliente
                Object serverMessage = this.inputServer.readObject();

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

                authenticated = false;

                this.logger.warning("Cliente " + this.client.getInetAddress().getHostAddress() + " desconectado por causa de falha interna do servidor");

                this.logger.error(e.getStackTrace().toString());
            }
        }
    }
}
