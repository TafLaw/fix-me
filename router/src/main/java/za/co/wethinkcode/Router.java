package za.co.wethinkcode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Router {
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";

    private String marketId;
    private Selector selector;
    private SelectableChannel senderChannel = null;
    private HashMap<String, ClientData> connectedClients = new HashMap<String, ClientData>();
    MessageHandler messageHandler = new MessageHandler();

    public static void main(String[] args) {
        Router router = new Router();
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
//                System.out.println(selections);
                if (selections > 0) {

                    Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
//                                                System.out.println(this.selector.selectedKeys().toArray().length);
//                                                System.out.println(selections);

                    while (iterator.hasNext()) {
                        Boolean skip = false;
                        SelectionKey key = iterator.next();

                        iterator.remove();
                        if (key.isValid()) {
                            if (key.isAcceptable()) {
                                this.accept(key);
                            }
                            if (key.isReadable()) {
                                switch (whichClient(key)) {
                                    case 0:
                                        try {
                                            if(transportMessage(this.readMarket(key, connectedClients.get("Market"))))
                                                continue;
                                            else {
                                                skip = true;
                                            }
                                        } catch (Exception e) {
                                            continue;
                                        }
                                        break;
                                    case 1:
                                        try {
                                            if(transportMessage(this.readBroker(key, connectedClients.get("Broker"))))
                                                continue;
                                            else skip = true;
                                        } catch (Exception e) {
                                            continue;
                                        }
                                        break;
                                }
//                                if (iterator.hasNext()){
//                                    System.out.println("dsssgdsg");
//                                if (key.channel().equals(key.channel()))
//                                    iterator.remove();}
//                                break;
                            }
                            if (!skip) {
                                if (key.isWritable() && messageHandler.getFlag()) {
                                    ClientData clientData = null;

                                    try {
                                        if (senderChannel.toString().split(" ")[1].split(":")[1].equalsIgnoreCase("5000")) {
                                            try {
                                                clientData = connectedClients.get("Market");
                                                this.writeMarket(clientData.socketchannel, clientData);
                                            } catch (Exception e) {
                                                continue;
                                            }
                                        } else if (senderChannel.toString().split(" ")[1].split(":")[1].equalsIgnoreCase("5001")) {
                                            try {
                                                clientData = connectedClients.get("Broker");
                                                this.writeBroker(clientData.socketchannel, clientData);
                                            } catch (Exception e) {
                                                continue;
                                            }
                                        }

                                    } catch (Exception e) {
                                        continue;
                                    }
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

    private boolean transportMessage(String message) {
        this.messageHandler.setContent(message);
        this.messageHandler.setFlag(true);
        return message != "";
    }

    private class ClientData {
        public String id;
        public String type;
        public ByteBuffer readBuffer;
        public ByteBuffer writeBuffer;
        public SelectionKey selectionKey;
        SocketChannel socketchannel;
    }

    private void writeBroker(SocketChannel socketChannel, ClientData brokerData) {
        try {
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Determine if there is a message to send
        if (messageHandler != null && messageHandler.getFlag()) {
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
        String tempMessage = "";

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

            tempMessage = new String(brokerData.readBuffer.array());
            message = tempMessage;
            System.out.println(YELLOW+message);

            tempMessage = tempMessage.replace("|", "\u0001");
            String pipe = "" + (char)1;
            String [] arrayMessage = tempMessage.split(pipe);
            int length = arrayMessage.length;
            System.out.println(arrayMessage[length-2]);

            String checksum = arrayMessage[length - 2].replace("=", "\u0001");
            messageHandler.validate_checksum(message, Integer.parseInt(checksum.split(pipe)[1]));

            brokerData.readBuffer.clear();
        } catch (IOException e) {
            System.out.println(RED+"Broker disconnected");
            connectedClients.remove("Broker");
            try {
                key.channel().close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            this.selector.selectedKeys().remove(key);
        }
        return message;
    }

    private String readMarket(SelectionKey key, ClientData marketData) {

        SocketChannel socketChannel = (SocketChannel) key.channel();
        String message = "";
        String tempMessage = "";
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

            tempMessage = new String(marketData.readBuffer.array());
            message = tempMessage;
            System.out.println(YELLOW+message);

            tempMessage = tempMessage.replace("|", "\u0001");
            String pipe = "" + (char)1;
            String [] arrayMessage = tempMessage.split(pipe);
            int length = arrayMessage.length;
            System.out.println(arrayMessage[length-2]);

            String checksum = arrayMessage[length - 2].replace("=", "\u0001");
            messageHandler.validate_checksum(message, Integer.parseInt(checksum.split(pipe)[1]));
            marketData.readBuffer.clear();

        } catch (IOException e) {
            System.out.println(RED+"Market disconnected");
            connectedClients.remove("Market");
            try {
                key.channel().close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
//            key.cancel();
//            this.selector.selectedKeys().remove(key);
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

            assignClientComponents(socketChannel, key);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void assignClientComponents(SocketChannel socketChannel, SelectionKey key) {
        try {
            ClientData clientData = new ClientData();
            String clientType = "";
            String lport = socketChannel.socket().getChannel().getLocalAddress().toString().split(":")[1];
            clientData.socketchannel = socketChannel;
            clientData.selectionKey = key;
            clientData.id = socketChannel.socket().getChannel().getRemoteAddress().toString().split(":")[1]+"0";
            clientData.readBuffer = ByteBuffer.allocate(1024);
            clientData.writeBuffer = ByteBuffer.allocate(1024);

            if (lport.equalsIgnoreCase("5000")){
                clientType = "Broker";
                Broker.receiverId = marketId;
                System.out.println(Broker.receiverId);
                Broker.brokerId = clientData.id;
                this.transportMessage(String.format("Broker Assigned ID [%s],%s", clientData.id, marketId));
                this.writeBroker(socketChannel, clientData);
            }
            else if (lport.equalsIgnoreCase("5001")){
                clientType = "Market";
                marketId = clientData.id;
                this.transportMessage(String.format("Market Assigned ID [%s]", clientData.id));
                this.writeMarket(socketChannel, clientData);
            }

            clientData.type = clientType;
            connectedClients.put(clientType, clientData);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //136921996123-me1hmshorbvj8dhcmrqffom3kcs79nts.apps.googleusercontent.com
//    q1qOup57TPLolhozawbci5pC

}
