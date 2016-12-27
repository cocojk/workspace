//import java.lang.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
 * packet을 받는 Thread로 데이터그램 패킷 수신과 관련된 모든 기능을 수행
 * 여기서는 시그널과 Timer에 연결된 부분을 관리 함.
 * 또 패킷을 받는 기능을 하고 보내는 것은 SndThread 내에서 모두 처리 함.
 * - Data 패킷를 받은 경우 ACK 보냄 ()
 * - ACK를 받은 경우 window 를 옮기고
 */
class RcvThread extends Thread {
	DatagramSocket socket;
	public DatagramPacket rcv_DatagramPacket[] = new DatagramPacket[MAXSIZE];// 송수신용 데이터그램 패킷
	private static final int MAXBUFFER = 512, MAXSIZE=16; //, WINSIZE=8;
	static final int HEADER_DATA = 8, HEADER_SEQ=0, HEADER_ACK=1, HEADER_FLAGS=2, HEADER_LENGTH=3,HEADER_CHECKSUM=4; // header length 
	public static int rcvseqNo=0,rcvackNo=0,rcvflag=0,rcvlength=0,current_ackNo=0;
	public static int ackseqNo=0,ackackNo=0,ackflag=0x12,acklength=0;
	public byte[] rcv_data, CRCchecksum=new byte[4];
	boolean	cond=true, DEBUG=false;
	DatagramPacket rcv_packet, sndAck;// 수신용 데이터그램 패킷
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
		 //*  -입력: byte[]와 length, (받은 패킷에 에러가 없는지 체크(verify checksum)하고)
		 //*  -동작: 헤더에 CRC32를 보고 다시 CRC32를 계산해서 비교 함
		 //*  -결과: 에러가 없는 경우 true, 에러가 발생한 경우 false를 보내줌 
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
		 //*  -입력: ack No, (지금 받은 패킷에 seq No를 기준으로 다음 받을 seq No가 ack No 임)
		 //*  -동작: ACK 패킷 구성(seq No, ack No, flags, length) 구성 후 송신자에게 응답함
		 //*  -결과: Ack 패킷을 보내줌 
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
	 *  keyboard 입력값을 가지고 ARQ의 해더와 데이터를 byte[] snd_data[seq] 구성하여 UDP 패킷 형태로  만들어 보
	 *  -입력: isn: 보낼 패킷이 가지는 seq No   - s: 사용자가 keyboard로 입력한 데이터 스트링 
	 *  -출력: DatagramPacket sndPacket 
	 *  ARQ Header = SeqNo.(1), AckNo(1), flags(1), length(1), CRC32(4) 
	 */
	void Receive(DatagramPacket rcv) {
		rcv_data = new byte[MAXBUFFER];
		try {
		       socket.receive(rcv);  // receive Frame
		       // sndThread.remoteport = rdtbuffer.rcv_packet.getPort();    // if(rdtbuffer.remoteport==0)  임의의 소켓에 대한 응답을 위해
		       //rdtbuffer.remoteinetaddr = rdtbuffer.rcv_packet.getAddress(); //  임의의 소켓에 대한 응답을 위해
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
	 *  Socket 으로 부터 패킷을 받고 데이터는 화면에 출력하고 ACK를 보내 줌, 
	 *  ACK 패킷을 받은 경우 송신 timeout를 취소하고 이전에 보낸 것을 잘 받았다고 알려줌.
	 *  -입력: Socket으로 부터 받은 패킷 
	 *  -동작: 받은 패킷에 에러가 없는지 체크(verify checksum)하고
	 *         에러가 없는 경우 데이터인지 ACK인지 구별하여 처리 
	 *  	   * 데이터인 경우화면에 출력 및  ack 전송
	 *  	   * ACK인 경우 sndThread 송신단에 패킷 전송이 잘 되었음을 알림 
	 *  ARQ Header = SeqNo.(1), AckNo(1), flags(1), length(1), CRC32(4) 
	 */
	public void run() {
		while (cond) {
			byte[] tempbyte=new byte[MAXBUFFER];
			rcv_packet = new DatagramPacket(tempbyte,tempbyte.length);
			Receive(rcv_packet); // Received Packet header and data
			sndThread.remoteinetaddr=rcv_packet.getAddress();// set remote port & remote addrif(sndThread.remoteinetaddr!=rcv_packet.getAddress()) 
			sndThread.remoteport=rcv_packet.getPort();// set remote port & remote addrif(sndThread.remoteport==rcv_packet.getPort()) 
			if(corrupted(rcv_data,rcvlength)) { // 수신된 패킷에 에러가 없는지 확인 (true = not corrupted, false=corruted) 
				// 수신된 데이터를 기반으로 Header를 구성하고 헤더 정보를 이용하여 데이터를 포함한 것인지, ACK를 포함한 것인지 확인 
					// 데이터 포함 경우, 화면에 출력하고 ACK 패킷을 만들어 응답해줌
			if((rcvlength!=0)&&(rcvflag==0x11)) ///data frame
			{
				if(rcvseqNo==ackseqNo) // seq 맞는 frame 받음
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
			
			
			
			} else System.out.println("CRC Corrupted : CRC checksum");// not currupted
		}
		System.out.println("grace out");
	}
	
	public void graceout(){
		cond=false;
	}
			
} // end ReceiverThread class
