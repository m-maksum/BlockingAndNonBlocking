package Blocking;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockingServerGUI {
    private static JTextArea logArea;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Blocking Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        // Title label in the top center
        JLabel titleLabel = new JLabel("Blocking Server");
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
            // Create server socket listening on port 12345
            ServerSocket serverSocket = new ServerSocket(12345);
            logArea.append("Blocking Server Berjalan...\n");

            while (true) {
                // Accept client connection
                Socket clientSocket = serverSocket.accept();
                logArea.append("Client connected: " + clientSocket + "\n");

                // Handle client connection in a new thread
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ClientHandler class to handle client connections
    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private Scanner in;

        // Constructor to initialize socket, input, and output
        public ClientHandler(Socket socket) throws IOException {
            clientSocket = socket;
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new Scanner(clientSocket.getInputStream());
        }

        // Run method executed when the thread starts
        public void run() {
            try {
                String clientMessage;
                // Read messages from client and send responses
                while ((clientMessage = in.nextLine()) != null) {
                    logArea.append("Received from client: " + clientMessage + "\n");

                    // Extract client's message and information
                    String portClient = "";
                    String clientString = "";
                    Pattern pattern = Pattern.compile("\\[(.*?) - (\\d+)](\\w+)");
                    Matcher matcher = pattern.matcher(clientMessage);
                    if (matcher.find()) {
                        String clientName = matcher.group(1);
                        portClient = matcher.group(2);
                        clientString = matcher.group(3);
                    } else {
                        logArea.append("Format not valid!\n");
                    }

                    // Create server responses based on client's message
                    String serverResponse;
                    if (clientString.equals("Method")) {
                        serverResponse = "This server is using the blocking method!";
                    } else if (clientString.equals("Port")) {
                        serverResponse = "Your client port is " + portClient;
                    } else {
                        serverResponse = "Hello " + portClient + " from Server!";
                    }

                    // Send response to client
                    out.println(serverResponse);
                }
            } catch (Exception e) {
                logArea.append("Client disconnected: " + clientSocket + "\n");
            } finally {
                try {
                    // Close client socket when the connection is lost
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
