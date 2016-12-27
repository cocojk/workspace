import java.io.*;
import java.util.*;
import gnu.io.*;

public class SimpleWrite {
	static Enumeration portList;
    static CommPortIdentifier portId;
    static String messageString = "Hello, world!\n";
    static SerialPort serialPort;
    static OutputStream outputStream;
    
    public static void main(String[] args) {
        portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
        	
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == 						      CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals("COM9")) {
                //if (portId.getName().equals("/dev/term/a")) {
                    try {
                        serialPort = (SerialPort)
                            portId.open("SimpleWriteApp", 2000);
                    } catch (PortInUseException e) {}
                    
                    try {
                        outputStream = serialPort.getOutputStream();
                    } catch (IOException e) { }
                    try {
                        serialPort.setSerialPortParams(9600,
                        		SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        	      SerialPort.PARITY_NONE);
                    
                    } catch (UnsupportedCommOperationException e) { }
                    
                    try {
                    	//System.out.printf("%s", messageString.getBytes().toString());
                     System.out.printf("%s",messageString.getBytes());
                     System.out.printf("%s", new String(messageString.getBytes()));
                    	outputStream.
			        write(messageString.getBytes());
                    } catch (IOException e) {}
                }
            }
        }
    }
}
