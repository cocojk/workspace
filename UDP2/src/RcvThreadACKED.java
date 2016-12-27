//import java.lang.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

class RcvThreadACKED extends Thread {
	DatagramSocket socket;
	boolean	sem=true;
	DatagramPacket rcv_packet;// ���ſ� �����ͱ׷� ��Ŷ
	Signaling p;
	public static boolean inputcheck = true;
	Robot jkrb2 ;
	RcvThreadACKED (DatagramSocket s, Signaling pp) {
		socket = s;
		p=pp; // should be defined (���� �ȵǸ� null point excetion �߻�)
	}	
	public void run() {
			byte buff[] = new byte[100];
			try {
				jkrb2 = new Robot();
			} catch (AWTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			rcv_packet = new DatagramPacket(buff,buff.length)/* Fill in the Blank */;
			
		while (sem) {
			try {
		       socket.receive(rcv_packet);  
		       UDPChattingACKED.remoteport= rcv_packet.getPort() ;   // ������ ���Ͽ� ���� ������ ����
		       UDPChattingACKED.remoteaddr=rcv_packet.getAddress(); // ������ ���Ͽ� ���� ������ ����
			} catch(IOException e) {
				System.out.println("Thread exception "+e);
			}
			
			   String result = new String(rcv_packet.getData(),0,rcv_packet.getLength());
			   //System.out.flush();
			   System.out.println("\n Receive Data : " + result); 
				BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
			
			   System.out.print("Input Data : ");
			   
				
			   p.ACKnotifying();
			   /* Fill in the Blank with Ack Notifying*/ /* ACKED */
		}
		
		System.out.println("grace out");
	}
	public void graceout(){
		sem=false;
	}
			
} // end ReceiverThread class
