package za.co.wethinkcode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Router {
    protected static ArrayList<ClientThread> clients;
    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket;
        int portNumber = 5000;

        try{
            serverSocket = new ServerSocket(portNumber);
            acceptClients(serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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
