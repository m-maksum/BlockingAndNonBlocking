// BlockingSocketClient.java
// Program Client

import java.io.*;
import java.net.Socket;

public class BlockingSocketClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to the server");

            // Mengirim pesan ke server
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("Hello from Client!");

            // Menerima pesan balasan dari server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverMessage = reader.readLine();
            System.out.println("Received from server: " + serverMessage);

            // Menutup socket
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
