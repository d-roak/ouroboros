package ouroboros.inet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Path implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6655164455439784952L;
	private int cost;
	private ArrayList<Integer> path;

	public Path() {
		this.cost = 0;
		this.path = new ArrayList<Integer>();
	}
	
	public Path(int cost, Collection<Integer> c) {
		this.cost = cost;
		this.path = new ArrayList<Integer>(c);
	}
	
	public void add(Integer node) {
		this.path.add(node);
	}
	
	public void add(Integer node, int cost) {
		this.path.add(node);
		this.cost += cost;
	}
	
	public int getCost() {
		return this.cost;
	}
	
	public int getNumberOfHops() {
		return this.path.size();
	}
	
	public Collection<Integer> getPath() {
		return this.path;
	}

	public boolean contains(int router) {
		return this.path.contains(new Integer(router));
	}
}
