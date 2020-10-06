package za.co.wethinkcode;

public class Main {
  public static void main(String[] args) throws Exception {
    //The args include buy or sell options , 1 for buy and 2 for sell.
    if (args.length == 2) {
      if (args[1].equals("1") || args[1].equals("2")) {
        Broker broker = new Broker(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        try {
          broker.contact();
        } catch (Exception e) {
          System.out.println(e);
        }
      } else {
        System.out.println("It's either buy or sell [buy = 1 or sell = 2]");
      }

    } else {
      System.out.println("Use java -jar target/broker.jar  market id(e.g 10000) 1 (buy) or 2 (sell)");
    }
  }
}