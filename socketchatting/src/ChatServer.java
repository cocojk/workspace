
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;
/**
 *
 * @author HYU
 */
public class ChatServer {
    String[] args;
    LinkedList clientList;
    public static final int CLIENTNUM =3; //설정 사용자수 
    public static String id[] = new String [CLIENTNUM]; // id/password save
    public static boolean statelogin[] =new boolean[CLIENTNUM]; //boolean true - login, false - no login 
    public ChatServer(String[] args) {
        this.args = args;
        clientList = new LinkedList();
    }
    public void runServer() {
        int port = Integer.parseInt(args[0]);
        ServerSocket server;
        for(int i=0;i<CLIENTNUM;i++)
        {
        	statelogin[i]=false;
        }
        File readFile = new File("mypassword.txt");//
        
        try {
		Scanner	scan = new Scanner(readFile);
		int i=0;
		while(scan.hasNext()){
	        	id[i] = scan.nextLine(); // get id/password
	        	i++;
	        }
        
        } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
        	System.out.println("invaild file");
			e.printStackTrace();
		}
       
        
        
        try {
            server = new ServerSocket(port);
        } catch (IOException ioe) {
            System.err.println("Couldn't run server on port " + port);
            return;
        }

        while (true) {
            try {
                System.out.println("Waiting for Request...");
                Socket connection = server.accept();
                System.out.println("Request arrived...");
                ChatClientHandler handler =
                        new ChatClientHandler(connection, this);
                synchronized(clientList) {
                clientList.add(handler);
                }
                new Thread(handler).start();
            } catch (IOException ioe1) {
            }
        }
    }
    
    void broadcast(String message) {
        for(int i = 0; i < clientList.size(); i++) {
            ChatClientHandler client = (ChatClientHandler)clientList.get(i);
            client.sendMessage(message);
        }
    }
    
    void removeClient(ChatClientHandler deadClient) {
        synchronized(clientList) {
        clientList.remove(deadClient);                
        }
    }
            
    public static void main(String[] args) {
    	if(args.length!=1)
    	{
    		System.out.println("사용법: java ChatServer port ");
			System.exit(0);		
	
    	}
        ChatServer serv = new ChatServer(args);
        serv.runServer();
    }
}
