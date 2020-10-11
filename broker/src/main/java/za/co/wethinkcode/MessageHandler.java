package za.co.wethinkcode;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

public class MessageHandler {

    Scanner input = new Scanner(System.in);

    public int buyOrSell(){

        System.out.println("Do you wish to Buy or Sell?");
        String userInput = input.next();
        if (userInput.toLowerCase().equals("buy") || userInput.toLowerCase().equals("sell")){
            if (userInput.toLowerCase().equals("buy")){
                return 1;
            } else {
                return 2;
            }
        } else {
            System.out.println("Enter a valid input");
            return buyOrSell();
        }
    }

    public String instrumentSelect(){

        String[] instruments = {"APPLE INC (AAPL)",
                "GOLD",
                "FACEBOOK INC (FB)",
                "INTERNATIONAL BUS MACH CORP (IBM)",
                "TWITTER INC (TWTR)"};
        System.out.println("\nSelect an instrument to trade: ");
        System.out.println("----Name and Symbol----");
        for (int i = 0; i < instruments.length; i++) {
            System.out.println((i+1)+". "+ instruments[i]);
        }
        System.out.println("\n>>type only the symbol e.g IBM");
        String userInput = input.next();
        return userInput;
    }

    public String quantity(){

        System.out.println("\nHow many shares would you like to buy?");
        String userInput = input.next();

        return userInput;
    }

    public String price(){

        System.out.println("\ntype your offer (R25 - R270)");
        String userInput = input.next();

        return userInput;

    }

    public String anotherTransaction(Console console){

//        Console console = new Console();

        System.out.println("\nWould you like to make another transaction? yes or no");
        String userInput = input.next();
        if (userInput.toLowerCase().equals("yes") || userInput.toLowerCase().equals("no")){
            if (userInput.equals("yes")){
                console.operation();
            } else {
                System.exit(1);
            }
            return userInput;
        } else {
            System.out.println("Enter a valid input");
            return anotherTransaction(console);
        }

    }

    public int generate_checksum(String message)
    {
        int total = 0;
        int checksum;
        message = message.replace('|', '\u0001');
        byte[] messageBytes = message.getBytes(StandardCharsets.US_ASCII);
//        System.out.println("Length"+message.length());
        for (byte chData : messageBytes)
        {
            total += chData;
        }

        checksum = total % 256;
//        System.out.println("10=" + checksum);
        return checksum;
    }

    public String orderStatus(String message)
    {
        HashMap<String,String> brokerOder = new HashMap<>();
        String status;

        String[] splitMessage = message.split("\\|");

        for (String split : splitMessage
        ) {
            String[] splitValues = split.split("=");
            if (splitValues.length == 2) {
                brokerOder.put(splitValues[0], splitValues[1]);
            }
        }

        status = brokerOder.get("39").equals("2") ? "Order has been accepted" : "Order has been rejected";
//        status = "35=D" +"|" + "49="+ brokerOder.get("56") + "|" + "56="+ brokerOder.get("49") + "|" +"39="+brokerOder.get("39")+ "|";


        return (status);
    }
}
