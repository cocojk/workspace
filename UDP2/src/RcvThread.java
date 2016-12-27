//import java.lang.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class RcvThread extends Thread {
	DatagramSocket socket;
	boolean	sem=true;
	DatagramPacket rcv_packet;// ���ſ� �����ͱ׷� ��Ŷ
	
	RcvThread (DatagramSocket s) {
		socket = s;
	}	
	public void run() {
		while (sem) {
			byte buff[] = new byte[100];
			rcv_packet =new DatagramPacket(buff,buff.length);/* Fill in the Blank by using buff[]  */		
			try {
		       socket.receive(rcv_packet); /* Fill in the Blank */;  
		       UDPChatting.remoteport = rcv_packet.getPort();    // ������ ���Ͽ� ���� ������ ����
		       UDPChatting.remoteaddr = rcv_packet.getAddress(); // ������ ���Ͽ� ���� ������ ����
			} catch(IOException e) {
				System.out.println("Thread exception "+e);
			}
			   String result = new String(buff);
			   System.out.println("\n Receive Data : " + result); 
			   System.out.print("Input Data : ");
				
		}
		
		System.out.println("grace out");
	}
	public void graceout(){
		sem=false;
	}
			
} // end ReceiverThread class
