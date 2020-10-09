package za.co.wethinkcode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MarketConnection extends Thread {
    private boolean shouldRun = true;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Market market;
    private String reply;

    public MarketConnection(Socket socket, Market market) {
        super("MarketConnection");
        System.out.println(this.getName());
        this.socket = socket;
        this.market = market;
    }

    public MarketConnection(){
        super("MarketConnection");
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

                    System.out.println("All Message in and out of the market!!!");
                    String reply = dataInputStream.readUTF();
                    System.out.println(reply);
                    this.reply = reply;
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

    public String getReply() {
        return reply;
    }
}
