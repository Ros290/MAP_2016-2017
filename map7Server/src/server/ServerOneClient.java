package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import tree.RegressionTree;
import data.Data;
import exception.TrainingDataException;

class ServeOneClient extends Thread {
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
    
	RegressionTree tree;

	public ServeOneClient(Socket s) throws IOException {
		socket = s;
		in = new ObjectInputStream(socket.getInputStream());
	    out = new ObjectOutputStream(socket.getOutputStream());
		start();
	}
	
	protected static Object readObject(Socket socket) throws ClassNotFoundException, IOException
	{
		Object o;
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		o = in.readObject();
		return o;
	}
	
	protected static void writeObject(Socket socket, Object o) throws IOException
	{
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(o);
		out.flush();
	}

	/*public void run() 
	{
		System.out.println("Nuovo client connesso");
		String result = "";
		String nome = "";
		Object o;
		boolean flag = true;
		try 
		{
			while (flag) 
			{
				try
				{	
					writeObject(socket,true);
					String choice1 = "1. Learn Decision Tree :\n";
					String choice2 = "2. Save Decision Tree (in tree.dat) :\n";
					String choice3 = "3. Store Decision Tree (from tree.dat) :\n";
					String choice4 = "4. Use Decision Tree for Prediction :\n";
					String choice5 = "5. Exit :";
					writeObject (socket, result + choice1 + choice2 + choice3 + choice4 + choice5);
					int command = Integer.parseInt((String)readObject(socket));
					switch(command)
					{
					case 1: // LEARNING A REGRESSION TREE
						writeObject(socket,true);
						writeObject(socket,"Inserire il nome della tabella:");
						nome = (String) readObject (socket);
						Data trainingSet = new Data(nome);
						tree = new RegressionTree(trainingSet);
						result = new String (tree.printRules() + tree.printTree());
						break;
					case 2: // SERIALIZE THE CURRENT REGRESSION TREE ON A FILE
						if( tree != null)
						{
							writeObject (socket,true);
							writeObject(socket,"Inserire il nome del file su cui salvare l'albero:");
							nome = (String)readObject(socket);
							tree.salva(nome);
							result = new String ("Salvato\n");
						}
						else
							throw new TrainingDataException();
						break;
					case 3: // STORE THE REGRESSION TREE FROM FILE
						writeObject(socket,true);
						writeObject(socket,"Inserire il nome del file da cui caricare l'albero:");
						nome = (String) readObject(socket);
						tree = RegressionTree.carica(nome);
						result = new String(tree.printRules() + tree.printTree());
						break;
					
					case 4: //USE THE CURRENT TREE TO PREDICT AN EXAMPLE
						try
						{
							if (tree != null)
								result = new String ((tree.predictClassServer(socket)).toString() +"\n");
							else
								throw new TrainingDataException();
						}
						catch (UnknownValueException e)
						{
							result = new String(e.toString());
						}
						break;
					case 5:
						flag = false;
						break;
					default:
						System.out.println("COMANDO INESISTENTE");
						result = new String ("COMANDO INESISTENTE\n");
					}// END SWITCH
				} 
				catch(IOException e)
				{
					if (e instanceof FileNotFoundException)
						result = new String(e.toString());
					else 
						break;
				} 
				catch(ClassNotFoundException e)
				{} 
				catch (TrainingDataException e1) 
				{
					result = new String (e1.toString());
				}
			}
		} 
		finally 
		{
			try 
			{
				writeObject(socket,false);
				socket.close();
				System.out.println("Server disponbile per un nuovo Client");
			} 
			catch (IOException e) 
			{
				System.out.println("Socket non chiuso!");
			}
		}
	}
*/
	public void run()
	{
		String result = "";
		String nome = "";
		Object o;
		boolean flag=true;
		
		while (flag) 
		{
			try {
				o = in.readObject();
				switch((int)o){
				case 0:	
					out.writeObject("OK");
					nome = (String)in.readObject();
					break;							
				case 1:
					nome = (String)in.readObject();
					Data TrainingSet = new Data(nome);
					tree = new RegressionTree(TrainingSet);
					result = new String (tree.printRules() + tree.printTree());
					out.writeObject("OK");
					out.writeObject(result);
					break;
				case 2:
					if( tree != null){
						nome = (String)readObject(socket);
						tree.salva(nome);
						result = new String ("Salvato\n");
					} else
						try {
							throw new TrainingDataException();
						} catch (TrainingDataException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				break;
				case 3:
					/*	writeObject(socket,true);
						//writeObject(socket,"Inserire il nome del file da cui caricare l'albero:");
						nome = (String) readObject(socket);
						tree = RegressionTree.carica(nome);
						result = new String(tree.printRules() + tree.printTree());*/
				break;			
				}
			} catch (ClassNotFoundException e) { flag=false;} 
			  catch (IOException e) { flag=false;}
			catch (TrainingDataException e) { flag = false;}
		}
		try{
			socket.close();
			System.out.println("closing...");
		} 
		catch (IOException e) {
			System.out.println("Socket non chiuso!");
		}
	}
}



