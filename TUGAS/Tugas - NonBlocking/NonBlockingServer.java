package NonBlocking;

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
            // Membuat ServerSocketChannel untuk menerima koneksi dari klien
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(12345));
            serverSocketChannel.configureBlocking(false); // Mengatur mode non-blocking

            // Membuat Selector untuk memantau operasi I/O pada channel-channel yang terdaftar
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); // Mendaftarkan serverSocketChannel untuk event OP_ACCEPT

            System.out.println("Non-Blocking Server is running...");

            while (true) {
                // Selector.select() akan memblokir sampai setidaknya satu channel siap untuk operasi I/O
                selector.select();

                // Mendapatkan kunci (SelectionKey) yang siap untuk operasi I/O
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isAcceptable()) {
                        // Jika channel dapat menerima koneksi (event OP_ACCEPT)
                        SocketChannel clientChannel = serverSocketChannel.accept(); // Menerima koneksi dari klien
                        clientChannel.configureBlocking(false); // Mengatur mode non-blocking untuk klien yang baru terhubung
                        clientChannel.register(selector, SelectionKey.OP_READ); // Mendaftarkan channel klien untuk event OP_READ
                        System.out.println("Client connected: " + clientChannel);
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

                            // Memeriksa apakah pesan sudah lengkap (berakhir dengan newline)
                            if (clientMessageBuilder.toString().endsWith("\n")) {
                                String clientMessage = clientMessageBuilder.toString().trim();
                                System.out.println("Received from client: " + clientMessage);

                                //Ekstrak pesan dari klien
                                String clientString = clientMessage.replaceAll("\\[\\d+\\]\\s*", "");

                                // Mengirim balasan ke klien
                                String serverResponse = clientString;
                                ByteBuffer responseBuffer = ByteBuffer.wrap(serverResponse.getBytes());
                                clientChannel.write(responseBuffer);

                                // Menghapus pesan dari StringBuilder untuk pesan berikutnya
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
