package za.co.wethinkcode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BrokerConnection extends Thread {
    private boolean shouldRun = true;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Broker broker;

    public BrokerConnection(Socket socket, Broker broker) {
        super("BrokerConnection");
        this.socket = socket;
        this.broker = broker;
    }

    public BrokerConnection(){
        super("BrokerConnection");
    }

    public void sendStringToServer(String text){
        try {
            dataOutputStream.writeUTF(text);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    public void run(){
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            while (shouldRun){
                try {
                    while (dataInputStream.available() == 0) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    String reply = dataInputStream.readUTF();
                    System.out.println("reply: "+reply);

                } catch (IOException e) {
                    e.printStackTrace();
                    close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }

    }

    public void close(){
        try {
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
