package ouroboros.events;

import peersim.core.Node;

public class Event implements Cloneable {

	private Node src;
	private Node dest;
	private int pid;
	private Object event;


	public Event(Node src, Node dest, int pid, Object event) {
		this.src = src;
		this.dest = dest;
		this.pid = pid;
		this.event = event;
	}
	
	public Node getSrc() {
		return src;
	}
	
	public Node getDest() {
		return dest;
	}
	
	public int getPid() {
		return pid;
	}
	
	public Object getEvent() {
		return event;
	}
	
	public Object clone() throws CloneNotSupportedException {
		Event nev = (Event) super.clone();
		nev.src = src;
		nev.dest = dest;
		return nev;
	}
}
