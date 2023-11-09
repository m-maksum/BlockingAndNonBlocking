import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NonBlockingServer {
    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(12345));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Non-Blocking Server is running...");

            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isAcceptable()) {
                        SocketChannel clientChannel = serverSocketChannel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);

                        // Meminta nama pengguna dari klien
                        ByteBuffer nameBuffer = ByteBuffer.allocate(1024);
                        int nameBytesRead = clientChannel.read(nameBuffer);
                        if (nameBytesRead > 0) {
                            nameBuffer.flip();
                            String username = new String(nameBuffer.array(), 0, nameBytesRead).trim();
                            System.out.println("Client connected: " + username + " (" + clientChannel + ")");
                        }
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        StringBuilder clientMessageBuilder = new StringBuilder();

                        int bytesRead;
                        while ((bytesRead = clientChannel.read(buffer)) > 0) {
                            buffer.flip();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);
                            clientMessageBuilder.append(new String(bytes));
                            buffer.clear();

                            if (clientMessageBuilder.toString().endsWith("\n")) {
                                String clientMessage = clientMessageBuilder.toString().trim();
                                System.out.println("Received from client: " + clientMessage);

                                String clientString = clientMessage.replaceAll("\\[.*?\\]\\s*", "");

                                String serverResponse = clientString;
                                ByteBuffer responseBuffer = ByteBuffer.wrap(serverResponse.getBytes());
                                clientChannel.write(responseBuffer);

                                clientMessageBuilder.setLength(0);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
