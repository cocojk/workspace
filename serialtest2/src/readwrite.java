import java.io.*;
import java.util.*;
import gnu.io.*;


public class readwrite
{
	static CommPortIdentifier portId;
    static Enumeration portList;
    static SerialPort serialPort; 
    static SerialPort serialPort2;
    public static void main(String[] args) {
        // �ý��ۿ� �ִ� ������ ����̹��� ����� �޾ƿ´�.
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

            	           // serialPort2 = (SerialPort) portId.open("SimpleReadApp", 2000);
            	            
            	        } catch (PortInUseException e) { }
            
                        // ��ü ����
                        SimpleRead reader = new SimpleRead();
                        SimpleWrite write = new SimpleWrite();
                                
            	}             
            }
             
        }
        
        
        
        
       
}
    
	

}