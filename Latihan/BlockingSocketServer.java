// BlockingSocketServer.java
// Program Server

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockingSocketServer {
    private static final int PORT = 8888;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String clientMessage = reader.readLine();
                System.out.println("Received from client: " + clientMessage);

                // Kirim pesan balasan ke client
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                writer.println("Hello from Server!");

                // Menutup socket client
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
