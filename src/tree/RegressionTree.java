package tree;
import java.util.TreeSet;

import data.Data;
import data.DiscreteAttribute;
import exception.UnknownValueException;
import utility.Keyboard;

/*
 * Struttura ad albero, ogni root pu� contenere o uno SplitNode (sotto-albero di profondit� 1) o un nodo foglia.
 * Qualora root avesse dei nodi figli, essi saranno definiti ricorsivamente come RegressionTree nel campo childTree
 */
public class RegressionTree 
{
	Node root;
	RegressionTree childTree[];
	
	RegressionTree ()
	{
		this.childTree = new RegressionTree [1];
	}
	
	/**
	 * definisce regressionTree come albero contenente i dati all'interno di Data
	 * @param trainingSet collezione di esempi che si vuole riportare tramite RegressionTree
	 */
	public RegressionTree (Data trainingSet)
	{
		learnTree (trainingSet, 0, trainingSet.getNumberOfExamples()-1, (trainingSet.getNumberOfExamples() * 10)/100);
		/*
		 * il limite di figli per ogni nodo � pari al 10% del numero totale di tutti gli esempi all'interno della collezione
		 */
	}
	
	boolean isLeaf(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf)
	{
		return ((end - begin)<= numberOfExamplesPerLeaf);
	}
	
	/**
	 * determina il "miglior" splitNode definibile con gli esempi compresi tra gli indici begin ed end nella collezione
	 * 
	 * @param trainingSet collezione degli esempi
	 * @param begin indice della sotto-collezione da cui si vuole definire lo splitNode desiderato
	 * @param end indice della sotto-collezione entro cui si vuole definire lo splitNode desiderato
	 * @return ritorna il "miglior SplitNode definibile per la sotto-collezione passata in parametro
	 */
	SplitNode determineBestSplitNode(Data trainingSet,int begin,int end)
	{
		/*
		 * � molto semplice, ogni splitNode definito fa riferimento ad un particolare attributo indipendente,
		 * perci� quello che viene fatto qui � definire TUTTI i possibili splitNode definibili, quindi se ne creano tanti quanti sono
		 * le classi indipendenti che definiscono la sotto-collezione. lo SplitNode che ha la varianza minore rispetto agli altri,
		 * allora sar� il "miglior" splitNode ricavabile dalla sotto-collezione
		 */
		
		/*
		 * Il TreeSet permette di ordinare automaticamente, ovvero ad ogni nuova aggiunta (add) di un elemento, l'unica
		 * premessa � che la classe quale sar� contenuta nel treeSet (in questo caso "SplitNode") deve
		 * fare l'override della funzione compareTo , implementandolo dalla classe Comparator <SplitNode>
		 */
		TreeSet<SplitNode> ts = new TreeSet<SplitNode>();	
		for (int i = 0; i < trainingSet.getNumberOfExplanatoryAttributes(); i++)
		{
			ts.add(new DiscreteNode (trainingSet, begin, end, (DiscreteAttribute)trainingSet.getExplanatoryAttribute(i)));
		}
		
		/*
		 * Poich� ad ogni nuovo splitNode definito, l'ordine degli esempi presenti nel trainingSet varia.
		 * allora svolgo un nuovo ordinamento, in base all'attributo dello splitNode che � stato scelto
		 * cos� da poter garantire una corretta esecuzione qualora si necessitase di definire ultieriori
		 * splitnode nel sottoinsieme del trainingSet (semplicemente, provate a non fare l'ordinamento e
		 * guardacaso gli splitNode saranno "sballati" da come ci si dovrebbe aspettare!)
		 * 
		 * Nel caso del treeSet, il "miglior" splitNode � quello pi� "piccolo" (inteso in base alla sua varianza 
		 * rispetto a quello degli altri "candidati")
		 */
		
		trainingSet.sort(ts.first().getAttribute(), begin, end);
		return ts.first();
	}
	
