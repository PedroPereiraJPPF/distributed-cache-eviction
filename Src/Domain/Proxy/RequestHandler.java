package Src.Domain.Proxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import Src.Domain.Server.Message.CompressedObject;
import Src.Domain.Server.Message.CompressionManager;
import Src.Domain.Server.Message.Message;
import Src.Domain.Structures.ServiceOrder.ServiceOrder;
import Src.Domain.Structures.ServiceOrder.ServiceOrderInterface;
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

                Message convertedMessage = (Message) clientMessage;

                String operation = convertedMessage.getOperation();

                if (Arrays.asList(new String[]{"get"}).contains(operation)) {
                    ServiceOrderInterface serviceOrder = this.messageToServiceOrder(convertedMessage);

                    ServiceOrderInterface value = ProxyServer.cache.find(serviceOrder.getCode());

                    if (value != null) {
                        this.logger.info("O valor buscado estava na cache o codigo é: " + value.getCode());

                        this.outputClient.writeObject(new Message(
                            value.getCode(),
                            value.getName(),
                            value.getDescription(),
                            value.getRequestTime()
                        ));

                        this.logger.info("Mensagem do cache enviada para o cliente de ip: " + this.client.getInetAddress().getHostAddress());

                        continue;
                    }
                } else if (Arrays.asList(new String[]{"delete"}).contains(operation)) {
                    // remove o valor da cache quando o cliente tenta deletar
                    ServiceOrderInterface serviceOrder = this.messageToServiceOrder(convertedMessage);

                    synchronized (ProxyServer.cache) {
                        ServiceOrderInterface value = ProxyServer.cache.find(serviceOrder.getCode());

                        if (value != null) {
                            ProxyServer.cache.delete(value.getCode());

                            this.logger.info("Item removido da cache");
                        }
                    }
                }

                this.outputServer.writeObject(clientMessage);

                this.logger.info("Mensagem enviada para servidor de IP: " + this.server.getInetAddress().getHostAddress());

                // recebe a resposta do servidor e reenvia para o cliente
                Object serverMessage = this.inputServer.readObject();

                if (serverMessage instanceof Message) {
                    Message message = (Message) serverMessage;
                    String serverOperation = message.getOperation();
                    String[] operationParts = {"none"};

                    if (serverOperation != null) {
                        serverOperation.split(":");
                    }

                    if (operationParts[0].equals("update-cache")) {
                        ServiceOrderInterface so = this.messageToServiceOrder(message);

                        if (operationParts[1].equals("get")) {
                            synchronized (ProxyServer.cache) {
                                this.logger.info("Adiciona novo item na cache");

                                ProxyServer.cache.insert(so);
                            }
                        } else if (operationParts[1].equals("update")) {
                            synchronized (ProxyServer.cache) {
                                this.logger.info("Atualiza um item da cache");

                                ProxyServer.cache.delete(so.getCode());

                                ProxyServer.cache.insert(so);
                            }
                        }
                    }
                }

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
            } catch (ParseException e) {
                e.printStackTrace();

                this.logger.error("Erro interno no proxy para o cliente: " + this.client.getInetAddress().getHostAddress());

                this.logger.error(e.getStackTrace().toString());
            }
        }
    }

    private ServiceOrderInterface messageToServiceOrder(Message message) throws ParseException {
        CompressedObject data = message.getData();
        ServiceOrderInterface serviceOrder = new ServiceOrder();
        int code = Integer.valueOf(CompressionManager.decodeParameter(data.getValues()[0], data.getFrequencyTable()));

        serviceOrder.setCode(code);

        if (data.getValues()[1] != null) {
            serviceOrder.setName(CompressionManager.decodeParameter(data.getValues()[1], data.getFrequencyTable()));
        }

        if (data.getValues()[2] != null) {
            serviceOrder.setDescription(CompressionManager.decodeParameter(data.getValues()[2], data.getFrequencyTable()));
        }

        if (data.getValues()[3] != null) {
            String decodedRequestTime = CompressionManager.decodeParameter(data.getValues()[3], data.getFrequencyTable());
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            Date requestTime = dateFormat.parse(decodedRequestTime);
            serviceOrder.setRequestTime(requestTime);
        }

        return serviceOrder;
    }
}
