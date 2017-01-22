package BDD;
import java.io.*;
import java.util.ArrayList;

public class BufferManager {
	private static int FILESIZE=480; //Taile maximale du fichier binaire
	private final int POOLSIZE=3; //taile maximale du buffer
	private ArrayList<Frame> bufferpool; //buffer
	private RandomAccessFile f; //fichier binaire utilisé
	private byte[] pageByte;   
	private int nbFreeFrame; //Indique le nombre de case totalement vide de l'arraylist bufferpool
	private long nbFrame; //Nombre page present sur le fichier
	private long nbTuple; //nombre de tuple presente sur le fichier
	private String fileLocation;
	
	public BufferManager(String fileLocation) throws IOException{
		try{
			File file=new File(fileLocation);
			if(!(file.exists())) { //verifie si le fichier existe
				throw new FileNotFoundException(fileLocation+" n'existe pas");
			}
			this.f=new RandomAccessFile(file,"rw");
			this.fileLocation=fileLocation;
			pageByte=new byte[DiskPage.DISKPAGEBYTESSIZE];
			this.nbFrame=4;
			this.nbFreeFrame=this.POOLSIZE;
			this.bufferpool=new ArrayList<Frame>();
			this.nbTuple=this.nbFrame*DiskPage.PAGESIZE;
		} catch(FileNotFoundException e) {
			e.printStackTrace(); 
		}
		
	}
	
	
	
	
	public ArrayList<Frame> getBufferpool() {
		return bufferpool;
	}
	
	
	/**
	 * Ecrit toute les pages qui ont un dirty==TRUE
	 * sur le disque
	 * 
	 */
	public void SaveAll() {
		for(Frame fra : bufferpool) {
			if(fra.isDirty()){
				if(fra.getPinCount()==0){
					this.writePageId(fra.getFrameId());
				}
			}
		}
		this.bufferpool= new ArrayList<Frame>();
	}
	
	
	/**
	 * Indique si la page Recherchée est dans le bufferpool
	 * @param diskPageId
	 * ID de la page recherchee
	 * @return
	 * TRUE si trouvé
	 * FALSE Sinon
	 */
	public boolean searchPageID(int diskPageId) {
		boolean found=false;
		for(Frame frame : bufferpool ) {
			if(frame.getDiskPageId()==diskPageId) {
				return true;
			}
		}
		return found;
	}
	
	/**
	 * Retourne la frame contenant la DiskPage demandee
	 * @param diskPageId
	 * @return
	 */
	public Frame searchFrame(int diskPageId) {
		Frame fra=new Frame(0,0);
		for(Frame frame : bufferpool ) {
			if(frame.getFrameId()==diskPageId) {
				fra=frame;
				return fra;
			}
		}
		return fra;
		
	}
	/**
	 * Supprime la frame demande, incremente nbFreeFrame 
	 * puis attribue au frame de nouveaux ID
	 * @param frameId
	 re*/
	public void removeFrame(int frameId){
		this.bufferpool.remove(frameId);
		this.nbFreeFrame++;
		int i=0;
		for(Frame fra : bufferpool) {
			fra.setFrameId(i);
			i++;
		}
	}
	/**
	 * Demande un bufferManager une DiskPage @param diskPageId
	 * Si la page se trouve dans le bufferpool 
	 * alors il retournera la page contenu dans le bufferpool
	 * 
	 * Sinon le buffermanager devra accédé au disque, inclure la Page dans le buffer 
	 * puis la retourner
	 * @param modify
	 * 	TRUE => La page va etre modifie donc bit dirty passe a 1
	 *  FALSE => La page ne va pas etre modifie donc le bit dirty passe a 0
	 * @return
	 *  la page demandee
	 * @throws IOException
	 */
	public DiskPage getPageID(int diskPageId,boolean modify) throws IOException {
		if(diskPageId>(this.nbFrame)) {
			throw new IOException("Pointeur superieur a la capacite de la BDD");
		}
		
		int id;
		
		if(this.searchPageID(diskPageId)) {
			System.out.println("Page N°"+diskPageId+"  trouvé dans le bufferpool");
			id=this.searchFrame(diskPageId).getFrameId();
			if(modify==true) {
				bufferpool.get(id).setDirty(true);
			}
			else {
				bufferpool.get(id).setDirty(false);
			}
			return bufferpool.get(id).getPage();
		}
		else {
			System.out.println("PAGE N°"+diskPageId+"  non trouvé dans le bufferpool");
			System.out.println("Lecture de la page N°"+diskPageId+" sur le disque");
			id=this.readPageId(diskPageId);
			if(modify==true) {
				bufferpool.get(id).setDirty(true);
			}
			else {
				bufferpool.get(id).setDirty(false);
			}
			return bufferpool.get(id).getPage();
		}
	}
	/**
	 * Indique que plus aucun processus accede au thread
	 * @param diskPageId
	 */
	public void donePageid(int diskPageId) {
		if(this.searchPageID(diskPageId)) {
			int id=this.searchFrame(diskPageId).getFrameId();
			Frame curFrame=bufferpool.get(id);
			curFrame.pinCountMoins();
		}
	}
	
