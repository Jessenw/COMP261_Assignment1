import java.util.ArrayList;

public class Road {
	int ID, oneway, speed, roadclass, notforcar, notforpede, notforbicy;
	String label, city;
	ArrayList<Segment> segs = new ArrayList<Segment>();

	public Road(int roadID, int oneway, int speed, int roadclass, int notforcar, int notforpede, int notforbicy,
			String label, String city) {
		this.ID = roadID;
		this.oneway = oneway;
		this.speed = speed;
		this.roadclass = roadclass;
		this.notforcar = notforcar;
		this.notforpede = notforpede;
		this.notforbicy = notforbicy;
		this.label = label;
		this.city = city;
	}
	
	public void addSeg(Segment seg){
		segs.add(seg);
	}
	
	public ArrayList<Segment> returnSeg(){
		return segs;
	}

	public int returnID(){
		return ID;
	}
	
	public int returnOneway(){
		return oneway;
	}
	
	public int returnSpeed(){
		return speed;
	}
	
	public int returnRoadclass(){
		return roadclass;
	}
	
	public int returnNotforcar(){
		return notforcar;
	}
	
	public int returnNotforpede(){
		return notforpede;
	}
	
	public int returnNotforbicy(){
		return notforbicy;
	}
	
	public String returnLabel(){
		return label;
	}
	
	public String returnCIty(){
		return city;
	}
	
}
