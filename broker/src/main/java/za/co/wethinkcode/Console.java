package za.co.wethinkcode;

public class Console {

    private MessageHandler messageHandler;
    private String theMessage;

    public Console(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public String getTheMessage() {
        return theMessage;
    }

    String 	copyString(char []src)
    {
        int	i;
        char []dest = new char[7];

        for (i = 0; i < 6; i++)
            dest[i] = src[i];
        dest[i] = '\0';;
        return new String(dest);
    }

    public String operation(){
        int buyOrSell;
        String instrument;
        String quantity;
        String price;

        buyOrSell = messageHandler.buyOrSell();
        instrument = messageHandler.instrumentSelect();
        quantity = messageHandler.quantity();
        price = messageHandler.price();

        String brokerId = copyString(Broker.brokerId.toCharArray());
        String receiverId = copyString(Broker.receiverId.toCharArray());

        //                                          senderID                 receiverID             buy=1 sell=2
        String body = String.format("35=D|49=%s|56=%s|54=%s|590=%s|53=%s|44=%s|", brokerId, receiverId, buyOrSell, instrument.toUpperCase(), quantity, price);

        //length
        String header = "8=FIX.4.4|9=" + body.length() + "|";

        String fixMessage = header + body;
        int checksum = messageHandler.generate_checksum(fixMessage);
        fixMessage = fixMessage + "10="+checksum+"|";
        System.out.println(fixMessage);
        theMessage = fixMessage;
        return fixMessage;
    }
}
