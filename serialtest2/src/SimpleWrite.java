import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Scanner;


class SimpleWrite implements Runnable{
	static Enumeration portList=readwrite.portList;
    static CommPortIdentifier portId=readwrite.portId;
    static String messageString = "Hello, world!\n";
    static SerialPort serialPort=readwrite.serialPort;
    OutputStream outputStream;
    Thread writeThread;
    
    public SimpleWrite()
    {
                    
                    try {
                        outputStream = serialPort.getOutputStream();
                    } catch (IOException e) { }
                    try {
                        serialPort.setSerialPortParams(9600,
                        		SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        	      SerialPort.PARITY_NONE);
                    
                    } catch (UnsupportedCommOperationException e) { }
                           
   writeThread = new Thread(this);
    // 쓰레드 동작
   writeThread.start();
    }

public void run() {

Scanner scan = new Scanner(System.in);

	while(true)
    {
    	
    	try {
            messageString =scan.nextLine();      
    		outputStream.write(messageString.getBytes());
    	} catch (IOException e) {}
    	
    	
    }

       
}

}