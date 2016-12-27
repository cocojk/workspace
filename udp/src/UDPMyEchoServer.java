
// 파일명 : UDPMyEchoServer.java
import java.net.*;
import java.io.*;
public class UDPMyEchoServer {
	final int MAXBUFFER = 512;
	public static void main (String[] args) {
		if(args.length!=1)
		{
			System.out.println("사용법: java UDPMyEchoServer port1 ");
			System.exit(0);		
		}
		
		int arg_port = Integer.parseInt(args[0]);// 포트 번호
		new UDPMyEchoServer().work(arg_port);
	}
	void work(int arg_port) {
		int port = arg_port;
		try {
			/*  
			 * UDP Socket 생성 (UDP server Socket)
			*/
			DatagramSocket Dsocket = new DatagramSocket(port); /* Fill in the blank */; 
			
			//int port1 =Dsocket.getPort();
			int port2 =Dsocket.getLocalPort();	
			InetAddress address1 =InetAddress.getLocalHost();
			//InetAddress address2 = Dsocket.getInetAddress();
			
			System.out.println("my Localport : " +String.valueOf(port2)+"my LocalIP : "  +address1.getHostAddress());
	
	//		System.out.println(String.valueOf(port)+"port open");
			byte buffer[] = new byte[MAXBUFFER];
//			System.out.println ("Running the UDP Echo Server...");
			while (true) {
				/* 
				 * UDP Packet 생성 (UDP server Socket으로부터 데이터 수신을 위한 UDP packet 생성)
				 * UDP Server Socket에서 UDP packet을 받기 위한 대기
				*/
				DatagramPacket recv_packet = new DatagramPacket(buffer,buffer.length);/* Fill in the blank */;  
				Dsocket.receive(recv_packet); 
				
				// 에코 데이터 생성을 위해 수신된 UDP packet으로부터 Echo을 위한 송신 UDP packet 생성
				DatagramPacket send_packet = new DatagramPacket(recv_packet.getData(),recv_packet.getLength(),recv_packet.getAddress(),recv_packet.getPort());/* Fill in the blank */;
				Dsocket.send (send_packet);
			}
		} catch(IOException e) {
			System.out.println(e);
		}
	}
}
