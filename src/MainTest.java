import java.io.FileNotFoundException;
import java.io.IOException;

import tree.RegressionTree;
import utility.Keyboard;
import data.Data;
import exception.TrainingDataException;
import exception.UnknownValueException;

public class MainTest {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException{
		
		
		
		int decision=0;
		do{
			System.out.println("Scegli una opzione");
			do{
			System.out.println("(1) Leggi Regression Tree da file");
			System.out.println("(2) Carica Regression Tree da archivio ");
			System.out.print("Risposta: ");
			decision=Keyboard.readInt();
		}while(!(decision==1) && !(decision ==2));
		
		String trainingfileName="";
		System.out.println("File name:");
		trainingfileName=Keyboard.readString();
		
		
		RegressionTree tree=null;
		if(decision==1)
		{
			System.out.println("Starting data acquisition phase!");
			Data trainingSet=null;
			try{
			
				trainingSet= new Data(trainingfileName+ ".dat");
			}
			catch(TrainingDataException e){System.out.println(e);return;}
		
			System.out.println("Starting learning phase!");
			tree=new RegressionTree(trainingSet);
			try {
				tree.salva(trainingfileName+".dmp");
				//aggiunto per vedere se lo salva correttamente
				System.out.println("SALVATAGGIO RIUSCITO CORRETTAMENTE");
			} catch (IOException e) {
				
				System.out.println(e.toString());
			}
		} else
			try {
				tree=RegressionTree.carica(trainingfileName+".dmp");
			} catch (ClassNotFoundException | IOException e) {
				System.out.print(e);
				return;
			}
			tree.printRules();
			tree.printTree();
			
			char risp='y';
			do{
				
				System.out.println("Starting prediction phase!");
				try {
					System.out.println(tree.predictClass());
				} catch (UnknownValueException e) {
					
					System.out.println(e);
				}
				System.out.println("Would you repeat ? (y/n)");
				risp=Keyboard.readChar();
				
			}while (Character.toUpperCase(risp)=='Y');
		
			
	
	 System.out.print("Vuoi scegliere una nuova operazione da menu?(y/n): ");
     if (Character.toUpperCase(Keyboard.readChar()) != 'Y') {
         break;
     }
 } while (true);
 System.out.println("PROGRAMMA TERMINATO! ARRIVEDERCI!!!");
}

}
