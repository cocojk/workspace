
// 파일명 : UDPMyEchoServer.java
import java.io.*;
import java.net.*;
public class UDPMyEchoServerMultiThread {
	final int MAXBUFFER = 512;
			private static int a;
	static DatagramSocket Dsocket;
	public static void main (String[] args) {
		if(args.length<1)
		{
			System.out.println("사용법: java UDPMyEchoServer port1 port2 ....");
			System.exit(0);		
		}
		else
		{
			int i=0;
			for(i=0;i<args.length;i++)
			{
				try
				{
		int arg_port = Integer.parseInt(args[i]);// 포트 번호
		work(arg_port);
				}
				catch(NullPointerException e)
				{
					System.err.println("nullpointerexception occur in port");
				}
			}
		}	
		}

	static void work(int port) {
			try {
		
				
				Dsocket = new DatagramSocket(port);/* Fill in the blank */ ;
			//	Dsocket.
			
				int port1 =Dsocket.getPort();
				int port2 =Dsocket.getLocalPort();	
				InetAddress address1 =InetAddress.getLocalHost();
				InetAddress address2 = Dsocket.getInetAddress();
				
				System.out.println("my Localport : " +String.valueOf(port2)+"my LocalIP : "  +address1.getHostAddress());
			/*	try
				{
				System.out.println("port :" + String.valueOf(port1)+"InetAddress" + address2.getHostAddress());
				}
				catch(NullPointerException e)
				{

					System.err.println("nullpointerexception occur in port &inetaddress");
					
					
				}*/
				Thread r1 = new Thread(new receiveFrame(Dsocket));/* Fill in the blank */ ;
				r1.start(); 
			
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
}
