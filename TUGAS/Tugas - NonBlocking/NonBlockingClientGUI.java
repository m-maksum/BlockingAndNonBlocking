package NonBlocking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NonBlockingClientGUI {
    private static JTextField nameField;
    private static JTextField messageField;
    private static JTextArea chatArea;
    private static SocketChannel socketChannel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Non-Blocking Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        JPanel namePanel = new JPanel();
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(15);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        frame.add(namePanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        frame.setVisible(true);

        // Connect to the server in a separate thread
        new Thread(() -> connectToServer()).start();
    }

    public static void connectToServer() {
        try {
            // Create SocketChannel to connect to the server
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("localhost", 12345));

            while (!socketChannel.finishConnect()) {
                // Wait for the connection to be established
            }

            chatArea.append("Connected to Server!\n");
        } catch (IOException e) {
            e.printStackTrace();
            chatArea.append(e+"\n");
        }
    }

    public static void sendMessage() {
        String name = nameField.getText();
        String message = messageField.getText();
        if (!name.isEmpty() && !message.isEmpty()) {
            int clientPort = socketChannel.socket().getLocalPort();
            ByteBuffer buffer = ByteBuffer.wrap(("[" + name + " - " + clientPort + "]" + message + "\n").getBytes());
            try {
                socketChannel.write(buffer);

                buffer.clear();
                int bytesRead = socketChannel.read(buffer);
                if (bytesRead > 0) {
                    buffer.flip();
                    String serverResponse = new String(buffer.array(), 0, bytesRead).trim();
                    chatArea.append("You: " + message + "\n");
                    chatArea.append("Server response: " + serverResponse + "\n");
                }

                messageField.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

