package za.co.wethinkcode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;

public class Market implements Executor {
    private SocketChannel socketChannel;
    ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private MarketModel marketModel = new MarketModel();

    public static void main(String[] args) {
        new Market();
    }

    public Market() {
        try {
            socketChannel = SocketChannel.open();
            // Connect to the server
            socketChannel.connect(new InetSocketAddress(5001));
            // Read the message
            marketModel.createInstrumentList();
            this.execute(this.read(socketChannel));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(SocketChannel sc, String message) {
        writeBuffer.clear();
        writeBuffer.put(message.getBytes());
        writeBuffer.flip();
        try {
            sc.write(writeBuffer);
        } catch (IOException e) {
            System.out.println("Server not running");
            System.exit(0);
        }
    }

    private void startMarket(String reply) {
        MarketBrokerMessage marketBrokerMessage = new MarketBrokerMessage(reply);
        marketBrokerMessage.purifyMessage();
        MarketSimulation marketSimulation = new MarketSimulation(marketBrokerMessage.getSanitizedMessage(),marketModel.getInstrumentList());
        marketSimulation.startSimulation();
        String message = marketSimulation.getFixTransactionResult();
        System.out.println("message");
        System.out.println(message);
        //System.exit(0);
        write(this.socketChannel, message);
    }

    @Override
    public void execute(Runnable command) {
        new Thread(command).start();
    }

    public Runnable read(SocketChannel sc){
        return new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        readBuffer.clear();
                        int read = sc.read(readBuffer);
                        readBuffer.flip();
                        String brokerMessage = new String(readBuffer.array());
                        startMarket(brokerMessage);
                    } catch (IOException e) {
                        System.out.println("Server not running");
                        System.exit(0);
                    }
                }
            }
        };
    }
}