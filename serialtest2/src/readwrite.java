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
        // 시스템에 있는 가능한 드라이버의 목록을 받아온다.
        portList = CommPortIdentifier.getPortIdentifiers();
       // enumeration type 인 portList 의 모든 객체에 대하여
        while (portList.hasMoreElements()) {
            // enumeration 에서 객체를 하나 가져온다.
            portId = (CommPortIdentifier) portList.nextElement();
            // 가져온 객체의 port type 이 serial port 이면
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                System.out.format("available : %s\n",portId.getName());
            	
            	if (portId.getName().equals("COM13")) {

            		
                    // Linux 의 경우
                    //if (portId.getName().equals("/dev/term/a"))
            		 try {
            	            /* 사용 메소드 : 
            	               public CommPort open(java.lang.String appname, int timeout)
            	               기능 : 
            	               어플리케이션 이름과 타임아웃 시간 명시 */
            	            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);

            	           // serialPort2 = (SerialPort) portId.open("SimpleReadApp", 2000);
            	            
            	        } catch (PortInUseException e) { }
            
                        // 객체 생성
                        SimpleRead reader = new SimpleRead();
                        SimpleWrite write = new SimpleWrite();
                                
            	}             
            }
             
        }
        
        
        
        
       
}
    
	

}