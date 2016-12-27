import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

class SimpleRead implements Runnable, SerialPortEventListener {
    static SerialPort serialPort;
    InputStream inputStream; 
    static OutputStream outputStream;
    Thread readThread;
    static int mode;
    static int inputnum=0;
	private static final int MAXBUFFER = 512, MAXSIZE=16; //, WINSIZE=8;
	static final int HEADER_DATA = 8, HEADER_SEQ=0, HEADER_ACK=1, HEADER_FLAGS=2, HEADER_LENGTH=3,HEADER_CHECKSUM=4; // header length 
	public static int rcvseqNo=0,rcvackNo=0,rcvflag=0,rcvlength=0,current_ackNo=0;
	public static int ackseqNo=0,ackackNo=0,ackflag=0x12,acklength=0;
	public byte[] rcv_data=new byte [MAXBUFFER]; 
	public byte[] CRCchecksum=new byte[4];
	boolean	cond=true, DEBUG=false;
	Signaling pp=SimpleWrite.sndsignal;
	Signaling readsignal =new Signaling();
	static SndThread sndThread=SimpleWrite.sndThread;
	
	
	// SimpleRead 생성자
    public SimpleRead() {
  
        try {
            // 시리얼 포트에서 입력 스트림을 획득한다.
            if(serialPort!=null)
            	inputStream = serialPort.getInputStream();
            else
            	System.out.println("error\n");
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
            serialPort.setSerialPortParams(115200, 			    		SerialPort.DATABITS_8, SerialPort.STOPBITS_1,	    		SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) { }
        
        
     // 쓰레드 객체 생성
        readThread = new Thread(this);

        // 쓰레드 동작
        readThread.start();
   
    }
	boolean corrupted(byte[] rcvd, int rcvl) { // Not corrupted: true, corrupted: false
		 //*  -입력: byte[]와 length, (받은 패킷에 에러가 없는지 체크(verify checksum)하고)
		 //*  -동작: 헤더에 CRC32를 보고 다시 CRC32를 계산해서 비교 함
		 //*  -결과: 에러가 없는 경우 true, 에러가 발생한 경우 false를 보내줌 
		byte[] tempCRC=new byte[4]; // ACK size without data
		// byte[] tempdata= rcvd;
		// check Checksum or CRC 
		for(int i=0;i<4;i++) rcvd[i+HEADER_CHECKSUM] = 0x00; // reset CRC
		if(DEBUG) {System.out.print("rcvCRC packet");for(int i=0;i<(rcvl+8);i++) System.out.print(" "+Byte.toString(rcvd[i]));System.out.println("");}
		tempCRC=getCRC(rcvd,rcvl+8);  // rcvlenght= data length including header
		if(DEBUG) {System.out.print("rcv CRC");for(int i=0;i<4;i++) System.out.print(" "+Byte.toString(tempCRC[i]));System.out.println("");}
		boolean result=true; //exact matching
		for(int i=0;i<4;i++) if(CRCchecksum[i]!=tempCRC[i]) {result=false; break;} // reset CRC
		return result;
	}
	
	public byte[] getCRC(byte[] frame, int length)	//CRC 계산
    {
    	byte[] tempCRC = new byte[4];
   		int crc  = 0xFFFFFFFF;       // initial contents of LFBSR
        int poly = 0xEDB88320;   // reverse polynomial

        for (int j = 0; j < length; j++) {
            int temp = (crc ^ frame[j]) & 0xff;

            // read 8 bits one at a time
            for (int i = 0; i < 8; i++) {
                if ((temp & 1) == 1) temp = (temp >>> 1) ^ poly;
                else                 temp = (temp >>> 1);
            }
            crc = (crc >>> 8) ^ temp;
        }

        // flip bits
        crc = crc ^ 0xffffffff;
    	
        tempCRC[3] = (byte)(crc & 0xff);
        tempCRC[2] = (byte)(crc >>> 8 & 0xff);
        tempCRC[1] = (byte)(crc >>> 16 & 0xff);
        tempCRC[0] = (byte)(crc >>> 24 & 0xff);
        
       // if(true) {System.out.println("calculated CRC : "+tempCRC);}
        
        return tempCRC;
    }
	void sendAck() {
		 //*  -입력: ack No, (지금 받은 패킷에 seq No를 기준으로 다음 받을 seq No가 ack No 임)
		 //*  -동작: ACK 패킷 구성(seq No, ack No, flags, length) 구성 후 송신자에게 응답함
		 //*  -결과: Ack 패킷을 보내줌 
		System.out.println("send ack!!");
    		byte[] tempCRC = new byte[4];
			byte[] tempbyte=new byte[8]; // ACK size without data
			tempbyte[HEADER_SEQ]=(new Integer(ackseqNo).byteValue());
			tempbyte[HEADER_ACK]=(new Integer(ackackNo).byteValue()); // ackNo could be updated by the RcvThread remotely
			tempbyte[HEADER_FLAGS]=(new Integer(ackflag).byteValue()); // ACK Packet flag==1
			tempbyte[HEADER_LENGTH]=(new Integer(acklength).byteValue()); //s.length, excluding header length(8)
			for(int i=0;i<4;i++) tempbyte[i+HEADER_CHECKSUM] = 0x00; // reset 
			
			tempCRC = getCRC(tempbyte,8);
			
			//System.out.print("hihihiih");
			for(int i=0;i<4;i++) tempbyte[i+HEADER_CHECKSUM] = tempCRC[i]; // reset 
			//System.out.print("hihihiih");
			
			try {
				//System.out.print("hihihiih");
				
				outputStream.write(tempbyte);
			//System.out.printf("after send ack");
			} catch (IOException e) { e.printStackTrace();
			//System.out.printf("ioexceiption occur");
			
			}
			System.out.println("endd!!");
	}

	/* 
	 *  keyboard 입력값을 가지고 ARQ의 해더와 데이터를 byte[] snd_data[seq] 구성하여 UDP 패킷 형태로  만들어 보
	 *  -입력: isn: 보낼 패킷이 가지는 seq No   - s: 사용자가 keyboard로 입력한 데이터 스트링 
	 *  -출력: DatagramPacket sndPacket 
	 *  ARQ Header = SeqNo.(1), AckNo(1), flags(1), length(1), CRC32(4) 
	 */
	void Receive() {
		
			System.out.println("receive ! occur");
			   rcvseqNo= (int) ((Byte)rcv_data[HEADER_SEQ]).intValue();
			   rcvackNo= (int) ((Byte)rcv_data[HEADER_ACK]).intValue();
			   rcvflag= (int) ((Byte)rcv_data[HEADER_FLAGS]).intValue();
			   rcvlength = (int) ((Byte)rcv_data[HEADER_LENGTH]).intValue();
			   for(int i=0;i<4;i++) CRCchecksum[i] = rcv_data[i+HEADER_CHECKSUM];
			   System.out.format("%d %d %d %d\n",rcvseqNo,rcvackNo,rcvflag,rcvlength);
	}
	
 
    public void run() {
    try {
    	while(true)
        {
    		Thread.sleep(20000);
        }
    } catch (InterruptedException e) { }
}
    
    public void totalreceive()
    {
    	SimpleWrite.mode=2;
    
    	inputnum=0;
		if(corrupted(rcv_data,rcvlength)) {
		//System.out.println("success here");	
		if((rcvlength!=0)&&(rcvflag==0x11)) ///data frame
		{
			if(rcvseqNo==ackseqNo) // seq 맞는 frame 받음
			{
				System.out.format("%d frame receive !\n",rcvseqNo);
				ackseqNo=(ackseqNo+1)%2;
				byte[] temp=new byte[MAXBUFFER];
				for(int i=HEADER_DATA;i<(HEADER_DATA+rcvlength);i++)
				{
					temp[i-HEADER_DATA]=rcv_data[i];
					
				}
				String temp2 = new String(temp);
				String temp3 = temp2.trim();
				System.out.println(temp3);
				System.out.print("Input Data : ");
				sendAck();
				//System.out.println("sendACk end");
			}
			else // seq 다른 frame 받음
			{
				System.out.format("%d frame receive ! (not %d frame)\n",rcvseqNo,ackseqNo);
			    sendAck();
			
			}
			
			
		}
		else if((rcvlength==0)&&(rcvflag==0x12)) //ack frame
		{
		
			if(rcvseqNo!=sndThread.seqNo) //correct frame
			{
				System.out.format("%d ack receive \n", rcvseqNo);
				sndThread.seqNo = (sndThread.seqNo+1)%2;
				System.out.print("Input Data : ");
				pp.acknotifying();
				
			}
			else // already receive frame
			{
				System.out.format("%d ack already receive \n", rcvseqNo);
				
			}
			
			
		}
		else
		{
		
			System.out.format("rcvlength : %d rcvflag : %x(hexa)", rcvlength,rcvflag);
		}
		
		
		
		} else System.out.println("CRC Corrupted : CRC checksum");
            	
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
            	   // byte 배열 객체 생성

                byte[] tempbyte=new byte[1];
    			
               //int bytenum=0;
                // 입력 스트림이 사용가능하면, 버퍼로 읽어 들인 후
                // String 객체로 변환하여 출력
                try {
                    while (inputStream.available() > 0) {
                    	//SimpleWrite.mode=2;
                        inputnum+=inputStream.read(tempbyte);
                       // System.out.format("inputnum :%d",inputnum);
                    if(inputnum<8)
                    {
                  	rcv_data[inputnum-1]=tempbyte[0];
                    
                    }
                    else if(inputnum==8)
                    {
                    rcv_data[inputnum-1]=tempbyte[0];
                    Receive();
                    	
                    }
                    else if(inputnum<=(8+rcvlength))
                    {
                    	
                    	rcv_data[inputnum-1]=tempbyte[0];
                    	
                    	
                    	
                    }
                    
                    if(inputnum==(8+rcvlength))
                    {
                    
                    	totalreceive();
                    	
                    }
                    
                    
                    }
                   
                } catch (IOException e) { 
                	
				}
                break;
            }
        }
    }