package BDD;
import java.util.Date;

public class Frame {
	private int frameId;
	private int diskPageId;
	private int pinCount;
	private boolean dirty;
	private int PagePosition;
	private double lastAccessTimestamp;
	private DiskPage Page;

	public Frame(int frameID,int diskPageId) {
		this.frameId=frameID;
		this.diskPageId=diskPageId;
		this.PagePosition=this.diskPageId*DiskPage.DISKPAGEBYTESSIZE;
		this.pinCount=0;
		this.dirty=false;
	}
	public void setDiskPage(DiskPage Page) {
		this.Page=Page;
		this.diskPageId=Page.getPageID();
		this.lastAccessTimestamp= new Date().getTime();
		this.PagePosition=this.diskPageId*DiskPage.DISKPAGEBYTESSIZE;
		this.pinCountPlus();
	}
	public int getFrameId() {
		return frameId;
	}
	public DiskPage getPage() {
		return this.Page;
	}
	public void pinCountPlus() {
		this.pinCount=1;
	}
	public void pinCountMoins() {
		this.pinCount=0;
	}
	public int getPagePosition() {
		return this.PagePosition;
	}
	public void setFrameId(int frameId) {
		this.frameId = frameId;
	}
	public int getDiskPageId() {
		return diskPageId;
	}
	public void setDiskPageId(int diskPageId) {
		this.diskPageId = diskPageId;
	}
	public int getPinCount() {
		return pinCount;
	}
	public void setPinCount(int pinCount) {
		this.pinCount = pinCount;
	}
	public boolean isDirty() {
		return dirty;
	}
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	public double getLastAccessTimestamp() {
		return lastAccessTimestamp;
	}
	public void setLastAccessTimestamp() {
		Date date=new Date();
		this.lastAccessTimestamp=date.getTime();
	}
	
	
	
}
