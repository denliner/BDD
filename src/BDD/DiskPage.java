package BDD;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;

public class DiskPage {
	private ArrayList<Tuple> diskpage; //ArrayList contenant les tuple de la page
	public static int actID=0;
	private int pageID;
	public static int RECORDSIZE=20;//taile en byte des tuple
	public static int BITSETSIZE=20; //taille en byte du bitset freespace
	public static int PAGESIZE=5;    //Nombre de tuple sur la page
	public static int DISKPAGEBYTESSIZE=120; //taille en byte de la classe
	private BitSet Freespace; //bitset servant a indiquer si une position de la page est vide
	public static int CLASSSIZE=120;
	public static int ALLRECORDSIZE=100; //Taille en byte de toute les tuples

	public DiskPage(int pageID) {
		Freespace = new BitSet(5);
		this.pageID=pageID;
		diskpage=new ArrayList<Tuple>();
		for(int i=0;i<PAGESIZE;i++) {
			diskpage.add(new Tuple());
		}
		this.Verif();
	}
	
	/**
	 * Initialise la page grace a un Array de byte
	 * @param byr
	 * @param pageID
	 */
	public DiskPage(byte[] byr,int pageID){

			Freespace = new BitSet(5);
			this.pageID=pageID;
			diskpage=new ArrayList<Tuple>();
			for(int i=0;i<PAGESIZE;i++) {
				diskpage.add(new Tuple());
			}
			ByteBuffer by=ByteBuffer.wrap(byr);
			this.iniBitset(by);
			this.iniRecord(by);
			this.Verif();
	}
	/**
	 * Re-initialise le Bit Array Freespace en fonction du contenu de la Page
	 */
	public void Verif() {
		int i=0;
		for(Tuple tup : diskpage) {
			if(tup.getAge()==(-1)) {
				this.Freespace.set(i);
			}
			else {
				this.Freespace.clear(i);
			}
			i++;
		}
		
	}
	
	/**
	 * Ajoute tout le record/tuple non vierge dans une List et @return cette List
	 * 
	 */
	public ArrayList<IndexTuple> getList() {
		this.Verif();
		ArrayList<IndexTuple> allTuple=new ArrayList<IndexTuple>();
		for(int i=0;i<PAGESIZE;i++) {
			Tuple a=this.getDiskpage().get(i);
			int index=(this.pageID*5)+i;
			if(!(this.Freespace.get(i))) {
				allTuple.add(new IndexTuple(a,index));
				
			}
		}
		return allTuple;
	}
	
	/**
	 * Initialise les tuple de la page grace au byte passee sous @param
	 * 
	 */
	public void iniRecord(ByteBuffer by) {
		for(int i=0;i<PAGESIZE;i++){
			byte[] b=new byte[RECORDSIZE];
			by.get(b);
			if(Freespace.get(i)) {
				this.diskpage.set(i, new Tuple());
			}
			else {
				this.diskpage.set(i, new Tuple(ByteBuffer.wrap(b)));
			}
		}
	}
	
	/**
	 * Remplace un tuple sur la page
	 * @param index
	 * @param tup
	 */
	public void addTuple(int index,Tuple tup) {
		this.getDiskpage().remove(index);
		this.getDiskpage().add(index, tup);
		this.getFreespace().clear(index);
	}
	/**
	 * Remplace un tuple par un tuple "vierge"
	 * @param index
	 */
	public void removeTuple(int index) {
		this.getDiskpage().remove(index);
		this.getDiskpage().add(index, new Tuple());
		this.getFreespace().set(index);
	}
	
	public void iniBitset(ByteBuffer by) {
		for(int i=0;i<PAGESIZE;i++) {
			if(by.getInt()==1) {
				Freespace.set(i);
			}
			else {
				Freespace.clear(i);
			}
			
		}
	}
	public void ToTrue(int bitIndex) {
		Freespace.set(bitIndex);
	}
	
	public void ToFalse(int bitIndex) {
		Freespace.set(bitIndex);
	}
	/**
	 * Return toute les donnee de la class sous forme de byte
	 * @return
	 */
	public byte[] toByte() {
		byte[] b=new byte[this.getDISKPAGEBYTESSIZE()];
		ByteBuffer buff=ByteBuffer.wrap(b);
		buff.put(this.saveBitset());
		buff.put(this.saveTuple());
		return buff.array();
	}
	/**
	 * Sauvegarde le Bit array sur le fichier
	 * @return
	 */
	public byte[] saveBitset() {
		byte[] bbitset=new byte[BITSETSIZE];
		ByteBuffer buff=ByteBuffer.wrap(bbitset);
		for(int i=0;i<PAGESIZE;i++) {
			if(this.Freespace.get(i)) {
				buff.putInt(1);
			}
			else {
				buff.putInt(0);
			}
		}
		return buff.array();
	}
	/**
	 * Converti le contenu de toutes les tuple en byte
	 * 
	 * @return
	 * Un tableau de byte obtenu grace a ByteBuffer contenant les byte 
	 * de toute les tuples de la page
	 */
	public byte[] saveTuple() {
		byte[] ballTuple=new byte[ALLRECORDSIZE];
		ByteBuffer buff=ByteBuffer.wrap(ballTuple);
		for(Tuple tup : this.diskpage) {
			buff.put(tup.toByte());
		}
		return buff.array();
	}


	public ArrayList<Tuple> getDiskpage() {
		return diskpage;
	}

	public void setDiskpage(ArrayList<Tuple> diskpage) {
		this.diskpage = diskpage;
	}

	public static int getActID() {
		return actID;
	}

	public static void setActID(int actID) {
		DiskPage.actID = actID;
	}

	public int getPageID() {
		return pageID;
	}

	public void setPageID(int pageID) {
		this.pageID = pageID;
	}

	public BitSet getFreespace() {
		return Freespace;
	}

	public void setFreespace(BitSet freespace) {
		Freespace = freespace;
	}

	public static int getCLASSSIZE() {
		return CLASSSIZE;
	}

	public static void setCLASSSIZE(int cLASSSIZE) {
		CLASSSIZE = cLASSSIZE;
	}

	public int getRECORDSIZE() {
		return RECORDSIZE;
	}

	public int getPAGESIZE() {
		return PAGESIZE;
	}

	public int getDISKPAGEBYTESSIZE() {
		return DISKPAGEBYTESSIZE;
	}
	
	
	
	
	
}
