/**
 * Write a description of class ChatJin here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import java.io.*; 
import java.net.*; 

/*
 * ���� ������� sndThread, rcvThread, Signaling, Timer �� ����
 * - arg(1): Server mode�� ���� port�� ����, arg(2) Server IP �ּҿ� ���� port�� ������
 *   arg(3)�� ��� �����ּҿ� ��Ʈ ���� �ܿ� maxwin ũ�⸦ ������ �� ��, arg (0): ������,
 * - key board ���� �Է� ���� ��Ʈ���� ��Ŷ���� ����� sndThread�� ���� ����
 * 
 */
public class RDT {
	final static int MAXBUFFER = 512;
	final static int MAXSIZE =2;
	//static int keyinposition =0;
	static SndThread sndThread; 
	static RcvThread rcThread; 
	public static DatagramSocket socket;  // main socket 
	public static Signaling inputsignal = new Signaling();  // signaling Interface
	public static Signaling sndsignal = new Signaling();
	public static Timeout tclick = new Timeout(); // Timeout Interface

	public static void main(String[] args) {
		int port=0;
		///////////
		//////////
		int mode =0; // mode=1: server mode, mode=2: client mode
		InetAddress addr=null;
		if (args.length == 0) {
				System.out.println("����: java UDPChatting localhost port or  java UDPChatting myport");
				System.exit(0);
		} else if(args.length == 1){
				// server mode without sending first: wait for an incoming message
			port = Integer.parseInt(args[0]); //server mode
			mode=1;//server mode
			SndThread.mode=1;
			RcvThread.mode=1;
		} else if(args.length == 2){				// client mode
				mode=2;//Client mode
				SndThread.mode=2;
				RcvThread.mode=2;
				port = Integer.parseInt(args[1]);
			try {
				addr = InetAddress.getByName(args[0]);
				//temp = addr.getAddress();
				//System.out.format("address byte : %d \n",temp.length);
			} catch (UnknownHostException e) { 
				e.printStackTrace();
			}
		} else {
			System.out.println("Mode Error ");
		}
		
		
		try {
			if(mode==1) socket = new DatagramSocket(port); //Server mode
			else {socket = new DatagramSocket(); } // Client mode
			
            // DatagramPacket recv_packet;// ���ſ� �����ͱ׷� ��Ŷ
			sndThread = new SndThread(socket,inputsignal,sndsignal, tclick);
			sndThread.start();
			rcThread = new RcvThread(socket,sndsignal,sndThread);
			rcThread.start();
			if(mode>1) { // set_remote_address_port(addr, port} if needed;
				sndThread.remoteinetaddr=addr;
				sndThread.remoteport=port;
			}
            System.out.println("Datagram my address on "+socket.getLocalAddress().getHostAddress()+" my port "+socket.getLocalPort());
			
				// Ű���� �Է� �б�
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			    
			/* <��� ���� ������ ���鼭 keyboard���� �Էµ� �����Ͱ� �ִ��� üũ ��>
			 * ����: keyboard���� �Էµ� �����͸� �۽��� �� �ֵ��� �۽� �����忡�� ������, �۽� �����尡 �������� �ñ׳��� ������
			 * ��, ���ϸ� ģ ��� ���α׷��� ������
			 */	
			if(mode!=1)
			{
				System.out.print("Input Data : ");
				
			}
			
			while (true) {
				 String data = br.readLine();
				if (data.length() == 0){ // no char carriage return 
					System.out.println("grace out call");
					break;
				} else if(sndThread.remoteinetaddr!=null) {  // Valid data
			
					//System.out.println("sndthread ");
					sndThread.PutData(data); 
				   sndsignal.sendkeynotifying(); //sndThread.sendkey(RDTbuffer.key_data+"\0");  /* Key board type to be sent through Datagram Socket*/
				   //keyinposition=(keyinposition+1)%MAXSIZE;// update key in position within the WIndow (winsize<16)
					inputsignal.waiting();
					//System.out.println("wakeup keyboard");
			
				} 
				if(sndThread.remoteinetaddr==null)
				System.out.println("server mode waiting for incoming packet");
			}

			// ���α׷� ������ ���� ���� ó��
			rcThread.graceout(); // grace exit of Receive Thread 
			socket.close(); // close socket
			System.exit(0); //p.graceout();
			// System.out.println("grace out called");
		} catch(UnknownHostException ex) {
			System.out.println("Error in the host address ");
		} catch(IOException e) {
			System.out.println(e);
		}
	}
}