	/**
	 * Verifie s'il existe une Frame du bufferpool qui est utilise par aucun thread
	 * puit lit une DiskPage depuis le HDD et l'alloue dans le bufferpool
	 * @param diskPageId
	 * @return L'index de la frame contenant la page dans le bufferpool
	 * @throws IOException
	 */
	public int readPageId(int diskPageId) throws IOException {
		int tempFrameId=this.LRUFrame(diskPageId);
		if(tempFrameId==-1) {
			System.out.println("aucune page disponible");
			return readPageId(diskPageId);
		}
		else 
		{
			System.out.println("La frame N°"+tempFrameId+" est disponible");
			Frame tempFrame=new Frame(tempFrameId,diskPageId);
			includePageId(tempFrame);
			return tempFrameId;
			
		}
	}
	
	/**
	 * Recupere la page demandee sur le disque et modifie le bufferpool
	 * @param tempFrame
	 */
	public void includePageId(Frame tempFrame) {
		this.pageByte=new byte[DiskPage.DISKPAGEBYTESSIZE];
		try {
			System.out.println("Position de la page n°"+tempFrame.getDiskPageId()+" ="+tempFrame.getPagePosition());
			f.seek(tempFrame.getPagePosition());
			f.read(pageByte);
		} catch (IOException e) {
			e.printStackTrace();
		}
		tempFrame.setDiskPage(new DiskPage(pageByte,tempFrame.getDiskPageId()));
		
		if(this.nbFreeFrame==0) //Cas où le bufferpool est plein
		{
			System.out.println("AUCUNE CASE LIBRE DANS LE BUFFERPOOL");
			System.out.println("La Page n°"+this.bufferpool.get(tempFrame.getFrameId()).getDiskPageId()+" a été retiré de la frame "+tempFrame.getFrameId());
			this.bufferpool.remove(tempFrame.getFrameId());
			this.bufferpool.add(tempFrame.getFrameId(), tempFrame);
			System.out.println("La page n°"+tempFrame.getDiskPageId()+"  a été ajouté a la frame n°"+tempFrame.getFrameId()+"\n");

		}
		else
		{
			System.out.println("Il reste des cases libre dans le bufferpool");
			this.bufferpool.add(tempFrame);
			System.out.println("La page n°"+tempFrame.getDiskPageId()+"  a été ajouté a la frame n°"+tempFrame.getFrameId()+"\n");
			this.nbFreeFrame--;
		}
	}
	
	/**
	 * Meme chose que getPageID mais pour recuperer un tuple particulier
	 * @param TupleId
	 * @param modify
	 * @return
	 * @throws Exception
	 */
	public Tuple getTupleAt(int TupleId,boolean modify) throws Exception {
		if(TupleId>=this.nbTuple) {
			throw new Exception("pointeur superieur a la capacité de la base de donnée"+ TupleId);
		}
		int DidskPageId=TupleId/DiskPage.PAGESIZE;
		int TupleArrayAdress=TupleId%DiskPage.PAGESIZE;
		return this.getPageID(DidskPageId, false).getDiskpage().get(TupleArrayAdress);
	}
	
	/**
	 * Ajoute un tuple particulier dans la base de donnee
	 * @param TupleId
	 * index du tuple dans le fichier
	 * @param tup
	 * @throws Exception
	 */
	public void AddTupleAt(int TupleId,Tuple tup) throws Exception {
		if(TupleId>=this.nbTuple) {
			throw new Exception("pointeur superieur a la capacitée de la base de donnée "+ TupleId);
		}
		int DidskPageId=TupleId/DiskPage.PAGESIZE;
		int TupleArrayAdress=TupleId%DiskPage.PAGESIZE;
		this.getPageID(DidskPageId, true).addTuple(TupleArrayAdress, tup);
		this.donePageid(DidskPageId);
	}
	/**
	 * Enleve un tuple dans la base de donnee
	 * @param TupleId
	 * id du tuple
	 * @throws Exception
	 */
	public void RemoveTupleAt(int TupleId) throws Exception {
		if(TupleId>=this.nbTuple) {
			throw new Exception("pointeur superieur a la capacitée de la base de donnée "+ TupleId);
		}
		int DidskPageId=TupleId/DiskPage.PAGESIZE;
		int TupleArrayAdress=TupleId%DiskPage.PAGESIZE;
		this.getPageID(DidskPageId, true).removeTuple(TupleArrayAdress);
		this.donePageid(DidskPageId);
	}
	
	
	/**
	 * Ajoute toute les Tuple non vides de chaque DiskPage du fichier binaire
	 * @param fileLocation
	 * @return
	 * @throws IOException
	 */
	public ArrayList<IndexTuple> readAllTuple(String fileLocation) throws IOException {
		ArrayList<IndexTuple> allTuple=new ArrayList<IndexTuple>();
		if(this.fileLocation==fileLocation) {
			this.nbFrame=f.length()/DiskPage.CLASSSIZE;
		}
		
		else {
			this.DoneAllPage();
			this.SaveAll();
			if(!(this.fileLocation==fileLocation)) {
				System.out.println("Changement de ficher "+fileLocation);
				this.ChangeFileLocation(fileLocation);
			}
		}
		
		int i=0;
		while(i<nbFrame) {
			allTuple.addAll(this.getPageID(i, false).getList());
			this.donePageid(i);
			i++;
			
		}
		this.DoneAllPage();
		return allTuple;
	}
	/**
	 * Permet d'indiquer que plus aucun processus n'accede aux frames
	 */
	public void DoneAllPage() {
		for(Frame fra : bufferpool) {
			fra.pinCountMoins();
		}
	}
	
