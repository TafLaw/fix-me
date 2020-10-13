package za.co.wethinkcode;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

public class MessageHandler {

    Scanner input = new Scanner(System.in);

    public int buyOrSell(){

        System.out.println(Broker.YELLOW + "Do you wish to Buy or Sell?");
        String userInput = input.next();
        if (userInput.toLowerCase().equals("buy") || userInput.toLowerCase().equals("sell")){
            if (userInput.toLowerCase().equals("buy")){
                return 1;
            } else {
                return 2;
            }
        } else {
            System.out.println(Broker.RED + "Enter a valid input");
            return buyOrSell();
        }
    }

    public String instrumentSelect(){

        String[] instruments = {"| APPLE INC (AAPL)                  | (114 - 134)" ,
                                "| GOLD                              | (17 - 37)",
                                "| FACEBOOK INC (FB)                 | (255 - 275)",
                                "| INTERNATIONAL BUS MACH CORP (IBM) | (117 - 137)",
                                "| TWITTER INC (TWTR)                | (38 - 58)"};

        System.out.println(Broker.YELLOW + "\nSelect an instrument to trade: ");
        System.out.println(Broker.YELLOW + "   | ----------Name and Symbol---------|----Price-----|");
        for (int i = 0; i < instruments.length; i++) {
            System.out.println((i+1)+". "+ instruments[i]);
        }
        System.out.println("\n>>type only the symbol e.g IBM");
        String userInput = input.next();
        return userInput;
    }

    public String quantity(){

        System.out.println(Broker.YELLOW + "\nHow many shares would you like to buy?");
        String userInput = input.next();

        return userInput;
    }

    public String price(){

        System.out.println(Broker.YELLOW + "\ntype your offer (25 - 280)");
        String userInput = input.next();

        return userInput;

    }

    public String anotherTransaction(Console console){

        System.out.println(Broker.YELLOW + "\nWould you like to make another transaction? yes or no");
        String userInput = input.next().toLowerCase();
        if (userInput.equals("yes") || userInput.equals("no")){
            if (userInput.equals("yes")){
                console.operation();
            } else {
                System.exit(1);
            }
            return userInput;
        } else {
            System.out.println(Broker.RED + "Enter a valid input");
            return anotherTransaction(console);
        }

    }

    public int generate_checksum(String message)
    {
        int total = 0;
        int checksum;
        message = message.replace('|', '\u0001');
        byte[] messageBytes = message.getBytes(StandardCharsets.US_ASCII);
        for (byte chData : messageBytes)
        {
            total += chData;
        }

        checksum = total % 256;
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

        status = brokerOder.get("39").equals("2") ? Broker.GREEN + "Order has been accepted" : Broker.RED + "Order has been rejected";

        return (status);
    }
}
