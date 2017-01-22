package BDD;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import up5.mi.pary.term.Terminal;

public class Join {
	public static final String FILENAME="testttt.png";
	public static final String FILENAME2="testttt2.png";
	
	/**
	 * Methode statique utilisé pour initialiser le ficher binaire
	 * @param filename
	 * nom du fichier
	 * @param b
	 * tableau de byte
	 * @param index
	 * permet de choisir model de on veut utiliser
	 */
	public static void iniFile(String filename,byte[] b,int index) {
		try {
			File file=new File(filename);
			RandomAccessFile f=new RandomAccessFile(file,"rw");
			DiskPage page0=new DiskPage(0);
			DiskPage page1=new DiskPage(1);
			DiskPage page2=new DiskPage(2);
			DiskPage page3=new DiskPage(3);
			if(index==0){
				page0.addTuple(0, new Tuple("Van",40,6969));
				page0.addTuple(2, new Tuple("Tony",20,2500));
				page0.addTuple(3, new Tuple("Zaed",23,2590));
				page1.addTuple(1, new Tuple("Taef",20,2530));
				page1.addTuple(3, new Tuple("dude",21,500));
				page1.addTuple(4, new Tuple("Weeb",18,2500));
				page2.addTuple(3, new Tuple("Tomi",28,2333));
				page3.addTuple(2, new Tuple("DH",21,2500));
			}
			else {
				page0.addTuple(0, new Tuple("Aniki",40,6969));
				page0.addTuple(2, new Tuple("PZ",20,2500));
				page0.addTuple(3, new Tuple("Zaed",23,2590));
				page1.addTuple(1, new Tuple("Yumil",20,2530));
				page1.addTuple(3, new Tuple("Tony",21,2500));
				page1.addTuple(4, new Tuple("Gundum",40,2500));
				page2.addTuple(0, new Tuple("POL",18,500));
				page2.addTuple(3, new Tuple("Tomi",28,2333));
				page3.addTuple(2, new Tuple("PAUPAU",21,2500));
			}
			ByteBuffer byt=ByteBuffer.wrap(b);
			byt.put(page0.toByte());
			byt.put(page1.toByte());
			byt.put(page2.toByte());
			byt.put(page3.toByte());
			f.write(byt.array());
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 * Les methodes JoinX(arrayList<Indextuple>,int)
	 * Ensemble de methode permetant de retourner dans une arraylist
	 * toute les tuple satisfaisant une relation (age,nom,salaire)
	 *  
	 */
	
	public static ArrayList<IndexTuple> joinAge(ArrayList<IndexTuple> a,int age) {
		ArrayList<IndexTuple> result=new ArrayList<IndexTuple>();
		for(IndexTuple tup : a) {
			if(tup.getTuple().getAge()==age) {
				result.add(tup);
			}
		}
		return result;
	}
	
	public static ArrayList<IndexTuple> joinNom(ArrayList<IndexTuple> a,String nom) {
		ArrayList<IndexTuple> result=new ArrayList<IndexTuple>();
		for(IndexTuple tup : a) {
			if(tup.getTuple().getName().equals(Tuple.nameSize(nom))) {
				result.add(tup);
			}
		}
		return result;
	}
	
	public static ArrayList<IndexTuple> joinSalary(ArrayList<IndexTuple> a,double salary) {
		ArrayList<IndexTuple> result=new ArrayList<IndexTuple>();
		for(IndexTuple tup : a) {
			if(tup.getTuple().getSalary()==salary) {
				result.add(tup);
			}
		}
		return result;
	}
	
	
	/**
	 * Ensemble de methode (JoinWithX) permetant de faire de l'equi-Join de deux ficher 
	 * en fonction de l'age, le nom ou le salaire
	 * @return
	 * Une arrayList de tupleJoin
	 */
	public static ArrayList<TupleJoin> joinWithAge(ArrayList<IndexTuple> a,ArrayList<IndexTuple> b) {
		ArrayList<TupleJoin> result=new ArrayList<TupleJoin>();
		int i=0;
		for(IndexTuple tup : a) {
			for(IndexTuple tup2 : b) {
				if(tup.getTuple().getAge()==tup2.getTuple().getAge()) {
					result.add(new TupleJoin(tup,tup2,i));
					i++;
				}
			}
		}
		return result;
	}
	
	public static ArrayList<TupleJoin> joinWithName(ArrayList<IndexTuple> a,ArrayList<IndexTuple> b) {
		ArrayList<TupleJoin> result=new ArrayList<TupleJoin>();
		int i=0;
		for(IndexTuple tup : a) {
			for(IndexTuple tup2 : b) {
				if(tup.getTuple().getName().equals(tup2.getTuple().getName())) {
					result.add(new TupleJoin(tup,tup2,i));
					i++;
				}
			}
		}
		return result;
	}
	
	public static ArrayList<TupleJoin> joinWithSalary(ArrayList<IndexTuple> a,ArrayList<IndexTuple> b) {
		ArrayList<TupleJoin> result=new ArrayList<TupleJoin>();
		int i=0;
		for(IndexTuple tup : a) {
			for(IndexTuple tup2 : b) {
				if(tup.getTuple().getSalary()==tup2.getTuple().getSalary()) {
					result.add(new TupleJoin(tup,tup2,i));
					i++;
				}
			}
		}
		return result;
	}
	
	
	
	/**
	 * Affiche sur le contenu de chaque tuple contenu dans une List 
	 * sur la console java
	 *
	 */
	public static void printAllTuple(ArrayList<IndexTuple> alltup) {
		for(IndexTuple tupp : alltup) {
			System.out.println(tupp.toString());
		}
	}
	/**
	 * Meme chose mais en utilisant la classe terminal
	 * @param alltup
	 * @param term
	 */
	public static void printAllTupleWithTerm(ArrayList<IndexTuple> alltup,Terminal term) {
		for(IndexTuple tupp : alltup) {
			term.println(tupp.toString());
			System.out.println(tupp.toString()); //for debug
		}
	}
	
	/**
	 * Affiche le contenu de chaque couple de tuple obtenu precedent grace 
	 * a un equi-join
	 * 
	 * 
	 */
	public static void printAllJoinTuple(ArrayList<TupleJoin> alltup) {
		for(TupleJoin tupp : alltup) {
			System.out.println(tupp.toString());
		}
	}
	
	public static void printAllJoinTupleWithTerm(ArrayList<TupleJoin> alltup,Terminal term) {
		for(TupleJoin tupp : alltup) {
			term.println(tupp.toString());
			System.out.println(tupp.toString()); //for debug
		}
	}
	
	
	public static void main(String[] args) {
		File file=new File("testttt.png");
		File file2=new File("testttt2.png");
		if(!((file.exists()) && (file2.exists()))) { //verifie si le ficher existe sinon initialise les deux fichier binaires
			Join.iniFile(file.getName(), new byte[480], 0);
			Join.iniFile(file2.getName(), new byte[480], 1);
		}
		try {
			BufferManager buff=new BufferManager(file.getName());
			Terminal term=new Terminal("terminal",800,600);
			Interfaceterm.choix(term, buff, 0); //lance l'interface graphique2
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
}
