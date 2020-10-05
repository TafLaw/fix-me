package za.co.wethinkcode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Router {
    ServerSocket serverSocket;
    ServerSocket marketServerSocket;
    ArrayList<ServerConnection> connections = new ArrayList<ServerConnection>();
    boolean shouldRun = true;

    protected static ArrayList<ClientThread> clients;

    public static void main(String[] args) throws Exception{
        new Router();
    }

    public Router(){
        int brokerPort = 5000;
        int marketPort = 5001;

        try{
            marketServerSocket = new ServerSocket(marketPort);
            serverSocket = new ServerSocket(brokerPort);
            while (shouldRun){
                //broker
                Socket socket = serverSocket.accept();
                ServerConnection serverConnection = new ServerConnection(socket, this, "BrokerConnection");
                serverConnection.start();
                connections.add(serverConnection);

                //market
                Socket marketSocket = marketServerSocket.accept();
                ServerConnection marketServerConnection = new ServerConnection(marketSocket, this, "MarketConnection");
                marketServerConnection.start();
                connections.add(marketServerConnection);
            }
//            acceptClients(serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void listenForData() {
//        while (true){
//            try {
//                while (dataInputStream.available() == 0){
//                    try {
//                        Thread.sleep(1);
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                String dataIn = dataInputStream.readUTF();
//                dataOutputStream.writeUTF(dataIn);
//            } catch (IOException e) {
//                e.printStackTrace();
//                break;
//            }
//        }
//
//        try {
//            dataInputStream.close();
//            dataOutputStream.close();
//            socket.close();
//            serverSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    //136921996123-me1hmshorbvj8dhcmrqffom3kcs79nts.apps.googleusercontent.com
//    q1qOup57TPLolhozawbci5pC
    private static void acceptClients(ServerSocket serverSocket) {
        /*
        * Available clients are the market and the broker
        * */
        clients = new ArrayList<ClientThread>();
        while (true){
            System.out.println("accept");
            try {
                Socket socket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(socket);
                Thread thread = new Thread(clientThread);
                thread.start();
                clients.add(clientThread);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
