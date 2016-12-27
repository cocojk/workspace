
/**
 * Write a description of class ChatJin here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import java.io.*; 
import java.net.*; 

public class UDPChatting { 
	final static int MAXBUFFER = 512;
	static RcvThread rcThread; 
	public static DatagramSocket socket;
	public static InetAddress remoteaddr;
	public InetAddress myinetaddr;
	public static int remoteport=0, myport=0; 

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
			if(myport==0) {socket = new DatagramSocket();} //client
			else 		 {socket = new DatagramSocket(myport);} //server
            System.out.println("Datagram my address on "+socket.getLocalAddress().getHostAddress()+"my port "+socket.getLocalPort());

			rcThread = new RcvThread(socket);
			rcThread.start();
			
			DatagramPacket send_packet;// �۽ſ� �����ͱ׷� ��Ŷ
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Input Data : ");
			    
			
			while (true) {
				// Ű���� �Է� �б�
				
				
				String data = br.readLine();
				if (data.length() == 0){ // no char carriage return 
					System.out.println("grace out call");
					break;
				} // else System.out.println("read line="+data);
				byte buffer[] = new byte[512];
				buffer = data.getBytes();// ��Ʈ���� ����Ʈ �迭�� �ٲ�
				// ������ �۽�
				if((remoteaddr!=null)) {
					send_packet =new DatagramPacket(buffer,buffer.length,remoteaddr,remoteport) /* Fill in the Blank */ ;
					socket.send(send_packet); //(/* Fill in the Blank */)
				} else   System.out.println("Server mode: unable to send until receive packet");
			}
			
		} catch(IOException e) {
			System.out.println(e);
		}
			rcThread.graceout(); // grace exit of Receive Thread 
			System.out.println("grace out called");
			socket.close();
	}
}

