package Blocking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BlockingClientGUI {
    private static JTextField nameField;
    private static JTextField messageField;
    private static JTextArea chatArea;
    private static Socket socket;
    private static PrintWriter out;
    private static Scanner in;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Blocking Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        // Panel for entering client's name
        JPanel namePanel = new JPanel();
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(15); // Set preferred width for the name field
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        // Panel for entering messages
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // Add components to the frame
        frame.add(namePanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Establish connection to the server
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
            chatArea.append("Connected to Server!\n");

            // Start a new thread to handle incoming messages from the server
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (in.hasNextLine()) {
                        String response = in.nextLine();
                        chatArea.append("Server: " + response + "\n");
                    }
                }
            });
            thread.start();

        } catch (IOException ex) {
            ex.printStackTrace();
            chatArea.append("Failed to connect to Server.\n");
        }

        frame.setVisible(true);
    }

    private static void sendMessage() {
        String name = nameField.getText();
        String message = messageField.getText();
        if (!name.isEmpty() && !message.isEmpty()) {
            int clientPort = socket.getLocalPort();
            out.println("[" + name + " - " + clientPort + "]" + message);
            chatArea.append("You: " + message + "\n");
            messageField.setText("");
        }
    }
}
