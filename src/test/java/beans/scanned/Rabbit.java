package beans.scanned;

import container.annotation.Denied;
import container.annotation.Report;
import container.annotation.SnowFlake;

@SnowFlake("rabbitFlake")
@Denied
@Report(path = "c:")
public class Rabbit {
	private int age;
	private int name;
	
	public void sing() {}
	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getName() {
		return name;
	}
	public void setName(int name) {
		this.name = name;
	}
}
