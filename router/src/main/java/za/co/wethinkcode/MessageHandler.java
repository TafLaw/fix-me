package za.co.wethinkcode;


import java.nio.charset.StandardCharsets;

public class MessageHandler {

//    public int generate_checksum(String s) {
//        int i;
//        int charAscii;
//        int numberOfBytes;
//        int complemented;
//        int sum = 0;
//        int c_sum = 0;
//        byte[] messageBytes = s.getBytes();
//
//        for (i = 0; i < s.length(); i++) {
//            charAscii = (int) (s.charAt(i));
////            System.out.println(charAscii);
////            System.out.println(s.charAt(i));
//            numberOfBytes = (int)(Math.floor(Math.log(charAscii) / Math.log(2))) + 1;
//
//            complemented = ((1 << numberOfBytes) - 1) ^ charAscii;
//            System.out.println("Number of byte: " + numberOfBytes);
//            sum =+ charAscii;
//        }
//        c_sum = sum % 256;
//        System.out.println("CheckSum: " + sum);
//        System.out.println("CheckSum: " + c_sum);
////        System.out.println("CheckSum: " + s.getBytes());
//        return 0;
//    }

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

        int myChecksum;

        myChecksum = generate_checksum(message);

        if(myChecksum == checksum){
            System.out.println("Validation Success: No errors found");
        } else {
            System.out.println("Validation Failed: Errors found");
        }

    }
}
