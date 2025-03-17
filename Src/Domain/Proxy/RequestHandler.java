package Src.Domain.Proxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import Src.Domain.Proxy.RmiMethods.ProxyRmiInterface;
import Src.Domain.Server.Message.CompressedObject;
import Src.Domain.Server.Message.CompressionManager;
import Src.Domain.Server.Message.Message;
import Src.Domain.Structures.ServerData.ServerData;
import Src.Domain.Structures.ServiceOrder.ServiceOrder;
import Src.Domain.Structures.ServiceOrder.ServiceOrderInterface;
import Utils.Logger;

public class RequestHandler implements Runnable {
    private String userName;
    private Socket client;
    private Socket server;
    private Logger logger;
    private ObjectInputStream inputClient;
    private ObjectOutputStream outputClient;
    private ObjectInputStream inputServer;
    private ObjectOutputStream outputServer;

    public RequestHandler(String serverIP, Integer serverPort, Socket client, Logger logger) throws IOException {
        this.logger = logger;

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

        Map<String, String> users = ProxyServer.users; 

        try {
            logger.info("Dados de authenticação solicitados");

            String response = (String) this.inputClient.readObject();

            logger.info("Dados de authenticação recebidos");

            String[] userData = response.split(":");

            if (userData.length < 2) {
                logger.info("Usuario: " + this.client.getInetAddress().getHostAddress() + " não reconhecido");
                
                this.outputClient.writeObject(new String("auth:invalid"));
                
                client.close();
                return;
            }

            this.userName = userData[0];

            String password = users.get(this.userName);

            if (password == null || !password.equals(userData[1])) {
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

                this.logger.info("\n============================================ \n" +
                                 " Mensagem recebida do cliente de IP: " + this.client.getInetAddress().getHostAddress() +
                                 "\n Usuario: " + this.userName + 
                                 "\n============================================ ");

                Message convertedMessage = (Message) clientMessage;

                String operation = convertedMessage.getOperation();

                if (operation.equals("get")) {
                    ServiceOrderInterface serviceOrder = this.messageToServiceOrder(convertedMessage);

                    ServiceOrderInterface value = ProxyServer.cache.find(serviceOrder.getCode());

                    // caso o item não esta na cache é feito a busca em outros proxies
                    if (value == null) {
                        boolean itemFoundInOtherProxy = false;

                        for (ServerData data : ProxyServer.rmiList) {
                            Registry registry = LocateRegistry.getRegistry(data.IP, data.port);

                            try {
                                ProxyRmiInterface proxyRmi = (ProxyRmiInterface) registry.lookup("proxy");

                                ServiceOrderInterface order = proxyRmi.getServiceOrder(serviceOrder);

                                if (order != null) {
                                    value = order;

                                    synchronized (ProxyServer.cache) {
                                        this.logger.info("\n============================================" +
                                                         "\n O item estava na cache do proxy com o proxy de IP: " + data.IP +
                                                         "\n Adiciona novo item na cache codigo: " + order.getCode() +
                                                         "\n============================================ ");

                                        ProxyServer.cache.insert(order);
                                    }

                                    itemFoundInOtherProxy = true;

                                    break;
                                }
                            } catch (NotBoundException e) {
                                System.out.println("Falha ao informar um proxy que um item foi deletado, dados rmi Ip: " + data.IP + " Port: " + data.port);
                                this.logger.error("Falha ao informar um proxy que um item foi deletado, dados rmi Ip: " + data.IP + " Port: " + data.port);
                            }                            
                        }

                        // envia a mensagem para o cliente caso encontre em algum cache
                        if (itemFoundInOtherProxy) {
                            this.outputClient.writeObject(new Message(
                                value.getCode(),
                                value.getName(),
                                value.getDescription(),
                                value.getRequestTime()
                            ));

                            this.logger.info("Mensagem do cache enviada para o cliente de ip: " + this.client.getInetAddress().getHostAddress());

                            continue;
                        }
                    } else {
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
                            ProxyServer.cache.delete(value);

                            this.logger.info("Item removido da cache");
                        }

                        for (ServerData data : ProxyServer.rmiList) {
                            Registry registry = LocateRegistry.getRegistry(data.IP, data.port);

                            try {
                                ProxyRmiInterface proxyRmi = (ProxyRmiInterface) registry.lookup("proxy");

                                proxyRmi.removeCacheItem(serviceOrder);
                            } catch (NotBoundException e) {
                                System.out.println("Falha ao informar um proxy que um item foi deletado, dados rmi Ip: " + data.IP + " Port: " + data.port);
                                this.logger.error("Falha ao informar um proxy que um item foi deletado, dados rmi Ip: " + data.IP + " Port: " + data.port);
                            }                            
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
                        operationParts = serverOperation.split(":");
                    }

                    if (operationParts[0].equals("update-cache")) {
                        ServiceOrderInterface so = this.messageToServiceOrder(message);

                        if (operationParts[1].equals("get")) {
                            synchronized (ProxyServer.cache) {
                                this.logger.info("Adiciona novo item na cache");

                                ProxyServer.cache.delete(so);

                                ProxyServer.cache.insert(so);
                            }
                        } else if (operationParts[1].equals("update") && ProxyServer.cache.find(so.getCode()) != null) {
                            synchronized (ProxyServer.cache) {
                                this.logger.info("Atualiza um item da cache");

                                ProxyServer.cache.delete(so);

                                ProxyServer.cache.insert(so);

                                for (ServerData data : ProxyServer.rmiList) {
                                    Registry registry = LocateRegistry.getRegistry(data.IP, data.port);
        
                                    try {
                                        ProxyRmiInterface proxyRmi = (ProxyRmiInterface) registry.lookup("proxy");
        
                                        proxyRmi.updateCacheItem(so);
                                    } catch (NotBoundException e) {
                                        System.out.println("Falha ao informar um proxy que um item foi atualizado, dados rmi Ip: " + data.IP + " Port: " + data.port);
                                        this.logger.error("Falha ao informar um proxy que um item foi atualizado, dados rmi Ip: " + data.IP + " Port: " + data.port);
                                    }                            
                                }
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
