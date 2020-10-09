package za.co.wethinkcode;

import java.util.HashMap;

public class MarketSimulation {
    private String api = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&&symbol=";
    private String apiKey = "&apikey=ZGDT96RXBD9R19AZ";
    private String httpUrl = api+getSymbol()+apiKey;
    private HashMap<String,String> brokerOder;

    public MarketSimulation(HashMap<String,String> brokerOder){
        this.brokerOder = brokerOder;
    }

    public String getSymbol(){
        return "FB";
    }

    public void startSimulation(){

    }
}