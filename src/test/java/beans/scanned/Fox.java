package beans.scanned;

import container.annotation.Copied;
import container.annotation.Report;
import container.annotation.SnowFlake;

@SnowFlake("foxFlake")
@Copied
@Report(path = "c:")
public class Fox {
	private int weight;
	
	public void trickMe() {}

	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
