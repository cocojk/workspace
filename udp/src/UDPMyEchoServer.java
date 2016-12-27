
// ���ϸ� : UDPMyEchoServer.java
import java.net.*;
import java.io.*;
public class UDPMyEchoServer {
	final int MAXBUFFER = 512;
	public static void main (String[] args) {
		if(args.length!=1)
		{
			System.out.println("����: java UDPMyEchoServer port1 ");
			System.exit(0);		
		}
		
		int arg_port = Integer.parseInt(args[0]);// ��Ʈ ��ȣ
		new UDPMyEchoServer().work(arg_port);
	}
	void work(int arg_port) {
		int port = arg_port;
		try {
			/*  
			 * UDP Socket ���� (UDP server Socket)
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
				 * UDP Packet ���� (UDP server Socket���κ��� ������ ������ ���� UDP packet ����)
				 * UDP Server Socket���� UDP packet�� �ޱ� ���� ���
				*/
				DatagramPacket recv_packet = new DatagramPacket(buffer,buffer.length);/* Fill in the blank */;  
				Dsocket.receive(recv_packet); 
				
				// ���� ������ ������ ���� ���ŵ� UDP packet���κ��� Echo�� ���� �۽� UDP packet ����
				DatagramPacket send_packet = new DatagramPacket(recv_packet.getData(),recv_packet.getLength(),recv_packet.getAddress(),recv_packet.getPort());/* Fill in the blank */;
				Dsocket.send (send_packet);
			}
		} catch(IOException e) {
			System.out.println(e);
		}
	}
}
