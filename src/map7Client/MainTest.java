package map7Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import utility.Keyboard;


public class MainTest {

	/**
	 * @param args
	 */
	private final ObjectOutputStream out;
	private final ObjectInputStream in ; // stream con richieste del client
	
	
        public MainTest(String ip, int port) throws IOException{
		InetAddress addr = InetAddress.getByName(ip); //ip
		System.out.println("addr = " + addr);
		Socket socket = new Socket(addr, port); //Port
		System.out.println(socket);
		
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());	; // stream con richieste del client

		
		String answer="";
		
		int decision=0;
		do{
		
			System.out.println("Learn Regression Tree from data [1]");
			System.out.println("Load Regression Tree from archive [2]");
			decision=Keyboard.readInt();
		}while(!(decision==1) && !(decision ==2));
		
		String tableName="";
		System.out.println("File name:");
		tableName=Keyboard.readString();
		try{
		
		if(decision==1)
		{
			System.out.println("Starting data acquisition phase!");
			
			
			
			out.writeObject(0);
			out.writeObject(tableName);
			answer=in.readObject().toString();
			if(!answer.equals("OK")){
				System.out.println(answer);
				return;
			}
				
			
			
		
			System.out.println("Starting learning phase!");
			out.writeObject(1);
			
		
		}
		else
		{
			out.writeObject(2);
			out.writeObject(tableName);
			
		}
		
		answer=in.readObject().toString();
		if(!answer.equals("OK")){
			System.out.println(answer);
			return;
		}
			
		
		
		// .........
		
		char risp='y';
		
		do{
			out.writeObject(3);
			
			System.out.println("Starting prediction phase!");
			answer=in.readObject().toString();
		
			
			while(answer.equals("QUERY")){
				// Formualting query, reading answer
				answer=in.readObject().toString();
				System.out.println(answer);
				int path=Keyboard.readInt();
				out.writeObject(path);
				answer=in.readObject().toString();
			}
		
			if(answer.equals("OK"))
			{ // Reading prediction
				answer=in.readObject().toString();
				System.out.println("Predicted class:"+answer);
				
			}
			else //Printing error message
				System.out.println(answer);
			
		
			System.out.println("Would you repeat ? (y/n)");
			risp=Keyboard.readChar();
				
		}while (Character.toUpperCase(risp)=='Y');
		
		}
		catch(IOException | ClassNotFoundException e){
			System.out.println(e.toString());
			
		}
	}

}
