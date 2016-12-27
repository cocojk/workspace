


	/**
	 * Write a description of class ChatJin here.
	 * 
	 * @author (your name) 
	 * @version (a version number or a date)
	 */

	import java.io.*; 
import java.net.*; 
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.*;
	public class UDPChattingACKED {
		final static int MAXBUFFER = 512;
		static RcvThreadACKED rcThread; 
		public static DatagramSocket socket;
		public static InetAddress remoteaddr;
		public InetAddress myinetaddr;
		public static int remoteport=0, myport=0; 
		static Signaling p= new Signaling(); // Object�� �����ؾ� waiting/notify�� ��

		public static void main(String[] args) {
				if(args.length == 2){
					remoteport = Integer.parseInt(args[1]);
					try {
						remoteaddr = InetAddress.getByName(args[0]);
					} catch (UnknownHostException e) {
						System.out.println("Error on port"+remoteport);e.printStackTrace();
					}
				} else if(args.length == 1){
					// server mode without sending first: wait for an incoming message
					myport = Integer.parseInt(args[0]); //server mode
				} else {
					System.out.println("����: java UDPChatting localhost port or port");
					System.exit(0);
				}
				
			try {
				if(myport==0) {socket = new DatagramSocket();}
				else 		 {socket = new DatagramSocket(myport);}
	            System.out.println("Datagram my address on "+socket.getLocalAddress().getHostAddress()+"my port "+socket.getLocalPort());

				rcThread = new RcvThreadACKED(socket, p);
				rcThread.start();
				
				DatagramPacket send_packet;// �۽ſ� �����ͱ׷� ��Ŷ
				//InputStreamReader  input = new InputStreamReader(System.in);
				Robot jkrb = new Robot();
				System.out.print("Input Data : ");
			    
				
				
				
				while (true) {
					// Ű���� �Է� �б�
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			///		Scanner scan = new Scanner(System.in);
				
			//		while((br.read())!=-1)
//					br.
						
				
					
					
					
					
					//System.out.print("Input Data : ");
					String data;
						data = br.readLine();
					if (data.length() == 0){ // no char carriage return 
						System.out.println("grace out call");
						break;
					} // else System.out.println("read line="+data);
				
					byte buffer[] = new byte[512];
					buffer = data.getBytes();// ��Ʈ���� ����Ʈ �迭�� �ٲ�
					
					// ������ �۽�
					if((remoteaddr!=null)) {
							send_packet = new DatagramPacket(buffer,buffer.length,remoteaddr,remoteport)/* Fill in the Blank */;
						socket.send(send_packet) /* Fill in the Blank */;
				
						
						p.waitingACK();
				//System.in.wait();
				//System.out.flush();
		
			
				RcvThreadACKED.inputcheck=true;
				//System.in.
						/* Fill in the Blank with waiting ACK */  /* ACKED */
					} else   System.out.println("Server mode: unable to send until receive packet");
				}
				
			} catch(IOException e) {
				System.out.println(e);
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
				rcThread.graceout(); // grace exit of Receive Thread 
				System.out.println("grace out called");
				socket.close();
		
		}
		
	}
	
