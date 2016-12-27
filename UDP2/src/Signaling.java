
	/*  Signaling for Thread (쓰래드 간 시그널 전송) Thread.waiting() - Thread.notify() 
	 *  송신 쓰래드가 보낼 것이 없을 경우 wait 하면서 송신 패킷이 있을 때까지 기다림.
	 *  wait for (keyboard input, ack packet, timeout)
	 *  notifying when (keyboard input to be sent(seqNo), ack packet received(ackNo), timeout expired(seq No)) 
	 *  return notify_state; 0: undefined, i=1:send key, i=2: receive ack, i=3: timeout 
	 */
public class Signaling {
	public static boolean ACKNOTIFY = false;
	public static int notify_state=0,timeouttaskNo=0,rcvackNo=0,sndseqNo=0;

	public synchronized void ACKnotifying() { // System.out.println("Graceful exit procedure"); 
		ACKNOTIFY = true;
		notifyAll();
	} 
	public synchronized void waitingACK() {  // System.out.println("Waiting for the event: ");
		ACKNOTIFY = false; 
		try{
		wait();
		 } 
		catch(InterruptedException e) { 
			System.out.println("InterruptedException caught"); 
		 } 
		}
}
