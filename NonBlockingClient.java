import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NonBlockingClient {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nInput Name: ");
        String namaClient = sc.nextLine();
        try {
             // Membuat SocketChannel untuk terhubung ke server
            Scanner scanner = new Scanner(System.in);
            System.out.print("Masukkan username: ");
            String username = scanner.nextLine();

            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost", 12345));
            socketChannel.configureBlocking(false); // Mengatur mode non-blocking

            System.out.println("Connected to Server!");

            while (true) {
                 // Meminta pengguna memasukkan pesan untuk dikirim ke server
                System.out.print("Enter message to server (type 'quit' to exit): ");
                String message = scanner.nextLine();

                 //Mendapatkan port client
                int clientPort = socketChannel.socket().getLocalPort();

                // Mengonversi pesan menjadi ByteBuffer untuk pengiriman
                ByteBuffer buffer = ByteBuffer.wrap(("[" + username + "@" + clientPort + "]" + message + "\n").getBytes());
                socketChannel.write(buffer); // Mengirim pesan ke server

                buffer.clear();
                int bytesRead = socketChannel.read(buffer); // Menerima respons dari server
                if (bytesRead > 0) {
                    buffer.flip();
                    String serverResponse = new String(buffer.array(), 0, bytesRead).trim();
                    System.out.println("Server response: " + serverResponse); // Menampilkan respons dari server
                }

                if ("quit".equals(message)) {
                    break; // Keluar dari loop jika pengguna memasukkan 'quit'
                }
            }

            socketChannel.close(); // Menutup koneksi dengan server setelah selesai
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
