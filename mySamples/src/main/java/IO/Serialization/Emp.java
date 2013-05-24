package IO.Serialization;

import java.io.Serializable;

public class Emp implements Serializable{
	private int id;
	private transient int age;
	private String name;
	
	
	
	public Emp(int id, int age, String name) {
		super();
		this.id = id;
		this.age = age;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "Emp [age=" + age + ", id=" + id + ", name=" + name + "]";
	}

	
	
	
}
