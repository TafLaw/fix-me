package za.co.wethinkcode;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

class MessageHandler {
    private Boolean flag=false;
    private String content;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int generate_checksum(String message)
    {
        int total = 0;
        int checksum;
        message = message.replace('|', '\u0001');
        byte[] messageBytes = message.getBytes(StandardCharsets.US_ASCII);
        System.out.println("Length"+message.length());
        for (byte chData : messageBytes)
        {
            total += chData;
        }

        checksum = total % 256;
        System.out.println("10=" + checksum);
        return checksum;
    }

    public void validate_checksum(String message, int checksum){

//        System.out.println("Passed : "+ message);
        int myChecksum;

        myChecksum = generate_checksum(message);
//        System.out.println("Passed : "+ checksum);
//        System.out.println("Passed : "+ myChecksum);

        if(myChecksum == checksum){
            System.out.println("Checksum success");
        } else {
            System.out.println("Error: Invalid checksum");
        }

    }

    public String removeChecksum(String message)
    {
        HashMap<String,String> brokerOder = new HashMap<>();
        String body;

        String[] splitMessage = message.split("\\|");

        for (String split : splitMessage
        ) {
            String[] splitValues = split.split("=");
            if (splitValues.length == 2) {
                brokerOder.put(splitValues[0], splitValues[1]);
            }
        }

        body = "35=D" +"|" + "49="+ brokerOder.get("56") + "|" + "56="+ brokerOder.get("49") + "|" +"39="+brokerOder.get("39")+ "|";
        String header = "8=FIX.4.4|9=" + body.length() + "|";

        return (header+body);
    }

}

