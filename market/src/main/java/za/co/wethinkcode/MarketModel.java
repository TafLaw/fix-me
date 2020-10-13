package za.co.wethinkcode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

public class MarketModel {
    private String[] instruments = {"AAPL", "GOLD", "FB", "IBM", "TWTR"};
    private HashMap<String, HashMap<String,Double>> instrumentList = new HashMap<String, HashMap<String, Double>>();

    public void createInstrumentList() {
        try {
            String apiKey = "&apikey=ZGDT96RXBD9R19AZ";
            String api = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&&symbol=";
            int counter = 0;
            while (counter < instruments.length) {
                URL url = new URL(api + instruments[counter] + apiKey);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line;
                StringBuffer content = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line);
                }
                bufferedReader.close();
                httpURLConnection.disconnect();
                try{
                    JSONObject jsonObject = new JSONObject(content.toString());
                    JSONObject jsonObject1 = new JSONObject(jsonObject.get("Global Quote").toString());
                    HashMap<String,Double> instrumentValues = new HashMap<String, Double>();
                    instrumentValues.put("price",Double.parseDouble(jsonObject1.get("05. price").toString()));
                    Random random = new Random();
                    instrumentValues.put("quantity",random.nextInt(50) + 20.0);
                    this.instrumentList.put(instruments[counter],instrumentValues);
                    counter++;
                } catch (Exception e) {
                    System.out.println("You have reached the maximum amount of api calls, try again in a minute");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            System.out.println("API is Down, try again later!!!");
            System.exit(0);
        }
    }

    public HashMap<String, HashMap<String,Double>> getInstrumentList() {
        return instrumentList;
    }
}
