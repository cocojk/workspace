import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class receiveFrame extends Thread {
	DatagramSocket Dsocket;
	receiveFrame (DatagramSocket s) {
		Dsocket = s;
	}	
	public void run() {
			while (true) {
				// 데이터 수신
				// 에코 데이터 생성 및 송신
				byte buffer[] = new byte[512];
		try {
			
			
			
				DatagramPacket recv_packet = new DatagramPacket(buffer,buffer.length);/* Fill in the blank */ ;
				Dsocket.receive(recv_packet); /* Fill in the blank */ 
			System.out.println(String.valueOf(Dsocket.getLocalPort())+"port receive message");
				
	
				DatagramPacket send_packet = new DatagramPacket(buffer,buffer.length,recv_packet.getAddress(),recv_packet.getPort());/* Fill in the blank */ ;
				Dsocket.send (send_packet);
		
		} catch(IOException e) {
			System.out.println(e);
		}
			}
	}
} // end ReceiverThread class
