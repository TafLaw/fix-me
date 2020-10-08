package za.co.wethinkcode;

import java.nio.charset.*;
import java.nio.channels.*;

public class FileHandler implements CompletionHandler<Integer, Message> {
    @Override

    //Override the message function found in the broker , it s reused.
    public void completed(Integer result, Message attach) {
      if (result == -1)
        {
          attach.mainThread.interrupt();
          System.out.println("Server shutdown unexpectedly, Broker shutting down...");
          return ;
        }
        //Once the message is read , get a response with the client ID
      if (attach.isRead) {
        attach.buffer.flip();
        Charset cs = Charset.forName("UTF-8");
        int limits = attach.buffer.limit();
        byte bytes[] = new byte[limits];
        attach.buffer.get(bytes, 0, limits);
        String msg = new String(bytes, cs);
        if (attach.clientId == 0)
        {
          attach.clientId = Integer.parseInt(msg);
          System.out.println("Server responded with Id: " + attach.clientId);
        }
        else
          System.out.println("Server Responded: "+ msg.replace((char)1, '|'));
        try {
          boolean status = Broker.proccessReply(msg);
          if (status == true && Broker.bs == 1)
            Broker.updateData(true);
          if (status == true && Broker.bs == 0)
            Broker.updateData(false);
        } catch (Exception e) {
          e.printStackTrace();
        }
        attach.buffer.clear();
        msg = testMe(attach);
        if (msg.contains("Goodbye!") || i > 3) {
          attach.mainThread.interrupt();
          return;
        }
        i++;
        System.out.println("\nBroker response:" + msg.replace((char)1, '|'));
        byte[] data = msg.getBytes(cs);
        attach.buffer.put(data);
        attach.buffer.flip();

        //Write
        attach.isRead = false; 
        attach.client.write(attach.buffer, attach, this);
      }else {
        attach.isRead = true;
        attach.buffer.clear();
        attach.client.read(attach.buffer, attach, this);
      }
    }
    @Override
    //Just some error handling
    public void failed(Throwable e, Message attach) {
      e.printStackTrace();
    }
    private String testMe(Message attach)
    {
      String msg;
      
      if (Broker.bs == 1)
        msg = Broker.buyProduct(Broker.dstId);
      else
        msg = Broker.sellProduct(Broker.dstId);
      return msg + getCheckSum(msg);
    }
    //get and return sum
    private String getCheckSum(String msg)
    {
        int j = 0;
        char t[];
        String summ = "" + (char)1;
        String data[] = msg.split(summ);
        for(int k = 0; k < data.length; k++)
        {
          t = data[k].toCharArray();
          for(int i = 0; i < t.length; i++)
          {
            j += (int)t[i];
          }
          j += 1;
        }
        return ("10="+ (j % 256) + summ);
    }
    private static int i = 0;
  }
