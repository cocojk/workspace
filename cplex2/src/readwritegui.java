import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.TooManyListenersException;

import javax.swing.*;
public class readwritegui
{
	static CommPortIdentifier portId;
    static Enumeration portList;
    static SerialPort serialPort;   

	
	public static void main (String [] args)
	{
		portList = CommPortIdentifier.getPortIdentifiers();

        // enumeration type 인 portList 의 모든 객체에 대하여
        while (portList.hasMoreElements()) {
            // enumeration 에서 객체를 하나 가져온다.
            portId = (CommPortIdentifier) portList.nextElement();
            // 가져온 객체의 port type 이 serial port 이면
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                
            	if (portId.getName().equals("COM9")) {

                    // Linux 의 경우
                    //if (portId.getName().equals("/dev/term/a"))
            		 try {
            	            /* 사용 메소드 : 
            	               public CommPort open(java.lang.String appname, int timeout)
            	               기능 : 
            	               어플리케이션 이름과 타임아웃 시간 명시 */
            	            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
            	        } catch (PortInUseException e) { }
            
                        // 객체 생성
                                
            	}               
            }
             
        }       
		JFrame frame = new JFrame ("Chatting");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ChattingPanel panel = new ChattingPanel();
		frame.getContentPane().add(panel);
		
		frame.setSize(new Dimension(800,400));
		frame.pack();
		frame.setVisible(true);
		

	}
	
}




class ChattingPanel extends JPanel implements Runnable, SerialPortEventListener
{
	private JLabel inputLabel;
	private JTextField inputtext;
	private JTextArea JText;
	static Enumeration portList=readwritegui.portList;
    static CommPortIdentifier portId=readwritegui.portId;
    static String messageString;
    static SerialPort serialPort=readwritegui.serialPort;
    OutputStream outputStream;
    Thread writeThread;
    InputStream inputStream; 
    Thread readThread;
    
	
    
    ChattingPanel()
	{
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
        

        try {
            outputStream = serialPort.getOutputStream();
        } catch (IOException e) { }
        try {
            serialPort.setSerialPortParams(9600,
            		SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
            	      SerialPort.PARITY_NONE);
        
        } catch (UnsupportedCommOperationException e) { }

        readThread = new Thread(this);
        writeThread = new Thread(this);
       readThread.start();
        
        
        
    	inputLabel = new JLabel("Enter message");

		
		JText = new JTextArea(15,40);
		JScrollPane scrollPane = new JScrollPane(JText);
		inputtext = new JTextField (30);
		inputtext.addActionListener(new tempListener());
		inputtext.setSize(new Dimension(100,10));
		add(inputLabel);
		add(inputtext);
		add(scrollPane);
	

		setPreferredSize (new Dimension(600,400));
		setBackground (Color.gray);
	}
	
	private class tempListener implements ActionListener, Runnable
	{

		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
		messageString = inputtext.getText();
	inputtext.setText("");
	   // 쓰레드 동작
	writeThread = new Thread(this);
	   writeThread.start();
	   
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Thread.currentThread()==writeThread)
			{
				try {
		    	//   System.out.println(messageString);		
		    		outputStream.write(messageString.getBytes());
		    		JText.append("usr : "+messageString+"\n");
		    		JText.setCaretPosition(JText.getDocument().getLength());
				
				}
		    	 catch (NullPointerException e) {} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			
		}
		
	}

	@Override
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
                byte[] readBuffer = new byte[1];    // byte 배열 객체 생성

                String readbuffer="";
             
               
                // 입력 스트림이 사용가능하면, 버퍼로 읽어 들인 후
                // String 객체로 변환하여 출력
                try {
                    while (inputStream.available() > 0) {
                        int numBytes=inputStream.read(readBuffer);
                       /* for(int i=0;i<numBytes;i++)
                        {
                        	
                      //  	System.out.print(readBuffer[i]);
                        }*/
                    	//  System.out.print(new String(readBuffer));
                    readbuffer = readbuffer + new String(readBuffer);
                        
                    }
                   
                    JText.append("partner  : "+new String(readbuffer)+"\n");
		    		JText.setCaretPosition(JText.getDocument().getLength());
            //      
                   
                    
                    // System.out.printf("%s\n",readBuffer);
                   // System.out.println("");
                } catch (IOException e) { }
                break;
            }
        }

	@Override
	public void run() {
		
		if(Thread.currentThread()==readThread)// TODO Auto-generated method stub
		{
			try {
		    	while(true)
		        {
		    		
		    		Thread.sleep(20000);
		        }
		    } catch (InterruptedException e) { }

			
			
		}
		
		}

	
}


