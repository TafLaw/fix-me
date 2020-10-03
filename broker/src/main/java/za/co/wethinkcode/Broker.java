package za.co.wethinkcode;

import java.io.IOException;
import java.net.Socket;

public class Broker {
    public static void main(String[] args) throws Exception{
        Socket socket;
        int brokerPort = 5000;
        try {
            socket = new Socket("localhost", brokerPort);
            Thread.sleep(1000);
            Thread server = new Thread(new RouterThread(socket));
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }

}
