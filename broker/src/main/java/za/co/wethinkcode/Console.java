package za.co.wethinkcode;

public class Console {


    public String operation(){

        MessageHandler messageHandler = new MessageHandler();

        int buyOrSell;
        String instrument = new String();
        String quantity;
        String price = new String();

//        Scanner input = new Scanner(System.in);
        buyOrSell = messageHandler.buyOrSell();
        instrument = messageHandler.instrumentSelect();
        quantity = messageHandler.quantity();
        price = messageHandler.price();

        //                                          senderID                 receiverID             buy=1 sell=2
        String body = "35=D" +"|" + "49="+ Broker.brokerId + "|" + "56="+ Broker.receiverId + "|" + "54="+ buyOrSell + "|" + "590=" + instrument + "|" + "53=" + quantity + "|" + "44=" + price + "|";

        //length
        String header = "8=FIX.4.4|9=" + body.length() + "|";

        String fixMessage = header + body;
        int checksum = messageHandler.generate_checksum(fixMessage);
        fixMessage = fixMessage + "10="+checksum+"|";
        System.out.println(fixMessage);
        return fixMessage;
    }
}
