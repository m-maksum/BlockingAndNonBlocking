import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NonBlockingClient {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Masukkan username: ");
            String username = scanner.nextLine();

            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost", 12345));
            socketChannel.configureBlocking(false);

            System.out.println("Connected to Server!");

            while (true) {
                System.out.print("Enter message to server (type 'quit' to exit): ");
                String message = scanner.nextLine();

                int clientPort = socketChannel.socket().getLocalPort();

                ByteBuffer buffer = ByteBuffer.wrap(("[" + username + "@" + clientPort + "]" + message + "\n").getBytes());
                socketChannel.write(buffer);

                buffer.clear();
                int bytesRead = socketChannel.read(buffer);
                if (bytesRead > 0) {
                    buffer.flip();
                    String serverResponse = new String(buffer.array(), 0, bytesRead).trim();
                    System.out.println("Server response: " + serverResponse);
                }

                if ("quit".equals(message)) {
                    break;
                }
            }

            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
