//import java.lang.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
 * packet�� �޴� Thread�� �����ͱ׷� ��Ŷ ���Ű� ���õ� ��� ����� ����
 * ���⼭�� �ñ׳ΰ� Timer�� ����� �κ��� ���� ��.
 * �� ��Ŷ�� �޴� ����� �ϰ� ������ ���� SndThread ������ ��� ó�� ��.
 * - Data ��Ŷ�� ���� ��� ACK ���� ()
 * - ACK�� ���� ��� window �� �ű��
 */
class RcvThread extends Thread {
	DatagramSocket socket;
	public DatagramPacket rcv_DatagramPacket[] = new DatagramPacket[MAXSIZE];// �ۼ��ſ� �����ͱ׷� ��Ŷ
	private static final int MAXBUFFER = 512, MAXSIZE=16; //, WINSIZE=8;
	static final int HEADER_DATA = 8, HEADER_SEQ=0, HEADER_ACK=1, HEADER_FLAGS=2, HEADER_LENGTH=3,HEADER_CHECKSUM=4; // header length 
	public static int rcvseqNo=0,rcvackNo=0,rcvflag=0,rcvlength=0,current_ackNo=0;
	public static int ackseqNo=0,ackackNo=0,ackflag=0x12,acklength=0;
	public byte[] rcv_data, CRCchecksum=new byte[4];
	boolean	cond=true, DEBUG=false;
	DatagramPacket rcv_packet, sndAck;// ���ſ� �����ͱ׷� ��Ŷ
	InetAddress dst_ip;
	int dst_port;
	Signaling pp;
	static SndThread sndThread; 
	
	RcvThread (DatagramSocket s,Signaling p,SndThread sndthread) {
		socket = s;
		pp=p;
		sndThread = sndthread;
	}	
	boolean corrupted(byte[] rcvd, int rcvl) { // Not corrupted: true, corrupted: false
		 //*  -�Է�: byte[]�� length, (���� ��Ŷ�� ������ ������ üũ(verify checksum)�ϰ�)
		 //*  -����: ����� CRC32�� ���� �ٽ� CRC32�� ����ؼ� �� ��
		 //*  -���: ������ ���� ��� true, ������ �߻��� ��� false�� ������ 
		byte[] tempCRC=new byte[4]; // ACK size without data
		// byte[] tempdata= rcvd;
		// check Checksum or CRC 
		for(int i=0;i<4;i++) rcvd[i+HEADER_CHECKSUM] = 0x00; // reset CRC
		if(DEBUG) {System.out.print("rcvCRC packet");for(int i=0;i<(rcvl+8);i++) System.out.print(" "+Byte.toString(rcvd[i]));System.out.println("");}
		tempCRC=sndThread.getCRC(rcvd,rcvl+8);  // rcvlenght= data length including header
		if(DEBUG) {System.out.print("rcv CRC");for(int i=0;i<4;i++) System.out.print(" "+Byte.toString(tempCRC[i]));System.out.println("");}
		boolean result=true; //exact matching
		for(int i=0;i<4;i++) if(CRCchecksum[i]!=tempCRC[i]) {result=false; break;} // reset CRC
		return result;
	}
	void sendAck() {
		 //*  -�Է�: ack No, (���� ���� ��Ŷ�� seq No�� �������� ���� ���� seq No�� ack No ��)
		 //*  -����: ACK ��Ŷ ����(seq No, ack No, flags, length) ���� �� �۽��ڿ��� ������
		 //*  -���: Ack ��Ŷ�� ������ 
    		byte[] tempCRC = new byte[4];
			byte[] tempbyte=new byte[8]; // ACK size without data
			tempbyte[HEADER_SEQ]=(new Integer(ackseqNo).byteValue());
			tempbyte[HEADER_ACK]=(new Integer(ackackNo).byteValue()); // ackNo could be updated by the RcvThread remotely
			tempbyte[HEADER_FLAGS]=(new Integer(ackflag).byteValue()); // ACK Packet flag==1
			tempbyte[HEADER_LENGTH]=(new Integer(acklength).byteValue()); //s.length, excluding header length(8)
			for(int i=0;i<4;i++) tempbyte[i+HEADER_CHECKSUM] = 0x00; // reset 
			tempCRC = sndThread.getCRC(tempbyte,8);
			for(int i=0;i<4;i++) tempbyte[i+HEADER_CHECKSUM] = tempCRC[i]; // reset 
			sndAck = new DatagramPacket (tempbyte, (8),sndThread.remoteinetaddr, sndThread.remoteport);
			try {
				socket.send(sndAck);
			} catch (IOException e) { e.printStackTrace();
			}
	}

