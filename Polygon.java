import java.util.*;

public class Polygon {
int type, endLevel, cityIdx;
ArrayList<Double> coords;
	
	public Polygon(int type, int endLevel, int cityIdx, ArrayList<Double> coords){
		this.type = type;
		this.endLevel = endLevel;
		this.cityIdx = cityIdx;
		this.coords = coords;
	}
}
