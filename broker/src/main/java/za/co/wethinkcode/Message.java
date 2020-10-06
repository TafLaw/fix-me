package za.co.wethinkcode;

import java.io.channels.*;
import java.io.*;

public class Message
{
    public AsynchronousSocketChannel client;
    public int clientId;
    public ByteBuffer buffer;
    public Thread mainThread;
    public boolean isRead;
}
  