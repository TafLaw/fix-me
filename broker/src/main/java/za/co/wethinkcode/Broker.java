package za.co.wethinkcode;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.Future;
import java.nio.channels.*;
import java.nio.*;


public class Broker {
    public Broker(int parseInt, int parseInt1) {
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Starting the broker");

     AsynchronousSocketChannel   channel = AsynchronousSocketChannel.open();
        SocketAddress   serverAddr = new InetSocketAddress("localhost", 5000);
        Future<Void>    result = channel.connect(serverAddr);
        result.get();
        System.out.println("Connected");
        Message   attach = new Message();
        attach.channel = channel;
        attach.buffer = ByteBuffer.allocate(2048);
        attach.isRead = true;
        attach.mainThread = Thread.currentThread();

        ReadWriteHandler    FileHandler = new ReadWriteHandler();
        channel.read(attach.buffer, attach, FileHandler);
        attach.mainThread.join();
    }

    public void contact() {
    }
}

class Message
{
    public AsynchronousSocketChannel client;
    public int clientId;
    public ByteBuffer buffer;
    public Thread mainThread;
    public boolean isRead;
    public AsynchronousSocketChannel channel;
}

class ReadWriteHandler implements CompletionHandler<Integer, Message> {
    @Override
    public void completed(Integer result, Message attach) {
        if(attach.isRead) {
            attach.buffer.flip();
            byte[]    bytes = new byte[attach.buffer.limit()];
            attach.buffer.get(bytes);
            Charset cs = Charset.forName("UTF-8");
            String  message = new String(bytes, cs);

            if(message.length() > 0) {
                // get the ID from the charBuffer written to the channel
                if (message.charAt(1) == 'I') {
                    System.out.println(message);
                    System.out.println("Message delivered");
                    String messageID = message.replaceAll("[^0-9]", "");
                    Integer id = Integer.parseInt(messageID);
                    attach.ID = id;
                } else {
                    String[] messageData = message.split("\\|");
                    if(messageData[2].equals("Executed") || messageData[2].equals("Rejected")) {
                        System.out.format("Market responded with: " + messageData[2] + "\n");
                        System.out.println();
                    } else {
                        System.out.println("Unable to get a response from Market. Try again");
                    }
                }
                message = this.getTextFromUser(attach.ID);

                try {
                    attach.buffer.clear();
                    byte[] data = message.getBytes(cs);
                    attach.buffer.put(data);
                    attach.buffer.flip();
                    attach.isRead = false;
                    attach.channel.write(attach.buffer, attach, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Connection disconnected");
                System.exit(0);
            }
        } else {
            attach.isRead = true;
            attach.buffer.clear();
            attach.channel.read(attach.buffer, attach, this);
        }
    }

    @Override
    public void failed(Throwable e, Message attach) {
        e.printStackTrace();
    }

    private String  getTextFromUser(Integer id) {
        String      message = "";

        // getting and validating the market ID.....
        System.out.println("Please enter a market ID:");
        boolean     validInput = false;
        while(!validInput) {
            try {
                Scanner     scanner = new Scanner(System.in);

                int   marketID = scanner.nextInt();
                if(marketID < 543210 || marketID == id) {
                    System.out.println("Invalid input");
                } else {
                    message += Integer.toString(marketID) + "|" + id + "|";
                    validInput = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("You have not entered a market ID (ID's start at 543210) ");
            }
        }

        // The option to either Buy or Sell
        System.out.println("Would you like to:\n1. Buy\n2. Sell");
        validInput = false;
        int         option;
        while(!validInput) {
            try {
                Scanner     scanner = new Scanner(System.in);

                option = scanner.nextInt();
                if(option < 1 || option > 2) {
                    System.out.println("Invalid input");
                } else if(option == 1) {
                    message += "BUY-";
                    validInput = true;
                } else if(option == 2) {
                    message += "SELL-";
                    validInput = true;
                }
            } catch (InputMismatchException e) {
                System.out.println(" You have not entered a number, enter either 1 or 2");
            }
        }

        // Get the correct instrument symbol
        System.out.println("Instrument symbol:");
        try {
            Scanner     scanner = new Scanner(System.in);

            message += scanner.nextLine().trim() + "|";
        } catch (InputMismatchException e) {
            System.out.println("You have not entered a valid instrument");
        }
        // get the Price
        System.out.println("Price:");
        validInput = false;
        int         price = 0;
        while(!validInput) {
            try {
                Scanner     scanner = new Scanner(System.in);

                price = scanner.nextInt();
                if(price < 1) {
                    System.out.println("Invalid input");
                } else {
                    message += Integer.toString(price) + "|";
                    validInput = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("You have not entered a valid price");
            }
        }

        // get Quantity
        System.out.println("Quantity:");
        validInput = false;
        int         quantity = 0;
        while(!validInput) {
            try {
                Scanner     scanner = new Scanner(System.in);

                quantity = scanner.nextInt();
                if(quantity < 1) {
                    System.out.println("Invalid input");
                } else {
                    message += Integer.toString(quantity) + "|";
                    validInput = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("You have not entered a valid quantity");
            }
        }

        // the calculate checksum fuction
        int     checksum = 0;
        for(int i = 0; i < message.length(); i++) {
            checksum += message.charAt(i);
        }

        message += Integer.toString(checksum);

        System.out.println(message);

        return message;
    }
}