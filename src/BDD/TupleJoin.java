package BDD;
/**
 * 
 * Classe derivee de IndexTuple utilisee lors de l'equi-join
 * (la methode toString() est le principal interet de la classe) 
 *
 */
public class TupleJoin extends IndexTuple {
	private Tuple tuple2;
	public TupleJoin(IndexTuple tuple1, IndexTuple tuple2,int index) {
		super(tuple1.getTuple(),index);
		this.tuple2=tuple2.getTuple();
	}
	
	public String toString() {
		return (" "+this.getTuple().toString()+" ||  "+this.getTuple2().toString()+"  i="+this.getIndex());
	}

	public Tuple getTuple2() {
		return tuple2;
	}

	public void setTuple2(Tuple tuple2) {
		this.tuple2 = tuple2;
	}

}
