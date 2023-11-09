package Blocking;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BlockingClient {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nInput Name: ");
        String namaClient = sc.nextLine();
        try {
            // Membuat koneksi ke server dengan alamat localhost (127.0.0.1) dan port 12345
            Socket socket = new Socket("localhost", 12345);
            // Membuat objek PrintWriter untuk menulis data ke server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // Membuat objek Scanner untuk membaca data dari server
            Scanner in = new Scanner(socket.getInputStream());
            // Membuat objek Scanner untuk membaca input dari pengguna
            Scanner scanner = new Scanner(System.in);

            // Mendapatkan nomor port client yang terhubung
            int clientPort = socket.getLocalPort();

            //Memberi pesan bila berhasil tersambung ke Server
            System.out.println("Connected to Server!");

            String message;
            while (true) {
                // Meminta pengguna memasukkan pesan untuk dikirim ke server
                System.out.print("Enter message to server: ");
                message = scanner.nextLine();
                // Mengirim pesan ke server dengan menyertakan nomor port client
                out.println("[" + namaClient + " - " + clientPort + "]" + message);

                // Membaca respons dari server dan menampilkannya ke pengguna
                // Membaca respons dari server dan menampilkannya ke pengguna
                if (in.hasNextLine()) {
                    String response = in.nextLine();
                    System.out.println("Server response: " + response);
                } else {
                    // Jika tidak dapat menerima respons dari server, menampilkan pesan dan menghentikan program
                    System.out.println("Disconnected from Server");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}