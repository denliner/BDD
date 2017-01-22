package BDD;

import java.io.IOException;
import java.util.ArrayList;

import up5.mi.pary.term.Terminal;

public class Interfaceterm {
	
	
	/**
	 * Class contenant toutes les methodes manipulant l'interface de l'utilisateur 
	 * 
	 */
	
	
	
	public static final String FILENAME=Join.FILENAME;
	public static final String FILENAME2=Join.FILENAME2;
	
	public static void endChoise(Terminal term,BufferManager buff, int bdd) throws IOException {
		term.println("1: menu");
		term.println("2 : quittez");
		int choise=term.readInt("Votre choix");
		if(choise==1) {
			choix(term,buff,bdd);
		}
		else {
			term.end();
		}
	}
	
	public static void choix(Terminal term,BufferManager buff, int BDD) throws IOException {
		buff.SaveAll();
		int choise;
		String fileLocation;
		if(BDD==0) {
			fileLocation=FILENAME;
		}
		else {
			fileLocation=FILENAME2;
		}
		buff.ChangeFileLocation(fileLocation);
		term.clear();
		term.println("BDD N°"+(BDD+1)+"\n");
		term.println("1: Lire la liste de tuple\n2:Ajouter un tuple\n3: effacer un tuple \n4:Re-initialiser les BDD \n5: Switcher de BDD");
		term.println("6: Faire une requete");
		term.println("7: Quittez");
		choise=term.readInt("votre choix");
		switch(choise) {
			case 1 :
				term.clear();
				Join.printAllTupleWithTerm(buff.readAllTuple(fileLocation),term);
				endChoise(term,buff,BDD);
				break;
			case 2:
				term.clear();
				choise=term.readInt("Quelle position");
				String nom=term.readString("nom");
				int age=term.readInt("age?");
				double salaire=term.readDouble("salaire");
				buff.getPageID(choise/5, true).addTuple(choise%5, new Tuple(nom,age,salaire));
				buff.donePageid(choise/5);
				term.clear();
				endChoise(term,buff,BDD);
				break;
			case 3:
				term.clear();
				choise=term.readInt("Quelle position");
				buff.getPageID(choise/5, true).removeTuple(choise%5);
				buff.donePageid(choise%5);
				term.clear();
				endChoise(term,buff,BDD);
				break;
			case 4:
				term.clear();
				Join.iniFile(fileLocation, new byte[480], BDD);
				Join.iniFile(FILENAME2,new byte[480],BDD);
				choix(term,buff,BDD);
				break;
			case 5:
				if(BDD==0) {
					choix(term,buff,1);
				}
				else {
					choix(term,buff,0);
				}
				break;
			case 6:
				term.clear();
				choixRequete(term, buff, fileLocation);
				endChoise(term,buff,BDD);
				break;
			default:
				term.end();
				break;
		}
		buff.SaveAll();
		
	}
	
	
	
	public static void choixRequete(Terminal term,BufferManager buff,String fileLocation) throws IOException {
		term.println("Quel type de requette voulez vous utilisez?");
		term.println("1: Jointure des deux BDD");
		term.println("2: Requete sur le fichier actuel");
		
		int choise=term.readInt("Votre choix");
		if(choise==2) {
			term.clear();
			term.println("1: age  2: nom  3: salaire");
			int choise2=term.readInt("Votre choix");
			ArrayList<IndexTuple> a;
			if(choise2==1) 
			{
				term.clear();
				a=Join.joinAge(buff.readAllTuple(fileLocation), term.readInt("age?"));
			}
			else {
				if(choise2==2) {
					term.clear();
					a=Join.joinNom(buff.readAllTuple(fileLocation), term.readString("nom?"));
				}
				else {
					term.clear();
					a=Join.joinSalary(buff.readAllTuple(fileLocation), term.readDouble("salaire?"));
					
				}
			}
			Join.printAllTupleWithTerm(a,term);
			
		}
		else {
			term.clear();
			term.println("1: age  2: nom  3: salaire");
			int choise3=term.readInt("Votre choix");
			ArrayList<TupleJoin> b;
			ArrayList<IndexTuple> alltup=new ArrayList<IndexTuple>();
			ArrayList<IndexTuple> alltup2=new ArrayList<IndexTuple>();
			alltup.addAll(buff.readAllTuple(FILENAME));
			alltup2.addAll(buff.readAllTuple(FILENAME2));
			if(choise3==1) 
			{
				term.clear();
				b=Join.joinWithAge(alltup,alltup2);
			}
			else {
				if(choise3==2) {
					term.clear();
					b=Join.joinWithName(alltup,alltup2);
				}
				else {
					term.clear();
					b=Join.joinWithSalary(alltup,alltup2);
					
				}
			}
			Join.printAllJoinTupleWithTerm(b,term);			
		}
}
}
