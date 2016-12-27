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
    

 // SimpleRead ������
    public SimpleRead() {
       
        try {
            // �ø��� ��Ʈ���� �Է� ��Ʈ���� ȹ���Ѵ�.
            inputStream = serialPort.getInputStream();
        } catch (IOException e) { }
        // �ø��� ��Ʈ�� �̺�Ʈ �����ʷ� �ڽ��� ����Ѵ�.
        try {
            serialPort.addEventListener(this);
        } catch (TooManyListenersException e) { }
        
        /* �ø��� ��Ʈ�� �����Ͱ� �����ϸ� �̺�Ʈ�� �� �� �߻��Ǵµ�
           �� ��, �ڽ��� �����ʷ� ��ϵ� ��ü���� �̺�Ʈ�� �����ϵ��� ���. */
        serialPort.notifyOnDataAvailable(true);

        // �ø��� ��� ����. Data Bit�� 8, Stop Bit�� 1, Parity Bit�� ����.
        try {
            serialPort.setSerialPortParams(9600, 			    		SerialPort.DATABITS_8, SerialPort.STOPBITS_1,	    		SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) { }
        
     // ������ ��ü ����
        readThread = new Thread(this);

        // ������ ����
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
        // �̺�Ʈ�� Ÿ�Կ� ���� switch ������ ����.
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
            
            // �����Ͱ� �����ϸ�
            case SerialPortEvent.DATA_AVAILABLE:
                byte[] readBuffer = new byte[30];    // byte �迭 ��ü ����

             
               
                // �Է� ��Ʈ���� ��밡���ϸ�, ���۷� �о� ���� ��
                // String ��ü�� ��ȯ�Ͽ� ���
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



