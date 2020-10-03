package za.co.wethinkcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RouterThread implements  Runnable {
    private Socket socket;
    private BufferedReader serverIn;
    private BufferedReader clientIn;
    private PrintWriter writer;

    public RouterThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            clientIn = new BufferedReader(new InputStreamReader(System.in));
            serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (!socket.isClosed()){
                if (serverIn.ready()){
                    String input = serverIn.readLine();
                    System.out.println("sererer");
                    if (input != null)
                        writer.println(input);
                }
                if (clientIn.ready())
                    System.out.println("Client : "+ clientIn.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO: Make the server relay messages between the clients
}
