package Src.Domain.Server.ApplicationServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import Src.Domain.Server.Server;
import Src.Domain.Server.Message.Message;
import Utils.Logger;

public class RequestHandler implements Runnable {
    private Logger logger;
    private Socket client;
    private Server serverCore;
    private ObjectOutputStream clientOutput;
    private ObjectInputStream clientInput;
    
    public RequestHandler(Socket client, Server server) throws IOException {
        this.logger = new Logger("Logs/ApplicationServerLogs.log");
        this.serverCore = server;
        this.client = client;

        try {
            this.clientInput = new ObjectInputStream(this.client.getInputStream());
            this.clientOutput = new ObjectOutputStream(this.client.getOutputStream());
        } catch (IOException e) {
            this.logger.error("Erro ao criar as entradas e saidas do cliente");

            this.logger.error(e.getMessage());

            throw e;
        }
    }

    public void run() {
        while (true) {
            try {
                Message message = (Message) this.clientInput.readObject();

                this.logger.info("Mensagem recebida do cliente: " + this.client.getInetAddress().getHostAddress());
                this.logger.info("Operação: " + message.getOperation());

                switch (message.getOperation()) {
                    case "store":
                        Message storeResponse = this.serverCore.storeServiceOrder(message);

                        this.logger.info("Novo dado salvo no banco pelo cliente: " + this.client.getInetAddress().getHostAddress());

                        this.clientOutput.writeObject(storeResponse);
                        break;
                    case "delete":
                        boolean deleteResponse = this.serverCore.deleteServiceOrder(message);

                        if (deleteResponse) {
                            this.logger.info("Ordem de serviço deletada");
                        } else {
                            this.logger.info("Falha ao deletar ordem de serviço");
                        }

                        this.clientOutput.writeObject(deleteResponse);

                        break;
                    case "getAll":
                        List<Message> listResponse = this.serverCore.listServiceOrders();
                
                        this.clientOutput.writeObject(listResponse);

                        this.logger.info("Listagem retornada para o cliente: " + this.client.getInetAddress().getHostAddress());
                    default:
                        break;
                }

                
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();

                this.logger.error("Erro ao receber dados do cliente");
                this.logger.error(e.getMessage());
            }
            
            
        }
    }
}
