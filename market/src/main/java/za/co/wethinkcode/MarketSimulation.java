package za.co.wethinkcode;

import java.util.HashMap;

public class MarketSimulation {
    private String api = "https://www.alphavantage.co/";
    private String apiKey = "ZGDT96RXBD9R19AZ";
    private HashMap<String,String> brokerOder;

    public MarketSimulation(HashMap<String,String> brokerOder){
        this.brokerOder = brokerOder;
    }

    public void startSimulation(){

    }
}