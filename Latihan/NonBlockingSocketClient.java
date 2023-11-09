// NonBlockingSocketClient.java
// Program Client

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NonBlockingSocketClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(SERVER_IP, SERVER_PORT));
            socketChannel.configureBlocking(false);

            // Mengirim pesan ke server
            String message = "Hello from Client!";
            socketChannel.write(ByteBuffer.wrap(message.getBytes()));

            // Menerima pesan balasan dari server
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (socketChannel.read(buffer) == 0) {
                // Menunggu data tersedia
            }

            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            String serverMessage = new String(data);
            System.out.println("Received from server: " + serverMessage);

            // Menutup socket
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
