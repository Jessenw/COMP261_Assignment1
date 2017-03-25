import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import javax.swing.JTextField;

public class DigitalMap extends GUI {
	static Map<Integer, Node> nodesMap = new HashMap<Integer, Node>();
	static Map<Integer, Road> roadsMap = new HashMap<Integer, Road>();
	static Map<Integer, Segment> segmentMap = new HashMap<Integer, Segment>();
	// List<String> roadLabels = new ArrayList<String>(); // Used for searching for trie

	private Location origin, topLeft, bottomRight;
	private double scale;
	
	Dimension dimension = getDrawingAreaDimension();
	int width = dimension.width/2;
	int height = dimension.height/2;

	Node selectedNode;
	ArrayList<Segment> selectedSegs = new ArrayList<Segment>();
	Trie trie = new Trie(); // Initializes trie structure which is used for prefix search

	@Override
	/*
	 * Iterates through the nodesMap and segmentSet collections and calls draw on each object. Passes origin and scale
	 * parameters which is used for zoom in/out
	 */
	protected void redraw(Graphics g) {
		for (Node n : nodesMap.values()) {
			n.draw(g, origin, scale);
		}

		for (Segment s : segmentMap.values()) {
			s.draw(g, origin, scale);
		}
		
		drawPolygons();
	}
	
	public void drawPolygons(){
		
	}

	@Override
	protected void onClick(MouseEvent e) {
		int allowance = 10; // How close to a node the user must click to register selection
		String outputString = "";
		Set<String> output = new HashSet<String>();

		for (Node n : nodesMap.values()) {
			if (e.getX() < (n.point.x + allowance + width) && e.getX() > (n.point.x - allowance + width)
					&& e.getY() < (n.point.y + allowance + height) && e.getY() > (n.point.y - allowance + height)) {

				getTextOutputArea().setText(""); // If a new node is selected, reset text output area
				if (selectedNode != null) {
					selectedNode.highlight(false); // If there is a previously selected node unselect it
				}

				n.highlight(true);
				selectedNode = n;
				getTextOutputArea().append("Intersection ID: " + n.id + ", ");

				// Retrieves associated road names based on inSegs and outSegs road id's
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
		for(Segment s : selectedSegs){
			s.highlight(false);
		}
		getTextOutputArea().setText(""); // Resets text output area

		String search = getSearchBox().getText();
		ArrayList<String> values = trie.search(search);
		int size = 0;
		
		if(values.isEmpty()) getTextOutputArea().append("No roads found with given prefix");
		else
			if(values.size() > 10) size = 10;
			else size = values.size();
		for (int i = 0; i < size; i++) {
			String str = values.get(i);
			getTextOutputArea().append(str + ",  \n");
			for (Road r : roadsMap.values()) { // highlighting road
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
		/* --Move-- */
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
		for (Segment s : segmentMap.values()) {
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
	}

	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons) {

		BufferedReader nodesReader, roadsReader, segmentsReader, polyReader;

		/* -- Node -- */
		try {
			// Sets initial Northern, Southern, Easter and Western most points
			double NORTH_LAT = -36.84;
			double SOUTH_LAT = -36.84;
			double WEST_LON = 174.76;
			double EAST_LON = 174.76;

			String currentLine;
			nodesReader = new BufferedReader(new FileReader(nodes));

			while ((currentLine = nodesReader.readLine()) != null) {
				String[] values = currentLine.split("\t"); // Splits current line at each tab
				int nodeID = Integer.parseInt(values[0]);
				double lat = Double.parseDouble(values[1]);
				double lon = Double.parseDouble(values[2]);
				Location location = Location.newFromLatLon(lat, lon);

				nodesMap.put(nodeID, new Node(nodeID, lat, lon, location, this.dimension));

				// Checking if current lat and lon are the furtherest point out from the centre
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
				String[] values = currentLine.split("\t"); // Splits current line at each tab
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
				// this.roadLabels.add(label);
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
				nodeid1.addToStart(newSeg);
				nodeid2.addToEnd(newSeg);
				roadobj.addSeg(newSeg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* --Polygons-- */
		try {
			String currentLine;
			polyReader = new BufferedReader(new FileReader(polygons));
			
			while((currentLine = polyReader.readLine()) != null){
				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new DigitalMap();
	}
}
