import java.io.IOException;
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
	static final int MAXBUFFER=512, MAXTIMEOUT=5, MAXSIZE = 16;
	final static int EVENT_REQSND = 1, EVENT_RCVACK = 2, EVENT_TIMEOUTPACKET = 3;
	public static byte[][] snd_data = (byte[][]) new byte[MAXSIZE][MAXBUFFER];
	DatagramSocket socket;
	DatagramPacket sndPacket;// �۽ſ� �����ͱ׷� ��Ŷ
	public InetAddress myinetaddr, remoteinetaddr;
	public int myport=0, remoteport=0, winsize=2; // s&w ARQ default
	Signaling pp;
	Timeout tout;
	boolean DEBUG=false;
	
	public int sf=0,sn=0,rn=0; 
	public static int seqNo=0,ackNo=0,flag=0,length=0;
	public int current_ackNo=0;
	public DatagramPacket snd_DatagramPacket[]=new DatagramPacket[MAXSIZE];// �ۼ��ſ� �����ͱ׷� ��Ŷ array
	public byte[][] snd_Datagramacket_header; // (seq,ack,flag,length,checksum(4byte)) 8 byte 
	static String key_in_data[]= new String[MAXSIZE];

	SndThread (DatagramSocket s,Signaling p, Timeout to) {
		socket = s;
		pp=p;
		tout=to;
	}	
	
	public void init() {
        if(!DEBUG) {System.out.print("Sf sn parameter init");}
		sf=sn=rn=0; // init Control NO.
	}
	/* 
	 * keyboard �Է°��� ���ۿ� ������ ����, (���߿� ����� �Է°��� ����ؼ� ��Ŷ�� ����� ����)
	 */
	public void PutData(String a, int i) {
		key_in_data[i] = (String)a;
	} 
	/* 
	 * ���ۿ� ����� keyboard �Է°��� ã�� ����
	 */
	public String GetData(int i) {
		return key_in_data[i];
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
        
        //if(DEBUG) {System.out.print("calculated CRC : "+new String(tempCRC));}
        
        return tempCRC;
    }

	/* 
	 *  keyboard �Է°��� ������ ARQ�� �ش��� �����͸� byte[] snd_data[seq] �����Ͽ� UDP ��Ŷ ���·�  ����� ��
	 *  -�Է�: isn: ���� ��Ŷ�� ������ seq No   - s: ����ڰ� keyboard�� �Է��� ������ ��Ʈ�� 
	 *  -���: DatagramPacket sndPacket 
	 *  ARQ Header = SeqNo.(1), AckNo(1), flags(1), length(1), CRC32(4) 
	 */
	public DatagramPacket MakeFrame(int isn, String s) { // Make ARQ Key in Frame -> snd_buffer[sn]
    	byte[] tempCRC = new byte[4];
		byte[] tmp= new byte[MAXSIZE];
		tmp = s.getBytes();
		int len= tmp.length;
		snd_data[isn][HEADER_SEQ]=(new Integer(isn).byteValue());
		snd_data[isn][HEADER_ACK]=(new Integer(ackNo).byteValue()); // ackNo could be updated by the RcvThread remotely
		snd_data[isn][HEADER_FLAGS]=(new Integer(flag).byteValue());
		snd_data[isn][HEADER_LENGTH]=(new Integer(len).byteValue()); //s.length, excluding header length(8)
		for(int i=0;i<4;i++) snd_data[isn][i+HEADER_CHECKSUM] = 0x00; // reset 
		for(int j=0;j<len;j++)
			snd_data[isn][j+HEADER_DATA] = tmp[j];
		tempCRC = getCRC(snd_data[isn],len+8);
		for(int i=0;i<4;i++) snd_data[isn][i+HEADER_CHECKSUM] = tempCRC[i]; // reset 
		if(DEBUG) {System.out.print("snd packet");
				for(int i=0;i<(len+8);i++) 
				System.out.print(" "+Byte.toString(snd_data[isn][i])); System.out.println("");
		}
		sndPacket = new DatagramPacket (snd_data[isn], (len+8),remoteinetaddr, remoteport);
		return sndPacket;
	}

	/* 
	 *  ��Ŷ�� ������ �ٷ� ����ϴ� ���� �ƴ϶� ���� ������ �� ����ϱ� ���� ���� ������ ��
	 *  snd_DatagramPacket[seq]seq ���� ���۵� ��Ŷ�� ���� ������ (���߿� ������ �� �ٷ� ���). 
	 */
	public void StoreFrame(int is,DatagramPacket d) {
		snd_DatagramPacket[is] = d;
	}
	/* 
	 *  ������ UDP ��Ŷ�� ����. 
	 */
	public void SendFrame(int i){
		try {
			socket.send(snd_DatagramPacket[i]);
		} catch (IOException e) { e.printStackTrace();
		}
	}
	public void sendTimeoutPacket (int i) {
	    try {
		    socket.send(snd_DatagramPacket[i]);
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
			pp.waiting(); // waiting until notifying & return wake up state 
			int kk = pp.get_state(); if(DEBUG) System.out.println("notify "+kk);
			if(kk==EVENT_REQSND){
				// Keyboard �Է� �����͸� ������� Header�� �����͸� �����ϰ� UDP ��Ŷ���� ����� ���� 
				// �Է�: keyboard �Է� ��Ʈ��, 
				// ����: Header �� ������ ���� �� UDP ��Ŷ���� ���� �׸��� �������� ���� Ÿ�Ӿƿ��� ������ ���� 
				if((sn +MAXSIZE-sf)%MAXSIZE < winsize) { 
					// sn is within MAXWIN
					if(Signaling.sndseqNo!=sn) 
					 System.out.println("key in position error: sndSeq No="+Signaling.sndseqNo+" sn="+sn);
					String a = GetData(Signaling.sndseqNo);  // Key in data is stored in Keydata[sn]
					if(DEBUG) System.out.println("key in string ="+a+" sndseqNo="+Signaling.sndseqNo+" sn="+sn); 
					DatagramPacket snd = MakeFrame(sn,a);
					StoreFrame(sn, snd); // put DatagramPacket into snd_DatagramPacket[sn] array
					SendFrame(sn); // put DatagramPacket into snd_DatagramPacket[sn] array
				    tout.Timeoutset(sn,1000,pp);	// Timeout Start
					sn=(MAXSIZE+sn+1)%MAXSIZE; // update sn 
				} else if(DEBUG) System.out.println("Seq No: exceed boundary (unable to sent until receive ack)");
			}
			
			if(kk == EVENT_RCVACK){ 
				//ack Received move sf -> rcvackNo
				// ������ ���� ��Ŷ�� ���� ACK ��Ŷ�� ���� ��� (rcvThread���� ����)
				// ����: ��Ŷ �۽� �� ������ timeout�� �����ϰ� ���� �������� �����ϱ� ���� timeout limit��  �ʱ�ȭ ��.
				tout.timeoutlimit = 0; // Packet transmission reset
				for(int i=sf;i< Signaling.rcvackNo;i++) 
					// if cumulative ack  �߰� ��ũ �ȿ͵� rcvackNo���� ack ó��
					tout.Timeoutcancel(i); 	//cancel timeoutTASK previous task 
				sf=Signaling.rcvackNo; 
				//update seq no.
			}
			
			if(kk==EVENT_TIMEOUTPACKET){
				// Timeout interrupt�� ���� ��� (�ֱ����� Ÿ�̸Ӱ� ������ �ð��� interrupt ����)
				// ����: ��Ŷ�� �������ϰ� �ٽ�  Ÿ�Ӿƿ��� ������ ����, ���� ���� �������� �����ϱ� ���� ���.
				if(tout.timeoutlimit < MAXTIMEOUT) 
				{
					System.out.println("Timeout task No ="+Signaling.timeouttaskNo+" sf="+sf);
					for(int tmp=sf;tmp<sn;tmp++){
						sendTimeoutPacket (tmp); // sf packet retransmission
						if(tmp>sf) tout.Timeoutcancel(tmp);
						tout.Timeoutset(tmp,1000,pp);	// Timeout Start
				}
					
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