	/**
	 * Permet de change de fichier binaire
	 * @param fileLocation
	 * @throws IOException
	 */
	public void ChangeFileLocation(String fileLocation) throws IOException{
		this.DoneAllPage();
		this.SaveAll();
		this.nbFreeFrame=this.POOLSIZE;
		this.bufferpool=new ArrayList<Frame>();
		try{
			File file=new File(fileLocation);
			this.f=new RandomAccessFile(file,"rw");
		} catch(FileNotFoundException e) {
			e.printStackTrace(); 
		}
		this.fileLocation=fileLocation;
	}
	
	
	public static int getFILESIZE() {
		return FILESIZE;
	}


	public static void setFILESIZE(int fILESIZE) {
		FILESIZE = fILESIZE;
	}


	public long getNbTuple() {
		return nbTuple;
	}


	public void setNbTuple(long nbTuple) {
		this.nbTuple = nbTuple;
	}


	public String getFileLocation() {
		return fileLocation;
	}


	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	/**
	 * Determine quelle est la page l'arrayList qui a ete utilise le moins recement
	 * @param FrameComp
	 * @return
	 */
	public int oldestLastAccessTimestamp(ArrayList<Frame> FrameComp) {
		int oldestId=-1;
		double oldestTime=-1;
		int i=0;
		for(Frame fra:bufferpool) {
			if(oldestTime==-1) {
				oldestTime=fra.getLastAccessTimestamp();
				oldestId=i;

			}
			else {
				if(fra.getLastAccessTimestamp()<oldestTime) {
					oldestTime=fra.getLastAccessTimestamp();
					oldestId=i;
				}
			i++;

			}
		}
		return oldestId;
		
	}
	public int LRUFrame(int diskPageId) {
		int remplaceid=-1;
		int i=0;
		ArrayList<Frame> FrameComp=new ArrayList<Frame>();
		if(!(this.nbFreeFrame<=0)) {
			return this.getBufferpool().size();
		}
		for(Frame fra: bufferpool) {
				fra.setFrameId(i);
				if(fra.getPinCount()==0) {
					if((fra.isDirty())) {						
					}
					FrameComp.add(fra);
				}
				i++;
		}
		if(!(FrameComp.isEmpty())) {
			remplaceid=this.oldestLastAccessTimestamp(FrameComp);
			if(!(remplaceid==-1)) {
				if((this.bufferpool.get(remplaceid).isDirty())) {
					this.writePageId(remplaceid);
				}
			}
			return remplaceid;
		}
		return remplaceid;
	}
	
	public void writePageId(int FrameId) {
		Frame tempFrame=bufferpool.get(FrameId);
		try {
			f.seek(tempFrame.getPagePosition());
			f.write(tempFrame.getPage().toByte());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Page n°"+tempFrame.getDiskPageId() + " ecrite sur le disque");
	}
	
	
	
	
	
	
	
	public void setBufferpool(ArrayList<Frame> bufferpool) {
		this.bufferpool = bufferpool;
	}
	public RandomAccessFile getF() {
		return f;
	}
	public void setF(RandomAccessFile f) {
		this.f = f;
	}
	public byte[] getPageByte() {
		return pageByte;
	}
	public void setPageByte(byte[] pageByte) {
		this.pageByte = pageByte;
	}
	public int getNbFreeFrame() {
		return nbFreeFrame;
	}
	public void setNbFreeFrame(int nbFreeFrame) {
		this.nbFreeFrame = nbFreeFrame;
	}
	public long getNbFrame() {
		return nbFrame;
	}
	public void setNbFrame(long nbFrame) {
		this.nbFrame = nbFrame;
	}
	public int getPOOLSIZE() {
		return POOLSIZE;
	}
	
	
}
