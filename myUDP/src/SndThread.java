import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.BitSet;

/*
 * packet을 보내는 Thread로 데이터그램 패킷과 관련된 모든 기능을 수행
 * 여기서는 시그널과 Timer에 연결된 부분을 관리 함.
 * 실지로 패킷을 보내고 받는 것은 SndThread 내에서 모두 처리 함.
 * - 패킷을 만들고 보냄 (MakeFrame (Header와 data), StoreFrame (재전송시 사용할  패킷을 버퍼에 저장), sendFrame(UDP 전송)
 * - ACK를 받은 경우 window 를 옮기고 Timeout인 경우 저장된 패킷을 재전
 */
public class SndThread extends Thread {
	static final int HEADER_FLAG=0, HEADER_ADDRESS=1, HEADER_CONTROL=2,HEADER_INFORMATION=3; // header length 
	static final int MAXBUFFER=512, MAXTIMEOUT=10,MAXSIZE=127;
	final static int EVENT_REQSND = 1, EVENT_RCVACK = 2, EVENT_TIMEOUTPACKET = 3;
	public static byte[] snd_data;
	DatagramSocket socket;
	DatagramPacket sndPacket;// 송신용 데이터그램 패킷
	public InetAddress myinetaddr, remoteinetaddr;
	public int myport=0, remoteport=0; 
	Signaling inputsignal;
	Signaling sndsignal;
	Timeout tout;
	static int mode=0;
	boolean DEBUG=false;
	public static int seqNo=0,ackNo=0,length=0;
	public DatagramPacket snd_DatagramPacket;// 송수신용 데이터그램 패킷 array
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
	 * keyboard 입력값을 버퍼에 저장해 놓음, (나중에 저장된 입력값을 사용해서 패킷을 만들어 보냄)
	 */
	public void PutData(String a) {
		key_in_data = (String)a;
	} 
	/* 
	 * 버퍼에 저장된 keyboard 입력값을 찾아 보냄
	 */
	public String GetData( ) {
		return key_in_data;
	}
	/* 
	 * UDP패킷 데이터  byte[] frame 에 대한 CRC32[4 byte]를 계산해서 return 함 
	 */
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
        
        if(DEBUG) {System.out.print("calculated CRC : "+new String(tempCRC));}
        
        return tempCRC;
    }

	/* 
	 *  keyboard 입력값을 가지고 ARQ의 해더와 데이터를 byte[] snd_data[seq] 구성하여 UDP 패킷 형태로  만들어 보
	 *  -입력: isn: 보낼 패킷이 가지는 seq No   - s: 사용자가 keyboard로 입력한 데이터 스트링 
	 *  -출력: DatagramPacket sndPacket 
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
	 *  패킷을 보내고 바로 폐기하는 것이 아니라 추후 재전송 시 사용하기 위해 따로 보관해 둠
	 *  snd_DatagramPacket[seq]seq 별로 전송된 패킷을 따로 보관함 (나중에 재전송 시 바로 사용). 
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
	 *  지정된 UDP 패킷을 보냄. 
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
		  
		  public static byte[] setbitstuffing(byte[] bytes)///flag 포함 시키면 x
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
	 * 2. ack received: sf timeout cancel if(sf < ack.no) (sf update 중요)로 교체
	 * 3. timeout received:  send packet array list (if (sf<i<sn), re-transmit array list
	 * 1. sendkey :  send packet array[sn], then (sn->sn+1);
	 *    buffer-> snd_packet array 에 저장 [재전송 시 다시 사용]
	 * send packet array[sn],  static timeout[] (일정회수 이상 반복 금지)
	 */	
	// seq no reset when? restart  seq=ack=len=0 flag=0xff 일 경우 가정 (따라서 첨음 패킷은 setting?)
	public void run() {
		while(true) { 		//  sndThread는 지속적으로 패킷 전송을 위해 무한 루프로 동작
			sndsignal.waiting(); // waiting until notifying & return wake up state 
			int kk = sndsignal.get_state(); if(DEBUG) System.out.println("notify "+kk);
			if(kk==EVENT_REQSND){
				// Keyboard 입력 데이터를 기반으로 Header와 데이터를 구성하고 UDP 패킷으로 만들어 보냄 
				// 입력: keyboard 입력 스트링, 
				// 동작: Header 및 데이터 구성 후 UDP 패킷으로 전송 그리고 재전송을 위한 타임아웃을 세팅해 놓음 
				 	String a = GetData();  // Key in data is stored in Keydata[sn]
					if(DEBUG) System.out.println("key in string ="+a+" sndseqNo="+seqNo); 
					DatagramPacket snd = MakeFrame(a);
					snd_DatagramPacket = snd;
					SendFrame(); // put DatagramPacket into snd_DatagramPacket[sn] array
				    tout.Timeoutset(0,1000,sndsignal);	// Timeout Start 
		
			} 
			
			if(kk == EVENT_RCVACK){ //ack Received move sf -> rcvackNo
				// 이전에 보낸 패킷에 대한 ACK 패킷을 받은 경우 (rcvThread에서 받음)
				// 동작: 패킷 송신 시 설정된 timeout를 제거하고 무한 재전송을 방지하기 위한 timeout limit를  초기화 함.
				tout.timeoutlimit = 0; // Packet transmission reset
				//for(int i=sf;i< Signaling.rcvackNo;i++) // if cumulative ack
			    tout.Timeoutcancel(0); 	//cancel timeoutTASK previous task 
			    inputsignal.keyboardnotifying();
				//sf=Signaling.rcvackNo; //update seq no.
			}
			
			if(kk==EVENT_TIMEOUTPACKET){
				// Timeout interrupt를 받은 경우 (주기적인 타이머가 세팅한 시간에 interrupt 받음)
				// 동작: 패킷을 재전송하고 다시  타임아웃을 세팅해 놓음, 또한 무한 재전송을 방지하기 위한 기능.
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
