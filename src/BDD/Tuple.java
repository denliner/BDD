package BDD;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Tuple {
	private String name;
	private int age;
	private double salary;
	
	public Tuple() { //initialise une tuple "vierge" ou "vide"
		this.name="NULL    ";
		this.age=-1;
		this.salary=-1;
	}
	public Tuple(String name,int age, double salaire) {
		this.name=nameSize(name);
		this.age=age;
		salary=salaire;
	}
	public static String nameSize(String name) {
		if(name.length()==8) {
			return name;
		}
		else {
			if(name.length()>8) {
				return name.substring(0, 7);
			}
			else {
				return nameSize(name+" ");
			}
		}		
	}
	public Tuple(ByteBuffer f) {
	 byte [] s=new byte[8];
	 f.position(0);
	 f.get(s);
	 this.name=new String(s, StandardCharsets.UTF_8).replace('_',' ');
	 f.position(8);
	 this.age=f.getInt();
	 this.salary=f.getDouble();
	}
	
	public byte[] toByte() {
		ByteBuffer buffer=ByteBuffer.allocate(20);
		buffer.put(this.name.getBytes());
		buffer.putInt(this.age);
		buffer.putDouble(this.salary);
		return buffer.array();
		
	}
	public String getName() {
		return name;
	}
	
	public int getAge() {
		return age;
	}
	
	public double getSalary() {
		return salary;
	}
	public String toString() {
		return ("Nom:   "+this.getName()+"   Age:"+this.getAge()+"   Salaire: "+this.getSalary());
	}
	
	/*public void Save(RandomAccessFile file) throws IOException {
		Bin.writeATuple(this.getName(), this.getAge(), this.getSalary(), file);
	}*/
	
}
