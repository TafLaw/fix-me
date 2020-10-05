package za.co.wethinkcode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Broker {

    BrokerConnection brokerConnection;

    public static void main(String[] args) {
        new Broker();
    }

    public Broker(){
        int brokerPort = 5000;
        try {
            Socket socket = new Socket("localhost", brokerPort);
            brokerConnection = new BrokerConnection(socket, this);
            brokerConnection.start();
            listenForInput();
//            Thread server = new Thread(new RouterThread(socket));
//            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void listenForInput() {
        Scanner scanner = new Scanner(System.in);

        while (true){
            while (!scanner.hasNext()){
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit"))
                break;
            brokerConnection.sendStringToServer(input);
        }
        brokerConnection.close();
    }

}
