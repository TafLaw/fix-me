package za.co.wethinkcode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Market {
    private SocketChannel socketChannel;
    ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
    ByteBuffer readBuffer = ByteBuffer.allocate(1024);

    public static void main(String[] args) {
        new Market();
    }
    public Market() {
        try {
            socketChannel = SocketChannel.open();
            // Connect to the server
            socketChannel.connect(new InetSocketAddress(5001));
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

    private void startMarket(String reply){
        MarketBrokerMessage marketBrokerMessage = new MarketBrokerMessage(reply);
        marketBrokerMessage.purifyMessage();
        MarketSimulation marketSimulation = new MarketSimulation(marketBrokerMessage.getSanitizedMessage());
        marketSimulation.startSimulation();
    }
}
