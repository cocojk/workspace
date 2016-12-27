
// 파일명: UDPMyEcho.java
import java.net.*;
import java.io.*;
public class UDPMyEcho {
final static int MAXBUFFER = 512;
	public static void main(String[] args) {
		int time=0;
		
		
		
		if (args.length < 2) {
			System.out.println("사용법: java UDPMyEcho localhost port (-t timeouttime)");
			System.exit(0);
		}
		
		byte buffer[] = new byte[MAXBUFFER];
		
		int port = Integer.parseInt(args[1]);
		try {
		
			InetAddress inetaddr = InetAddress.getByName(args[0]);
			DatagramSocket socket = new DatagramSocket()/* Fill in the blank */;
			DatagramPacket send_packet;// 송신용 데이터그램 패킷
			DatagramPacket recv_packet;// 수신용 데이터그램 패킷
		
			socket.setSoTimeout(5000);
				for(int i=0;i<args.length;i++)
				{
					if(args[i].equals("-t"))
					{
						time = Integer.parseInt(args[i+1]);
						socket.setSoTimeout(time);
						break;
					}
					
				}
				
			
			
			BufferedReader br = new BufferedReader(new 
			InputStreamReader(System.in));
			String data =null;
			int count=0;
				// 키보드 입력 읽기
				System.out.print("Input Data : ");

				
				while(true)
				{
				
					if(count ==0)
					{
						data = br.readLine();
						
					if (data.length() == 0)
					break;
				
				buffer = data.getBytes();// 스트링을 바이트 배열로 바꿈
				
					}
					
					// 데이터 송신
				send_packet = new DatagramPacket(buffer,buffer.length,inetaddr,port); /* Fill in the blank */;
				if(count !=0)
				{
					String temp = new String(buffer);
				System.out.println(temp +" resend!");
				}
				
				socket.send (send_packet);
				// 에코 데이터 수신
				try
				{
				recv_packet = new DatagramPacket(buffer,buffer.length); /* Fill in the blank */;
				socket.receive (recv_packet);
				int temp1 =recv_packet.getPort();
				InetAddress temp2 =recv_packet.getAddress();
				System.out.println("recv port : "+String.valueOf(temp1)+ "recv IP :" +temp2.getHostAddress());
				count =0;
				String result = new String(buffer);
				System.out.println("Echo Data : " + result);
			
				System.out.print("Input Data : ");
			
				}
				catch (SocketTimeoutException e)
				{
				
					System.out.println("timeout occur resend !!");
				count++;
				}
				// 화면 출력
				}
			
			socket.close();
		} catch(UnknownHostException ex) {
			System.out.println("Error in the host address ");
		} catch(IOException e) {
			System.out.println(e);
		}
	}
}
