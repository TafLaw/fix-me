package za.co.wethinkcode;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Market {
    MarketConnection marketConnection;

    public static void main(String[] args) {
        new Market();
    }

    public Market(){
        int marketPort = 5001;
        try {
            Socket socket = new Socket("localhost", marketPort);
            marketConnection = new MarketConnection(socket, this);
            marketConnection.start();
            startMarket(marketConnection.getReply());
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
            marketConnection.sendStringToServer(input);
        }
        marketConnection.close();
    }

    private void startMarket(String reply){
        MarketBrokerMessage marketBrokerMessage = new MarketBrokerMessage(reply);
        marketBrokerMessage.purifyMessage();
        MarketSimulation marketSimulation = new MarketSimulation(marketBrokerMessage.getSanitizedMessage());
        marketSimulation.startSimulation();
    }
}
