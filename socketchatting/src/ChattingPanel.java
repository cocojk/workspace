
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


class ChattingPanel extends JPanel implements Runnable // gui class
{
	private JLabel inputLabel;
	private JTextField inputtext;
	private JTextArea JText;
	static String messageString; 
    Thread writeThread;
    Thread readThread;
    BufferedReader reader;
    PrintWriter writer;
    
	
    
    ChattingPanel(BufferedReader input1,PrintWriter input2)
	{
    	reader =input1;
    	writer =input2;
        readThread = new Thread(this);
        writeThread = new Thread(this);
       readThread.start();
        
        
        
    	inputLabel = new JLabel("Enter message");

		
		JText = new JTextArea(15,40);
		JScrollPane scrollPane = new JScrollPane(JText);
		inputtext = new JTextField (30);
		inputtext.addActionListener(new tempListener());
		inputtext.setSize(new Dimension(100,10));
		add(inputLabel);
		add(inputtext);
		add(scrollPane);
	

		setPreferredSize (new Dimension(600,400));
		setBackground (Color.gray);
	}
	
	private class tempListener implements ActionListener, Runnable
	{

		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
		messageString = inputtext.getText();
	inputtext.setText("");
	   // 쓰레드 동작
	   writeThread = new Thread(this);
	   writeThread.start();
	   
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Thread.currentThread()==writeThread) // write part !
			{
				
		    	//   System.out.println(messageString);		
		    		writer.println(messageString);
		    		writer.flush();
		    		//JText.append("usr : "+messageString+"\n");
		    		//JText.setCaretPosition(JText.getDocument().getLength());
				
				
			}

			
		}
		
	}


	public void run() {
		
		if(Thread.currentThread()==readThread) //read part !
		{
			String msg;
			
		    	while(true)
		        {
		    	try {
					msg =reader.readLine();
					JText.append(msg+"\n"); 
		    		JText.setCaretPosition(JText.getDocument().getLength());
				
		    	} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	
		    		
		        }
		   
			
			
		}
		
		}

	
}


