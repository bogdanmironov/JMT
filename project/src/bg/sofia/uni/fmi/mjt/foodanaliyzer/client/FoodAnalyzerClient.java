package bg.sofia.uni.fmi.mjt.foodanaliyzer.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class FoodAnalyzerClient {
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 8192;
    private static final String CONNECTED_TO_SERVER_MSG = "Connected to the server.";
    private static final String ENTER_MESSAGE_PROMPT = "Enter message: ";
    private static final String EXIT_PROMPT = "quit";
    private static final String SERVER_RESPONSE_START = "The server replied <";
    private static final String SERVER_RESPONSE_END = ">";
    private static final String SERVER_SENDING_START = "Sending message <";
    private static final String SERVER_SENDING_END = "> to the server...";
    private static final String CHARSET = "UTF-8";

    private static final ByteBuffer BUFFER = ByteBuffer.allocateDirect(BUFFER_SIZE);

    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            System.out.println(CONNECTED_TO_SERVER_MSG);

            while (true) {
                System.out.print(ENTER_MESSAGE_PROMPT);
                String message = scanner.nextLine();

                if (EXIT_PROMPT.equals(message)) {
                    break;
                }

                serverCommunication(message, socketChannel);
            }

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

    private static void serverCommunication(String message, SocketChannel socketChannel) throws IOException {
        System.out.println(SERVER_SENDING_START + message + SERVER_SENDING_END);

        BUFFER.clear();
        BUFFER.put(message.getBytes());
        BUFFER.flip();
        socketChannel.write(BUFFER);

        BUFFER.clear();
        socketChannel.read(BUFFER);
        BUFFER.flip();

        byte[] byteArray = new byte[BUFFER.remaining()];
        BUFFER.get(byteArray);
        String reply = new String(byteArray, CHARSET);

        System.out.println(SERVER_RESPONSE_START + reply + SERVER_RESPONSE_END);
    }
}
