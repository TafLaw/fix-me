package za.co.wethinkcode;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Router implements Runnable{
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            while (!socket.isClosed()){
System.out.println("hheher");
                String input = reader.readLine();

                if (input != null){
                    for (ClientThread client : clients){
                        client.getWriter().write(input);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PrintWriter getWriter() {
        return writer;
    }
}
