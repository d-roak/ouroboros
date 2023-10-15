package ouroboros;

import peersim.core.*;

public class OuroborosNode extends GeneralNode implements Comparable<Node> {

	public OuroborosNode(String prefix) {
		super(prefix);
	}

	public boolean between(Node n1, Node n2) {
		if (this.getID() > n1.getID() && this.getID() < n2.getID())
			return true;
		else if (n1.getID() > n2.getID() && (this.getID() > n1.getID() || this.getID() < n2.getID()))
			return true;
		return false;
	}
	
	public long proximity(Node n) {
		return Math.abs(this.getID()-n.getID());
	}

	@Override
	public int compareTo(Node n) {
		if (this.getID() < n.getID())
			return -1;
		else if (this.getID() > n.getID())
			return 1;
		else
			return 0;
	}

}
