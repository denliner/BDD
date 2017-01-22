package BDD;
/**
 * 
 * Classe utiliser pour garder l'index des tuple dans le fichier lorsque qu'elles sont
 * recuperee par la methode readAllTUple du buffermanager
 * 
 *
 */
public class IndexTuple {
	private Tuple tuple;
	private int index;
	public IndexTuple(Tuple tuple,int index) {
		this.tuple=tuple;
		this.index=index;
	}
	public Tuple getTuple() {
		return tuple;
	}
	public void setTuple(Tuple tuple) {
		this.tuple = tuple;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String toString() {
		return (this.index+ "  "+this.tuple.toString());
	}
}
