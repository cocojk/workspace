
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author HYU
 */
public class ChatClientHandler implements Runnable {
    private Socket connection;
    private BufferedReader reader;
    private PrintWriter writer;
    private ChatServer server;
    private String myName;
    private boolean loginaccept =true; // if true -- correct login / false -- in correct login
    
    public ChatClientHandler(Socket connection, ChatServer sv) {
        this.connection = connection;
        this.server = sv;
        
        try {
            reader =
                    new BufferedReader(
                    new InputStreamReader(
                    connection.getInputStream()));

            writer =
                    new PrintWriter(
                    new OutputStreamWriter(
                    connection.getOutputStream()));
            login();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    public void login() {
    
    	int logincheck =0; //1 -- correct login , 0 -- not correct login
    	String login=new String();
		try {
			login = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	for(int i=0;i<ChatServer.CLIENTNUM;i++)
    	{
    		if((ChatServer.id[i].equals(login))&&(ChatServer.statelogin[i]==false))
    		{
    		ChatServer.statelogin[i]=true;
    		writer.println("connect server\n");	
    		writer.flush();
    		Scanner scan =new Scanner(login);
    		
    		scan.useDelimiter("/");
    		myName =scan.next();
    		logincheck=1; //if correct login, set 1
    				
    		
    		}
    		
    	}
    	if(logincheck==0) //send not correct login!
    	{
    		writer.println("invaild id/password or already connect \n");
    		writer.flush();
    		loginaccept =false; // distinguish correct & incorrect client true -correct false - incorrect
    	}
    	
    
    }

    public void run() {
        try {
            while(loginaccept) {//check this client is correct & incorrect
                String msg = reader.readLine();
                server.broadcast(myName + ":" + msg);
            }
            //System.out.println("Received:"+clientName);
            //writer.println("Hello " + clientName);
            //writer.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            server.removeClient(this);
        }
    }
    
    void sendMessage(String msg) {
        writer.println(msg);
        writer.flush();
    }
}
