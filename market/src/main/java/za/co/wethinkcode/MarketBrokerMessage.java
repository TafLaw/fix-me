package za.co.wethinkcode;

import java.util.HashMap;

public class MarketBrokerMessage {
    private String fixBrokerMessage;
    private HashMap<String,String> sanitizedMessage;

    public MarketBrokerMessage(String fixBrokerMessage){
        this.fixBrokerMessage = fixBrokerMessage;
    }

    public void purifyMessage(){

    }

    public HashMap<String, String> getSanitizedMessage() {
        return sanitizedMessage;
    }
}
