package ouroboros.transports;

import peersim.core.Node;

public abstract class TCPEvent {
	
	private Node src;
	private int pid;
	
	public TCPEvent(Node src, Node dest, int pid) {
		this.src = src;
		this.pid = pid;
	}

	public int getPid() {
		return pid;
	}

	public Node getSrc() {
		return src;
	}
}
