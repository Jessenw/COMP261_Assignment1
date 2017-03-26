import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DigitalMap extends GUI {

	static Map<Integer, Node> nodesMap = new HashMap<Integer, Node>();
	static Map<Integer, Road> roadsMap = new HashMap<Integer, Road>();
	static Map<Integer, Segment> segmentMap = new HashMap<Integer, Segment>();
	static ArrayList<Segment> segmentList = new ArrayList<Segment>();
	static Set<Polygon> polygonSet = new HashSet<Polygon>();

	private Location origin, topLeft, bottomRight;
	private double scale;

	Dimension dimension = getDrawingAreaDimension();
	int width = dimension.width / 2;
	int height = dimension.height / 2;

	Node selectedNode;
	ArrayList<Segment> selectedSegs = new ArrayList<Segment>();
	Trie trie = new Trie();

	@Override
	protected void redraw(Graphics g) {
		for (Polygon polygon : polygonSet) {
			polygon.draw(g, origin, scale);
		}

		for (Node n : nodesMap.values()) {
			n.draw(g, origin, scale);
		}

		for(Segment s : segmentList){
			s.draw(g, origin, scale);
		}
	}

	@Override
	protected void onClick(MouseEvent e) {
		// How close to a node the user must click to register selection
		int tolerance = 10;

		String outputString = "";
		Set<String> output = new HashSet<String>();

		for (Node n : nodesMap.values()) {
			if (e.getX() < (n.point.x + tolerance + width) && e.getX() > (n.point.x - tolerance + width)
					&& e.getY() < (n.point.y + tolerance + height) && e.getY() > (n.point.y - tolerance + height)) {

				// Resets text output area
				getTextOutputArea().setText("");

				if (selectedNode != null) {
					// If there is a previously selected node unselect it
					selectedNode.highlight(false);
				}

				n.highlight(true);
				selectedNode = n;
				getTextOutputArea().append("Intersection ID: " + n.id + ", ");

				/*
				 * Retrieves roads labels of roads which are connected to the
				 * node
				 */
				for (Segment s : n.inSegs) {
					for (Road r : roadsMap.values()) {
						if (s.roadid == r.ID)
							output.add(r.label);
					}
				}

				for (Segment s : n.outSegs) {
					for (Road r : roadsMap.values()) {
						if (s.roadid == r.ID)
							output.add(r.label);
					}
				}

				for (String o : output)
					outputString = outputString + o + ", ";
				break; // Forces there to only be one node selected at a time
			}
		}
		getTextOutputArea().append(outputString);
	}

	@Override
	protected void onSearch() {
		// Resets previously selected segments
		for (Segment s : selectedSegs) {
			s.highlight(false);
		}
		// Resets text output area
		getTextOutputArea().setText("");

		String search = getSearchBox().getText(); // Gets string from search box
		ArrayList<String> values = trie.search(search); // Gets values from trie
		int size = 0; // Variable to make sure only 10 values are printed

		if (values.isEmpty()) {
			getTextOutputArea().append("No roads found with given prefix");
		} else if (values.size() > 10) {
			size = 10;
		} else {
			size = values.size();
		}

		for (int i = 0; i < size; i++) {
			String str = values.get(i);
			getTextOutputArea().append(str + ",  \n");
			// highlights segments in values
			for (Road r : roadsMap.values()) {
				if (r.label.equals(str)) {
					ArrayList<Segment> segs = r.segs;
					for (Segment s : segs) {
						selectedSegs.add(s);
						s.highlight(true);
					}
				}
			}
		}
	}

	@Override
	protected void onMove(Move m) {
		/* --Zoom-- */
		if (m == Move.ZOOM_IN) {
			scale = scale * 1.2;
		} else if (m == Move.ZOOM_OUT) {
			scale = scale / 1.2;
		}

		/* --Move Nodes-- */
		for (Node n : nodesMap.values()) {
			if (m == Move.NORTH) {
				n.move("north");
			} else if (m == Move.EAST) {
				n.move("east");
			} else if (m == Move.SOUTH) {
				n.move("south");
			} else if (m == Move.WEST) {
				n.move("west");
			}
		}

		/* --Moves Segments-- */
		for (Segment s : segmentList) {
			if (m == Move.NORTH) {
				s.move("north");
			} else if (m == Move.EAST) {
				s.move("east");
			} else if (m == Move.SOUTH) {
				s.move("south");
			} else if (m == Move.WEST) {
				s.move("west");
			}
		}

		/* --Moves Polygons-- */
		for (Polygon p : polygonSet) {
			if (m == Move.NORTH) {
				p.move("north");
			} else if (m == Move.EAST) {
				p.move("east");
			} else if (m == Move.SOUTH) {
				p.move("south");
			} else if (m == Move.WEST) {
				p.move("west");
			}
		}
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {
		BufferedReader nodesReader, roadsReader, segmentsReader, polyReader;

		/* --Node-- */
		try {
			// Sets initial Northern, Southern, Easter and Western most points
			// at the center of Auckland
			double NORTH_LAT = -36.84;
			double SOUTH_LAT = -36.84;
			double WEST_LON = 174.76;
			double EAST_LON = 174.76;

			String currentLine;
			nodesReader = new BufferedReader(new FileReader(nodes));

			while ((currentLine = nodesReader.readLine()) != null) {
				String[] values = currentLine.split("\t");

				int nodeID = Integer.parseInt(values[0]);
				double lat = Double.parseDouble(values[1]);
				double lon = Double.parseDouble(values[2]);
				Location location = Location.newFromLatLon(lat, lon);

				nodesMap.put(nodeID, new Node(nodeID, lat, lon, location, this.dimension));

				// Checks if the current latitude or longitude are further from
				// the center compared to the previously set latitude and
				// longitude
				if (lat > NORTH_LAT) {
					NORTH_LAT = lat;
				}
				if (lat < SOUTH_LAT) {
					SOUTH_LAT = lat;
				}
				if (lon < WEST_LON) {
					WEST_LON = lon;
				}
				if (lon > EAST_LON) {
					EAST_LON = lon;
				}
			}

			topLeft = Location.newFromLatLon(NORTH_LAT, WEST_LON);
			bottomRight = Location.newFromLatLon(SOUTH_LAT, EAST_LON);
			// Sets the maps origin and scale
			origin = Location.newFromLatLon(NORTH_LAT, WEST_LON);
			scale = 200 / (topLeft.y - bottomRight.y);

		} catch (IOException e) {
			e.printStackTrace();
		}

		/* --Road-- */
		try {
			String currentLine;
			roadsReader = new BufferedReader(new FileReader(roads));
			roadsReader.readLine(); // Skips over the first line

			while ((currentLine = roadsReader.readLine()) != null) {
				String[] values = currentLine.split("\t");
				int roadID = Integer.parseInt(values[0]);
				String label = values[2];
				String city = values[3];
				int oneway = Integer.parseInt(values[4]);
				int speed = Integer.parseInt(values[5]);
				int roadclass = Integer.parseInt(values[6]);
				int notforcar = Integer.parseInt(values[7]);
				int notforpede = Integer.parseInt(values[8]);
				int notforbicy = Integer.parseInt(values[9]);

				roadsMap.put(roadID,
						new Road(roadID, oneway, speed, roadclass, notforcar, notforpede, notforbicy, label, city));
				trie.add(label); // Used for prefix searching
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* --Segment-- */
		try {
			String currentLine;
			segmentsReader = new BufferedReader(new FileReader(segments));
			segmentsReader.readLine(); // Skips over the first line

			while ((currentLine = segmentsReader.readLine()) != null) {
				ArrayList<Location> coords = new ArrayList<Location>();

				String[] values = currentLine.split("\t");
				Road roadobj = roadsMap.get(Integer.parseInt(values[0]));
				int roadid = Integer.parseInt(values[0]);
				double length = Double.parseDouble(values[1]);
				Node nodeid1 = nodesMap.get(Integer.parseInt(values[2]));
				Node nodeid2 = nodesMap.get(Integer.parseInt(values[3]));

				// Adds the remaining values in the current line to an array
				// ignoring the the first 3 values of the line
				for (int j = 4; j < values.length; j = j + 2) {
					Location location = Location.newFromLatLon(Double.parseDouble(values[j]),
							Double.parseDouble(values[j + 1]));
					coords.add(location);
				}
				Segment newSeg = new Segment(roadid, nodeid1, nodeid2, length, coords, this.dimension);
				segmentMap.put(roadid, newSeg);
				segmentList.add(newSeg);
				// unsure if these are the right way around
				nodeid1.addToStart(newSeg);
				nodeid2.addToEnd(newSeg);
				roadobj.segs.add(newSeg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* --Polygons-- */
		try {
			polyReader = new BufferedReader(new FileReader(polygons));
			String line;
			Integer type = null;
			String allCoords;
			List<String> allCoordsSplit;
			List<Location> coords;
			String values[];

			while ((line = polyReader.readLine()) != null) {

				// Type
				if (line.contains("Type")) {
					values = line.split("=");
					type = Integer.decode(values[1]);
				}

				// Data
				if (line.contains("Data")) {
					allCoords = line.split("=")[1];
					// Takes the first and last bracket out
					allCoords = allCoords.substring(1, allCoords.lastIndexOf(')'));
					// Splits the list
					allCoordsSplit = Arrays.asList(allCoords.split("\\),\\("));
					coords = new ArrayList<Location>();

					for (String coord : allCoordsSplit) {
						double lat = Double.parseDouble(coord.split(",")[0]);
						double lon = Double.parseDouble(coord.split(",")[1]);
						Location location = Location.newFromLatLon(lat, lon);
						coords.add(location);
					}
					polygonSet.add(new Polygon(type, coords, dimension));
				}
			}

		} catch (IOException e) {
			System.out.println("IOException" + e);
		}
	}

	public static void main(String[] args) {
		new DigitalMap();
	}
}