	/* 
	 *  keyboard �Է°��� ������ ARQ�� �ش��� �����͸� byte[] snd_data[seq] �����Ͽ� UDP ��Ŷ ���·�  ����� ��
	 *  -�Է�: isn: ���� ��Ŷ�� ������ seq No   - s: ����ڰ� keyboard�� �Է��� ������ ��Ʈ�� 
	 *  -���: DatagramPacket sndPacket 
	 *  ARQ Header = SeqNo.(1), AckNo(1), flags(1), length(1), CRC32(4) 
	 */
	void Receive(DatagramPacket rcv) {
		rcv_data = new byte[MAXBUFFER];
		try {
		       socket.receive(rcv);  // receive Frame
		       // sndThread.remoteport = rdtbuffer.rcv_packet.getPort();    // if(rdtbuffer.remoteport==0)  ������ ���Ͽ� ���� ������ ����
		       //rdtbuffer.remoteinetaddr = rdtbuffer.rcv_packet.getAddress(); //  ������ ���Ͽ� ���� ������ ����
			} catch(IOException e) {
				System.out.println("Thread exception "+e);
			}	
			   rcv_data = rcv_packet.getData();
			   // rcvlength = rcv_packet.getLength();
			   rcvseqNo= (int) ((Byte)rcv_data[HEADER_SEQ]).intValue();
			   rcvackNo= (int) ((Byte)rcv_data[HEADER_ACK]).intValue();
			   rcvflag= (int) ((Byte)rcv_data[HEADER_FLAGS]).intValue();
			   rcvlength = (int) ((Byte)rcv_data[HEADER_LENGTH]).intValue();
			   for(int i=0;i<4;i++) CRCchecksum[i] = rcv_data[i+HEADER_CHECKSUM];
	}
	
	/* 
	 *  Socket ���� ���� ��Ŷ�� �ް� �����ʹ� ȭ�鿡 ����ϰ� ACK�� ���� ��, 
	 *  ACK ��Ŷ�� ���� ��� �۽� timeout�� ����ϰ� ������ ���� ���� �� �޾Ҵٰ� �˷���.
	 *  -�Է�: Socket���� ���� ���� ��Ŷ 
	 *  -����: ���� ��Ŷ�� ������ ������ üũ(verify checksum)�ϰ�
	 *         ������ ���� ��� ���������� ACK���� �����Ͽ� ó�� 
	 *  	   * �������� ���ȭ�鿡 ��� ��  ack ����
	 *  	   * ACK�� ��� sndThread �۽Ŵܿ� ��Ŷ ������ �� �Ǿ����� �˸� 
	 *  ARQ Header = SeqNo.(1), AckNo(1), flags(1), length(1), CRC32(4) 
	 */
	public void run() {
		while (cond) {
			byte[] tempbyte=new byte[MAXBUFFER];
			rcv_packet = new DatagramPacket(tempbyte,tempbyte.length);
			Receive(rcv_packet); // Received Packet header and data
			sndThread.remoteinetaddr=rcv_packet.getAddress();// set remote port & remote addrif(sndThread.remoteinetaddr!=rcv_packet.getAddress()) 
			sndThread.remoteport=rcv_packet.getPort();// set remote port & remote addrif(sndThread.remoteport==rcv_packet.getPort()) 
			if(corrupted(rcv_data,rcvlength)) { // ���ŵ� ��Ŷ�� ������ ������ Ȯ�� (true = not corrupted, false=corruted) 
				// ���ŵ� �����͸� ������� Header�� �����ϰ� ��� ������ �̿��Ͽ� �����͸� ������ ������, ACK�� ������ ������ Ȯ�� 
					// ������ ���� ���, ȭ�鿡 ����ϰ� ACK ��Ŷ�� ����� ��������
			if((rcvlength!=0)&&(rcvflag==0x11)) ///data frame
			{
				if(rcvseqNo==ackseqNo) // seq �´� frame ����
				{
					System.out.format("%d frame receive !\n",rcvseqNo);
					ackseqNo=(ackseqNo+1)%2;
					byte[] temp=new byte[MAXBUFFER];
					for(int i=HEADER_DATA;i<(HEADER_DATA+rcvlength);i++)
					{
						temp[i-HEADER_DATA]=tempbyte[i];
						
					}
					String temp2 = new String(temp);
					String temp3 = temp2.trim();
					System.out.println(temp3);
					System.out.print("Input Data : ");
					sendAck();
				}
				else // seq �ٸ� frame ����
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
			
			
			
			} else System.out.println("CRC Corrupted : CRC checksum");// not currupted
		}
		System.out.println("grace out");
	}
	
	public void graceout(){
		cond=false;
	}
			
} // end ReceiverThread class
