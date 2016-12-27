//import java.lang.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.BitSet;

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
	static final int HEADER_FLAG=0, HEADER_ADDRESS=1, HEADER_CONTROL=2,HEADER_INFORMATION=3; // header length 	
	private static final int MAXBUFFER = 512, MAXSIZE=16; //, WINSIZE=8;
	public static int rcvseqNo=0,rcvackNo=0,rcvlength=0,current_ackNo=0;
	public static boolean rcvflag=true;
	public static int ackseqNo=0,ackackNo=0,ackflag=0x12,acklength=0;
	public byte[] rcv_data, CRCchecksum=new byte[4];
	public byte[] rcv_info;
	boolean	cond=true, DEBUG=false;
	static int mode,rcvmode;
	byte[] content ;
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
		System.out.println(rcvl);
		for(int i=0;i<4;i++) rcvd[i+rcvl+2] = 0x00; // reset CRC
		if(true) {System.out.print("rcvCRC packet");for(int i=0;i<(rcvl+2);i++) System.out.print(" "+Byte.toString(rcvd[i]));System.out.println("");}
		tempCRC=sndThread.getCRC(rcvd,rcvl+2);  // rcvlenght= data length including header
		if(true) {System.out.print("rcv CRC");for(int i=0;i<4;i++) System.out.print(" "+Byte.toString(tempCRC[i]));System.out.println("");}
		boolean result=true; //exact matching
		
		for(int i=0;i<4;i++) if(CRCchecksum[i]!=tempCRC[i]) {result=false; break;} // reset CRC
		return result;
	}
	void sendAck() {
		 //*  -�Է�: ack No, (���� ���� ��Ŷ�� seq No�� �������� ���� ���� seq No�� ack No ��)
		 //*  -����: ACK ��Ŷ ����(seq No, ack No, flags, length) ���� �� �۽��ڿ��� ������
		 //*  -���: Ack ��Ŷ�� ������ 
    		byte[] tempCRC = new byte[4];
			BitSet tempbit =new BitSet(8);
			byte[] tempbyte =new byte[8];
			tempbit.set(1,8);
			byte[] flag = tempbit.toByteArray();
    		// ACK size without data
			tempbyte[HEADER_FLAG]=flag[0];
			tempbyte[HEADER_ADDRESS]=makeaddress()[0]; // ackNo could be updated by the RcvThread remotely
			tempbyte[HEADER_CONTROL]=makecontrol()[0]; // ACK Packet flag==1
			for(int i=0;i<4;i++) tempbyte[i+HEADER_CONTROL+1] = 0x00;// reset 
			
			byte[] temp=new byte[2];
			temp[0]=tempbyte[1];
			temp[1]=tempbyte[2];
			
			tempCRC = sndThread.getCRC(temp,2);
			for(int i=0;i<4;i++) tempbyte[i+HEADER_CONTROL+1] = tempCRC[i]; // reset 
			byte[] afterstuffing=setbitstuffing(tempbyte);
			sndAck = new DatagramPacket (afterstuffing,afterstuffing.length ,sndThread.remoteinetaddr, sndThread.remoteport);
			try {
				socket.send(sndAck);
			} catch (IOException e) { e.printStackTrace();
			}
	}
	public byte[] makecontrol()
	{
		BitSet temp = new BitSet(8);
		temp.set(8);
		temp.set(3);
		temp.set(4,6);
		if(ackseqNo==1)
		{
			temp.set(0);
		}
		
		return temp.toByteArray();
	}
	
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
			   byte[] flag=new byte[1];
			   flag[0]=rcv_data[HEADER_FLAG];
			   rcvflag=checkflag(flag);
			   byte[] temp =new byte[rcv_data.length-1];
			   for(int i=0;i<rcv_data.length-1;i++)
			   {
				   temp[i]=rcv_data[i+1];
			   }
			   content = getframecontent(temp); //�� �� flag����
			   rcv_info =new byte[content.length-6];
			   rcvlength=content.length-6;
			   byte[] address = new byte[1];
			   address[0]=content[0];
			   rcvmode = checkaddress(address);
			   byte[] control = new byte[1];
			   control[0]=content[1];
			   checkcontrol(control);
			   int temp2=0;
			   for(int i=2;i<content.length-4;i++)
			   {
				   rcv_info[temp2]=content[i];
				   temp2++;
			   }
			   
			   for(int i=0;i<4;i++) CRCchecksum[i] = content[content.length-4+i];
	}
	
	void checkcontrol(byte[] bytes)
	{
		BitSet temp =fromByteArray(bytes);
		if(temp.get(8)==true)
		{
			//rcvlength=0;/////////ack
		if(temp.get(0)==true)
		{
			rcvseqNo=1;
		}
		else
		{
			rcvseqNo=0;
		}
		
		}
		else
		{
			//rcvlength=1;////////data
	
			if(temp.get(4)==true)
			{
				rcvseqNo=1;
			}
			else
			{
				rcvseqNo=0;
			}
			
		}
		
		
		
	}
	
	byte[] getframecontent(byte[] bytes)
	{
		
	
		BitSet bits =fromByteArray(bytes);	
		int count=0;
		BitSet temp=new BitSet();
		for(int i=0;i<bytes.length*8-1;i++)
		{
			if(bits.get(i)==true)
			{
				count++;
				System.out.print("1");
			}
			else
			{
				count=0;
				System.out.print("0");
			}
			if(count==6)
			{
				//System.out.print(count);
				temp=bits.get(i+2,bytes.length*8);
				break;
			}
		}
		
		byte[] tempbyte=changebyteorder(temp.toByteArray());
		
		byte[] tempbyte2=clearbitstuffing(tempbyte);
		BitSet tt=fromByteArray(tempbyte2);
		String ttt="";
		for(int i=0;i<tempbyte2.length*8;i++)
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
		System.out.println(tempbyte2.length);		
		
		System.out.println(ttt);
		
	
		return tempbyte2;
	}
	
	
	
	
	int checkaddress(byte[] bytes)
	{
		BitSet bits=new BitSet(8);
		bits.set(0);
		BitSet input=fromByteArray(bytes);
		int count=0;
		for(int i=0;i<8;i++)
		{
			if(bits.get(i)==input.get(i))
			{
			count++;
			}
		}
		if(count==8)
			return 2;
		
		BitSet bits2=new BitSet(8);
		bits2.set(0,2);
		count=0;
		for(int i=0;i<8;i++)
		{
			if(bits.get(i)==input.get(i))
			{
			count++;
			}
		}
		if(count==8)
			return 1;
		
		return 0;
		
	}
	boolean checkflag(byte[] bytes)
	{
		//boolean temp=true;
		BitSet bits=new BitSet(8);
		bits.set(1,8);
		BitSet input=fromByteArray(bytes);
		for(int i=0;i<8;i++)
		{
			if(bits.get(i)!=input.get(i))
			{
				return false;
			}
		}
		
		return true;
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
			if(corrupted(content,rcvlength)) { // ���ŵ� ��Ŷ�� ������ ������ Ȯ�� (true = not corrupted, false=corruted) 
				// ���ŵ� �����͸� ������� Header�� �����ϰ� ��� ������ �̿��Ͽ� �����͸� ������ ������, ACK�� ������ ������ Ȯ�� 
					// ������ ���� ���, ȭ�鿡 ����ϰ� ACK ��Ŷ�� ����� ��������
			if((rcvlength!=0)&&(rcvflag==true)) ///data frame
			{
				if(rcvseqNo==ackseqNo) // seq �´� frame ����
				{
					System.out.format("%d frame receive !\n",rcvseqNo);
					ackseqNo=(ackseqNo+1)%2;
					SndThread.ackNo=ackseqNo;
					String temp3 = new String(rcv_info);
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
			else if((rcvlength==0)&&(rcvflag==true)) //ack frame
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
			
			 System.out.println("start flag not true ");	
			}
			
			
			
			} else System.out.println("CRC Corrupted : CRC checksum");// not currupted
		}
		System.out.println("grace out");
	}
	
	public void graceout(){
		cond=false;
	}
			
} // end ReceiverThread class
