package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.exception.SocketConnectionException;
import bg.sofia.uni.fmi.mjt.order.server.repository.MJTOrderRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TShirtShopClientHandler implements Runnable {
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String NEW_ORDER_COMMAND = "request";
    private static final int NEW_ORDER_COMMAND_PARAM_LEN = 3;
    private static final int NEW_ORDER_COMMAND_SIZE = 0;
    private static final int NEW_ORDER_COMMAND_COLOR = 1;
    private static final int NEW_ORDER_COMMAND_DESTINATION = 2;
    private static final String GET_ORDER_COMMAND = "get";
    private static final String GET_ORDER_ALL_SUBCOMMAND = "all";
    private static final String GET_ORDER_SUCCESSFUL_SUBCOMMAND = "all-successful";
    private static final String GET_ORDER_MY_SUBCOMMAND = "my-order";

    private final Socket socket;
    private final int clientNumber;
    private final MJTOrderRepository commonRepository;

    public TShirtShopClientHandler(Socket socket, int clientNumber, MJTOrderRepository commonRepository) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        this.commonRepository = commonRepository;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(
            "Client Request Handler for client #" + clientNumber + ", address: " + socket.getRemoteSocketAddress()
        );

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (processInput(inputLine, out)) break;
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try {
            socket.close();
        } catch (IOException e) {
            throw new SocketConnectionException("Could not close client #" + clientNumber + " socket");
        }
    }

    private boolean processInput(String inputLine, PrintWriter out) {
        if (DISCONNECT_COMMAND.equals(inputLine)) {
            out.println("Disconnected from the server");
            return true;
        }

        String[] splitInputByCommand = inputLine.split("\\s", 2);
        switch (splitInputByCommand[0]) {
            case GET_ORDER_COMMAND -> {
                if (splitInputByCommand.length != 2) {
                    out.println("Please enter full command");
                    return false;
                }
                getOrder(splitInputByCommand[1], out);
            }
            case NEW_ORDER_COMMAND-> {
                if (splitInputByCommand.length != 2) {
                    out.println("Please enter full command");
                    return false;
                }
                newOrder(splitInputByCommand[1], out);
            }
            default -> unknownCommand(out);
        }
        return false;
    }

    void getOrder(String command, PrintWriter clientOut) {
        String[] splitCommand = command.split("\\s");

        switch (splitCommand[0]) {
            case GET_ORDER_MY_SUBCOMMAND -> {
                if (splitCommand.length != 2) {
                    clientOut.println("syntax: get my-order id=[id]");
                    return;
                }
                String[] splitId = splitCommand[1].split("=");
                clientOut.println(commonRepository.getOrderById(Integer.parseInt(splitId[1])));
            }
            case GET_ORDER_ALL_SUBCOMMAND -> {
                if (splitCommand.length != 1) {
                    clientOut.println("syntax: get all");
                    return;
                }
                clientOut.println(commonRepository.getAllOrders().toString());
            }
            case GET_ORDER_SUCCESSFUL_SUBCOMMAND -> {
                if (splitCommand.length != 1) {
                    clientOut.println("syntax: get all-successful");
                    return;
                }
                clientOut.println(commonRepository.getAllSuccessfulOrders());
            }
        }
    }

    void newOrder(String command, PrintWriter clientOut) {
        String[] splitCommand = command.split("\\s");

        if (splitCommand.length != NEW_ORDER_COMMAND_PARAM_LEN) {
            clientOut.println("Request must have 3 parameters");
            return;
        }

        clientOut.println(commonRepository.request(
            splitCommand[NEW_ORDER_COMMAND_SIZE].split("=")[1],
            splitCommand[NEW_ORDER_COMMAND_COLOR].split("=")[1],
            splitCommand[NEW_ORDER_COMMAND_DESTINATION].split("=")[1]
        ));
    }

    void unknownCommand(PrintWriter clientOut) {
        clientOut.println("Unknown command");
    }
}
