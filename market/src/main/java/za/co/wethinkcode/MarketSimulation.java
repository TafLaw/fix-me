package za.co.wethinkcode;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class MarketSimulation {
    private HashMap<String,String> brokerOder;
    private HashMap<String,HashMap<String,Double>> stockList;
    private String fixTransactionResult;

    MarketSimulation(HashMap<String,String> brokerOder,HashMap<String,HashMap<String,Double>> stockList){
        this.brokerOder = brokerOder;
        this.stockList = stockList;
    }

    public void startSimulation(){
        if(brokerOder.get("54").equals("1") || brokerOder.get("54").equals("2")){
           this.transaction();
        }
    }

    private void transaction(){
        System.out.println(stockList);
        if (stockList.containsKey(brokerOder.get("590"))) {
            if (checkPrice() && checkQuantity()){
                this.updateList();
                this.accepted();
            } else {
                this.rejected();
            }
        } else {
            this.rejected();
        }
        System.out.println(stockList);
    }

    private Boolean checkPrice(){
        double price = stockList.get(brokerOder.get("590")).get("price");
        double brokerPrice = validateUnit(brokerOder.get("44"));
        return brokerPrice >= price - 10 && brokerPrice <= price + 10;
    }

    private Boolean checkQuantity(){
        double quantity = stockList.get(brokerOder.get("590")).get("quantity");
        double brokerQuantity = validateUnit(brokerOder.get("53"));
        if(quantity <= 0 || brokerQuantity <= 0){
            this.rejected();
        }
        return brokerQuantity <= quantity;
    }

    private Double validateUnit(String check){
        double result = 0.0;
        try {
            result = Double.parseDouble(check);
        } catch (Exception e){
            this.rejected();
        }
        return result;
    }

    private void updateList(){
        double quantity = stockList.get(brokerOder.get("590")).get("quantity");
        stockList.get(brokerOder.get("590")).replace("quantity",quantity - validateUnit(brokerOder.get("53")));
    }

    private void rejected(){
        String body = "35=D" +"|" + "49="+ brokerOder.get("56") + "|" + "56="+ brokerOder.get("49") + "|" +"39=8"+ "|";
        String header = "8=FIX.4.4|9=" + body.length() + "|";
        int checkSum = generate_checksum(body);
        this.fixTransactionResult = header + body + "10=" + checkSum + "|";
    }

    private void accepted(){
        String body = "35=D" +"|" + "49="+ brokerOder.get("56") + "|" + "56="+ brokerOder.get("49") + "|" +"39=2"+ "|";
        String header = "8=FIX.4.4|9=" + body.length() + "|";
        int checkSum = generate_checksum(body);
        this.fixTransactionResult = header + body + "10=" + checkSum + "|";
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

    public String getFixTransactionResult() {
        return fixTransactionResult;
    }
}