package Src.View.Client;

import java.io.EOFException;
import java.text.ParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import Src.Domain.Client.Client;
import Src.Domain.Server.Message.Message;
import Src.Domain.Structures.ServiceOrder.ServiceOrderInterface;

public class ClientView {
    private Client client;
    private Scanner scanner;

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";

    private static final String STAR = "\u2B50";
    private static final String ARROW = "\u27A1";
    private static final String CHECK = "\u2714";
    private static final String CROSS = "\u274C";

    public ClientView() {
        this.client = new Client();
        this.scanner = new Scanner(System.in);
    }

    public void start() throws ParseException {
        boolean authenticated = false;

        System.out.println("Para inciar é necessario fazer a authenticação");
        while (true) {
            System.out.println("Digite 1 para authenticar e 2 para sair");
            String response = this.scanner.nextLine();

            if (response.equals("2")) {
                break;
            }

            System.out.print("Digite seu usuario: ");
            String user = this.scanner.nextLine();

            System.out.print("Digite sua senha: ");
            String password = this.scanner.nextLine();

            String userData = user + ":" + password;

            authenticated = this.client.authenticate(userData);

            if (authenticated) {
                System.out.println("Bem vindo ao sistema");
                
                break;
            }

            System.out.println("A authenticação falhou tente novamente");
        }

        while (authenticated) {
            try {
                System.out.println(BLUE + "\n--- Interface do Cliente ---" + RESET);
                System.out.println(YELLOW + "1. " + ARROW + " Armazenar Ordem de Serviço" + RESET);
                System.out.println(YELLOW + "2. " + ARROW + " Deletar Ordem de Serviço por ID" + RESET);
                System.out.println(YELLOW + "3. " + ARROW + " Obter Ordem de Serviço por ID" + RESET);
                System.out.println(YELLOW + "4. " + ARROW + " Atualizar Ordem de Serviço" + RESET);
                System.out.println(YELLOW + "5. " + ARROW + " Listar Todas as Ordens de Serviço" + RESET);
                System.out.println(YELLOW + "6. " + ARROW + " Contar Todas as Ordens de Serviço" + RESET);
                System.out.println(RED + "7. " + ARROW + " Sair" + RESET);
                System.out.print(CYAN + "Escolha uma opção: " + RESET);
            
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        storeServiceOrder();
                        break;
                    case 2:
                        deleteServiceOrderById();
                        break;
                    case 3:
                        getServiceOrderById();
                        break;
                    case 4:
                        updateServiceOrder();
                        break;
                    case 5:
                        listServiceOrders();
                        break;
                    case 6:
                        countServiceOrders();
                        break;
                    case 7:
                        System.out.println(PURPLE + "Saindo... " + CROSS + RESET);
                        return;
                    default:
                        System.out.println(RED + "Opção inválida. Por favor, tente novamente." + RESET);
                }
            } catch (InputMismatchException e) {
                System.out.println(RED + "Opção inválida. Por favor, tente novamente." + RESET);
                scanner.nextLine();
            } catch (EOFException e) {
                System.out.println(RED + "A conexão com o servidor foi perdida, reinicie a aplicação e tente novamente em alguns segundos." + RESET);

                authenticated = false;
            } catch (Exception e) {
                System.out.println(RED + "ERRO INTERNO DO SERVIDOR." + e.getMessage() + RESET);

                e.printStackTrace();
            }
        }
    }

    public void storeServiceOrder() throws ParseException, EOFException {
        Message message = new Message("store");

        System.out.println("Digite os detalhes da ordem de serviço:");

        System.out.print("Nome da Ordem de Serviço: ");
        message.setName(this.scanner.nextLine());

        System.out.print("Descrição da Ordem de Serviço: ");
        message.setDescription(this.scanner.nextLine());

        this.client.storeServiceOrder(message);

        System.out.println(GREEN + "Ordem de serviço armazenada com sucesso. " + CHECK + RESET);
    }

    public void deleteServiceOrderById() throws EOFException {
        System.out.print(CYAN + ARROW + " Digite o ID da Ordem de Serviço: " + RESET);
        int id = scanner.nextInt();
        scanner.nextLine();

        Message message = new Message("delete");
        message.setCode(id);

        if (id < 0) {
            System.out.println(RED + "ID da ordem de serviço inválido. " + CROSS + RESET);
            return;
        }

        if (client.deleteServiceOrder(message)) {
            System.out.println(GREEN + "Ordem de serviço deletada com sucesso. " + CHECK + RESET);
        } else {
            System.out.println(RED + "Falha ao deletar a ordem de serviço. " + CROSS + RESET);
        }
    }

    public void getServiceOrderById() throws ParseException, EOFException {
        System.out.print(CYAN + ARROW + " Digite o ID da Ordem de Serviço: " + RESET);
        int id = scanner.nextInt();
        scanner.nextLine();

        Message message = new Message("get");
        message.setCode(id);

        ServiceOrderInterface serviceOrder = client.getServiceOrder(message);
        if (serviceOrder != null) {
            System.out.println(GREEN + "Ordem de serviço encontrada: " + CHECK + " " + serviceOrder + RESET);
        } else {
            System.out.println(RED + "Ordem de serviço não encontrada. " + CROSS + RESET);
        }
    }

    public void updateServiceOrder() throws EOFException {
        Message message = new Message("update");

        System.out.println("Digite os detalhes da ordem de serviço:");
        
        System.out.print("ID da Ordem de Serviço: ");
        int id = this.scanner.nextInt();
        message.setCode(id);
        this.scanner.nextLine();

        if (id < 0) {
            System.out.println(RED + "ID da ordem de serviço inválido. " + CROSS + RESET);
            return;
        }
        
        System.out.print("Nome da Ordem de Serviço: ");
        message.setName(this.scanner.nextLine());

        System.out.print("Descrição da Ordem de Serviço: ");
        message.setDescription(this.scanner.nextLine());

        ServiceOrderInterface order = client.updateServiceOrder(message);

        if (order == null) {
            System.out.println(RED + "Falha ao atualizar a ordem de serviço. " + CROSS + RESET);
            return;
        }

        System.out.println(GREEN + "Ordem de serviço atualizada com sucesso. " + CHECK + RESET);
    }

    public void listServiceOrders() throws ParseException, EOFException {
        Message message = new Message("getAll");

        List<ServiceOrderInterface> orders = client.listServiceOrders(message);

        if (orders.isEmpty()) {
            System.out.println(RED + "Nenhuma ordem de serviço encontrada. " + CROSS + RESET);
        } else {
            System.out.println(GREEN + "Listando ordens de serviço:" + RESET);
            for (ServiceOrderInterface order : orders) {
                System.out.println(PURPLE + STAR + " " + order + RESET);
            }
        }
    }

    public void countServiceOrders() throws ParseException, EOFException {
        int orders = this.client.countServiceOrders();

        if (orders == 0) {
            System.out.println(RED + "Nenhuma ordem de serviço encontrada. " + CROSS + RESET);
        } else {
            System.out.println(GREEN + "Contagem de ordens de serviço: " + orders + RESET);
        }
    }
}
