import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.util.Enumeration;
import java.util.Scanner;

class SimpleWrite implements Runnable{
	 static SerialPort serialPort;
    static OutputStream outputStream;
    Thread writeThread;
    final static int MAXBUFFER = 512;
	final static int MAXSIZE =2;
	//static int keyinposition =0;
	static SndThread sndThread; 
	public static DatagramSocket socket;  // main socket 
	public static Signaling inputsignal = new Signaling();  // signaling Interface
	public static Signaling sndsignal = new Signaling();
	public static Timeout tclick = new Timeout(); // Timeout Interface
    static int mode;
    
    public SimpleWrite()
    {
                    
                    try {
                        outputStream = serialPort.getOutputStream();
                        SimpleRead.outputStream= serialPort.getOutputStream();
                    } catch (IOException e) { }
                    try {
                        serialPort.setSerialPortParams(115200,
                        		SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                        	      SerialPort.PARITY_NONE);
                    
                    } catch (UnsupportedCommOperationException e) { }
                           
                    sndThread = new SndThread(outputStream,inputsignal,sndsignal, tclick);
        			sndThread.start();                    
   writeThread = new Thread(this);
    // 쓰레드 동작
   writeThread.start();
    }

public void run() {
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	 
	if(mode!=1)
	{
		System.out.print("Input Data : ");
		
	}
	
	while (true) {
		 String data = null;
		try {
			data = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (data.length() == 0){ // no char carriage return 
			System.out.println("grace out call");
			break;
		} else if(mode==2) {  // Valid data
	
			//System.out.println("sndthread ");
			sndThread.PutData(data); 
		   sndsignal.sendkeynotifying(); //sndThread.sendkey(RDTbuffer.key_data+"\0");  /* Key board type to be sent through Datagram Socket*/
		   //keyinposition=(keyinposition+1)%MAXSIZE;// update key in position within the WIndow (winsize<16)
			inputsignal.waiting();
			//System.out.println("wakeup keyboard");
	
		} 
		if(mode==1)
		System.out.println("server mode waiting for incoming packet");
	}

       
}
}