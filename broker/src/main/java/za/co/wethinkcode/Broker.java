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
    static String receiverId;
    static String brokerId;

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
            e.printStackTrace();
        }
    }

    private void read(SocketChannel sc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String message = new String(readBuffer.array());
                        System.out.println(message);
                        String[] messages = message.split(",");
                        System.out.println("Pos: "+ messages);
                        readBuffer.clear();
                        int read = sc.read(readBuffer);
                        readBuffer.flip();
                        System.out.println("length: "+messages.length);
                        if (messages.length == 2){
                            System.out.println("lana");
                            message = messages[0];
                            receiverId = messages[1];
                            brokerId = messages[0].split("\\[")[1].split("]")[0];
                            System.out.println(brokerId +"=== "+ receiverId);
                        }
                        System.out.println(message);
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
                Scanner scanner = new Scanner(System.in);
                Console console = new Console();
                String message = console.operation();
                MessageHandler messageHandler = new MessageHandler();
                while (true) {
//                    Scanner scanner = new Scanner(System.in);
                    String fixMessage = message;
                    writeBuffer.clear();
                    writeBuffer.put(fixMessage.getBytes());
                    writeBuffer.flip();
                    try {
                        sc.write(writeBuffer);
                        messageHandler.anotherTransaction();
//                        String c = scanner.next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
