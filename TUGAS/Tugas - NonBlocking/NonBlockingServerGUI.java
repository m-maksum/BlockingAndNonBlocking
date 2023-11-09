package NonBlocking;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class NonBlockingServerGUI {
    private static JTextArea logArea;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Non-Blocking Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        // Title label in the top center
        JLabel titleLabel = new JLabel("Non-Blocking Server");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.add(titleLabel, BorderLayout.NORTH);

        // JTextArea for server logs
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Button to stop the server
        JButton stopButton = new JButton("Stop Server");
        stopButton.addActionListener(e -> {
            try {
                frame.dispose(); // Close the JFrame
                System.exit(0); // Terminate the program
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        frame.add(stopButton, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Start the server in a separate thread
        new Thread(() -> startServer()).start();
    }

    public static void startServer() {
        try {
            // Create ServerSocketChannel to accept connections
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(12345));
            serverSocketChannel.configureBlocking(false);

            // Create Selector to monitor I/O operations on registered channels
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            logArea.append("Non-Blocking Server is running...\n");

            while (true) {
                selector.select();

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isAcceptable()) {
                        SocketChannel clientChannel = serverSocketChannel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        logArea.append("Client connected: " + clientChannel + "\n");
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        StringBuilder clientMessageBuilder = new StringBuilder();

                        int bytesRead;
                        while ((bytesRead = clientChannel.read(buffer)) > 0) {
                            buffer.flip();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);
                            clientMessageBuilder.append(new String(bytes));
                            buffer.clear();

                            if (clientMessageBuilder.toString().endsWith("\n")) {
                                String clientMessage = clientMessageBuilder.toString().trim();
                                logArea.append("Received from client: " + clientMessage + "\n");

                                String clientString = clientMessage.replaceAll("\\[\\d+\\]\\s*", "");
                                String serverResponse = clientString;
                                ByteBuffer responseBuffer = ByteBuffer.wrap(serverResponse.getBytes());
                                clientChannel.write(responseBuffer);

                                clientMessageBuilder.setLength(0);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

