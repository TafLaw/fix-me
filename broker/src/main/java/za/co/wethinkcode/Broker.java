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

    private String firstMessage;
    private MessageHandler messageHandler;
    private Console console;
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
            messageHandler = new MessageHandler();
            console = new Console(messageHandler);

            socketChannel = SocketChannel.open();
            // Connect to the server
            socketChannel.connect(new InetSocketAddress(5000));
            // Read the message
            this.read(socketChannel);
            //Send a message
            this.write(socketChannel);
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
                        String message = new String(readBuffer.array());

                        String[] messages = message.split(",");

                        if (messages.length == 2){
                            firstMessage = message = messages[0];
                            receiverId = messages[1];
                            brokerId = messages[0].split("\\[")[1].split("]")[0];
                        }

                        System.out.println(message);
                    } catch (IOException e) {
                        System.out.println("Server not running");
                        System.exit(0);
                    }
                }
            }
        }).start();
    }

    private void write(SocketChannel sc) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean land = true;
                String message = null;
                while (true) {
                    if (land && firstMessage == null)
                        continue;
                    else {
                        if (land){
                            message = console.operation();
                            land = false;
                        }
                        String fixMessage = message;
                        writeBuffer.clear();
                        writeBuffer.put(fixMessage.getBytes());
                        writeBuffer.flip();
                        try {
                            sc.write(writeBuffer);
                            messageHandler.anotherTransaction(console);
                            message = console.getTheMessage();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }).start();
    }

}
