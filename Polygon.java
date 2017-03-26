import java.awt.*;
import java.util.List;

public class Polygon {

	private Integer type;
	private List<Location> coords;
	private int width, height;

	public Polygon(Integer type, List<Location> coords, Dimension dimension) {
		this.type = type;
		this.coords = coords;
		this.width = dimension.width / 2;
		this.height = dimension.height / 2;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<Location> getCoords() {
		return coords;
	}

	public void setCoords(List<Location> coords) {
		this.coords = coords;
	}

	public void move(String dir) {
		for (int i = 0; i < coords.size(); i++) {
			if (dir == "north") {
				Location loc = coords.get(i);
				loc = loc.moveBy(0, -10);
				coords.set(i, loc);
			} else if (dir == "east") {
				Location loc = coords.get(i);
				loc = loc.moveBy(-10, 0);
				coords.set(i, loc);
			} else if (dir == "south") {
				Location loc = coords.get(i);
				loc = loc.moveBy(0, 10);
				coords.set(i, loc);
			} else if (dir == "west") {
				Location loc = coords.get(i);
				loc = loc.moveBy(10, 0);
				coords.set(i, loc);
			}
		}
	}

	public void draw(Graphics g, Location origin, double scale) {
		if (type <= 19 && type >= 1) {
			//
			g.setColor(Color.decode("0xC7C9C8"));
		} else if (type <= 39 && type >= 20) {
			// Sets the color of the parks/forests/reserves
			g.setColor(Color.decode("#cbe6a3"));
		} else {
			// Sets the color of the water
			g.setColor(Color.decode("#a3ccff"));
		}

		int[] xPoints = new int[coords.size()];
		int[] yPoints = new int[coords.size()];
		int i = 0;

		for (Location location : coords) {
			xPoints[i] = location.asPoint(origin, scale).x + width;
			yPoints[i] = location.asPoint(origin, scale).y + height;
			i++;
		}
		g.fillPolygon(xPoints, yPoints, i);
	}
}
