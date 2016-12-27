import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.BitSet;

/*
 * packet�� ������ Thread�� �����ͱ׷� ��Ŷ�� ���õ� ��� ����� ����
 * ���⼭�� �ñ׳ΰ� Timer�� ����� �κ��� ���� ��.
 * ������ ��Ŷ�� ������ �޴� ���� SndThread ������ ��� ó�� ��.
 * - ��Ŷ�� ����� ���� (MakeFrame (Header�� data), StoreFrame (�����۽� �����  ��Ŷ�� ���ۿ� ����), sendFrame(UDP ����)
 * - ACK�� ���� ��� window �� �ű�� Timeout�� ��� ����� ��Ŷ�� ����
 */
public class SndThread extends Thread {
	static final int HEADER_FLAG=0, HEADER_ADDRESS=1, HEADER_CONTROL=2,HEADER_INFORMATION=3; // header length 
	static final int MAXBUFFER=512, MAXTIMEOUT=10,MAXSIZE=127;
	final static int EVENT_REQSND = 1, EVENT_RCVACK = 2, EVENT_TIMEOUTPACKET = 3;
	public static byte[] snd_data;
	DatagramSocket socket;
	DatagramPacket sndPacket;// �۽ſ� �����ͱ׷� ��Ŷ
	public InetAddress myinetaddr, remoteinetaddr;
	public int myport=0, remoteport=0; 
	Signaling inputsignal;
	Signaling sndsignal;
	Timeout tout;
	static int mode=0;
	boolean DEBUG=false;
	public static int seqNo=0,ackNo=0,length=0;
	public DatagramPacket snd_DatagramPacket;// �ۼ��ſ� �����ͱ׷� ��Ŷ array
	static String key_in_data= new String();

	SndThread (DatagramSocket s,Signaling input,Signaling snd, Timeout to) {
		socket = s;
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
        
        if(DEBUG) {System.out.print("calculated CRC : "+new String(tempCRC));}
        
        return tempCRC;
    }

	/* 
	 *  keyboard �Է°��� ������ ARQ�� �ش��� �����͸� byte[] snd_data[seq] �����Ͽ� UDP ��Ŷ ���·�  ����� ��
	 *  -�Է�: isn: ���� ��Ŷ�� ������ seq No   - s: ����ڰ� keyboard�� �Է��� ������ ��Ʈ�� 
	 *  -���: DatagramPacket sndPacket 
	 *  ARQ Header = SeqNo.(1), AckNo(1), flags(1), length(1), CRC32(4) 
	 */
	public DatagramPacket MakeFrame(String s) { // Make ARQ Key in Frame -> snd_buffer[sn]
    	byte[] tempCRC = new byte[4];
    	BitSet temp=new BitSet(8);
    	temp.set(1,7);
    	byte[] flag=temp.toByteArray();
		byte[] tmp= new byte[MAXSIZE];
		byte[] address = remoteinetaddr.getAddress();
		tmp = s.getBytes();
		int len= tmp.length;
		byte[] snd_data2=new byte[MAXBUFFER];
		snd_data2[HEADER_FLAG]=flag[0];
		///////////
		///		
		byte[] stuffing=new byte[len+6];
		
		///////////////////////////////////
		stuffing[HEADER_ADDRESS-1]=makeaddress()[0];// ackNo could be updated by the RcvThread remotely
		stuffing[HEADER_CONTROL-1]=makecontrol()[0];
		for(int j=0;j<len;j++)
		stuffing[HEADER_INFORMATION+j-1]=tmp[j]; //s.length, excluding header length(8)
		
		for(int i=0;i<4;i++) stuffing[HEADER_INFORMATION+len-1+i] = 0x00; // reset 
		
		tempCRC = getCRC(stuffing,len+2);
		System.out.println(len);
		for(int i=0;i<4;i++) stuffing[HEADER_INFORMATION+len-1+i] = tempCRC[i]; // reset 
		if(DEBUG) {System.out.print("snd packet");
				for(int i=0;i<(len+8);i++) 
				System.out.print(" "+Byte.toString(snd_data[i])); System.out.println("");
		}
		
		byte[] afterstuffing=setbitstuffing(stuffing);
		BitSet tt=fromByteArray(afterstuffing);
		String ttt="";
		for(int i=0;i<afterstuffing.length*8;i++)
		{
			if(tt.get(i)==true)
			{
				ttt="1"+ttt;
			}
			else
			{
				ttt="0"+ttt;
			}
		}
		System.out.println(afterstuffing.length);		
		
		System.out.println(ttt);
		
		for(int i=0;i<afterstuffing.length;i++)
		{
			snd_data2[i+1]=afterstuffing[i];
		}
		snd_data2[afterstuffing.length+1]=flag[0];
		//////////////////////////////////////////
		snd_data =new byte[afterstuffing.length+2];
		for(int i=0;i<afterstuffing.length+2;i++)
		{
			snd_data[i]=snd_data2[i];
		}
	/*	String bit="";
		BitSet bits=fromByteArray(snd_data);
		for(int i=0;i<(afterstuffing.length+2)*8;i++)
		{
			
			if(bits.get(i)==true)
			{
				bit="1"+bit;
			}
			else
			{
				bit="0"+bit;
			}
			
		}
		System.out.println(bit);
		*////////////////
		sndPacket = new DatagramPacket (snd_data, (HEADER_INFORMATION+len+5),remoteinetaddr, remoteport);
		return sndPacket;
	}

