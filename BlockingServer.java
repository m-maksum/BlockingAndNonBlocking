package Blocking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockingServer {
    public static void main(String[] args) {
        try {
            // Membuat server socket yang mendengarkan koneksi pada port 12345
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Blocking Server Berjalan...");

            while (true) {
                // Menerima koneksi dari client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                // Memulai thread baru untuk menangani koneksi dari client
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Kelas ClientHandler untuk menangani koneksi dari client
    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private Scanner in;

        // Konstruktor ClientHandler untuk menginisialisasi socket, input, dan output
        public ClientHandler(Socket socket) throws IOException {
            clientSocket = socket;
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new Scanner(clientSocket.getInputStream());
        }

        // Metode run() yang dijalankan ketika thread dimulai
        public void run() {
            try {
                String clientMessage;
                // Membaca pesan dari client dan mengirim balasan ke client
                while ((clientMessage = in.nextLine()) != null) {
                    System.out.println("Received from client: " + clientMessage);

                    // Mengektrak pesan dan informasi yang dikirim client
                    // Menggunakan Regex untuk extract Port dan Pesan dari Client
                    String portClient = "";
                    String clientString = "";
                    Pattern pattern = Pattern.compile("\\[(\\d+)\\]\\s*(\\w+)\\b");
                    Matcher matcher = pattern.matcher(clientMessage);
                    if (matcher.find()) {
                        // Mengambil string nomor dan string teks dari hasil pencocokan
                        portClient = matcher.group(1);
                        clientString = matcher.group(2);           
                    } else {
                        System.out.println("Format not valid!");
                    } 

                    //Membuat Skenario pembalasan pesan dari Server ke Client
                    String serverResponse = "";
                    if(clientString.equals("Method")){
                        serverResponse = "This server using blocking method!";
                    } else if(clientString.equals("Port")){
                        serverResponse = "Your client port is " + portClient;
                    } else{
                        serverResponse = "Hello " + portClient + " from Server!";
                    }

                    // Mengirim balasan ke client
                    out.println("Server: " + serverResponse);
                }
            } catch (Exception e) {
                System.out.println("Client disconnected: " + clientSocket);
            } finally {
                try {
                    // Menutup socket client ketika koneksi terputus
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
