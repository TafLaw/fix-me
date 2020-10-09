package za.co.wethinkcode;

import java.nio.charset.StandardCharsets;

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

        System.out.println("Passed : "+ message);
        int myChecksum;

        myChecksum = generate_checksum(message);
        System.out.println("Passed : "+ checksum);
        System.out.println("Passed : "+ myChecksum);

        if(myChecksum == checksum){
            System.out.println("Validation Success: No errors found");
        } else {
            System.out.println("Validation Failed: Errors found");
        }

    }

}

