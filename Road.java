import java.util.ArrayList;

public class Road {
	int ID, oneway, speed, roadclass, notforcar, notforpede, notforbicy;
	String label, city;

	ArrayList<Segment> segs = new ArrayList<Segment>();

	/**
	 * 
	 * @param roadID
	 * @param oneway
	 * @param speed
	 * @param roadclass
	 * @param notforcar
	 * @param notforpede
	 * @param notforbicy
	 * @param label
	 * @param city
	 */
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
}
