package ouroboros.protocol;

import java.util.*;

import peersim.cdsim.*;
import peersim.edsim.*;
import peersim.config.*;
import peersim.core.*;

public class Ouroboros implements EDProtocol, CDProtocol, Linkable{
	
	/******************************
	 * Configuration Constants
	 ******************************/
	private static final String THIRD_SIZE = "thirdsize";
	private static final String PASSIVE_SIZE = "passivesize";
	private static final String NEIGHBORS_SIZE = "neighborsize";
	private static final String POINTERS_SIZE = "pointersize";
	private static final String SAMPLE_SIZE = "samplesize";
	private static final String TRANSPORT_PROTOCOL = "transport";
	
	/******************************
	 * Variables
	 ******************************/
	private Node node;
	
	/******************************
	 * Variables for configuration
	 ******************************/
	private static int thirdSize;
	private static int passiveSize;
	private static int neighborSize;
	private static int pointerSize;
	private static int sampleSize;
	
	/******************************
	 * Views
	 ******************************/
	private Set<Node> tempActive;
	private List<Node> active;
	private Set<Node> passive;
	private Set<Node> third;
	
	/******************************
	 * Active Views
	 ******************************/
	private Set<Node> neighbors;
	private Set<Node> pointers;
	
	/******************************
	 * Initializer
	 ******************************/
	public Ouroboros(String name) {
		node = CommonState.getNode();
		
		//Configuration
		Ouroboros.passiveSize = Configuration.getInt(name + "." + PASSIVE_SIZE);
		Ouroboros.thirdSize = Configuration.getInt(name + "." + THIRD_SIZE);
		Ouroboros.neighborSize = Configuration.getInt(name + "." + NEIGHBORS_SIZE);
		Ouroboros.pointerSize = Configuration.getInt(name + "." + POINTERS_SIZE);
		Ouroboros.sampleSize = Configuration.getInt(name + "." + SAMPLE_SIZE);
				
		//Sets
		tempActive = new HashSet<Node>();
		active = new ArrayList<Node>(neighborSize + pointerSize);
		passive = new HashSet<Node>(passiveSize);
		third = new HashSet<Node>(thirdSize);
		neighbors = new HashSet<Node>(neighborSize);
		pointers = new HashSet<Node>(pointerSize);
	}

	@Override
	public void onKill() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int degree() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Node getNeighbor(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addNeighbor(Node neighbour) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Node neighbor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nextCycle(Node node, int protocolID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processEvent(Node node, int pid, Object event) {
		// TODO Auto-generated method stub
		
	}
}
