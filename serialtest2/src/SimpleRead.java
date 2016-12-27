import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

	


class SimpleRead implements Runnable, SerialPortEventListener {
    static CommPortIdentifier portId=readwrite.portId;
    static Enumeration portList=readwrite.portList;
    static SerialPort serialPort=readwrite.serialPort;
    InputStream inputStream; 
    Thread readThread;
    

 // SimpleRead 생성자
    public SimpleRead() {
       
        try {
            // 시리얼 포트에서 입력 스트림을 획득한다.
            inputStream = serialPort.getInputStream();
        } catch (IOException e) { }
        // 시리얼 포트의 이벤트 리스너로 자신을 등록한다.
        try {
            serialPort.addEventListener(this);
        } catch (TooManyListenersException e) { }
        
        /* 시리얼 포트에 데이터가 도착하면 이벤트가 한 번 발생되는데
           이 때, 자신이 리스너로 등록된 객체에게 이벤트를 전달하도록 허용. */
        serialPort.notifyOnDataAvailable(true);

        // 시리얼 통신 설정. Data Bit는 8, Stop Bit는 1, Parity Bit는 없음.
        try {
            serialPort.setSerialPortParams(9600, 			    		SerialPort.DATABITS_8, SerialPort.STOPBITS_1,	    		SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) { }
        
     // 쓰레드 객체 생성
        readThread = new Thread(this);

        // 쓰레드 동작
        readThread.start();
   
    }
    public void run() {
    try {
    	while(true)
        {
    		Thread.sleep(20000);
        }
    } catch (InterruptedException e) { }
}

    
    public void serialEvent(SerialPortEvent event) {
        // 이벤트의 타입에 따라 switch 문으로 제어.
        switch (event.getEventType()) {
        case SerialPortEvent.BI:
        case SerialPortEvent.OE:
        case SerialPortEvent.FE:
        case SerialPortEvent.PE:
        case SerialPortEvent.CD:
        case SerialPortEvent.CTS:
        case SerialPortEvent.DSR:
        case SerialPortEvent.RI:
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            break;
            
            // 데이터가 도착하면
            case SerialPortEvent.DATA_AVAILABLE:
                byte[] readBuffer = new byte[30];    // byte 배열 객체 생성

             
               
                // 입력 스트림이 사용가능하면, 버퍼로 읽어 들인 후
                // String 객체로 변환하여 출력
                try {
                    while (inputStream.available() > 0) {
                        int numBytes=inputStream.read(readBuffer);
                        System.out.format("numbyte :%d\n",numBytes);
                        for(int i=0;i<numBytes;i++)
                        {
                        	
                        	System.out.print(readBuffer[i]);
                        }
                    
                        System.out.print(new String(readBuffer));
                    }
                   // System.out.printf("%s\n",readBuffer);
                    System.out.println("");
                } catch (IOException e) { }
                break;
            }
        }
    }



