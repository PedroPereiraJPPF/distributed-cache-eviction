package Src.Domain.Client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import Src.Domain.Client.Interface.ClientInterface;
import Src.Domain.LocalizationServer.RmiMethods.LocalizationInterface;
import Src.Domain.Server.Server;
import Src.Domain.Server.Interface.ServerInterface;
import Src.Domain.Server.Message.CompressedObject;
import Src.Domain.Server.Message.CompressionManager;
import Src.Domain.Server.Message.Message;
import Src.Domain.Structures.ServerData.ServerData;
import Src.Domain.Structures.ServiceOrder.ServiceOrder;
import Src.Domain.Structures.ServiceOrder.ServiceOrderInterface;

public class Client implements ClientInterface {
    private ServerData localizationServerData;
    private ServerData serverData;
    private ServerInterface server;
    private Socket serverSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private boolean authenticated; 

    public Client() {
        this.localizationServerData = new ServerData("10.10.70.78", 5000);

        this.connectServer();
    }

    @Override
    public ServerInterface connectServer() {
        try {
            // conecta com o servidor de localização e pega o endereço do novo
            this.serverSocket = new Socket(this.localizationServerData.IP, this.localizationServerData.port);

            this.input = new ObjectInputStream(this.serverSocket.getInputStream());

            this.serverData = (ServerData) this.input.readObject();

            this.serverSocket.close();

            this.server = new Server();

            return this.server;
        } catch (Exception e) {
            System.out.println("Erro ao conectar com o servidor");

            System.out.println(e.getMessage());

            return null;
        }
    }

    // nesse metodo vou receber os dados e tentar authenticar o usuario
    // caso a resposta do servidor seja positiva a authenticação foi concluida
    public boolean authenticate(String userData) {
        try {
            // descarta a conexão antiga e cria uma nova com o servidor recebido
            this.serverSocket = new Socket(this.serverData.IP, this.serverData.port);

            this.input = new ObjectInputStream(this.serverSocket.getInputStream());
            this.output = new ObjectOutputStream(this.serverSocket.getOutputStream());

            this.output.writeObject(userData);

            String authResponse = (String) this.input.readObject();

            this.authenticated = authResponse.equals("auth:valid");

            return this.authenticated;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ServiceOrderInterface storeServiceOrder(Message message) throws ParseException, EOFException, IOException, ClassNotFoundException {
        try {
            this.output.writeObject(message);

            Message response = (Message) this.input.readObject();

            return this.messageToServiceOrder(response);
        } catch (EOFException e) {
            throw e;
        } catch (ClassNotFoundException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public boolean deleteServiceOrder(Message message) throws EOFException, IOException, ClassNotFoundException {
        try {
            this.output.writeObject(message);

            boolean response = (boolean) this.input.readObject();

            return response;
        } catch (EOFException e) {
            throw e;
        } catch(ClassNotFoundException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public ServiceOrderInterface getServiceOrder(Message message) throws ParseException, EOFException, IOException, ClassNotFoundException {
        Message serverMessage = null;

        try {
            this.output.writeObject(message);

            serverMessage = (Message) this.input.readObject();
        } catch (EOFException e) {
            throw e;
        } catch(ClassNotFoundException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        if (serverMessage == null) {
            return null;
        }

        return this.messageToServiceOrder(serverMessage);
    }

    @Override
    public ServiceOrderInterface updateServiceOrder(Message message) throws EOFException, IOException, ClassNotFoundException {
        Message serverMessage = null;

        try {
            this.output.writeObject(message);

            serverMessage = (Message) this.input.readObject();
        } catch (EOFException e) {
            throw e;
        } catch(ClassNotFoundException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        if (serverMessage == null) {
            return null;
        }

        try {
            return this.messageToServiceOrder(serverMessage);
        } catch (ParseException e) {
            e.printStackTrace();

            System.out.println("Erro ao converter mensagem do servidor");

            return null;
        }
    }

    @Override
    public List<ServiceOrderInterface> listServiceOrders(Message newMessage) throws ParseException, EOFException, IOException, ClassNotFoundException {
        List<ServiceOrderInterface> orders = new ArrayList<>();

        try {
            this.output.writeObject(newMessage);

            List<Message> messages = (ArrayList<Message>) this.input.readObject();

            for (Message message : messages) {
                orders.add(this.messageToServiceOrder(message));
            }
        } catch (EOFException e) {
            throw e;
        } catch (ClassNotFoundException | IOException e) {
            throw e;
        }

        return orders;
    }

    @Override
    public int countServiceOrders() throws EOFException, IOException, ClassNotFoundException {
        Message message = new Message("getAll");

        try {
            return this.listServiceOrders(message).size();
        } catch (ParseException e) {
            e.printStackTrace();

            System.out.println("Erro ao converter mensagem do servidor");

            return 0;
        }
    }

    public void changeServer() throws RemoteException, NotBoundException {
        // Conecta com o rmi do servidor de localização para avisar da falha
        Registry registry = LocateRegistry.getRegistry("10.10.70.78", 4000);

        LocalizationInterface localization = (LocalizationInterface) registry.lookup("localization");

        localization.serverInactive(serverData.IP, serverData.port);

        this.connectServer();
    }

    private ServiceOrderInterface messageToServiceOrder(Message message) throws ParseException {
        CompressedObject data = message.getData();
        ServiceOrderInterface serviceOrder = new ServiceOrder();
        int code = Integer.valueOf(CompressionManager.decodeParameter(data.getValues()[0], data.getFrequencyTable()));

        serviceOrder.setCode(code);
        serviceOrder.setName(CompressionManager.decodeParameter(data.getValues()[1], data.getFrequencyTable()));
        serviceOrder.setDescription(CompressionManager.decodeParameter(data.getValues()[2], data.getFrequencyTable()));
        serviceOrder.setRequestTime(new Date());

        return serviceOrder;
    }
}
