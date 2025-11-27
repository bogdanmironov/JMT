package bg.sofia.uni.fmi.mjt.foodanaliyzer.server;

import bg.sofia.uni.fmi.mjt.foodanaliyzer.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.httpclient.FDCHttpClient;
import bg.sofia.uni.fmi.mjt.foodanaliyzer.storage.FoodFileStorage;

import java.io.IOException;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Scanner;

public class FoodAnalyzerServer {
    private static final String EXIT_SERVER_COMMAND = "EXIT";
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 7777;
    private static final int BUFFER_SIZE = 8192;

    private final int port;
    private boolean isServerWorking;

    private ByteBuffer buffer;
    private final Selector selector;

    private final ServerSocketChannel serverSocketChannel;
    private final CommandExecutor commandExecutor;

    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            CommandExecutor commandExecutor =
                new CommandExecutor(configureStorage(), new FDCHttpClient(HttpClient.newHttpClient()),
                    configureExceptionLoggingWriter());
            var server = new FoodAnalyzerServer(SERVER_PORT, commandExecutor, serverSocketChannel, Selector.open());

            server.startThread();

            Scanner scanner = new Scanner(System.in);
            while (server.isServerWorking) {
                var res = scanner.nextLine();
                if (EXIT_SERVER_COMMAND.equals(res)) {
                    server.stop();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FoodAnalyzerServer(int port, CommandExecutor commandExecutor,
                              ServerSocketChannel serverSocketChannel, Selector selector) {
        this.port = port;
        this.commandExecutor = commandExecutor;
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
    }

    //Server start/stop
    public void startThread() {
        new Thread(() -> {
            try {
                this.start();
            } catch (IOException e) {
                isServerWorking = false;
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void start() throws IOException {
        configureServerSocketChannel();
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        isServerWorking = true;

        while (isServerWorking) {
            serverCommunication(commandExecutor);
        }
    }

    public void stop() throws IOException {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }

        for (SelectionKey key : selector.keys()) {
            key.channel().close();
        }

        selector.close();
        serverSocketChannel.close();
        commandExecutor.close();
    }

    //Server communication
    private void serverCommunication(CommandExecutor commandExecutor) {
        try {
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                return;
            }

            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isReadable()) {
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    String clientInput = getClientInput(clientChannel);
                    System.out.println(clientInput);
                    if (clientInput == null) {
                        continue;
                    }

                    String output = commandExecutor.execute(CommandCreator.newCommand(clientInput));
                    writeClientOutput(clientChannel, output);
                } else if (key.isAcceptable()) {
                    accept(selector, key);
                }

                keyIterator.remove();
            }
        } catch (IOException e) {
            System.out.println("Error occurred while processing client request: " + e.getMessage());
        }
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    //Server configuration
    private void configureServerSocketChannel() throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, this.port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private static FoodFileStorage configureStorage() {
        final String rootPath = "";
        final Path workingDir = Paths.get(rootPath).toAbsolutePath();
        final String foodFileName = "project/res/food";
        final String foodFilePath = foodFileName + ".txt";
        final String foodReportFileName = foodFileName + "_report";
        final String foodReportFilePath = foodReportFileName + ".txt";

        return new FoodFileStorage(workingDir.resolve(foodFilePath), workingDir.resolve(foodReportFilePath));
    }

    //Do we want it open the whole time?
    //Isn't it better to return Path and open with resources whenever we need it?
    //...also where best to close stream
    private static Writer configureExceptionLoggingWriter() throws IOException {
        final String rootPath = "";
        final Path workingDir = Paths.get(rootPath).toAbsolutePath();
        final Path exceptionFilePath = workingDir.resolve("project/res/exceptions.txt");

        return Files.newBufferedWriter(exceptionFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