	public byte[] makecontrol()
	{
		BitSet temp=new BitSet(8);
		temp.set(3);
		if(seqNo!=0)
		{
		 temp.set(4);	
		}
		if(ackNo!=0)
		{
			temp.set(0);
		}
		byte[] temp2 = temp.toByteArray();
		return temp2;
	}
	/* 
	 *  ��Ŷ�� ������ �ٷ� ����ϴ� ���� �ƴ϶� ���� ������ �� ����ϱ� ���� ���� ������ ��
	 *  snd_DatagramPacket[seq]seq ���� ���۵� ��Ŷ�� ���� ������ (���߿� ������ �� �ٷ� ���). 
	 */
	public byte[] makeaddress()
	{
		BitSet temp = new BitSet(8);
		if(mode==1)//server 00000011
		{
			temp.set(0);
			temp.set(1);
		}
		else ////client 00000001
		{
			temp.set(0);
		}
		temp.set(7);
		byte[] temp2 = temp.toByteArray();
		return temp2;
	}
	/* 
	 *  ������ UDP ��Ŷ�� ����. 
	 */
	public void SendFrame( ){
		try {
			socket.send(snd_DatagramPacket);
		} catch (IOException e) { e.printStackTrace();
		}
	}
	public void sendTimeoutPacket () {
	    try {
		    socket.send(snd_DatagramPacket);
	     } catch (IOException e) { e.printStackTrace();
	     }
	}	
	  public static BitSet fromByteArray(byte[] bytes) {
		    BitSet bits = new BitSet(bytes.length*8);
		    for (int i = 0; i < bytes.length * 8; i++) {
		      if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
		        bits.set(i);
		       // System.out.println("dsd");
		      }
		      
		    }
		    return bits;
		  }
		  
		  public static byte[] setbitstuffing(byte[] bytes)///flag ���� ��Ű�� x
		  {
			  BitSet bits = new BitSet();
			  bits =fromByteArray(bytes);
			  int check=0;
			  int index=0;
			  for(int i=0;i<bytes.length*8;i++)
			  {
				  if(bits.get(index)==true)
				  {
					  check++;
					  index++;
				  }
				  else
				  {
					  check=0;
					  index++;
				  }
				  
				  if(check==5)
				  {
					   BitSet temp =bits.get(index,bytes.length*8);
					   bits.set(index,false);
					   for(int j=0;j<(bytes.length*8-index+1);j++)
					   {
					   
						   bits.set(index+1+j,temp.get(j));
						   
					   }
					   
					   index++;
					   check=0;
					   
				  }
			  }
			  
			 byte[] temp2 = bits.toByteArray();
			 byte[] temp3 = changebyteorder(temp2);
			 
			 
			  return temp3;
		  }
		  
		  public static byte[] clearbitstuffing(byte[] bytes)
		  {
			  BitSet bits = new BitSet();
			  bits =fromByteArray(bytes);
			  int check=0;
			  int index=0;
			  for(int i=0;i<bytes.length*8;i++)
			  {
				  if(bits.get(index)==true)
				  {
					  check++;
					  index++;
				  }
				  else
				  {
					  check=0;
					  index++;
				  }
				  
				  if(check==5)
				  {
					   BitSet temp =bits.get(index+1,bytes.length*8);
					   //bits.set(index,false);
					   for(int j=0;j<(bytes.length*8-index);j++)
					   {
					   
						   bits.set(index+j,temp.get(j));
						   
					   }
					   
					   index++;
					   check=0;
					   
				  }
			  }
			  
			 byte[] temp2 = bits.toByteArray();
			 byte[] temp3 = changebyteorder(temp2);
			 
			  return temp3;
		  }

		  public static byte[] changebyteorder(byte[] bytes)
		  {
			  byte[] temp=new byte[bytes.length];
			  for(int i=0;i<bytes.length;i++)
			  {
				  temp[i]=bytes[bytes.length-i-1];
			  }
			  
			  return temp;
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
					DatagramPacket snd = MakeFrame(a);
					snd_DatagramPacket = snd;
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
