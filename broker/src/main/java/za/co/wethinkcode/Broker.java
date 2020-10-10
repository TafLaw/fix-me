package za.co.wethinkcode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Broker {

    private SocketChannel socketChannel;
    ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
    ByteBuffer readBuffer = ByteBuffer.allocate(1024);

    public static void main(String[] args) {
        new Broker();
    }
    public Broker() {
        try {
            socketChannel = SocketChannel.open();
            // Connect to the server
            socketChannel.connect(new InetSocketAddress(5000));
            //Send a message
            this.write(socketChannel);
            // Read the message
            this.read(socketChannel);
        } catch (IOException e) {
            System.out.println("Server not running");
            System.exit(0);
        }
    }

    private void read(SocketChannel sc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        readBuffer.clear();
                        int read = sc.read(readBuffer);
                        readBuffer.flip();
                        System.out.println(new String(readBuffer.array()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void write(SocketChannel sc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Scanner scanner = new Scanner(System.in);
                    String next = scanner.next();
                    writeBuffer.clear();
                    writeBuffer.put(next.getBytes());
                    writeBuffer.flip();
                    try {
                        sc.write(writeBuffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
