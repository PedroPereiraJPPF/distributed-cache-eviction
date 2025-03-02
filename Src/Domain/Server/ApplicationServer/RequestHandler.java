package Src.Domain.Server.ApplicationServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Src.Domain.Server.Message.Message;
import Utils.Logger;

public class RequestHandler implements Runnable {
    private Logger logger;
    private Socket client;
    private ObjectOutputStream clientOutput;
    private ObjectInputStream clientInput;
    
    public RequestHandler(Socket client) throws IOException {
        this.logger = new Logger("Logs/ApplicationServerLogs.log");
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
                
                
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();

                this.logger.error("Erro ao receber dados do cliente");
                this.logger.error(e.getMessage());
            }
            
            
        }
    }
}
