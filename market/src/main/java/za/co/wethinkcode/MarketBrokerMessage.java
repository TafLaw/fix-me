package za.co.wethinkcode;

import java.util.HashMap;

public class MarketBrokerMessage {
    private String fixBrokerMessage;
    private HashMap<String, String> sanitizedMessage = new HashMap<String, String>();

    public MarketBrokerMessage(String fixBrokerMessage) {
        this.fixBrokerMessage = fixBrokerMessage;
        //this.fixBrokerMessage =  "8=FIX.4.4|9=45|35=D|49=null|56=null|54=2|590=FB|53=20|44=260|10=84|";
    }

    public void purifyMessage() {
        System.out.println(this.fixBrokerMessage);
        String[] splitMessage = this.fixBrokerMessage.split("\\|");
        for (String split : splitMessage
        ) {
            String[] splitValues = split.split("=");
            if (splitValues.length == 2) {
                this.sanitizedMessage.put(splitValues[0], splitValues[1]);
            }
        }
    }

    public HashMap<String, String> getSanitizedMessage() {
        return this.sanitizedMessage;
    }
}
