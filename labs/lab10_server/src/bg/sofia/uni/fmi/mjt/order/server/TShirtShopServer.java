package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.repository.MJTOrderRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TShirtShopServer {
    private static final int SERVER_PORT = 4444;
    private static final int MAX_EXECUTOR_THREADS = 10;
    private int nextClientId = 0;

    private final MJTOrderRepository repository = new MJTOrderRepository();

    public void run() {

        try (ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            Thread.currentThread().setName("TShirt Shop Server Thread");
            System.out.println("Server started and listening for connect requests");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection request from client " + clientSocket.getInetAddress());

                TShirtShopClientHandler clientHandler = new TShirtShopClientHandler(
                    clientSocket,
                    nextClientId++,
                    repository
                );

                executor.execute(clientHandler);
            }

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the server socket", e);
        }
    }
}
