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

        // enumeration type �� portList �� ��� ��ü�� ���Ͽ�
        while (portList.hasMoreElements()) {
            // enumeration ���� ��ü�� �ϳ� �����´�.
            portId = (CommPortIdentifier) portList.nextElement();
            // ������ ��ü�� port type �� serial port �̸�
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                
            	if (portId.getName().equals("COM9")) {

                    // Linux �� ���
                    //if (portId.getName().equals("/dev/term/a"))
            		 try {
            	            /* ��� �޼ҵ� : 
            	               public CommPort open(java.lang.String appname, int timeout)
            	               ��� : 
            	               ���ø����̼� �̸��� Ÿ�Ӿƿ� �ð� ��� */
            	            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
            	        } catch (PortInUseException e) { }
            
                        // ��ü ����
                                
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
	   // ������ ����
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
                byte[] readBuffer = new byte[1];    // byte �迭 ��ü ����

                String readbuffer="";
             
               
                // �Է� ��Ʈ���� ��밡���ϸ�, ���۷� �о� ���� ��
                // String ��ü�� ��ȯ�Ͽ� ���
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


