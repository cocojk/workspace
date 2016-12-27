import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
 * packet�� ������ Thread�� �����ͱ׷� ��Ŷ�� ���õ� ��� ����� ����
 * ���⼭�� �ñ׳ΰ� Timer�� ����� �κ��� ���� ��.
 * ������ ��Ŷ�� ������ �޴� ���� SndThread ������ ��� ó�� ��.
 * - ��Ŷ�� ����� ���� (MakeFrame (Header�� data), StoreFrame (�����۽� �����  ��Ŷ�� ���ۿ� ����), sendFrame(UDP ����)
 * - ACK�� ���� ��� window �� �ű�� Timeout�� ��� ����� ��Ŷ�� ����
 */
public class SndThread extends Thread {
	static final int HEADER_DATA = 8, HEADER_SEQ=0, HEADER_ACK=1, HEADER_FLAGS=2, HEADER_LENGTH=3,HEADER_CHECKSUM=4; // header length 
	static final int MAXBUFFER=512, MAXTIMEOUT=10,MAXSIZE=127;
	final static int EVENT_REQSND = 1, EVENT_RCVACK = 2, EVENT_TIMEOUTPACKET = 3;
	public static byte[] snd_data= new byte[MAXBUFFER];
	 static OutputStream outputStream;  
     Signaling inputsignal;
	Signaling sndsignal;
	Timeout tout;
	boolean DEBUG=false;
	public static int seqNo=0,ackNo=0,flag=0x11,length=0;
	static String key_in_data= new String();

	SndThread (OutputStream output,Signaling input,Signaling snd, Timeout to) {
		outputStream =output;
	    inputsignal = input;
	    sndsignal = snd;
		tout=to;
	}	
	
	public void init() {
        if(!DEBUG) {System.out.print("Sf sn parameter init");}
	}
	/* 
	 * keyboard �Է°��� ���ۿ� ������ ����, (���߿� ����� �Է°��� ����ؼ� ��Ŷ�� ����� ����)
	 */
	public void PutData(String a) {
		key_in_data = (String)a;
	} 
	/* 
	 * ���ۿ� ����� keyboard �Է°��� ã�� ����
	 */
	public String GetData( ) {
		return key_in_data;
	}
	/* 
	 * UDP��Ŷ ������  byte[] frame �� ���� CRC32[4 byte]�� ����ؼ� return �� 
	 */
    public byte[] getCRC(byte[] frame, int length)	//CRC ���
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
        
        if(true) {System.out.println("calculated CRC : "+tempCRC);}
        
        return tempCRC;
    }

	/* 
	 *  keyboard �Է°��� ������ ARQ�� �ش��� �����͸� byte[] snd_data[seq] �����Ͽ� UDP ��Ŷ ���·�  ����� ��
	 *  -�Է�: isn: ���� ��Ŷ�� ������ seq No   - s: ����ڰ� keyboard�� �Է��� ������ ��Ʈ�� 
	 *  -���: DatagramPacket sndPacket 
	 *  ARQ Header = SeqNo.(1), AckNo(1), flags(1), length(1), CRC32(4) 
	 */
	public void MakeFrame(String s) { // Make ARQ Key in Frame -> snd_buffer[sn]
    	byte[] tempCRC = new byte[4];
		byte[] tmp= new byte[MAXSIZE];
		tmp = s.getBytes();
		int len= tmp.length;
		snd_data[HEADER_SEQ]=(new Integer(seqNo).byteValue());
		snd_data[HEADER_ACK]=(new Integer(ackNo).byteValue()); // ackNo could be updated by the RcvThread remotely
		snd_data[HEADER_FLAGS]=(new Integer(flag).byteValue());
		snd_data[HEADER_LENGTH]=(new Integer(len).byteValue()); //s.length, excluding header length(8)
		for(int i=0;i<4;i++) snd_data[i+HEADER_CHECKSUM] = 0x00; // reset 
		for(int j=0;j<len;j++)
			snd_data[j+HEADER_DATA] = tmp[j];
		tempCRC = getCRC(snd_data,len+8);
		for(int i=0;i<4;i++) snd_data[i+HEADER_CHECKSUM] = tempCRC[i]; // reset 
		if(DEBUG) {System.out.print("snd packet");
				for(int i=0;i<(len+8);i++) 
				System.out.print(" "+Byte.toString(snd_data[i])); System.out.println("");
		}
	}

	/* 
	 *  ��Ŷ�� ������ �ٷ� ����ϴ� ���� �ƴ϶� ���� ������ �� ����ϱ� ���� ���� ������ ��
	 *  snd_DatagramPacket[seq]seq ���� ���۵� ��Ŷ�� ���� ������ (���߿� ������ �� �ٷ� ���). 
	 */
	
	/* 
	 *  ������ UDP ��Ŷ�� ����. 
	 */
	public void SendFrame( ){
		try {
			byte[] tempbyte=new byte[(8+snd_data[HEADER_LENGTH])];
			for(int i=0;i<(8+snd_data[HEADER_LENGTH]);i++)
			{
				tempbyte[i]=snd_data[i];
				
			}
			outputStream.write(tempbyte);
		} catch (IOException e) { e.printStackTrace();
		}
	}
	public void sendTimeoutPacket () {
	    try {
			byte[] tempbyte=new byte[(8+snd_data[HEADER_LENGTH])];
			for(int i=0;i<(8+snd_data[HEADER_LENGTH]);i++)
			{
				tempbyte[i]=snd_data[i];
				
			}
	    	outputStream.write(tempbyte);
	    	} catch (IOException e) { e.printStackTrace();
	     }
	}	
	
	/* if sf==sn, then no packet to be sent
	 * 2. ack received: sf timeout cancel if(sf < ack.no) (sf update �߿�)�� ��ü
	 * 3. timeout received:  send packet array list (if (sf<i<sn), re-transmit array list
	 * 1. sendkey :  send packet array[sn], then (sn->sn+1);
	 *    buffer-> snd_packet array �� ���� [������ �� �ٽ� ���]
	 * send packet array[sn],  static timeout[] (����ȸ�� �̻� �ݺ� ����)
	 */	
	// seq no reset when? restart  seq=ack=len=0 flag=0xff �� ��� ���� (���� ÷�� ��Ŷ�� setting?)
	public void run() {
		while(true) { 		//  sndThread�� ���������� ��Ŷ ������ ���� ���� ������ ����
			sndsignal.waiting(); // waiting until notifying & return wake up state 
			int kk = sndsignal.get_state(); if(DEBUG) System.out.println("notify "+kk);
			if(kk==EVENT_REQSND){
				// Keyboard �Է� �����͸� ������� Header�� �����͸� �����ϰ� UDP ��Ŷ���� ����� ���� 
				// �Է�: keyboard �Է� ��Ʈ��, 
				// ����: Header �� ������ ���� �� UDP ��Ŷ���� ���� �׸��� �������� ���� Ÿ�Ӿƿ��� ������ ���� 
				 	String a = GetData();  // Key in data is stored in Keydata[sn]
					if(DEBUG) System.out.println("key in string ="+a+" sndseqNo="+seqNo); 
					 MakeFrame(a);
					SendFrame(); // put DatagramPacket into snd_DatagramPacket[sn] array
				    tout.Timeoutset(0,1000,sndsignal);	// Timeout Start 
		
			} 
			
			if(kk == EVENT_RCVACK){ //ack Received move sf -> rcvackNo
				// ������ ���� ��Ŷ�� ���� ACK ��Ŷ�� ���� ��� (rcvThread���� ����)
				// ����: ��Ŷ �۽� �� ������ timeout�� �����ϰ� ���� �������� �����ϱ� ���� timeout limit��  �ʱ�ȭ ��.
				tout.timeoutlimit = 0; // Packet transmission reset
				//for(int i=sf;i< Signaling.rcvackNo;i++) // if cumulative ack
			    tout.Timeoutcancel(0); 	//cancel timeoutTASK previous task 
			    inputsignal.keyboardnotifying();
				//sf=Signaling.rcvackNo; //update seq no.
			}
			
			if(kk==EVENT_TIMEOUTPACKET){
				// Timeout interrupt�� ���� ��� (�ֱ����� Ÿ�̸Ӱ� ������ �ð��� interrupt ����)
				// ����: ��Ŷ�� �������ϰ� �ٽ�  Ÿ�Ӿƿ��� ������ ����, ���� ���� �������� �����ϱ� ���� ���.
				if(tout.timeoutlimit < MAXTIMEOUT) {
					System.out.println("Timeout occur");
					//System.out.println("Timeout task No ="+Signaling.timeouttaskNo+" sf="+sf);
						sendTimeoutPacket (); // sf packet retransmission
						tout.Timeoutset(0,1000,sndsignal);	// Timeout Start
					tout.timeoutlimit++;
				} else {
					System.out.println("Timeout reset (unable to be sent)");
					tout.timeoutlimit = 0; // Packet transmission fail
				}
			}else if(kk>EVENT_TIMEOUTPACKET){
				System.out.println("Out of notify state");
				System.exit(0);
			}
		} 
	}
}
