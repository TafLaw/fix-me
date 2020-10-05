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

    //136921996123-me1hmshorbvj8dhcmrqffom3kcs79nts.apps.googleusercontent.com
//    q1qOup57TPLolhozawbci5pC

}
