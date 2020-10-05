package za.co.wethinkcode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection extends  Thread {
    private Socket socket;
    private Router router;
    private boolean shouldRun = true;
    private MarketConnection marketConnection;
    private BrokerConnection brokerConnection;
    private String client;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public ServerConnection(Socket socket, Router router, String client) {
        super("ServerConnectionThread");
        this.socket = socket;
        this.router = router;
        this.client = client;
        marketConnection = new MarketConnection();
        brokerConnection = new BrokerConnection();
    }

    public void sendStringToClient(String text){
        try {
            dataOutputStream.writeUTF(text);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendStringToAllClients(String text){
        ServerConnection serverConnection;

        for (int i = 0; i < router.connections.size(); i ++){
            serverConnection = router.connections.get(i);
            serverConnection.sendStringToClient(text);
        }
    }

    public void run(){
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            while (shouldRun){
                while (dataInputStream.available() == 0){
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String textIn = dataInputStream.readUTF();
                sendStringToAllClients(textIn);
            }
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
