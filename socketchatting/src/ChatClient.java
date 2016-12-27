
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author HYU
 */
public class ChatClient {
    BufferedReader reader;
    PrintWriter writer;
    
    ChatClient(String[] args) {
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        Socket connection = null;
        try {
            connection = new Socket(hostname, port);
        } catch (IOException ioe) {
            System.err.println("Connection failed");
            return;
        }
        
        

        try {
            reader =
                    new BufferedReader(
                    new InputStreamReader(
                    connection.getInputStream()));
            writer =
                    new PrintWriter(
                    new OutputStreamWriter(
                    connection.getOutputStream()));
            writer.println(args[2]);  // send id/password first !
                      
        } catch (IOException ioe1) {
            ioe1.printStackTrace();
        }
        JFrame frame = new JFrame ("Chatting");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ChattingPanel panel = new ChattingPanel(reader,writer);
		frame.getContentPane().add(panel);
		
		frame.setSize(new Dimension(800,400));
		frame.pack();
		frame.setVisible(true);
		
        
        
    }
   
    public static void main(String[] args) {
    	if(args.length!=3)
    	{
    		System.out.println("»ç¿ë¹ý: java ChatClient ipaddr port id/password ");
			System.exit(0);		
	
    	}
    	
    	new ChatClient(args);
    }
}
