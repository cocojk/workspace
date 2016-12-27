import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import gnu.io.*;


public class readwrite2
{
	static CommPortIdentifier portId;
    static Enumeration portList;
    static SerialPort serialPort;    
    int mode=0; // 2-client 1-server
    public static void main(String[] args) {
        // �ý��ۿ� �ִ� ������ ����̹��� ����� �޾ƿ´�.
    	int mode =0; // mode=1: server mode, mode=2: client mode
		if (args.length == 0) {
				System.out.println("����: java readwrite server or  java readwrite client client");
				System.exit(0);
		} else if(args.length == 1){
				// server mode without sending first: wait for an incoming message
			mode=1;//server mode
			SimpleRead.mode=1;
			SimpleWrite.mode=1;
			System.out.println("server mode \n");
	    	
		} else if(args.length == 2){				// client mode
				mode=2;//Client mode
				SimpleRead.mode=2;
				SimpleWrite.mode=2;
				System.out.println("client mode \n");
		    	
		} else {
			System.out.println("Mode Error ");
		}
		
		
		
    	
    	
        portList = CommPortIdentifier.getPortIdentifiers();
        // enumeration type �� portList �� ��� ��ü�� ���Ͽ�
        while (portList.hasMoreElements()) {
            // enumeration ���� ��ü�� �ϳ� �����´�.
            portId = (CommPortIdentifier) portList.nextElement();
            // ������ ��ü�� port type �� serial port �̸�
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
            	 System.out.format("available : %s\n",portId.getName());
             	      	
            	if (portId.getName().equals("COM13")) {

                    // Linux �� ���
                    //if (portId.getName().equals("/dev/term/a"))
            		 try {
            	            /* ��� �޼ҵ� : 
            	               public CommPort open(java.lang.String appname, int timeout)
            	               ��� : 
            	               ���ø����̼� �̸��� Ÿ�Ӿƿ� �ð� ��� */
            			 serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
         	            SimpleRead.serialPort=serialPort;
         	         SimpleWrite.serialPort=serialPort;
         	             } catch (PortInUseException e) { }
            
                        // ��ü ����
                        SimpleRead reader = new SimpleRead();
                        SimpleWrite write = new SimpleWrite();
                                
            	}               
            }
             
        }
        
        
        
        
    } 
}
    