		/**
		 * Costruttore di alberi di regressione in maniera ricorsiva (definisce 
		 * il sotto-albero di prondit� 1 e definisce gli eventuali sotto-alberi dei suoi figli, e 
		 * cos� via dicendo dei figli dei figli e blablabla). Occhio ad usarlo, pu� influire in 
		 * maniera non banale sul trainingSet. per la costruzione dell'albero di regressione verrano 
		 * considerati tutti gli esempi che saranno compresi dall'indice begin all'indice end del 
		 * trainingSet.
		 * 
		 * @param trainingSet collezione di esempi che si vuole riportare all'interno di un albero di regressione
		 * @param begin indice della sotto-collezione da cui si vuole definire lo splitNode desiderato
		 * @param end indice della sotto-collezione entro cui si vuole definire lo splitNode desiderato
		 * @param numberOfExamplesPerLeaf definisce il limite massimo di figli che pu� avere un padre
		 */
		private void learnTree(Data trainingSet,int begin, int end,int numberOfExamplesPerLeaf)
		{
			if( isLeaf(trainingSet, begin, end, numberOfExamplesPerLeaf))
			{
				//determina la classe che compare pi� frequentemente nella partizione corrente
				root=new LeafNode(trainingSet,begin,end);
			}
			else //split node
			{
				root=determineBestSplitNode(trainingSet, begin, end);
				if(root.getNumberOfChildren()>1)
				{
					childTree=new RegressionTree[root.getNumberOfChildren()];
					for(int i=0;i<root.getNumberOfChildren();i++)
					{
						childTree[i]=new RegressionTree();
						childTree[i].learnTree(trainingSet, ((SplitNode)root).getSplitInfo(i).beginIndex, ((SplitNode)root).getSplitInfo(i).endIndex, numberOfExamplesPerLeaf);
					}
				}
				else
					root=new LeafNode(trainingSet,begin,end);
			}
		}
			
		public String toString()
		{
			String tree=root.toString()+"\n";
			
			if( root instanceof LeafNode)
			{
			
			}
			else //split node
			{
				for(int i=0;i<childTree.length;i++)
					tree +=childTree[i];
			}
			return tree;
		}
		
		/**
		 * Stampa le regole dell'albero di regressione
		 */
		public void printRules()
		{
			System.out.println("********* RULES ***********\n");
			System.out.println(printRules2(""));
			System.out.println("**************************\n");
		}
		
		private String printRules2(String rule)
		{
			String rule2 = "";
			if (root instanceof LeafNode)
			{
				LeafNode lf = (LeafNode) root;
				rule2 += rule + " ==> " + "Class: " + lf.getPretictedClassValue() + "\n";
			}
			else
			{
				SplitNode sn = (SplitNode) root;
				for (int i = 0; i < root.getNumberOfChildren(); i++)
				{
					if (!rule.equals("") && i == 0)
						rule += " AND ";
					rule2 += childTree[i].printRules2( rule + sn.getAttribute().getName() + " = " + sn.getSplitInfo(i).getSplitValue().toString());
				}
			}
			return rule2;
		}
		
		/**
		 *Stampa l'albero di regressione 
		 */
		public void printTree()
		{
			System.out.println("********* TREE **********\n");
			System.out.println(toString());
			System.out.println("*************************\n");
		}
		
		/**
		 * consente all'utente di percorrere in profondit� l'albero di decisione, seguendo il percorso
		 * a proprio piacimento
		 * @return ritorna il risultato della predizione
		 * @throws UnknownValueException
		 */
		public Double predictClass() throws UnknownValueException
		{
			//controllo che tipo � root, se � foglia allora si � giunto a coclusione e ritorna l'attributo di classe trovato
			if (root instanceof LeafNode)
			{
				LeafNode lf = (LeafNode) root;
				return lf.predictedClassValue;
			}
			//altrimeni � splitNode
			else
			{
				SplitNode sn = (SplitNode) root;
				//mostro quali sono i percorsi possibili per continuare la predizione
				System.out.println(sn.formulateQuery());
				int choice = Keyboard.readInt();
				if ((choice < 0) || (choice >= childTree.length))
					throw new UnknownValueException("The answer should be an integer between 0 and "+(childTree.length-1)+"!");
				//ricevuta la scelta (choice) dall'utente, procedo con la predizione analizzando il figlio scelto
				return childTree[choice].predictClass();
			}
		}
		
		
}