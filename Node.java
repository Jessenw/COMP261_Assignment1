
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.List;
import java.awt.Point;
import java.util.ArrayList;

public class Node {
	int id;
	int size = 2;
	double lat, lon;
	Location location;
	Point point;
	ArrayList<Segment> inSegs = new ArrayList<Segment>();
	ArrayList<Segment> outSegs = new ArrayList<Segment>();
	boolean highlight = false;
	int height, width;

	Dimension dimension;

	public Node(int id, double lat, double lon, Location location, Dimension dimension) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.location = location;
		this.height = dimension.height / 2;
		this.width = dimension.width / 2;
	}

	public void highlight(boolean highlight) {
		this.highlight = highlight; // Called by onCick
	}

	public void draw(Graphics g, Location origin, double scale) {
		point = new Point(location.asPoint(origin, scale));
		g.setColor(Color.decode("#4c4c4c"));
		g.fillRect((int) point.x + width, (int) point.y + height, size, size);
		if (highlight) { // Draws are green box to highlight node when selected
			// by the onClick method
			g.setColor(Color.ORANGE);
			g.fillRect((int) point.x - 4 + width, (int) point.y - 4 + height, 8, 8);
		}
	}

	public void move(String dir) {
		if (dir == "north") {
			location = location.moveBy(0, -10);
		} else if (dir == "east") {
			location = location.moveBy(-10, 0);
		} else if (dir == "south") {
			location = location.moveBy(0, 10);
		} else if (dir == "west") {
			location = location.moveBy(10, 0);
		}
	}

	public void addToStart(Segment seg) {
		inSegs.add(seg);
	}

	public void addToEnd(Segment seg) {
		outSegs.add(seg);

	}
}
