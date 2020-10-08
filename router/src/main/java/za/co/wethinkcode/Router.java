package za.co.wethinkcode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.Year;
import java.util.*;

public class Router {
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";

    private Selector selector;
    private SelectableChannel senderChannel = null;
    private HashMap<String, ClientData> connectedClients = new HashMap<String, ClientData>();
    MessageHandler messageHandler = new MessageHandler();

    public static void main(String[] args) {
        Router router = new Router();
        //new Thread(router.msgSender).start();
        router.start();

    }

    // Initialize the server
    public Router() {
        try {
            this.selector = Selector.open();
            ServerSocketChannel brokerSever = ServerSocketChannel.open();
            brokerSever.configureBlocking(false);
            brokerSever.bind(new InetSocketAddress(5000));
            brokerSever.register(this.selector, SelectionKey.OP_ACCEPT);

            ServerSocketChannel marketServer = ServerSocketChannel.open();
            marketServer.configureBlocking(false);
            marketServer.bind(new InetSocketAddress(5001));
            marketServer.register(this.selector, SelectionKey.OP_ACCEPT);
            System.out.println(YELLOW + "Server running, waiting for client connections...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Start the server waiting for the selector event
    public void start() {
        while (true) {
            try {
                int selections = this.selector.select();
                if (selections > 0) {

//                        System.out.println(this.selector.selectedKeys().toArray()[0].toString().split(", ")[0]);
                    Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isValid()) {
                            if (key.isAcceptable()) {
                                this.accept(key);
                            }
                            if (key.isReadable()) {
                                switch (whichClient(key)) {
                                    case 0:

                                        transportMessage(this.readMarket(key, connectedClients.get("Market")));
                                        break;
                                    case 1:
                                        try {
                                            transportMessage(this.readBroker(key, connectedClients.get("Broker")));
                                        } catch (Exception e) {
                                            System.out.println(RED+"Broker disconnected");
                                            continue;
                                        }
                                        break;
                                }
                            }
                            if (key.isWritable() && messageHandler.getFlag()) {
                                SocketChannel checkKey = (SocketChannel) key.channel();
                                ClientData clientData = null;

                                if (senderChannel.toString().split(" ")[1].split(":")[1].equalsIgnoreCase("5000")) {
                                    clientData = connectedClients.get("Market");
                                    this.writeMarket(clientData.key, clientData);
                                } else if (senderChannel.toString().split(" ")[1].split(":")[1].equalsIgnoreCase("5001")) {
                                    clientData = connectedClients.get("Broker");
                                    this.writeBroker(clientData.key, clientData);
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

    private void transportMessage(String message) {
        this.messageHandler.setContent(message);
        this.messageHandler.setFlag(true);
    }

    private class ClientData {
        public String id;
        public String type;
        public ByteBuffer readBuffer;
        public ByteBuffer writeBuffer;
        SocketChannel key;
    }

    private void writeBroker(SocketChannel socketChannel, ClientData brokerData) {
        try {
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Determine if there is a message to send
        if (messageHandler != null && messageHandler.getFlag()) {
            //System.out.println(messageHandler);
            try {
                messageHandler.setFlag(false);
                brokerData.writeBuffer.clear();
                brokerData.writeBuffer.put(messageHandler.getContent().getBytes());
                brokerData.writeBuffer.flip();
                socketChannel.write(brokerData.writeBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeMarket(SocketChannel socketChannel, ClientData marketData) {
        try {
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Determine if there is a message to send
        if (messageHandler != null && messageHandler.getFlag()) {
            //System.out.println(messageHandler);
            try {
                messageHandler.setFlag(false);
                marketData.writeBuffer.clear();
                marketData.writeBuffer.put(messageHandler.getContent().getBytes());
                marketData.writeBuffer.flip();
                socketChannel.write(marketData.writeBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readBroker(SelectionKey key, ClientData brokerData) {
        String message = "";
        SocketChannel socketChannel = (SocketChannel) key.channel();
        senderChannel = key.channel();
        try {
            socketChannel.configureBlocking(false);
            brokerData.readBuffer.clear();
            int read = socketChannel.read(brokerData.readBuffer);
            if (read == -1) {
                socketChannel.close();
                key.cancel();
            }
            brokerData.readBuffer.flip();

            message = new String(brokerData.readBuffer.array());
            System.out.println(YELLOW+message);

            brokerData.readBuffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    private String readMarket(SelectionKey key, ClientData marketData) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        String message = "";
        senderChannel = key.channel();
        try {
            socketChannel.configureBlocking(false);
            marketData.readBuffer.clear();
            int read = socketChannel.read(marketData.readBuffer);
            if (read == -1) {
                socketChannel.close();
                key.cancel();
            }
            marketData.readBuffer.flip();

            message = new String(marketData.readBuffer.array());
            System.out.println(YELLOW+message);

            marketData.readBuffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    private int whichClient(SelectionKey key) {
        int theClient = 0;
        String lport;
        try {
            SocketChannel checkKey = (SocketChannel) key.channel();
            lport = checkKey.socket().getChannel().getLocalAddress().toString().split(":")[1];
            if (lport.equalsIgnoreCase("5000"))
                theClient = EClient.BROKER.ordinal();
            else if (lport.equalsIgnoreCase("5001"))
                theClient = EClient.MARKET.ordinal();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return theClient;
    }

    // Accept client connection
    private void accept(SelectionKey key) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        try {

            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println(String.format(GREEN+"\nNew client connected... host:%s;port:%d", socketChannel.socket().getLocalAddress(), socketChannel.socket().getPort()));
            socketChannel.configureBlocking(false);
            socketChannel.register(this.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

            assignClientComponents(socketChannel);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void assignClientComponents(SocketChannel socketChannel) {
        try {
            ClientData clientData = new ClientData();
            String clientType = "";
            String lport = socketChannel.socket().getChannel().getLocalAddress().toString().split(":")[1];
            clientData.key = socketChannel;
            clientData.id = socketChannel.socket().getChannel().getRemoteAddress().toString().split(":")[1];
            clientData.readBuffer = ByteBuffer.allocate(1024);
            clientData.writeBuffer = ByteBuffer.allocate(1024);

            if (lport.equalsIgnoreCase("5000"))
                clientType = "Broker";
            else if (lport.equalsIgnoreCase("5001"))
                clientType = "Market";

            clientData.type = clientType;
            connectedClients.put(clientType, clientData);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //136921996123-me1hmshorbvj8dhcmrqffom3kcs79nts.apps.googleusercontent.com
//    q1qOup57TPLolhozawbci5pC

}
